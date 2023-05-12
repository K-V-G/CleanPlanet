package com.example.cleanplanet;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.cleanplanet.dataBase.DBHelper;
import com.example.cleanplanet.indexAir.AirVisualService;
import com.example.cleanplanet.model.AirVisualResponse;
import com.example.cleanplanet.model.pointsHelper;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class mainView extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private long lastGPSTurnOffTime = 0;
    private static final long MIN_TIME_BETWEEN_GPS_TOASTS = 10 * 1000;
    TextView welcomeUser;
    Button mapButton;
    Button goAllPoints;
    TextView tvLocationGPS;
    private DBHelper dbHelper;

    private LocationManager locationManager;

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.airvisual.com/").build();

    @Override
    @TargetApi(Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        dbHelper = new DBHelper(this);
        try {
            dbHelper.updateDataBase();
        } catch (IOException e) {
            throw new Error("UnableToUpdateDataBase");
        }

        List<pointsHelper> points = dbHelper.getAllPoints();
        for (pointsHelper p: points
        ) {
            System.out.println("_______________________________");
            System.out.println(p.toString());
            System.out.println("__________________________________");
        }

        welcomeUser = findViewById(R.id.welcomeUser);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainView.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String name = sharedPreferences.getString("userName", "");
        welcomeUser.setText(name);
        editor.remove("userName").commit();

        Clock clock = Clock.system(ZoneId.of("UTC+07"));
        LocalDateTime localDateTime = LocalDateTime.now(clock);
        int localTimeHour = localDateTime.getHour();
        setTimesOfDay(localTimeHour, name);

        mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainView.this, mapView.class);
                startActivity(intent);
            }
        });
        goAllPoints = findViewById(R.id.goAllPoints);
        goAllPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainView.this, DataBaseActivityFutureDelete.class);
                startActivity(intent);
            }
        });

    }

    public void setTimesOfDay(int localTimeHour, String name) {
        if (localTimeHour >= 4 && localTimeHour < 12) {
            String text = "Доброе утро,\n" + name;
            welcomeUser.setText(text);
        }
        else if (localTimeHour >= 12 && localTimeHour < 15) {
            String text = "Добрый день,\n" + name;
            welcomeUser.setText(text);
        }
        else if (localTimeHour >= 15 && localTimeHour < 23){
            String text = "Добрый вечер,\n" + name;
            welcomeUser.setText(text);
        }
        else {
            String text = "Доброй ночи,\n" + name;
            welcomeUser.setText(text);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);
        }
        // Запрос разрешения
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_CODE);
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000 * 10, 10, locationListener);
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                        locationListener);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        /**
         * Функция, которая будет вызываться каждый раз, когда устройство выйдет за пределы 10 м
         * каждый раз
         * @param location - местоположение устройства
         */
        @Override
        public void onLocationChanged(Location location) {
            getIndexOnLocation(location);
        }

        /**
         * Функйия обработки недоступности местоположения
         * Внутри идет вызов меню на получение доступа к определению местоположения
         * @param provider the name of the location provider
         */
        @Override
        public void onProviderDisabled(String provider) {
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!gpsEnabled && !networkEnabled) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastGPSTurnOffTime > MIN_TIME_BETWEEN_GPS_TOASTS) {
                    Toast.makeText(mainView.this, "Местоположение недоступно", Toast.LENGTH_LONG).show();
                    lastGPSTurnOffTime = currentTime;
                }
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(mainView.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mainView.this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mainView.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
                return;
            }
            getIndexOnLocation(locationManager.getLastKnownLocation(provider));
        }
    };

    private void getIndexOnLocation(Location location) {
        Response response;
        String json;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody body = null;
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {

        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.airvisual.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AirVisualService service = retrofit.create(AirVisualService.class);
            service.getNearestCity(location.getLatitude(), location.getLongitude(), "ce4c0569-45ef-46d8-a5b2-abdbfb4d3164")
                    .enqueue(new Callback<AirVisualResponse>() {

                        @Override
                        public void onResponse(Call<AirVisualResponse> call, retrofit2.Response<AirVisualResponse> response) {
                            if (response.isSuccessful()) {
                                AirVisualResponse.Data.Current.Pollution pollution = response.body().data.current.pollution;
                                int aqius = pollution.aqius;
                                StringBuilder str = new StringBuilder();
                                str.append(aqius);
                                tvLocationGPS.setText(str.toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            } else {
                                Log.e("ERROR", "Response error: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<AirVisualResponse> call, Throwable t) {
                            Log.e("ERROR", "Network error: " + t.getMessage());
                        }
                    });
        }
    }


}