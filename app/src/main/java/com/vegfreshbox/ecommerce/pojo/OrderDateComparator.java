package com.vegfreshbox.ecommerce.pojo;

import java.util.Comparator;

/**
 * Created by ganesh on 09/01/17.
 */

public class OrderDateComparator implements Comparator {


    public int compare(Object o1, Object o2) {
        OrderHistoryPojo s1 = (OrderHistoryPojo) o1;
        OrderHistoryPojo s2 = (OrderHistoryPojo) o2;

        return s2.getId().compareTo(s1.getId());
    }
}