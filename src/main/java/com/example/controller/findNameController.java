package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.User;
import com.example.service.AsyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class findNameController {

    @Autowired
    AsyncService asyncService;

    @GetMapping("/users/")
    public List<String> findUsers() throws InterruptedException, ExecutionException{
        long start = System.currentTimeMillis();
        long heavyProcessTime = 3000L;
        long lightProcessTime = 1000L;

        log.warn("request started");
        CompletableFuture<User> heavyProcess = asyncService.findName("heavy", heavyProcessTime);
        CompletableFuture<User> lightProcess = asyncService.findName("light", lightProcessTime);

        // heavyProcessが終わったら実行される処理
        heavyProcess.thenAcceptAsync(heavyProcessResult -> {
            log.warn("finished name = " + heavyProcessResult + ", sleep = " + heavyProcessTime);
        });

        // lightProcessが終わったら実行される処理
        lightProcess.thenAcceptAsync(lightProcessResult ->{
            log.warn("finished name = " + lightProcessResult + ", sleep = " + lightProcessTime);
        });

        // 指定した処理が終わったらこれ以降の処理が実行される
        CompletableFuture.allOf(heavyProcess, lightProcess).join();

        // 返却値の作成
        List<String> users = new ArrayList<>();
        users.add(heavyProcess.get().toString());
        users.add(lightProcess.get().toString());

        Thread.sleep(10L);

        long end = System.currentTimeMillis();
        // 処理全体の時間を出力
        log.warn("request finished. time: " + (end - start) + "ms");

        return users;
    }

    /**
     * カスタマイズ検証
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @GetMapping("/users2/")
    public List<String> findUsers2() throws InterruptedException, ExecutionException{

        long start = System.currentTimeMillis();

        log.warn("async request started");
        CompletableFuture<List<String>> users1 = asyncService.findUsersLogic("users1", 3000L);
        CompletableFuture<List<String>> users2 = asyncService.findUsersLogic("users2", 1000L);
        CompletableFuture<List<String>> users3 = asyncService.findUsersLogic2("users3", 3000L);
        CompletableFuture<List<String>> users4 = asyncService.findUsersLogic2("users4", 1000L);

        users1.thenAcceptAsync(x ->{
            log.warn("finished users1");
        });

        users2.thenAcceptAsync(x ->{
            log.warn("finished users2");
        });

        users3.thenAcceptAsync(x ->{
            log.warn("finished users4");
        });

        users3.thenAcceptAsync(x ->{
            log.warn("finished users4");
        });

        CompletableFuture.allOf(users1, users2, users3, users4).join();

        List<String> resultUsers = new ArrayList<String>();
        resultUsers.addAll(users1.get());
        resultUsers.addAll(users2.get());
        resultUsers.addAll(users3.get());
        resultUsers.addAll(users4.get());

        long end = System.currentTimeMillis();
        // 処理全体の時間を出力
        log.warn("request finished. time: " + (end - start) + "ms");

        return resultUsers;
    }
}