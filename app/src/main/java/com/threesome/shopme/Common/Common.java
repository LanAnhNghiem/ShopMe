package com.threesome.shopme.Common;

import com.threesome.shopme.Retrofit.IGoogleAPI;
import com.threesome.shopme.Retrofit.RetrofitClient;

/**
 * Created by Nhan on 11/25/2017.
 */

public class Common {

    public static final String driver_tb1 = "Drivers";
    public static final String user_driver_tb1 = "DriversInformation";
    public static final String user_rider_tb1 = "RidersInformation";
    public static final String pickup_request_tb1 = "PickupRequest";

    public static final String baseURL = "https://maps.googleapis.com";

    public static IGoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}
