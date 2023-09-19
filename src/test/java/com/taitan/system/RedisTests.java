package com.taitan.system;

import com.taitan.system.pojo.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis 单元测试
 *
 * @author: haoxr
 * @date: 2023/02/17
 */
@SpringBootTest
@Slf4j
public class RedisTests {

    @Autowired
    private  RedisTemplate redisTemplate;

    @Test
    public void testRedis() {
        System.out.println();
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
