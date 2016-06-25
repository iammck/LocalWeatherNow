package com.mck.localweathernow.service;

import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Integration test to assert the OpenWeatherMapService does use the
 * network to retrieve an expected result.
 *
 * Created by Michael on 5/15/2016.
 */
public class TestOpenWeatherMapService {

    @Test
    public void testRequestCurrentWeather() throws Exception {
        CurrentWeatherData result = OpenWeatherMapService.instance()
                .requestCurrentWeather(47.6067,-122.3321);
        assertTrue("result is null!", result != null);
        assertTrue("result code is not 200",  result.cod == 200);
        // check erroneous values
        result = OpenWeatherMapService.instance().requestCurrentWeather(547.6067,-122.3321);
        assertTrue("result is null!", result != null);
        result = OpenWeatherMapService.instance().requestCurrentWeather(null,null);
        assertTrue("result is null!", result == null);
    }

    @Test
    public void testRequestForecastWeather() throws Exception {
        ForecastWeatherData result = OpenWeatherMapService.instance()
                .requestForecastWeather(47.6067, -122.3321, 3);
        assertTrue("result is null!", result != null);
        assertTrue("result code is not 200",  result.cod.equals("200"));
        // check erroneous values
        result = OpenWeatherMapService.instance().requestForecastWeather(547.6067, -4422.3321, 3);
        assertTrue("result is null!", result != null);
        assertTrue("result code is not 404",  result.cod.equals("404"));
    }

}