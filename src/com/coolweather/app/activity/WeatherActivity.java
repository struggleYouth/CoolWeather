package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {
	
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;   //��ʾ��������
	private TextView publishText;   //��ʾ����ʱ��
	private TextView weatherDespText;   //��ʾ����������Ϣ
	private TextView temp1Text;   //��ʾ�������1
	private TextView temp2Text;   //��ʾ�������2
	private TextView currentDateText;  //��ʾ��ǰ����
	
	private Button switchCity;   //�л����а�ť
	private Button refreshWeather;   //����������ť

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		//��ʼ���ؼ�
		weatherInfoLayout = (LinearLayout) findViewById(R.id.id_weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.id_cityName);
		publishText = (TextView) findViewById(R.id.id_publishText);
		weatherDespText = (TextView) findViewById(R.id.id_weather_desp);
		temp1Text = (TextView) findViewById(R.id.id_temp1);
		temp1Text = (TextView) findViewById(R.id.id_temp2);
		currentDateText = (TextView) findViewById(R.id.id_current_date);
		
		String countyCode = getIntent().getStringExtra("county_code");
		
		if(!TextUtils.isEmpty(countyCode)){
			//���ؼ�����ʱ��ȥ������
			publishText.setText("ͬ����...���Ժ�");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);   //��ѯ�ؼ����Ŷ�Ӧ����������
		}else{
			//û���ؼ�����ʱ��ֱ����ʾ��������
			showWeather();
		}
		
		switchCity = (Button) findViewById(R.id.id_switch_city);
		refreshWeather = (Button) findViewById(R.id.id_refresh_weather);
		switchCity.setOnClickListener((OnClickListener) this);
		refreshWeather.setOnClickListener((OnClickListener) this);
	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.id_switch_city:
			Intent intent = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.id_refresh_weather:
			publishText.setText("ͬ���У����Ե�...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	/**
	 * ��ѯ�ؼ����Ŷ�Ӧ����������
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address,"countyCode");
	}
	
	/**
	 * ��ѯ������������Ӧ������
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city"+weatherCode+".xml";
		queryFromServer(address,"countyCode");
	}
	
	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������ź�������Ϣ
	 * @param address
	 * @param type
	 */
	private void queryFromServer(final String address,final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				// TODO Auto-generated method stub
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						//�ӷ��������ص������н�����������
						String[] array = response.split("\\|");
						if(array != null && array.length ==2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					//������������ص�������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("ͬ��ʧ��...");
					}
				});
			}
		});
		
	}
	
	/*
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("����"+prefs.getString("publish_time", "")+"����");
		currentDateText.setText(prefs.getString("current_date", ""));
		
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
		//����AutoUpdateService����
		Intent intent = new Intent(WeatherActivity.this,AutoUpdateService.class);
		startActivity(intent);
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
