package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private CoolWeatherDB coolWeatherDB;
	private List<Province> listProvince;   //ʡ�б�
	private List<City> listCity;   //���б�
	private List<County> listCounty;   //���б�
	
	private Province selectedProvince;   //ѡ�е�ʡ��
	private City selectedCity;   //ѡ�еĳ���
	private int currentLevel;   //��ǰѡ�еļ���
	
	private TextView textView;
	private ListView listView;
	
	private List<String> dataList = new ArrayList<String>();
	private ArrayAdapter<String> adapter; 
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false)){
			Intent inter = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
			startActivity(inter);
			finish();
			return;
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_area);
		
		textView = (TextView) findViewById(R.id.id_title_text);
		listView = (ListView) findViewById(R.id.id_list_view);
		
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = listProvince.get(position);
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = listCity.get(position);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){
					String countyCode = listCounty.get(position).getCountyCode();
					Intent inter = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					inter.putExtra("county_code", countyCode);
					startActivity(inter);
					finish();
				}
			}
		}) ;
		queryProvinces();//����ʡ������
	}
	
	/**
	 * ��ѯȫ������ʡ�����ȴ����ݿ��ѯ��û�о���ȥ�������ϲ�ѯ
	 */
	public void queryProvinces(){
		listProvince = coolWeatherDB.getlistProvince();
		if(listProvince.size() > 0){
			dataList.clear();
			for(Province province : listProvince){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}	
	}

	/**
	 * ��ѯѡ��ʡ�������У����ȴ����ݿ��ѯ��û�о���ȥ�������ϲ�ѯ
	 */
	public void queryCities(){
		listCity = coolWeatherDB.getlistCityByPid(selectedProvince.getProvinceId());
		if(listCity.size() > 0){
			dataList.clear();
			for(City city : listCity){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}	
	}
	/**
	 * ��ѯѡ���е������أ����ȴ����ݿ��ѯ��û�о���ȥ�������ϲ�ѯ
	 */
	protected void queryCounties() {
		// TODO Auto-generated method stub
		listCounty = coolWeatherDB.getlistCountyByCid(selectedCity.getCityId());
		if(listCounty.size() > 0){
			dataList.clear();
			for(County county : listCounty){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}	
	}
	
	/**
	 * ���ݴ��˵Ĵ��ź����ʹӷ�������ѯʡ��������
	 * @param code
	 * @param type
	 */
	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		
		showProgressDialog();
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result =false;
				if("province".equals(type)){
					result = Utility.handleProvinceResponse(coolWeatherDB, response);
				}else if("city".equals(type)){
					result = Utility.handleCityResponse(coolWeatherDB, response, selectedProvince.getProvinceId());
				}else if("county".equals(type)){
					result = Utility.handleCountyResponse(coolWeatherDB, response, selectedCity.getCityId());
				}
				if(result){
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// ͨ��runOnUiThread()�����ص����̴߳����߼�
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {				
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * ��ʾ���ȶԻ���
	 */
	
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б���ʡ�б����ֱ���˳�
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_area, menu);
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
