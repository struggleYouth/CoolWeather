package com.coolweather.app.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	
	/**
     * Province表*建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province(province_id integer primary key autoincrement,province_name text,province_code text)";
    /**
     * C表*建表语句
     */
    public static final String CREATE_CITY = "create table City(city_id integer primary key autoincrement,city_name text,city_code text,city_province_id integer)";
    /**
     * County表*建表语句
     */
    public static final String CREATE_COUNTY = "create table County(county_id integer primary key autoincrement,county_name text,county_code text,county_city_id integer)";

	
	
	public CoolWeatherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public CoolWeatherOpenHelper(Context context, String name, CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE); //创建Province表
		db.execSQL(CREATE_CITY);  //创建City表
		db.execSQL(CREATE_COUNTY);  //创建County表

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
