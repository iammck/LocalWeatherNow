package com.mck.localweathernow.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Michael on 5/28/2016.
 */
public class Snow {
    public Double threeHour; // Snow volume for the last 3 hours

    public Snow() {
    }

    /**
     * Snow has a data parameter name that is illegal in java so need to change it.
     */
    public static class SnowDeserializer implements JsonDeserializer<Snow> {
        @Override
        public Snow deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Snow result = new Snow(); // the json should look like "3h"=10.3
            JsonElement vObj = json.getAsJsonObject().get("3h");
            if (vObj != null) {
                result.threeHour = json.getAsJsonObject().get("3h").getAsDouble();
            }

            return result;

        }
    }
}
