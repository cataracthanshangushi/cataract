package com.taitan.system;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;

/**
 * SpringBoot 配置加载顺序单元测试类
 *
 * @author: haoxr
 * @date: 2023/02/23
 */
@SpringBootTest
@Slf4j
public class ConfigLoadOrderTests {

    /**
     * 测试配置加载顺序
     */
    @Test
    public void testConfigLoadOrder(ApplicationContext context) {
        Environment environment = context.getEnvironment();
        String property = environment.getProperty("config.name");
        log.info(property);
    }

    @SneakyThrows
    @org.junit.Test
    @Async
    public void test4(){
        long t1 = System.currentTimeMillis();
        Thread.sleep(5000);
        System.out.println(Thread.activeCount());
        long t2 = System.currentTimeMillis();
        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t2-t1);
    }

}
