package com.example.bajajchallenge;

import com.example.bajajchallenge.service.MutualFollowerService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

    private final MutualFollowerService service;

    public StartupRunner(MutualFollowerService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        service.process();
    }
}
