package com.taitan.system;

import com.taitan.system.pojo.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Scanner;

/**
 * Redis 单元测试
 *
 * @author: haoxr
 * @date: 2023/02/17
 */
@SpringBootTest
@Slf4j
public class  RedisTests {

    @Autowired
    private  RedisTemplate redisTemplate;

    @Test
    public void testRedis() {
        String a = new String("ab"); // a 􀔅􀓞􀓻􀭚􁊠
        String b = new String("ab"); // b􀔅􀝚􀓞􀓻􀭚􁊠,􀩒􁨝􁌱􀙖􀨻􀓞􀻏
        String aa = "ab"; // 􀶱􀣁􀬉􁰁􀿰􀓾
        String bb = "ab"; // 􀕗􀬉􁰁􀿰􀓾􀺱􀲤
//        if (aa == bb) // true
            System.out.println(aa == bb);
        if (a == b) // false􀒅􁶋􀝶􀓞􀩒􁨝
            System.out.println("aWXb");
        if (a.equals(b)) // true
            System.out.println("aEQb");
        if (42 == 42.0) { // true
            System.out.println("true");
        }
    }

    @Test
    public void testRedis1() {
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()){
            String line=scan.next();
            if(line.split(",").length>1000){
                System.out.println(0);
                continue;
            }
            int result=0;
            String[] split = line.replaceAll(",","").split("0+");
            if(split.length!=0){
                for(String temp:split){
                    result+=calculate(temp.length());
                }
            }
        }

    }
    private static int calculate(int length){
        return (length>=1&&length<=3)?1:(int)Math.ceil(length/3.0);
    }

    /**
     * Redis 序列化测试
     */
    @Test
    public void testRedisSerializer() {
        SysUser user = new SysUser();

        user.setId(1l);
        user.setNickname("张三");
        // 写
        redisTemplate.opsForValue().set("user", user);

        // 读
        SysUser userCache = (SysUser)redisTemplate.opsForValue().get("user");
        log.info("userCache:{}", userCache);

    }




}
