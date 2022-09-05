package com.zerobase.weather.service;

import com.zerobase.weather.domain.DateWeather;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface DateWeatherService {

    DateWeather getDateWeather(LocalDate date);
    void saveDateWeather();
}
