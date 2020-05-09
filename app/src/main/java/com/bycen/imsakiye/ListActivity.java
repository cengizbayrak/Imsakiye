package com.bycen.imsakiye;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private final ArrayList<Oruc> oruclar = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recyclerView);
        TextView hedef = findViewById(R.id.tarih);
        TextView kalan = findViewById(R.id.kalan);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Takvim");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        populate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleData() {
        String json = "";
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Oruc oruc = new Oruc();
                    oruc.tarih = object.getString("tarih");
                    if (!TextUtils.isEmpty(oruc.tarih)) {
                        String[] parts = oruc.tarih.split("\\.");
                        if (parts.length > 0) {
                            if (parts[0].length() == 1) {
                                parts[0] = "0" + parts[0];
                            }
                            oruc.tarih = parts[0] + "." + parts[1];
                        }
                    }
                    oruc.imsak = object.getString("imsak");
                    oruc.gunes = object.getString("gunes");
                    oruc.ogle = object.getString("ogle");
                    oruc.ikindi = object.getString("ikindi");
                    oruc.iftar = object.getString("iftar");
                    oruc.yatsi = object.getString("yatsi");
                    oruclar.add(oruc);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void populate() {
        String json = "";
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int read = is.read(buffer);
            Log.d(TAG, "populate: read buffer: " + String.valueOf(read));
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "populate: exception: " + e.getMessage());
        }

        if (TextUtils.isEmpty(json))
            return;

        try {
            JSONArray array = new JSONArray(json);
            ArrayList<Oruc> oruclar = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Oruc oruc = new Oruc();
                oruc.tarih = object.getString("tarih");
                oruc.imsak = object.getString("imsak");
                oruc.gunes = object.getString("gunes");
                oruc.ogle = object.getString("ogle");
                oruc.ikindi = object.getString("ikindi");
                oruc.iftar = object.getString("iftar");
                oruc.yatsi = object.getString("yatsi");
                oruclar.add(oruc);
            }

            final Adapter adapter = new Adapter(ListActivity.this, oruclar);
            int spanCount = 1;
            int orientation = StaggeredGridLayoutManager.VERTICAL;
            StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(spanCount, orientation);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(lm);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(adapter.today());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "populate: exception: " + e.getMessage());
        }
    }
}