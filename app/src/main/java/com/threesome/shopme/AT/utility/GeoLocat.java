package com.threesome.shopme.AT.utility;

import com.firebase.geofire.GeoLocation;

/**
 * Created by Kunka on 12/20/2017.
 */

public class GeoLocat {
    private String key;
    private GeoLocation location;

    public GeoLocat(String key, GeoLocation location) {
        this.key = key;
        this.location = location;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }
}
