package com.weather.incube.weather.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pankaj on 15-12-2017.
 */

public class DataList implements Serializable {

    private List<Weather> weather;
    private Main main;
    private String dt_txt;

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }
}