package com.weather.incube.weather.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pankaj on 15-12-2017.
 */

public class WeatherResponse implements Serializable {

    private String cod;
    private List<DataList> list;
    private City city;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public List<DataList> getList() {
        return list;
    }

    public void setList(List<DataList> list) {
        this.list = list;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
