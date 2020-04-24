package com.cengizb.imsakiye;

import androidx.annotation.Nullable;
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

    private static final Locale locale = new Locale("tr", "TR");
    private static final String pattern = "dd.MM.yyyy";
    private static final SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);

    String tarih;
    String imsak;
    String gunes;
    String ogle;
    String ikindi;
    String iftar;
    String yatsi;

    @Nullable
    Date tarih() {
        try {
            return formatter.parse(tarih);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "tarih: exception: " + e.getMessage());
        }
        return null;
    }

    @Nullable
    Date imsak() {
        try {
            Date date = formatter.parse(tarih);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String[] parts = imsak.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            return calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "imsak: exception: " + e.getMessage());
        }
        return null;
    }

    @Nullable
    Date iftar() {
        try {
            Date date = formatter.parse(tarih);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String[] parts = iftar.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            return calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "iftar: exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        final String format = "Object: %d {\n" +
                "\tTarih: %s\n" +
                "\tİmsak: %s\n" +
                "\tGüneş: %s\n" +
                "\tÖğle: %s\n" +
                "\tİkindi: %s\n" +
                "\tİftar: %s\n" +
                "\tYatsı: %s\n" + "}";
        Locale l = Util.locale();
        int hash = hashCode();
        return String.format(l, format, hash, tarih, imsak, gunes, ogle, ikindi, iftar, yatsi);
    }
}