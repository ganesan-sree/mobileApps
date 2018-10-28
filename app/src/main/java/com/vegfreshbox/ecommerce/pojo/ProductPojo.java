package com.vegfreshbox.ecommerce.pojo;

/**
 * Created by ganesh on 02/01/17.
 */

public class ProductPojo {
    private String id;
    private String name;
    private String image;
    private String imagelocal;
    private String price;
    private String wgt;
    private String isStockAvailable;

    private String quantity;

    public String getIsStockAvailable() {
        return isStockAvailable;
    }

    public void setIsStockAvailable(String isStockAvailable) {
        this.isStockAvailable = isStockAvailable;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

   

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }


	public String getImagelocal() {
		return imagelocal;
	}

	public void setImagelocal(String imagelocal) {
		this.imagelocal = imagelocal;
	}

	public String getWgt() {
		return wgt;
	}

	public void setWgt(String wgt) {
		this.wgt = wgt;
	}
    
	
    
}
