package com.github.example.task;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Slf4j
public class Test {
    @Scheduled(cron = "*/10 * * * * *")
    public void cron() {
        Thread t = Thread.currentThread();
        String name = t.getName();
        LocalDateTime currentDateTime = LocalDateTime.now();
        log.info("定时执行，线程名称：{}, 时间：{}",name, currentDateTime);
    }
}
