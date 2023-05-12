package com.example.cleanplanet.model;

import com.google.gson.annotations.SerializedName;

public class AirVisualResponse {
    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("current")
        public Current current;

        public static class Current {
            @SerializedName("pollution")
            public Pollution pollution;

            public static class Pollution {
                @SerializedName("aqius")
                public int aqius;
            }
        }
    }
}
