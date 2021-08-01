package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AsyncService {

    int count = 0;
    int count9 = 90000;

    @Async("Thread2")
    public CompletableFuture<User> findName(String name, Long sleepTime) throws InterruptedException{
        log.warn("Async function started. processName: " + name + "sleepTime: " + sleepTime);
        Thread.sleep(sleepTime);

        count++;
        User user = new User();
        user.setName(name);
        user.setId(count);

        // 非同期処理のハンドリングができるようにCompletableFuture実際に使いたい返却値を入れて利用する
        return CompletableFuture.completedFuture(user);
    }

    @Async("Thread2")
    public CompletableFuture<List<String>> findUsersLogic(String name, long sleepTime) throws InterruptedException, ExecutionException{
        log.warn("Async function started. sleepTime: " + sleepTime);
        Thread.sleep(sleepTime);

        log.warn("request started");
        User process1 = findName2("processA");
        User process2 = findName2("processB");

        List<String> users = new ArrayList<>();
        users.add(process1.toString());
        users.add(process2.toString());

        Thread.sleep(10L);

        log.warn("Async function finished. " + name);
        return CompletableFuture.completedFuture(users);
    }

    @Async("Thread2")
    public CompletableFuture<List<String>> findUsersLogic2(String name, long sleepTime) throws InterruptedException, ExecutionException{
        log.warn("Async function started. sleepTime: " + sleepTime);
        Thread.sleep(sleepTime);

        log.warn("request started");
        User process1 = findName2("processC");
        User process2 = findName2("processD");

        List<String> users = new ArrayList<>();
        users.add(process1.toString());
        users.add(process2.toString());

        Thread.sleep(10L);

        log.warn("Async function finished. " + name);
        return CompletableFuture.completedFuture(users);
    }

    private User findName2(String name) throws InterruptedException{
        log.warn("function started. processName: " + name);

        count++;
        User user = new User();
        user.setName(name);
        user.setId(count);

        log.warn("function finished. processName: " + name);
        return user;
    }
}
