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

	public static void main(String[] args) throws IOException, SQLException {
		SpringApplication.run(Demo1Application.class, args);
		ElkService elkService = new ElkService();
		
//        Connection oracleConnection = elkService.createSqlConnection();
        try {
        	elkService.main(args, null);
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

}
