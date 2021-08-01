package com.example;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@SpringBootApplication
public class SpringAsyncSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAsyncSampleApplication.class, args);
    }

    @Bean("Thread1") // この設定は指定していないので利用されていない
    public Executor taskExecutor1() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setQueueCapacity(1);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("Thread1--");
        executor.initialize();
        return executor;
    }

    @Bean("Thread2") // ここで設定した"Thread2"を@Asyncに設定するとその設定が利用される
    public Executor taskExecutor2() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // デフォルトのThreadのサイズ。あふれるとQueueCapacityのサイズまでキューイングする
        executor.setQueueCapacity(1); // 待ちのキューのサイズ。あふれるとMaxPoolSizeまでThreadを増やす
        executor.setMaxPoolSize(500); // どこまでThreadを増やすかの設定。この値からあふれるとその処理はリジェクトされてExceptionが発生する
        executor.setThreadNamePrefix("Thread2--");
        executor.initialize();
        return executor;
    }

    @Bean("Reject") // この設定にするとキューイングできないしThreadも増やせないしでRejectedExceptionが発生する
    public Executor rejectTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setQueueCapacity(0);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("Reject--");
        executor.initialize();
        return executor;
    }

}
