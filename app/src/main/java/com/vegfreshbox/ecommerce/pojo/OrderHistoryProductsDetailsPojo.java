package com.vegfreshbox.ecommerce.pojo;

import java.util.ArrayList;

/**
 * Created by ganesh on 27/01/17.
 */

public class OrderHistoryProductsDetailsPojo {

    String id;
    String orderDate;
    String deliveryAddress;
    String phone;
    String status;
    String total;
    
    ArrayList <ProductPojo> products= new ArrayList<ProductPojo>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public ArrayList<ProductPojo> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<ProductPojo> products) {
		this.products = products;
	}


    
    
    
    
    


   
}
