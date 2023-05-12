package com.example.cleanplanet.indexAir;

import com.example.cleanplanet.model.AirVisualResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AirVisualService {
    @GET("v2/nearest_city")
    Call<AirVisualResponse> getNearestCity(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("key") String apiKey
    );
}
