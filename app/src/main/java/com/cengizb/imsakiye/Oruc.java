package com.cengizb.imsakiye;

import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cengiz Bayrak on 26.05.2017.
 * <p>
 * Oruç
 */
class Oruc {
    private static final String TAG = "Oruc";

    private static Locale locale = new Locale("tr", "TR");
    private static String pattern = "dd.MM.yyyy";
    private static SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);

    String tarih;
    String imsak;
    String gunes;
    String ogle;
    String ikindi;
    String iftar;
    String yatsi;

    @Nullable
    Date getTarih() {
        try {
            return formatter.parse(this.tarih);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "getTarih: Exception: " + e.getMessage());
        }
        return null;
    }

    @Nullable
    Date getImsak() {
        try {
            Date tarih = formatter.parse(this.tarih);
            Calendar imsak = Calendar.getInstance();
            imsak.setTime(tarih);
            String[] parts = this.imsak.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            imsak.set(Calendar.HOUR_OF_DAY, hour);
            imsak.set(Calendar.MINUTE, minute);
            return imsak.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "getImsak: Exception: " + e.getMessage());
        }
        return null;
    }

    @Nullable
    Date getIftar() {
        try {
            Date tarih = formatter.parse(this.tarih);
            Calendar iftar = Calendar.getInstance();
            iftar.setTime(tarih);
            String[] parts = this.iftar.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            iftar.set(Calendar.HOUR_OF_DAY, hour);
            iftar.set(Calendar.MINUTE, minute);
            return iftar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "getIftar: Exception: " + e.getMessage());
        }
        return null;
    }
}