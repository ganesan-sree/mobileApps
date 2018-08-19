package com.vegfreshbox.ecommerce.pojo;

/**
 * Created by ganesh on 06/01/17.
 */

public class AddressPojo {
    String id;
    String name;
    String mobile;
    String email;
    String city;
    String state;
    String pincode;
    String flat;
    String colony;


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public void setColony(String colony) {
        this.colony = colony;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPincode() {
        return pincode;
    }

    public String getFlat() {
        return flat;
    }

    public String getColony() {
        return colony;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}


		@Override
		public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getFlat());
		sb.append(" ");
		sb.append(getColony());
		sb.append(" ");
		sb.append(getCity());
		sb.append(" ");
		sb.append(getPincode());
		sb.append(" ");
		sb.append(getMobile());
		
		
			return sb.toString();
		}
}
