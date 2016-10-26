package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Utility {

	public Utility() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * �����ʹ�����������ص�ʡ������
	 */
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
		
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length>0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\/");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//���������������ݴ洢��Province��
					coolWeatherDB.savaProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����ʹ�������������м�����
	 */
	public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		
		if (!TextUtils.isEmpty(response)) {
			String[] allCitys = response.split(",");
			if (allCitys != null && allCitys.length>0) {
				for (String p : allCitys) {
					String[] array = p.split("\\/");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setCity_pId(provinceId);
					//���������������ݴ洢��City��
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * �����ʹ�����������ص��ؼ�����
	 */
	public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
		
		if (!TextUtils.isEmpty(response)) {
			String[] allCountys = response.split(",");
			if (allCountys != null && allCountys.length>0) {
				for (String p : allCountys) {
					String[] array = p.split("\\/");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCounty_cId(cityId);
					//���������������ݴ洢��Province��
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * �������������ص�JSON���ݣ����������������ݴ洢������
	 */
	public static void handleWeatherResponse(Context context,String response){
		
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * �����������ص�������Ϣ�浽SharedPreferences�ļ�
	 */
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,
			String temp1,String temp2,String weatherDesp,String publishTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp",weatherDesp);
		editor.putString("publish_time",publishTime);
		editor.putString("current_date",sdf.format(new Date()));
		editor.commit();
	}
}
