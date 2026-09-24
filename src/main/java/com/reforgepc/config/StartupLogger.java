package com.reforgepc.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println();
        System.out.println("ReForgePC is running at: http://localhost:8080/");
        System.out.println();
    }
}