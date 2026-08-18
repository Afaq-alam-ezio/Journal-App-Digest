package net.engineeringdigest.journalApp.POJOs;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@NoArgsConstructor
public class WeatherData{

    public Coord coord;
    public List<Weather> weather;
    public String base;
    public Main main;
    public int visibility;
    public Wind wind;
    public Clouds clouds;
    public int dt;
    public Sys sys;
    public int timezone;
    public int id;
    public String name;
    public int cod;

    @Data
    @NoArgsConstructor
    public static class Clouds{
        public int all;
    }

    @Data
    @NoArgsConstructor
    public static class Coord{
        public double lon;
        public double lat;
    }

    @Data
    @NoArgsConstructor
    public static class Main{
        public double temp;
        public double feels_like;
        public double temp_min;
        public double temp_max;
        public int pressure;
        public int humidity;
        public int sea_level;
        public int grnd_level;
    }


    @Data
    @NoArgsConstructor
    public static class Sys{
        public int type;
        public int id;
        public String country;
        public int sunrise;
        public int sunset;
    }

    @Data
    @NoArgsConstructor
    public static class Weather{
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    @Data
    @NoArgsConstructor
    public static class Wind{
        public double speed;
        public int deg;
    }
}

