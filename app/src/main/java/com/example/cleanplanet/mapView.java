package com.example.cleanplanet;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cleanplanet.dataBase.DBHelper;
import com.example.cleanplanet.model.pointsHelper;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.ui_view.ViewProvider;

import java.io.IOException;
import java.util.List;

public class mapView extends AppCompatActivity implements MapObjectTapListener {
    private final String MAPKIT_API_KEY = "80492c56-4b48-413b-92a7-5d758ec2f9d3";
    private final Point TARGET_LOCATION = new Point(56.010569, 92.852572);
    private DBHelper dbHelper;
    private MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        MapKitFactory.setApiKey(MAPKIT_API_KEY);

        MapKitFactory.initialize(this);
        // Создание MapView.
        setContentView(R.layout.map_view);
        super.onCreate(savedInstanceState);
        mapView = (MapView)findViewById(R.id.mapview);

        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.LINEAR, 5),
                null);
        MapObjectCollection collection = mapView.getMap().getMapObjects().addCollection();
        collection.addTapListener(this);


        for(pointsHelper pointsHelperClass : points) {
            TextView textView = new TextView(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);

            textView.setText(pointsHelperClass.toString());

            ViewProvider viewProvider = new ViewProvider(textView);


            Point mappoint= new Point(pointsHelperClass.getLatitude(), pointsHelperClass.getLongitude());
            PlacemarkMapObject placemarkMapObject = collection.addPlacemark(mappoint,
                    ImageProvider.fromResource(this, R.drawable.icon));
            placemarkMapObject.setUserData(pointsHelperClass.toString());
        }
    }


    @Override
    protected void onStop() {
        // Вызов onStop нужно передавать инстансам MapView и MapKit.
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        // Вызов onStart нужно передавать инстансам MapView и MapKit.
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public boolean onMapObjectTap(MapObject mapObject, Point point) {

        System.out.println(mapObject.getUserData());
        return true;
    }
}

