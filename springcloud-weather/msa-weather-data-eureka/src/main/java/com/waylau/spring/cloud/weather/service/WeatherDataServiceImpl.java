package com.waylau.spring.cloud.weather.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waylau.spring.cloud.weather.vo.WeatherResponse;

/**
 * WeatherDataService 实现.
 * 
 * @since 1.0.0 2017年11月22日
 * @author <a href="https://waylau.com">Way Lau</a> 
 */
@Service
public class WeatherDataServiceImpl implements WeatherDataService {
	private final static Logger logger = LoggerFactory.getLogger(WeatherDataServiceImpl.class);  
	
	private static final String WEATHER_URI = "http://wthrcdn.etouch.cn/weather_mini?spm=5176.11156381.0.0.52f650b3tAwzdX";
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	public WeatherResponse getDataByCityId(String cityId) {
		String uri = WEATHER_URI + "&citykey=" + cityId;
		return this.doGetWeahter(uri);
	}

	@Override
	public WeatherResponse getDataByCityName(String cityName) {
		String uri = WEATHER_URI + "&city=" + cityName;
		return this.doGetWeahter(uri);
	}
	
	private WeatherResponse doGetWeahter(String uri) {
		String key = uri;
		String strBody = null;
		ObjectMapper mapper = new ObjectMapper();
		WeatherResponse resp = null;
		ValueOperations<String, String>  ops = stringRedisTemplate.opsForValue();
		// 先查缓存，缓存有的取缓存中的数据
		if (stringRedisTemplate.hasKey(key)) {
			logger.info("Redis has data");
			strBody = ops.get(key);
		} else {
			logger.info("Redis don't has data");
			// 缓存没有，直接抛出异常
			throw new RuntimeException("Don't has data!");
		}

		try {
			resp = mapper.readValue(strBody, WeatherResponse.class);
		} catch (IOException e) {
			//e.printStackTrace();
			logger.error("Error!",e);
		}
		
		return resp;
	}

}
