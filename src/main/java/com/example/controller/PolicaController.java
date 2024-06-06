package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/")
public class PolicaController {

	 @GetMapping("/")
	  public String hello() {
		 return "<!DOCTYPE html>\n" +
	                "<html lang=\"en\">\n" +
	                "<head>\n" +
	                "  <meta charset=\"UTF-8\">\n" +
	                "  <title>My Application</title>\n" +
	                "</head>\n" +
	                "<body>\n" +
	                "  <h1>Hi! Your application works fine, deployed on port 8090!</h1>\n" +
	                "  <p>**Data on elasticsearch and database:**<br>\n" +
	                "  Check the relevant sections of your application logic to see data from Elasticsearch and the database. \n" +
	                "  This message doesn't directly display that data for security reasons.</p>\n" +
	                "</body>\n" +
	                "</html>";
	  }
}
