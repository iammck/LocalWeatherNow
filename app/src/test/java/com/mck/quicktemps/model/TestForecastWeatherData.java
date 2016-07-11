package com.mck.quicktemps.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test ForecastWeatherData for de/serialization to/from json.
 * Created by Michael on 5/15/2016.
 */
public class TestForecastWeatherData {
    String inputJson;
    @Before
    public void setUp() throws Exception {
        // result string from url
        // http://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&cnt=2&APPID=mykey
        inputJson = "{\"city\":{\"id\":1851632,\"name\":\"Shuzenji\"," +
                "\"coord\":{\"lon\":138.933334,\"lat\":34.966671},\"country\":\"JP\"," +
                "\"population\":0,\"sys\":{\"population\":0}}," +
                "\"cod\":\"200\",\"message\":0.0037,\"cnt\":2," +

                "\"list\":[{" +

                "\"dt\":1463335200,\"main\":{\"temp\":284.3," +
                "\"temp_min\":284.3,\"temp_max\":284.301,\"pressure\":935.35," +
                "\"sea_level\":1031.39,\"grnd_level\":935.35,\"humidity\":97,\"temp_kf\":0}," +
                "\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\"," +
                "\"icon\":\"10n\"}],\"clouds\":{\"all\":88}," +
                "\"wind\":{\"speed\":0.88,\"deg\":189.004},\"rain\":{\"3h\":0.12}," +
                "\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2016-05-15 18:00:00\"}," +

                "{\"dt\":1463346000,\"main\":{\"temp\":285.18," +
                "\"temp_min\":285.179,\"temp_max\":285.18,\"pressure\":934.42," +
                "\"sea_level\":1030.52,\"grnd_level\":934.42,\"humidity\":93,\"temp_kf\":0}," +
                "\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\"," +
                "\"icon\":\"10n\"}],\"clouds\":{\"all\":76}," +
                "\"wind\":{\"speed\":1.05,\"deg\":198.001},\"rain\":{\"3h\":0.105}," +
                "\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2016-05-15 21:00:00\"}" +

                "]}";
    }

    /**
     * Can ForecastWeatherData class instances be instantiated?
     */
    @Test
    public void testInstantiateForecastWeatherDataClass(){
        System.out.println("@Test - testInstantiateForecastWeatherDataClass");
        assertNotNull("Unable to instantiate ForecastWeatherData",new ForecastWeatherData());
    }

    /**
     * Can get instance from json string.
     */
    @Test
    public void testGetInstanceFromJSonString(){
        Gson gson = new Gson();
        ForecastWeatherData result = gson.fromJson(inputJson, ForecastWeatherData.class);
        assertNotNull("Was unable to instantiate ForecastWeatherData from json", result);
    }



    /**
     * Has Expected fields
     */
    @Test
    public void testHasExpectedFields(){
        GsonBuilder builder = new GsonBuilder();
        // need to register rain and snow classes
        builder.registerTypeAdapter(Rain.class, new Rain.RainDeserializer());
        builder.registerTypeAdapter(Snow.class, new Snow.SnowDeserializer());
        Gson gson = builder.create(); // new Gson();
        ForecastWeatherData result = gson.fromJson(inputJson, ForecastWeatherData.class);
        // check city
        City city = result.city;
        assertTrue("Expecting city coord lon value", city.coord.lon.equals(138.933334));
        assertTrue("Expecting city coord lat value", city.coord.lat.equals(34.966671));
        assertTrue("Expecting city name value", city.name.equals("Shuzenji"));
        assertTrue("Expecting city country value", city.country.equals("JP"));

        // check cod, message and count
        assertTrue("Expecting cod value" , result.cod.equals("200"));
        // even though inc is a number should be a string because of error code msgs
        assertTrue("Expecting message value" , result.message.equals("0.0037"));
        assertTrue("Expecting cnt value" , result.cnt.equals(2));

        // check each of the two periods in the list
        Period period = result.list[0];
        assertTrue("Expecting dt value but was " + period.dt ,
                period.dt.equals(1463335200L));
        // check weather values
        Weather pWeather =  period.weather[0];
        assertTrue("Expecting weather id value", pWeather.id.equals(500));
        assertTrue("Expecting weather main value", pWeather.main.equals("Rain"));
        assertTrue("Expecting weather description value", pWeather.description.equals("light rain"));
        assertTrue("Expecting weather icon value", pWeather.icon.equals("10n"));
        // check main values
        Main pMain = period.main;
        assertTrue("Expecting temp value", pMain.temp.equals(284.3));
        assertTrue("Expecting pressure value", pMain.pressure.equals(935.35));
        assertTrue("Expecting humidity value", pMain.humidity.equals(97d));
        assertTrue("Expecting temp min value", pMain.temp_min.equals(284.3));
        assertTrue("Expecting temp max value", pMain.temp_max.equals(284.301));
        assertTrue("Expecting sea level value", pMain.sea_level.equals(1031.39));
        assertTrue("Expecting ground level value", pMain.grnd_level.equals(935.35));
        //rain, snow, wind and clouds
        assertTrue("Expecting rain value", period.rain.threeHour.equals(0.12));
        assertTrue("Expecting null snow value" , period.snow == null);
        assertTrue("Expecting wind speed value" , period.wind.speed.equals(0.88));
        assertTrue("Expecting wind deg wind value" , period.wind.deg.equals(189.004));
        assertTrue("Expecting clouds all value" , period.clouds.all.equals(88d));

        period = result.list[1];
        assertTrue("Expecting dt value" , period.dt.equals(1463346000L));
        // check weather values
        pWeather =  period.weather[0];
        assertTrue("Expecting weather id value", pWeather.id.equals(500));
        assertTrue("Expecting weather main value", pWeather.main.equals("Rain"));
        assertTrue("Expecting weather description value", pWeather.description.equals("light rain"));
        assertTrue("Expecting weather icon value", pWeather.icon.equals("10n"));
        // check main values
        pMain = period.main;
        assertTrue("Expecting temp value", pMain.temp.equals(285.18));
        assertTrue("Expecting pressure value", pMain.pressure.equals(934.42));
        assertTrue("Expecting humidity value", pMain.humidity.equals(93d));
        assertTrue("Expecting temp min value", pMain.temp_min.equals(285.179));
        assertTrue("Expecting temp max value", pMain.temp_max.equals(285.18));
        assertTrue("Expecting sea level value", pMain.sea_level.equals(1030.52));
        assertTrue("Expecting ground level value", pMain.grnd_level.equals(934.42));
        //rain, snow, wind and clouds
        assertTrue("Expecting rain value", period.rain.threeHour.equals(0.105));
        assertTrue("Expecting null snow value" , period.snow == null);
        assertTrue("Expecting wind speed value" , period.wind.speed.equals(1.05));
        assertTrue("Expecting wind deg wind value" , period.wind.deg.equals(198.001));
        assertTrue("Expecting clouds all value" , period.clouds.all.equals(76d));
    }

    /**
     * Can instantiate from error code.
     */
    @Test
    public void canInstantiateFromErrorJson() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create(); // new Gson();
        String inputJson = "{\"cod\":401, \"message\": \"Invalid API key. " +
                "Please see http://openweathermap.org/faq#error401 for more info.\"}";
        String expecting = "Invalid API key. Please see http://openweathermap.org/faq#error401 " +
                "for more info.";
        ForecastWeatherData result = gson.fromJson(inputJson, ForecastWeatherData.class);
        assertTrue("Expecting message value", result.message.equals(expecting));
    }
}