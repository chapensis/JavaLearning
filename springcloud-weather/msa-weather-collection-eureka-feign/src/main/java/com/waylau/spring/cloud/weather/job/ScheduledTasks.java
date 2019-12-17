package com.waylau.spring.cloud.weather.job;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.waylau.spring.cloud.weather.service.CityClient;
import com.waylau.spring.cloud.weather.service.WeatherDataCollectionService;
import com.waylau.spring.cloud.weather.vo.City;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling // 表示该类为定时监听的任务类
@Component
@Configuration
public class ScheduledTasks {
	@Autowired
	private WeatherDataCollectionService weatherDataCollectionService;

	@Autowired
	private CityClient cityClient;

	public void reportCurrentByCron() {
		log.info("Weather Data Sync Job. Start！");
		// 获取城市ID列表
		List<City> cityList = null;

		try {
			// 由城市数据API微服务提供数据
			cityList = cityClient.listCity();
		} catch (Exception e) {
			log.error("Exception!", e);
		}

		if (cityList == null || cityList.size() == 0) {
			log.info("Weather Data Sync Job. No city！");
			return;
		}

		// 遍历城市ID获取天气
		for (City city : cityList) {
			String cityId = city.getCityId();
			log.info("Weather Data Sync Job, cityId:" + cityId);

			weatherDataCollectionService.syncDateByCityId(cityId);
		}

		log.info("Weather Data Sync Job. End!");
	}
}