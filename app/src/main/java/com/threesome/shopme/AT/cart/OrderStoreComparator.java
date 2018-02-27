package com.threesome.shopme.AT.cart;

import java.util.Comparator;
import com.threesome.shopme.AT.utility.Constant;

/**
 * Created by Nhan on 1/8/2018.
 */

public class OrderStoreComparator implements Comparator<OrderStore>{
    @Override
    public int compare(OrderStore orderStore, OrderStore t1) {
        if(orderStore.getStatus() == Constant.CODE_DA_HUY)
            return -1;
        else if(orderStore.getStatus() == Constant.CODE_DA_GIAO)
            return 0;
        else if(orderStore.getStatus() == Constant.CODE_SHIP)
            return 1;

        if(orderStore.isSeen() == true){
            return 1;
        }
        else
            return -1;
    }
}
