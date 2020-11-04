学习笔记

### 1. 并行GC （Parallel GC)

年轻代和老年代的垃圾回收都会触发 STW 事件。
在年轻代使用 标记-复制（ mark-copy）算法，在老年代使用 标记-清除-整理（ mark-sweepcompact）算法。
-XX： ParallelGCThreads=N 来指定 GC 线程数， 其默认值为 CPU 核心数。
并行垃圾收集器适用于多核服务器，主要目标是增加吞吐量。因为对系统资源的有效使用，能达到
更高的吞吐量:
• 在 GC 期间，所有 CPU 内核都在并行清理垃圾，所以总暂停时间更短；
• 在两次 GC 周期的间隔期，没有 GC 线程在运行，不会消耗任何系统资源。  

```powershell
# 默认启动命令：
java -jar gateway-server-0.0.1-SNAPSHOT.jar

PS C:\Users\nealk> jps -l
2208
5168 sun.tools.jps.Jps
25960
1772 gateway-server-0.0.1-SNAPSHOT.jar

PS C:\Users\nealk> jmap -heap 1772
Attaching to process ID 1772, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.144-b01

using thread-local object allocation.    
Parallel GC with 8 thread(s)     #默认使用并行gc

Heap Configuration:									#堆内存分配情况，对应JVM的堆内存配置
   MinHeapFreeRatio         = 0						 #最小堆内存可用比例
   MaxHeapFreeRatio         = 100					 #最大堆内存可用比例
   MaxHeapSize              = 4265607168 (4068.0MB)    #最大堆空间，默认物理内存1/4
   NewSize                  = 89128960 (85.0MB)		   #新生代分配空间大小   
   MaxNewSize               = 1421869056 (1356.0MB)    #最大新生代空间
   OldSize                  = 179306496 (171.0MB)	   #老年代大小
   NewRatio                 = 2				          #新生代比例
   SurvivorRatio            = 8					      #新生代与survivor比例
   MetaspaceSize            = 21807104 (20.796875MB)    # meta区大小
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:                  						#堆使用情况
PS Young Generation									#新生代使用情况
Eden Space:										   #
   capacity = 203423744 (194.0MB)
   used     = 128132056 (122.19625091552734MB)
   free     = 75291688 (71.80374908447266MB)
   62.98775820388008% used
From Space:											#s0
   capacity = 9961472 (9.5MB)
   used     = 9942816 (9.482208251953125MB)
   free     = 18656 (0.017791748046875MB)
   99.81271844161184% used	
To Space:											#s1
   capacity = 13107200 (12.5MB)
   used     = 0 (0.0MB)
   free     = 13107200 (12.5MB)
   0.0% used
PS Old Generation
   capacity = 102236160 (97.5MB)
   used     = 12178784 (11.614593505859375MB)
   free     = 90057376 (85.88540649414062MB)
   11.912403595753204% used

15857 interned Strings occupying 2118144 bytes.
```

### 2. 串行GC （Serial GC)

串行 GC 对年轻代使用 mark-copy（标记-复制） 算法，对老年代使用 mark-sweep-compact
（标记-清除-整理）算法。
两者都是单线程的垃圾收集器，不能进行并行处理，所以都会触发全线暂停（ STW），停止所
有的应用线程。
因此这种 GC 算法不能充分利用多核 CPU。不管有多少 CPU 内核， JVM 在垃圾收集时都只能使
用单个核心。
CPU 利用率高，暂停时间长。简单粗暴，就像老式的电脑，动不动就卡死。
该选项只适合几百 MB 堆内存的 JVM，而且是单核 CPU 时比较有用。  

```
#启动参数 -XX:+UseSerialGC
java -jar -XX:+UseSerialGC gateway-server-0.0.1-SNAPSHOT.jar
```

```
PS C:\Users\nealk> jps -l
19488 gateway-server-0.0.1-SNAPSHOT.jar
2208
25268 sun.tools.jps.Jps
25960
2476 org.jetbrains.jps.cmdline.Launcher
PS C:\Users\nealk>
```

