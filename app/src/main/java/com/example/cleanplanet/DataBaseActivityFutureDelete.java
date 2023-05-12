package com.example.cleanplanet;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cleanplanet.dataBase.DBHelper;
import com.example.cleanplanet.model.pointsHelper;

import java.io.IOException;
import java.util.List;

public class DataBaseActivityFutureDelete extends AppCompatActivity {

    private DBHelper dbHelper;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_base);
        dbHelper = new DBHelper(this);
        try {
            dbHelper.updateDataBase();
        } catch (IOException e) {
            throw new Error("UnableToUpdateDataBase");
        }
        layout = (LinearLayout) findViewById(R.id.liner);

       /* pointsHelper helper = new pointsHelper(14, "Test2", "Text Point", "Улица Пушкина, дом Колотушкина", null, null, "testContact");*/
        dbHelper.deletePoint(14);

        List<pointsHelper> points = dbHelper.getAllPoints();
        LinearLayout childLayout = new LinearLayout(this);
        int index = 0;
        for (pointsHelper p: points
        ) {
            TextView textView = new TextView(this);
            String str = p.toString() + "\n";
            textView.setText(str);
            textView.setLayoutParams(new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setTextSize(10);
            childLayout.setOrientation(LinearLayout.VERTICAL);
            childLayout.addView(textView, index);
            index ++;
        }
        layout.addView(childLayout);

/*        try {
            dbHelper.checkMethod();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*TextView textView = new TextView(this);
        TextView textView2 = new TextView(this);
        textView.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry...like Aldus PageMaker including versions of Lorem Ipsum.");
        textView.setLayoutParams(new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(26);

        textView2.setText("Hello");

        childLayout.setOrientation(LinearLayout.VERTICAL);
        childLayout.addView(textView, 0);
        childLayout.addView(textView2, 0);
        layout.addView(childLayout);*/
    }
}