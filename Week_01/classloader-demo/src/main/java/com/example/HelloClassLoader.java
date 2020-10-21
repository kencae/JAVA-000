package com.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author
 * @date 2020/10/21-9:05
 */
public class HelloClassLoader extends ClassLoader{

    public static void main(String[] args) throws Exception{
        Class<?> hello = new HelloClassLoader().findClass("Hello");
        Object object = hello.newInstance();
        Method helloMethod = hello.getMethod("hello");
        helloMethod.invoke(object);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = loadClassData(name);
        return defineClass(name,bytes,0,bytes.length);
    }

    private byte[] loadClassData(String name){
        File file = new File("src/main/java/com/example/"+name+".xlass");
        try (FileInputStream inputStream = new FileInputStream(file);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            int length = 0;
            while((length=inputStream.read())!=-1){
                outputStream.write(255-length);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
