package com.weather.incube.weather.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weather.incube.weather.Interface.ApiClient;
import com.weather.incube.weather.Interface.ApiInterface;
import com.weather.incube.weather.R;
import com.weather.incube.weather.adapter.CityAdapter;
import com.weather.incube.weather.model.City;
import com.weather.incube.weather.model.DataList;
import com.weather.incube.weather.model.WeatherResponse;
import com.weather.incube.weather.service.GPSTracker;
import com.weather.incube.weather.util.Constants;
import com.weather.incube.weather.util.FontUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity implements CityAdapter.CityAdapterListener {

    private static final String TAG = "WeatherActivity";
    private GPSTracker gps;
    private Context context;
    private WeatherResponse weatherResponse;
    private double latitude, longitude;
    private RecyclerView cityList;
    private CityAdapter adapter;
    private SearchView searchCity;
    private LinearLayout bottomLayout;
    private DrawerLayout drawer, drawer_layout;
    private ImageView imageDay1, imageDay2, imageDay3, imageDay4;
    private TextView current_temperature_field, dayText1, dayText2,
                dayText3, dayText4, tempDay1, weatherText,
                tempDay2, tempDay3, tempDay4, cityAndCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        context = this;
        initialize();
    }
	
	// Test @ Method 
	public void test2(){
	
	}

    public void initialize(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        current_temperature_field = findViewById(R.id.current_temperature_field);
        dayText1 = findViewById(R.id.dayText1);
        dayText2 = findViewById(R.id.dayText2);
        dayText3 = findViewById(R.id.dayText3);
        dayText4 = findViewById(R.id.dayText4);
        tempDay1 = findViewById(R.id.tempDay1);
        tempDay2 = findViewById(R.id.tempDay2);
        tempDay3 = findViewById(R.id.tempDay3);
        tempDay4 = findViewById(R.id.tempDay4);
        imageDay1 = findViewById(R.id.imageDay1);
        imageDay2 = findViewById(R.id.imageDay2);
        imageDay3 = findViewById(R.id.imageDay3);
        imageDay4 = findViewById(R.id.imageDay4);
        weatherText = findViewById(R.id.weatherText);
        cityAndCountry = findViewById(R.id.cityAndCountry);
        cityList = findViewById(R.id.cityList);
        drawer_layout = findViewById(R.id.drawer_layout);
        bottomLayout = findViewById(R.id.bottomLayout);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        cityList.setLayoutManager(mLayoutManager);
        cityList.setItemAnimator(new DefaultItemAnimator());

        String cityJson = loadJSONFromAsset();
        Type collectionType = new TypeToken<List<City>>() {}.getType();
        List<City> allCityList = new Gson().fromJson(cityJson, collectionType);

        adapter = new CityAdapter(this, allCityList, this);
        cityList.setAdapter(adapter);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchCity = (SearchView) findViewById(R.id.searchCity);
        searchCity.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchCity.setMaxWidth(Integer.MAX_VALUE);
        searchCity.setIconified(false);

        // listening to search query text change
        searchCity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        setUpFont();
    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("city.list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void setUpFont(){
        FontUtils.setFont(context, weatherText, FontUtils.WEATHER_REGULAR,0);
        FontUtils.setFont(context, cityAndCountry, FontUtils.WEATHER_REGULAR,0);
        FontUtils.setFont(context, current_temperature_field, FontUtils.WEATHER_REGULAR,0);
        FontUtils.setFont(context, dayText1, FontUtils.WEATHER_REGULAR,0);
        FontUtils.setFont(context, dayText2, FontUtils.WEATHER_REGULAR,0);
        FontUtils.setFont(context, dayText3, FontUtils.WEATHER_REGULAR,0);
        FontUtils.setFont(context, dayText4, FontUtils.WEATHER_REGULAR,0);
        FontUtils.setFont(context, tempDay1, FontUtils.WEATHER_REGULAR,0);
        FontUtils.setFont(context, tempDay2, FontUtils.WEATHER_REGULAR,0);
        FontUtils.setFont(context, tempDay3, FontUtils.WEATHER_REGULAR,0);
        FontUtils.setFont(context, tempDay4, FontUtils.WEATHER_REGULAR,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isConnectingToInternet(context)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.PERMISSION_ACCESS_FINE_LOCATION);
            }else{
                getLocationOfUser();
            }
        }else{
            checkNetworkConnection();
        }
    }

    public void getLocationOfUser(){
        gps = new GPSTracker(WeatherActivity.this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            getWeatherByLocation();
        }else{
            gps.showSettingsAlert();
        }
    }

    public void getWeatherByLocation(){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<WeatherResponse> call = apiService.getWeatherByLocation(latitude, longitude, Constants.APP_ID,
                Constants.CELSIUS);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.code() == HttpsURLConnection.HTTP_OK){
                    weatherResponse  = response.body();
                    setUpTemperature();
                }else if(response.code() == HttpsURLConnection.HTTP_NO_CONTENT){
                    Toast.makeText(context, context.getResources().getString(R.string.no_data_found),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.error_occurred),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getWeatherByCity(long cityId){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<WeatherResponse> call = apiService.getWeatherByCity(cityId, Constants.APP_ID,
                Constants.CELSIUS);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.code() == HttpsURLConnection.HTTP_OK){
                    weatherResponse  = response.body();
                    setUpTemperature();
                }else if(response.code() == HttpsURLConnection.HTTP_NO_CONTENT){
                    Toast.makeText(context, context.getResources().getString(R.string.no_data_found),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.error_occurred),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUpTemperature(){
        String temp = ""+Math.round(weatherResponse.getList().get(0).getMain().getTemp());
        current_temperature_field.setText(Html.fromHtml(temp + "&#176;"));
        tempDay1.setText(Html.fromHtml(temp + "&#176;"));
        dayText1.setText(context.getResources().getString(R.string.now));
        String imageUrl = Constants.BASE_URL + Constants.IMAGE_URL
                + weatherResponse.getList().get(0).getWeather().get(0).getIcon()+".png";
        Glide.with(this)
                .load(imageUrl)
                //.placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(imageDay1);
        cityAndCountry.setText(weatherResponse.getCity().getName() + ", " + weatherResponse.getCity().getCountry());
        weatherText.setText(weatherResponse.getList().get(0).getWeather().get(0).getDescription());

        Log.d(TAG, "Main = "+weatherResponse.getList().get(0).getWeather().get(0).getMain().toString());

        if(weatherResponse.getList().get(0).getWeather().get(0)
                .getMain().toString().equalsIgnoreCase(Constants.CLEAR)){
            drawer_layout.setBackgroundResource(R.drawable.clear);
            bottomLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.clear));
        }else if(weatherResponse.getList().get(0).getWeather().get(0)
                .getMain().toString().equalsIgnoreCase(Constants.RAIN)){
            drawer_layout.setBackgroundResource(R.drawable.rain);
            bottomLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.rain));
        }else if(weatherResponse.getList().get(0).getWeather().get(0)
                .getMain().toString().equalsIgnoreCase(Constants.CLOUDS)){
            drawer_layout.setBackgroundResource(R.drawable.cloudy);
            bottomLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.cloud));
        }else if(weatherResponse.getList().get(0).getWeather().get(0)
                .getMain().toString().equalsIgnoreCase(Constants.SNOW)){
            drawer_layout.setBackgroundResource(R.drawable.snow);
            bottomLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.snow));
        }

        Date date1 = new Date();
        Date date2 = new Date();
        Date date3 = new Date();

        Date currentDate = getDateFromString(weatherResponse.getList().get(0).getDt_txt());
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, 1);
        date1 = cal.getTime();
        cal.add(Calendar.DATE, 1);
        date2 = cal.getTime();
        cal.add(Calendar.DATE, 1);
        date3 = cal.getTime();

        for(DataList dataList : weatherResponse.getList()) {
            Date dateTemp = getDateFromString(dataList.getDt_txt());
            if (date1.equals(dateTemp)) {
                dayText2.setText(""+date1.getDate());
                tempDay2.setText(Html.fromHtml(
                        Math.round(dataList.getMain().getTemp()) + "&#176;"));
                Glide.with(this)
                        .load(Constants.BASE_URL + Constants.IMAGE_URL
                                + dataList.getWeather().get(0).getIcon()+".png") // image url
                        .placeholder(R.drawable.ic_launcher_foreground) // any placeholder to load at start
                        .error(R.drawable.ic_launcher_foreground)  // any image in case of error
                        .into(imageDay2);
            }
            if (date2.equals(dateTemp)) {
                dayText3.setText(""+date2.getDate());
                tempDay3.setText(Html.fromHtml(
                        Math.round(dataList.getMain().getTemp()) + "&#176;"));
                Glide.with(this)
                        .load(Constants.BASE_URL + Constants.IMAGE_URL
                                + dataList.getWeather().get(0).getIcon() + ".png") // image url
                        .placeholder(R.drawable.ic_launcher_foreground) // any placeholder to load at start
                        .error(R.drawable.ic_launcher_foreground)  // any image in case of error
                        .into(imageDay3);
            }
            if(date3.before(dateTemp)){
                dayText4.setText(""+date3.getDate());
                tempDay4.setText(Html.fromHtml(
                        Math.round(dataList.getMain().getTemp()) + "&#176;"));
                Glide.with(this)
                        .load(Constants.BASE_URL + Constants.IMAGE_URL
                                + dataList.getWeather().get(0).getIcon()+".png") // image url
                        .placeholder(R.drawable.ic_launcher_foreground) // any placeholder to load at start
                        .error(R.drawable.ic_launcher_foreground)  // any image in case of error
                        .into(imageDay4);
            }

        }
    }

    public Date getDateFromString(String stringDate) {
        Date date = new Date();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date =  df.parse(stringDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return date;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLocationOfUser();
            } else {
                // User refused to grant permission.
                Toast.makeText(this, context.getResources().getString(R.string.location_permission_deny),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
                // connected to wifi
                //Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
                // connected to the mobile provider's data plan
                //Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            return false;
            // not connected to the internet
        }
        return false;
    }

    /**
     * check network connection and go to Setting
     */
    private void checkNetworkConnection() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                WeatherActivity.this);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.network_connection_error));

        // set dialog message
        alertDialogBuilder
                .setMessage(context.getResources().getString(R.string.please_check_network))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.setting),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(
                                        android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        })
                .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    @Override
    public void onCitySelected(City city) {
        drawer.closeDrawers();
        Log.d(TAG, "city Id = "+city.getId());
        getWeatherByCity(city.getId());
        //Toast.makeText(context, city.getName()+" "+ city.getId(), Toast.LENGTH_SHORT).show();
    }
}
