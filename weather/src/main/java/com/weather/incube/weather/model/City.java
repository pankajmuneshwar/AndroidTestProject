package com.weather.incube.weather.model;

import java.io.Serializable;

/**
 * Created by pankaj on 15-12-2017.
 */

public class City implements Serializable {
    private long id;
    private String name;
    private String country;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
