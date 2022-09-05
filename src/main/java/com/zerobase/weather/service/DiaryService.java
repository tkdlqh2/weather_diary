package com.zerobase.weather.service;

import com.zerobase.weather.domain.Diary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public interface DiaryService {

    void createDiary(LocalDate date, String text);

    List<Diary> readDiary(LocalDate date);

    List<Diary> readDiaries(LocalDate startDate, LocalDate endDate);

    void updateDiary(LocalDate date, String text);

    void deleteDiary(LocalDate date);
}
