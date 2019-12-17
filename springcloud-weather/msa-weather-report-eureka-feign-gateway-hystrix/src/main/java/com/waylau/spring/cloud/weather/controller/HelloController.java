package com.waylau.spring.cloud.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waylau.spring.cloud.weather.service.DataClient;
/**
 * Hello Controller.
 * 
 * @since 1.0.0 2017年11月20日
 * @author <a href="https://waylau.com">Way Lau</a> 
 */
@RestController
public class HelloController {
	
	@Autowired
	private DataClient dataClient;
	
	//@RequestMapping("/hello")
	@GetMapping("/hello")
	public String hello() {
		return dataClient.getDataByCityId("101280104").getData().toString();
	}
}
