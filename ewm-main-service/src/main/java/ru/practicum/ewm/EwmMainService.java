package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum.statsclient.client", "ru.practicum.statsclient", "ru.practicum.ewm"})
public class EwmMainService {

    public static void main(String[] args) {
        SpringApplication.run(EwmMainService.class, args);
    }
}
