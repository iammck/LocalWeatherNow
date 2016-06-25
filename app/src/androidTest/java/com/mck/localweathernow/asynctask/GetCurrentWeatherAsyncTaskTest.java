package com.mck.localweathernow.asynctask;

import com.mck.localweathernow.model.CurrentWeatherData;

import org.junit.Assert;
import org.junit.Test;

/**
 * checks to see if the asyncTask returns the expected result.
 * Uses MockOpenWeatherMapService to be assured the correct result from
 * service call.
 *
 * Created by Michael on 5/16/2016.
 */
public class GetCurrentWeatherAsyncTaskTest {

    private class Callback implements GetCurrentWeatherAsyncTask.Callback {
        private CurrentWeatherData result;
        @Override
        public void onCurrentWeatherResult(CurrentWeatherData data) {
            result = data;
        }
    }

    @Test
    public void doesReturnExpectedResult() throws Exception {
        // set up the MockOpenWeatherMapService
        MockOpenWeatherMapService mockService = new MockOpenWeatherMapService();
        CurrentWeatherData expectedResult = new CurrentWeatherData();
        mockService.setCurrentResult(expectedResult);
        MockOpenWeatherMapService.setup(mockService);
        // handle this as the Callback
        Callback callback = new Callback();
        // need lat and long, any will do
        Double lat = 42.0;
        Double lon = -122.6;
        // create and execute
        GetCurrentWeatherAsyncTask task = new GetCurrentWeatherAsyncTask(callback, lat, lon);
        task.execute();
        // wait
        Thread.sleep(500);
        // assert it happened
        Assert.assertEquals("The result does not equal the expected. ",
                expectedResult, callback.result);
        MockOpenWeatherMapService.tearDown();
    }

}