package com.mck.quicktemps.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Michael on 5/28/2016.
 */
public class Rain {
    public Double threeHour; // Rain volume for the last 3 hours

    public Rain() {
    }


    /**
     * Rain has a data parameter name that is illegal in java so need to change it.
     */
    public static class RainDeserializer implements JsonDeserializer<Rain> {
        @Override
        public Rain deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Rain result = new Rain(); // the json should look like "3h"=10.3
            JsonElement vObj = json.getAsJsonObject().get("3h");
            if (vObj != null) {
                result.threeHour = json.getAsJsonObject().get("3h").getAsDouble();
            }

            return result;

        }
    }

}
