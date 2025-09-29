package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloJava {
    public static void main(String[] args) {
        System.out.println("Hello, Java 17!");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Java vendor: " + System.getProperty("java.vendor"));
        
        SpringApplication.run(HelloJava.class, args);
    }
}