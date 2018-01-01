package com.cengizb.imsakiye;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cengiz Bayrak on 5.06.2017.
 * <p>
 * main
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView tarih;
    private TextView gun;
    private TextView ogun;
    private TextView kalan;

    private ArrayList<Oruc> oruclar = new ArrayList<>();
    private Oruc bugunku;

    private final long def_hour = 60 * 60 * 1000;
    private final long def_minute = 60 * 1000;
    private final long def_second = 1000;
    private final long def_time = 10 * def_minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button list = (Button) findViewById(R.id.list);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        tarih = (TextView) findViewById(R.id.tarih);
        gun = (TextView) findViewById(R.id.gun);
        ogun = (TextView) findViewById(R.id.ogun);
        kalan = (TextView) findViewById(R.id.kalan);

        getData();
        if (represent()) {
            try {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        represent();
                    }
                }, 1, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void getData() {
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
            Log.e(TAG, "getData: Exception: " + ex.getMessage());
        }
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray array = new JSONArray(json);
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
                    Date tarih = oruc.getTarih();
                    if (tarih != null) {
                        if (DateUtils.isToday(tarih.getTime())) {
                            this.bugunku = oruc;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "getData: Exception: " + e.getMessage());
            }
        }
    }

    private boolean represent() {
        Date simdikiZaman = new Date();
        Date hedefTarih = null;
        Date imsak = bugunku.getImsak();
        String hedefOgun = "İmsak";
        if (imsak != null) {
            if (imsak.after(simdikiZaman)) {
                hedefTarih = imsak;
            } else {
                hedefOgun = "İftar";
                Date iftar = bugunku.getIftar();
                if (iftar != null) {
                    if (iftar.after(simdikiZaman)) {
                        hedefTarih = iftar;
                    } else {
                        int index = oruclar.indexOf(bugunku);
                        bugunku = oruclar.get(index + 1);
                        hedefTarih = bugunku.getImsak();
                    }
                }
            }
        }
        if (hedefTarih != null) {
            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm", new Locale("tr", "TR"));
            final String targetZaman = format1.format(hedefTarih);
            final String ogunText = hedefOgun + " " + targetZaman;
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ogun.setText(ogunText);
                }
            });
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyy EEEE", new Locale("tr", "TR"));
                final String targetTarih = format.format(hedefTarih);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int index = oruclar.indexOf(bugunku) + 1;
                        tarih.setText(targetTarih);
                        gun.setText(String.format("%s. gün", String.valueOf(index)));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "run: Exception: " + e.getMessage());
            }

            final long differ = hedefTarih.getTime() - simdikiZaman.getTime();
            final long hour = (int) (TimeUnit.MILLISECONDS.toHours(differ));
            final long minute = (int) (TimeUnit.MILLISECONDS.toMinutes(differ) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(differ)));
            final long second = (int) (TimeUnit.MILLISECONDS.toSeconds(differ) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(differ)));

            String saat = String.valueOf(hour);
            if (saat.length() == 1) {
                saat = "0" + saat;
            }
            String dakika = String.valueOf(minute);
            if (dakika.length() == 1) {
                dakika = "0" + dakika;
            }
            String saniye = String.valueOf(second);
            if (saniye.length() == 1) {
                saniye = "0" + saniye;
            }
            final String yazi = saat + ":" + dakika + ":" + saniye;
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    kalan.setText(yazi);
                    boolean uyar = (hour < 1 && minute > 0) || (hour < 1 && minute < 1 && second > 0);
                    if (uyar) {
                        kalan.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light));
                    } else {
                        kalan.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.defaultTextColor));
                    }
                }
            });

            if (differ < def_time && second == 0) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                CharSequence message = hedefOgun + "'a " + String.valueOf(minute) + " dakika kaldı";
                CharSequence summary = hedefOgun + " zamanı";
                android.support.v4.app.NotificationCompat.BigTextStyle bigTextStyle = new android.support.v4.app.NotificationCompat.BigTextStyle();
                bigTextStyle.bigText(message);
                bigTextStyle.setSummaryText(summary);
                android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(getApplicationContext())
                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ? R.mipmap.ic_launcher : R.mipmap.ic_launcher)
                        .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true)
                        .setPriority(android.support.v4.app.NotificationCompat.PRIORITY_MAX)
                        .setContentTitle(getApplicationContext().getString(R.string.app_name))
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(null)
                        .setStyle(bigTextStyle);
                builder.build().flags |= android.support.v4.app.NotificationCompat.FLAG_AUTO_CANCEL;
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                notificationManagerCompat.notify(
                        1000,
                        builder.build());
            }
            return true;
        }
        return false;
    }
}