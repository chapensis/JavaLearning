package com.waylau.spring.cloud.weather.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hi")
public class HelloController {
	
	@GetMapping("/hello")
	public String getHello() {
		return "hello yangchang";
	}
}
