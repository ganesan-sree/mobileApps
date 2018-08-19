package net.thegreshams.firebase4j.demo;

public class Product {
	
	public Product(String productId,String productName,String productPrice,String wgt,String productQuantity){
		this.productId=productId;
		this.productName=productName;
		this.productQuantity=productQuantity;
		this.productPrice=productPrice;
		this.wgt=wgt;
		
	}
	
	private String productId;
	private String productName;
	private String productPrice;
	private String productQuantity;
	private String subTotal;
	private String productImage;
	private String wgt;
	private String imagelocal;
	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}
	public String getProductQuantity() {
		return productQuantity;
	}
	public void setProductQuantity(String productQuantity) {
		this.productQuantity = productQuantity;
	}
	public String getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}
	
	
	
	public String getProductImage() {
		return productImage;
	}
	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}
	public String getImagelocal() {
		return imagelocal;
	}
	public void setImagelocal(String imagelocal) {
		this.imagelocal = imagelocal;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProductId=");
		sb.append(productId);
		sb.append("\n");
		sb.append("ProductName=");
		sb.append(productName);
		sb.append("\n");
		sb.append("ProductPrice=");
		sb.append(productPrice);
		sb.append("\n");
		sb.append("ProductQty=");
		sb.append(productQuantity);
		sb.append("\n");
		
		
		return sb.toString();
	}
	public String getWgt() {
		return wgt;
	}
	public void setWgt(String wgt) {
		this.wgt = wgt;
	}
	
	
	
	
	
	
	

}
