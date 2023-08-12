package com.taitan.system;


import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public class Msym {

    public void test(String[] arg){
        for (String string : arg) {
            System.out.println(string);
        }
    }
    public void test1(){

            System.out.println("string");

    }
    @Test
    public void demo1() throws Exception {
        //获取字节码对象
        Class<Msym> clazz = (Class<Msym>) Class.forName("com.taitan.system.Msym");
        //获取一个对象
        Constructor con =  clazz.getConstructor();
        Msym m = (Msym) con.newInstance();
        String[] s = new String[]{"aa","bb"};
        //获取Method对象
        Method method = clazz.getMethod("test1");
        //调用invoke方法来调用
        method.invoke(m);
    }
}
