package com.example.cleanplanet.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cleanplanet.model.pointsHelper;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "cleanPlanet.db";
    private static String DB_PATH = "";
    private static String TABLE_POINTS = "recyclingPoints";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }

    public List<pointsHelper> getAllPoints() {
        List<pointsHelper> points = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_POINTS;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                pointsHelper point = new pointsHelper();
                point.set_id(Integer.parseInt(cursor.getString(0)));
                point.setTitle(cursor.getString(1));
                point.setShortDescription(cursor.getString(2));
                if (cursor.getString(3) != null) {
                    point.setAddress(cursor.getString(3));
                }
                if (cursor.getString(4) != null) {
                    point.setLatitude(Double.parseDouble(cursor.getString(4)));
                }
                if (cursor.getString(5) != null) {
                    point.setLongitude(Double.parseDouble(cursor.getString(5)));
                }
                point.setContacts(cursor.getString(6));
                points.add(point);
            }while (cursor.moveToNext());
        }
        return points;
    }

    public void addPoint(pointsHelper pointsHelper) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", pointsHelper.getTitle());
        values.put("shortDescription", pointsHelper.getShortDescription());
        values.put("contacs", pointsHelper.getContacts());
        values.put("address", pointsHelper.getAddress());
        values.put("latitude", pointsHelper.getLatitude());
        values.put("longitude", pointsHelper.getLongitude());
        database.insert(TABLE_POINTS, null, values);
        database.close();
    }

    public int updatePoint(pointsHelper pointsHelper) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", pointsHelper.getTitle());
        values.put("shortDescription", pointsHelper.getShortDescription());
        values.put("contacs", pointsHelper.getContacts());
        values.put("address", pointsHelper.getAddress());
        values.put("latitude", pointsHelper.getLatitude());
        values.put("longitude", pointsHelper.getLongitude());
        return database.update(TABLE_POINTS, values, "_id" + " = ?", new String[]
                {String.valueOf(pointsHelper.get_id())});
    }

    public void deletePoint(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_POINTS, "_id" + " = ?", new String[]
                {String.valueOf(id)});
        database.close();
    }


    public void checkMethod() throws JSONException, IOException {
        final String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";// путь к Geocoding API по HTTP
        final Map<String, String> params = Maps.newHashMap();
        params.put("sensor", "false");// исходит ли запрос на геокодирование от устройства с датчиком местоположения
        params.put("address", "Россия, Москва, улица Поклонная, 12");// адрес, который нужно геокодировать
        final String url = baseUrl + '?' + encodeParams(params);// генерируем путь с параметрами
        System.out.println(url);// Путь, что бы можно было посмотреть в браузере ответ службы
        JSONObject response = null;// делаем запрос к вебсервису и получаем от него ответ
        try {
            response = JsonReader.read(url);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(response);
        // как правило наиболее подходящий ответ первый и данные о координатах можно получить по пути
        // //results[0]/geometry/location/lng и //results[0]/geometry/location/lat
        assert response != null;
        JSONObject location = response.getJSONArray("results").getJSONObject(0);
        location = location.getJSONObject("geometry");
        location = location.getJSONObject("location");
        final double lng = location.getDouble("lng");// долгота
        final double lat = location.getDouble("lat");// широта
        System.out.println(String.format("%f,%f", lat, lng));// итоговая широта и долгота
    }

    protected static String encodeParams(final Map<String, String> params) {
        final String paramsUrl = Joiner.on('&').join(// получаем значение вида key1=value1&key2=value2...
                Iterables.transform(params.entrySet(), new Function<Entry<String, String>, String>() {

                    @Override
                    public String apply(final Entry<String, String> input) {
                        try {
                            final StringBuffer buffer = new StringBuffer();
                            buffer.append(input.getKey());// получаем значение вида key=value
                            buffer.append('=');
                            buffer.append(URLEncoder.encode(input.getValue(), "utf-8"));// кодирует строку в
                            // соответствии со стандартом
                            // HTML 4.01
                            return buffer.toString();
                        } catch (final UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }));
        return paramsUrl;
    }
}
