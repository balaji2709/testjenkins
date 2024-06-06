package com.example.dubaipolica;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.example.dubaipolica.service.ElkService;

@SpringBootApplication
@ComponentScan({"com.example"})
public class Demo1Application {

	public static void main(String[] args) {
        System.out.println("Hello, World!");
    }

}