```powershell
PS C:\Users\nealk> jmap -heap 19488
Attaching to process ID 19488, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.144-b01

using thread-local object allocation.
Mark Sweep Compact GC								#

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 4265607168 (4068.0MB)
   NewSize                  = 89456640 (85.3125MB)
   MaxNewSize               = 1421869056 (1356.0MB)
   OldSize                  = 178978816 (170.6875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 80609280 (76.875MB)
   used     = 38604824 (36.816429138183594MB)
   free     = 42004456 (40.058570861816406MB)
   47.891289935848576% used
Eden Space:
   capacity = 71696384 (68.375MB)
   used     = 34104864 (32.524932861328125MB)
   free     = 37591520 (35.850067138671875MB)
   47.568457566841865% used
From Space:
   capacity = 8912896 (8.5MB)
   used     = 4499960 (4.291496276855469MB)
   free     = 4412936 (4.208503723144531MB)
   50.48819149241728% used
To Space:
   capacity = 8912896 (8.5MB)
   used     = 0 (0.0MB)
   free     = 8912896 (8.5MB)
   0.0% used
tenured generation:
   capacity = 178978816 (170.6875MB)
   used     = 18129472 (17.28961181640625MB)
   free     = 160849344 (153.39788818359375MB)
   10.129395425210545% used

16052 interned Strings occupying 2157368 bytes.
```

### 3. CMS GC 

​	-XX： +UseConcMarkSweepGC
其对年轻代采用并行 STW 方式的 mark-copy (标记-复制)算法，对老年代主要使用并发 marksweep (标记-清除)算法。
CMS GC 的设计目标是避免在老年代垃圾收集时出现长时间的卡顿，主要通过两种手段来达成此
目标：

1. 不对老年代进行整理，而是使用空闲列表（ free-lists） 来管理内存空间的回收。
2. 在 mark-and-sweep （ 标记-清除） 阶段的大部分工作和应用线程一起并发执行。
也就是说，在这些阶段并没有明显的应用线程暂停。但值得注意的是，它仍然和应用线程争抢
CPU 时间。默认情况下， CMS 使用的并发线程数等于 CPU 核心数的 1/4。
如果服务器是多核 CPU，并且主要调优目标是降低 GC 停顿导致的系统延迟，那么使用 CMS    是个很明智的选择。进行老年代的并发回收时，可能会伴随着多次年轻代的 minor GC  

```powershell
# 启动指定gc
java -jar -XX:+UseConcMarkSweepGC gateway-server-0.0.1-SNAPSHOT.jar

PS C:\Users\nealk> jps -l
2208
25960
20860 gateway-server-0.0.1-SNAPSHOT.jar
23356 sun.tools.jps.Jps
2476 org.jetbrains.jps.cmdline.Launcher
PS C:\Users\nealk> jmap -heap 20860
Attaching to process ID 20860, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.144-b01

using parallel threads in the new generation.
using thread-local object allocation.
Concurrent Mark-Sweep GC            					#CMS gc

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 4265607168 (4068.0MB)
   NewSize                  = 89456640 (85.3125MB)
   MaxNewSize               = 697892864 (665.5625MB)
   OldSize                  = 178978816 (170.6875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 80543744 (76.8125MB)
   used     = 18565728 (17.705657958984375MB)
   free     = 61978016 (59.106842041015625MB)
   23.050490426668024% used
Eden Space:
   capacity = 71630848 (68.3125MB)
   used     = 13465480 (12.841682434082031MB)
   free     = 58165368 (55.47081756591797MB)
   18.798437231959056% used
From Space:
   capacity = 8912896 (8.5MB)
   used     = 5100248 (4.863975524902344MB)
   free     = 3812648 (3.6360244750976562MB)
   57.22324146943934% used
To Space:
   capacity = 8912896 (8.5MB)
   used     = 0 (0.0MB)
   free     = 8912896 (8.5MB)
   0.0% used
concurrent mark-sweep generation:
   capacity = 178978816 (170.6875MB)
   used     = 17472488 (16.663063049316406MB)
   free     = 161506328 (154.0244369506836MB)
   9.762321815784054% used

16048 interned Strings occupying 2156824 bytes.
```



### 4.G1 GC
G1 的全称是 Garbage-First，意为垃圾优先，哪一块的垃圾最多就优先清理它。
G1 GC 最主要的设计目标是：将 STW 停顿的时间和分布，变成可预期且可配置的。事实上， G1 GC 是一款软实时垃圾收集器，可以为
其设置某项特定的性能指标。为了达成可预期停顿时间的指标， G1 GC 有一些独特的实现。
首先，堆不再分成年轻代和老年代，而是划分为多个（通常是 2048 个）可以存放对象的 小块堆区域(smaller heap regions)。每个小块，可能一会被定义成 Eden 区，一会被指定为 Survivor区或者Old 区。在逻辑上，所有的 Eden 区和 Survivor区合起来就是年轻代，所有的 Old 区拼在一起那就是老年代
-XX： +UseG1GC -XX:MaxGCPauseMillis=50