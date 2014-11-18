package com.lepin.entity;

public class Commodity {
	private int commodityId;// 编号
	private String title;// 标题
	private String pics;// 图片
	private String descrip;// 描述
	private Long price;// 价格
	private int state;
	private Category category;
	private Merchant merchant;

	public int getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(int commodityId) {
		this.commodityId = commodityId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPics() {
		return pics;
	}

	public void setPics(String pics) {
		this.pics = pics;
	}

	public String getDescrip() {
		return descrip;
	}

	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	@Override
	public String toString() {
		return "Commodity [commodityId=" + commodityId + ", title=" + title + ", pics=" + pics
				+ ", descrip=" + descrip + ", price=" + price + ", state=" + state + ", category="
				+ category + ", merchant=" + merchant + "]";
	}

}
