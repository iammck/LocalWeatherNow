package com.mck.localweathernow.asynctask;


import com.mck.localweathernow.model.ForecastWeatherData;

import org.junit.Assert;
import org.junit.Test;

/**
 * checks to see if the asyncTask returns the expected result.
 * Uses MockOpenWeatherMapService to be assured the correct result from
 * service call.
 *
 * Created by Michael on 5/28/2016.
 */
public class GetForecastWeatherAsyncTaskTest {

    public class Callback implements GetForecastWeatherAsyncTask.Callback {
        private ForecastWeatherData result;
        @Override
        public void onForecastWeatherResult(ForecastWeatherData data) {
            result = data;
        }
    }

    @Test
    public void doesReturnExpectedResult() throws Exception {
        // set up the MockOpenWeatherMapService
        MockOpenWeatherMapService mockService = new MockOpenWeatherMapService();
        ForecastWeatherData expectedResult = new ForecastWeatherData();
        mockService.setForecastResult(expectedResult);
        MockOpenWeatherMapService.setup(mockService);
        // handle this as the Callback
        Callback callback = new Callback();
        // need lat and long, any will do
        Double lat = 42.0;
        Double lon = -122.6;
        Integer periods = 3;
        // create and execute
        GetForecastWeatherAsyncTask task = new GetForecastWeatherAsyncTask(callback, lat, lon, periods);
        task.execute();
        // wait
        Thread.sleep(500);
        // assert it happened
        Assert.assertEquals("The result does not equal the expected. ",
                expectedResult, callback.result);
        MockOpenWeatherMapService.tearDown();
    }

}