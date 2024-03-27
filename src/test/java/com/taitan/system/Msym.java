package com.taitan.system;


import com.taitan.system.common.enums.MenuTypeEnum;
import com.taitan.system.framework.easycaptcha.config.EasyCaptchaConfig;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

@ActiveProfiles({"dev"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes={SystemApplication.class})
public class   Msym {


    @Autowired
    private EasyCaptchaConfig easyCaptchaConfig;

    @Test
    public void test(String[] arg) {
        String a = new String("ab"); // a 􀔅􀓞􀓻􀭚􁊠
        String b = new String("ab"); // b􀔅􀝚􀓞􀓻􀭚􁊠,􀩒􁨝􁌱􀙖􀨻􀓞􀻏
        String aa = "ab"; // 􀶱􀣁􀬉􁰁􀿰􀓾
        String bb = "ab"; // 􀕗􀬉􁰁􀿰􀓾􀺱􀲤
        if (aa == bb) // true
            System.out.println("aaWXbb");
        if (a == b) // false􀒅􁶋􀝶􀓞􀩒􁨝
            System.out.println("aWXb");
        if (a.equals(b)) // true
            System.out.println("aEQb");
        if (42 == 42.0) { // true
            System.out.println("true");
        }
    }
    @Test
    public void test1(){

        for (MenuTypeEnum meun:MenuTypeEnum.values()){
            System.out.println(meun+",ordinal:"+meun.ordinal()+",name:"+meun.getLabel());
        }

    }

    @SneakyThrows
    @Test
    @Async
    public void test4(){


        long t1 = System.currentTimeMillis();
        Thread.sleep(5000);
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
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

