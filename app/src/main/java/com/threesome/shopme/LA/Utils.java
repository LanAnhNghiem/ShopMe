package com.threesome.shopme.LA;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by LanAnh on 22/12/2017.
 */

public class Utils {
    public static Gson gson;

    public static Gson getGsonParser() {
        if(null == gson) {
            GsonBuilder builder = new GsonBuilder();
            gson = builder.create();
        }
        return gson;
    }
}
