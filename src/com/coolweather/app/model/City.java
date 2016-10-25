package com.coolweather.app.model;

public class City {
	
	private int cityId;
	private int city_pId;
	private String cityName;
	private String cityCode;
	
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public int getCity_pId() {
		return city_pId;
	}
	public void setCity_pId(int city_pId) {
		this.city_pId = city_pId;
	}
	
}
