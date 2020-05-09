package com.bycen.imsakiye;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    private final ArrayList<Oruc> oruclar = new ArrayList<>();
    private Oruc bugunku;

    @SuppressWarnings("unused")
    private final long def_hour = 60 * 60 * 1000;
    private final long def_minute = 60 * 1000;
    @SuppressWarnings("unused")
    private final long def_second = 1000;
    @SuppressWarnings("FieldCanBeLocal")
    private final long def_time = 10 * def_minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        tarih = findViewById(R.id.tarih);
        gun = findViewById(R.id.gun);
        ogun = findViewById(R.id.ogun);
        kalan = findViewById(R.id.kalan);

        data();
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

    private void data() {
        String json = "";
        try {
            InputStream is = getAssets().open("data.json");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(TAG, "data: exception: " + ex.getMessage());
        }
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Oruc oruc = new Oruc();
                    oruc.tarih = object.getString(part.tarih.value());
                    oruc.imsak = object.getString(part.imsak.value());
                    oruc.gunes = object.getString(part.gunes.value());
                    oruc.ogle = object.getString(part.ogle.value());
                    oruc.ikindi = object.getString(part.ikindi.value());
                    oruc.iftar = object.getString(part.iftar.value());
                    oruc.yatsi = object.getString(part.yatsi.value());
                    oruclar.add(oruc);
                    Date tarih = oruc.tarih();
                    if (tarih != null) {
                        if (DateUtils.isToday(tarih.getTime())) {
                            this.bugunku = oruc;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "data: exception: " + e.getMessage());
            }
        }
    }

    private boolean represent() {
        if (bugunku == null)
            return false;

        Date simdi = new Date();
        Date imsak = bugunku.imsak();
        Date hedef = null;
        String hedefOgun = "İmsak";
        if (imsak != null) {
            if (imsak.after(simdi)) {
                hedef = imsak;
            } else {
                hedefOgun = "İftar";
                Date iftar = bugunku.iftar();
                if (iftar != null) {
                    if (iftar.after(simdi)) {
                        hedef = iftar;
                    } else {
                        int index = oruclar.indexOf(bugunku);
                        bugunku = oruclar.get(index + 1);
                        hedef = bugunku.imsak();
                    }
                }
            }
        }

        if (hedef == null)
            return false;

        final String hedefZaman = date(hedef, format.hhmm);
        final String ogunText = hedefOgun + " " + hedefZaman;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ogun.setText(ogunText);
            }
        });
        try {
            final String hedefTarih = date(hedef, format.full);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int index = oruclar.indexOf(bugunku) + 1;
                    tarih.setText(hedefTarih);
                    gun.setText(String.format("%s. gün", String.valueOf(index)));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "run: exception: " + e.getMessage());
        }

        final long differ = hedef.getTime() - simdi.getTime();
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
        if (saniye.length() == 1)
            saniye = "0" + saniye;
        final String yazi = saat + ":" + dakika + ":" + saniye;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                kalan.setText(yazi);
                boolean uyar = (hour < 1 && minute > 0) || (hour < 1 && second > 0);
                int color = R.color.defaultTextColor;
                if (uyar)
                    color = android.R.color.holo_red_light;
                color = ContextCompat.getColor(MainActivity.this, color);
                kalan.setTextColor(color);
            }
        });

        if (differ < def_time && second == 0) {
            notify(hedefOgun, minute);
        }
        return true;
    }

    private void notify(String ogun, long minute) {
        final String appName = getString(R.string.app_name);
        final String id = "imsakiye";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(id, id, importance);
                channel.setDescription(appName);
                channel.enableLights(true);
                channel.enableVibration(true);
                nm.createNotificationChannel(channel);
            }
        }

        final Context context = getApplicationContext();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);
        CharSequence message = ogun + "'a " + String.valueOf(minute) + " dakika kaldı";
        CharSequence summary = ogun + " zamanı";

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle()
                .bigText(message)
                .setSummaryText(summary);

        Bitmap large = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        int small = R.mipmap.ic_launcher;
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
//            small = R.mipmap.ic_launcher;
        int color = ContextCompat.getColor(context, R.color.colorPrimary);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int priority = NotificationCompat.PRIORITY_MAX;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                .setLargeIcon(large)
                .setSmallIcon(small)
                .setColor(color)
                .setSound(sound)
                .setAutoCancel(true)
                .setPriority(priority)
                .setContentTitle(appName)
                .setContentText(message)
                .setContentIntent(pending)
                .setDeleteIntent(null)
                .setStyle(style);
        builder.build().flags |= NotificationCompat.FLAG_AUTO_CANCEL;
        int notificationId = 1000;
        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }

    private enum part {
        tarih("tarih"),
        imsak("imsak"),
        gunes("gunes"),
        ogle("ogle"),
        ikindi("ikindi"),
        iftar("iftar"),
        yatsi("yatsi");

        final String _value;

        part(String value) {
            this._value = value;
        }

        String value() {
            return this._value;
        }
    }

    private String date(Date date, format format) {
        final String language = "tr";
        final String country = "TR";
        final String pattern = format.value;
        Locale locale = new Locale(language, country);
        return new SimpleDateFormat(pattern, locale).format(date);
    }

    private enum format {
        hhmm("HH:mm"),
        full("dd MMMM yyyy EEEE");

        final String value;

        format(String value) {
            this.value = value;
        }
    }
}