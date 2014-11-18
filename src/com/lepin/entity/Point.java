package com.lepin.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Point implements Parcelable {

	public Point() {
		super();
	}

	public Point(String name) {
		super();
		this.name = name;
	}

	public int getInfoId() {
		return infoId;
	}

	public long getLat() {
		return lat;
	}

	public long getLon() {
		return lon;
	}

	public void setLat(long lat) {
		this.lat = lat;
	}

	public void setLon(long lon) {
		this.lon = lon;
	}

	public int getCityId() {
		return cityId;
	}

	public int getPointId() {
		return pointId;
	}

	public String getName() {
		return name;
	}

	public void setPointId(int pointId) {
		this.pointId = pointId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	private int pointId;// 途径点编号
	private int infoId;// 对应的信息编号
	private int cityId;// 城市编号
	private String name;// 途径点名称

	private long lat;// 纬度
	private long lon;// 经度

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(pointId);
		dest.writeInt(infoId);
		dest.writeInt(cityId);
		dest.writeString(name);
		dest.writeLong(lat);
		dest.writeLong(lon);

	}

	public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {

		@Override
		public Point createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			Point point = new Point();
			point.setPointId(source.readInt());
			point.setInfoId(source.readInt());
			point.setCityId(source.readInt());
			point.setName(source.readString());
			point.setLat(source.readLong());
			point.setLon(source.readLong());
			return point;
		}

		@Override
		public Point[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Point[size];
		}
	};

	@Override
	public String toString() {
		return "Point [pointId=" + pointId + ", infoId=" + infoId + ", cityId=" + cityId
				+ ", name=" + name + ", lat=" + lat + ", lon=" + lon + "]";
	}
	
}
