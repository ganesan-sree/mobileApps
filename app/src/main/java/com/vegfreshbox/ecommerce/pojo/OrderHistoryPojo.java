package com.vegfreshbox.ecommerce.pojo;

/**
 * Created by ganesh on 09/01/17.
 */

public class OrderHistoryPojo {

    String id;
    String orderdate;
    String orderamount;
    String orderstatus;

    public void setId(String id) {
        this.id = id;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public void setOrderamount(String orderamount) {
        this.orderamount = orderamount;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getId() {
        return id;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public String getOrderamount() {
        return orderamount;
    }

    public String getOrderstatus() {
        return orderstatus;
    }
}
