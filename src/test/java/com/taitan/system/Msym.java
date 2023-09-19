package com.taitan.system;


import com.taitan.system.framework.easycaptcha.config.EasyCaptchaConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@ActiveProfiles({"dev"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes={SystemApplication.class})


public class Msym {


    @Autowired
    private EasyCaptchaConfig easyCaptchaConfig;


    public void test(String[] arg){
        for (String string : arg) {
            System.out.println(string);
        }
    }
    @Test
    public void test1(){

//            System.out.println(easyCaptchaConfig);
        System.out.println(twoSum(new int[]{2,7,11,15},9));

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
    public int[] twoSum(int[] nums, int target) {
        for(int i=0;i<nums.length;i++){
            for(int j=i+1;j<nums.length-i;j++){
                if(nums[i]+nums[j]==target){
                    return new int[]{i,j};
                }
            }
        }
        return null;
    }
}

