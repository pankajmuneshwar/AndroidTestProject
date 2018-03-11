package com.weather.incube.weather.Interface;

import com.weather.incube.weather.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("/data/2.5/forecast")
    Call<WeatherResponse> getWeatherByLocation(@Query("lat") double lat,
                                               @Query("lon") double lon,
                                               @Query("appid") String appid,
                                               @Query("units") String units);

    @GET("/data/2.5/forecast")
    Call<WeatherResponse> getWeatherByCity(@Query("id") long id,
                                               @Query("appid") String appid,
                                               @Query("units") String units);
}
