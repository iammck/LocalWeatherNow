package com.mck.localweathernow.service;


import android.app.Application;
import android.graphics.Bitmap;
import android.test.ApplicationTestCase;

import org.junit.Test;

/**
 * Created by Michael on 5/15/2016.
 */
public class TestOpenWeatherMapServiceWithInstrumentation extends ApplicationTestCase<Application> {
    public TestOpenWeatherMapServiceWithInstrumentation() {
        super(Application.class);
    }

    /**
     * Integration test to retrieve a bitmap icon from OpenWeatherMap.
     * Requires the use of BitmapFactory so must be an instrumentationTest
     * @throws Exception
     */
    @Test
    public void testRequestWeatherIcon() throws Exception {
        Bitmap result = OpenWeatherMapService.instance().requestWeatherIcon(getContext(), "10d");
        assertTrue("Resulting bit map was null.", result != null);
    }

}