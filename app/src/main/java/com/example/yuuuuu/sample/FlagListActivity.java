package com.example.yuuuuu.sample;

import android.app.Activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * Created by YUNA on 2017-08-08.
 *            s0woo on 2017-08-22.
 */

public class FlagListActivity extends Activity{

    private static int COUNTRY_NUM= 20; //참여국 전체 개수

    //참여국 나라 배열
    String[] countries = new String[] {
            "All country",
            "Albania",
            "Andorra",
            "Argentina",
            "Armenia",
            "Australia",
            "Azerbaycan",
            "Bermuda",
            "Estonia",
            "Finland",
            "France",
            "Germany",
            "Greece",
            "Israel",
            "Japan",
            "Kenya",
            "Korea",
            "Luxembourg",
            "Malaysia",
            "USA"
    };

    //참여국 국기 이미지 배열
    int[] flags = new int[]{
            R.drawable.flag_all,
            R.drawable.flag_albania,
            R.drawable.flag_andorra,
            R.drawable.flag_argen,
            R.drawable.flag_armenia,
            R.drawable.flag_australia,
            R.drawable.flag_azer,
            R.drawable.flag_bermuda,
            R.drawable.flag_estonia,
            R.drawable.flag_finland,
            R.drawable.flag_france,
            R.drawable.flag_germany,
            R.drawable.flag_greece,
            R.drawable.flag_israel,
            R.drawable.flag_japan,
            R.drawable.flag_kenya,
            R.drawable.flag_korea,
            R.drawable.flag_luxembourg,
            R.drawable.flag_malaysia,
            R.drawable.flag_usa
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<COUNTRY_NUM;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", countries[i]);
            hm.put("flag", Integer.toString(flags[i]) );
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = { "flag","txt"};

        // Ids of views in listview_layout
        int[] to = { R.id.country,R.id.country_text};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.grid_single, from, to);

        // Getting a reference to gridview of MainActivity
        GridView gridView = (GridView) findViewById(R.id.gridview);

        // Setting an adapter containing images to the gridview
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(FlagListActivity.this, countries[position] + "", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FlagListActivity.this, PlayerActivity.class);
                String sport = getIntent().getStringExtra("sport");
                intent.putExtra("country",countries[position]);
                intent.putExtra("sport", sport);
                startActivity(intent);
            }
        });

    }
}