package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	/*
	 * 数据库名
	 */
    public static final String DB_NAME = "cool_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;
    private static CoolWeatherDB coolWeatherDB;
    private static SQLiteDatabase sqlData;
	
    /**
     * 将构造函数私有化
     */
    public CoolWeatherDB(Context context) {
        // TODO Auto-generated constructor stub

        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        sqlData = dbHelper.getWritableDatabase();
    }
    
    /**
     * 获取CoolWeatherDB实例
     */
    public synchronized static CoolWeatherDB getInstance(Context context){
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }
    
    /**
     * 将Province实例存储到数据库 
     */
    public void savaProvince(Province province){
    	if(province != null){
    		ContentValues valuesP= new ContentValues();
    		valuesP.put("province_name", province.getProvinceName());
    		valuesP.put("province_code", province.getProvinceCode());
    		sqlData.insert("Province", null, valuesP);
    	}
    }
    
    /**
     * 从数据库读取全国所有省份信息
     */
    public List<Province> getlistProvince(){
    	
    	List<Province> listProvince = new ArrayList<Province>();
    	
    	Cursor cursor = sqlData.query("Province", null, null, null, null, null, null);
    	
		if(cursor.moveToFirst()){
			do {
				Province province = new Province();
				province.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				listProvince.add(province);
			} while (cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
    	return listProvince;
    }
    
    /**
     * 将City实例存储到数据库 
     */
    public void saveCity(City city){
    	if(city != null){
    		ContentValues valuesC = new ContentValues();
    		valuesC.put("city_name", city.getCityName());
    		valuesC.put("city_code", city.getCityCode());
    		valuesC.put("city_pid", city.getCity_pId());
    		sqlData.insert("City", null, valuesC);
    	}
    }
    
    /**
     * 从数据库读取某个省下所有的城市信息
     */
    public List<City> getlistCityByPid(int provinceId){
    	
    	List<City> listCity = new ArrayList<City>();
    	
    	Cursor cursor = sqlData.query("City", null, "city_province_id", new String[] {String.valueOf(provinceId)}, null, null, null);
    	
		if(cursor.moveToFirst()){
			do {
				City city = new City();
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setCity_pId(cursor.getInt(cursor.getColumnIndex("city_province_id")));
				listCity.add(city);
			} while (cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
    	return listCity;
    }
    
    /**
     * 将County实例存储到数据库 
     */
    public void saveCounty(County county){
    	if(county != null){
    		ContentValues valuesCC = new ContentValues();
    		valuesCC.put("county_name", county.getCountyName());
    		valuesCC.put("county_code", county.getCountyCode());
    		valuesCC.put("countyy_cid", county.getCounty_cId());
    		sqlData.insert("County", null, valuesCC);
    	}
    }
    
    /**
     * 从数据库读取某城市下所有的县级信息
     */
    public List<County> getlistCountyByCid(int cityId){
    	
    	List<County> listCounty = new ArrayList<County>();
    	
    	Cursor cursor = sqlData.query("County", null, "county_city_id", new String[]{String.valueOf(cityId)}, null, null, null);
    	
		if(cursor.moveToFirst()){
			do {
				County county = new County();
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCounty_cId(cursor.getInt(cursor.getColumnIndex("county_city_id")));
				listCounty.add(county);
			} while (cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
    	return listCounty;
    }
}
