package com.zerobase.weather.service;

import com.zerobase.weather.WeatherApplication;
import com.zerobase.weather.domain.DateWeather;
import com.zerobase.weather.domain.Diary;
import com.zerobase.weather.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DiaryServiceImpl implements DiaryService{

    @Value("${openweathermap.key}")
    private String apiKey;
    @Value("${openweathermap.city}")
    private String city;

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DateWeatherService dateWeatherService;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create Diary");
        DateWeather dateWeather = dateWeatherService.getDateWeather(date);
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
        logger.info("finished to create Diary");
    }

    @Transactional(readOnly = true)
    @Override
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Override
    public void updateDiary(LocalDate date, String text) {
        Optional<Diary> optionalDiary = diaryRepository.getFirstByDate(date);
        if(optionalDiary.isPresent()){
            Diary curDiary = optionalDiary.get();
            curDiary.setText(text);
            diaryRepository.save(curDiary);
        }
    }

    @Override
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }


}
