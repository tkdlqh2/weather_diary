package com.zerobase.weather.service;

import com.zerobase.weather.domain.DateWeather;
import com.zerobase.weather.repository.DateWeatherRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DateWeatherServiceImpl implements DateWeatherService{

    @Value("${openweathermap.key}")
    private String apiKey;
    @Value("${openweathermap.city}")
    private String city;

    @Autowired
    DateWeatherRepository dateWeatherRepository;

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    @Override
    public void saveDateWeather() {
        dateWeatherRepository.save(getWeatherFromApi());
    }

    @Override
    public DateWeather getDateWeather(LocalDate date) {
        Optional<DateWeather> optionalDateWeather= dateWeatherRepository.findByDate(date);
        if(optionalDateWeather.isPresent()){
            return optionalDateWeather.get();
        }else{
            return getWeatherFromApi();
        }
    }

    private DateWeather getWeatherFromApi(){
        Map<String,Object> parsedWeather = getParsedWeather();
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double)parsedWeather.get("temp"));
        return dateWeather;
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;
        try{
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if(responseCode == 200){
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while((inputLine = br.readLine()) != null){
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        }catch (Exception e){
            return "failed to get response";
        }

    }

    private Map<String,Object> parseWeather(String jsonString){
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try{
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        }catch (ParseException e){
            throw new RuntimeException(e);
        }

        Map<String,Object> resultMap = new HashMap<>();
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp",mainData.get("temp"));
        JSONObject weatherData = (JSONObject) ((JSONArray) jsonObject.get("weather")).get(0);
        resultMap.put("main",weatherData.get("main"));
        resultMap.put("icon",weatherData.get("icon"));

        return resultMap;
    }


    private Map<String, Object> getParsedWeather() {
        return parseWeather(getWeatherString());
    }


}
