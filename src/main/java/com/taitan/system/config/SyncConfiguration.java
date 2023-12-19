package com.taitan.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class SyncConfiguration {
    @Bean(name = "asyncPoolTaskExecutor")
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数，设置核心线程数。核心线程数是线程池中一直保持活动的线程数量，即使它们是空闲的。
        taskExecutor.setCorePoolSize(10);
        //设置线程池维护线程的最大数量。当缓冲队列已满并且核心线程数的线程都在忙碌时，线程池会创建新的线程，直到达到最大线程数。
        taskExecutor.setMaxPoolSize(100);
        //设置缓冲队列的容量。当所有的核心线程都在忙碌时，新的任务将会被放入缓冲队列中等待执行。
        taskExecutor.setQueueCapacity(50);
        //设置非核心线程的空闲时间。当超过核心线程数的线程在空闲时间达到设定值后，它们将被销毁，以减少资源的消耗。
        taskExecutor.setKeepAliveSeconds(200);
        //异步方法内部线程名称
        taskExecutor.setThreadNamePrefix("async-");
        /**
         * 当线程池的任务缓存队列已满并且线程池中的线程数目达到maximumPoolSize，如果还有任务到来就会采取任务拒绝策略
         * 通常有以下四种策略：
         * ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。
         * ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。
         * ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
         * ThreadPoolExecutor.CallerRunsPolicy：重试添加当前的任务，自动重复调用 execute() 方法，直到成功
         */
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }
}