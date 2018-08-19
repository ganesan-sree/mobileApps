package com.vegfreshbox.ecommerce.pojo;

/**
 * Created by ganesh on 01/01/17.
 */

public class MasterCategory {

    private  String id;
    private String name;
    private String image;
    private String imageLocal;
    
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

	public String getImageLocal() {
		return imageLocal;
	}

	public void setImageLocal(String imageLocal) {
		this.imageLocal = imageLocal;
	}
    
    
}
