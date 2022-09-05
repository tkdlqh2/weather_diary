package com.zerobase.weather.repository;

import com.zerobase.weather.domain.DateWeather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate> {
    Optional<DateWeather> findByDate(LocalDate date);
}
