package com.bycen.imsakiye;

import android.annotation.SuppressLint;
import android.content.Context;
<<<<<<< HEAD:app/src/main/java/com/bycen/imsakiye/Adapter.java
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
=======
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
>>>>>>> d669d036e046e1901f5a2f5a2b22fc03bf252ba7:app/src/main/java/com/cengizb/imsakiye/Adapter.java
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cengiz Bayrak on 26.05.2017.
 * <p>
 * adaptör
 */
public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private static final String TAG = "Adapter";

    private final Context context;
    private final ArrayList<Oruc> oruclar;

    Adapter(Context context, ArrayList<Oruc> oruclar) {
        this.context = context;
        this.oruclar = oruclar;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int i) {
        final Oruc oruc = oruclar.get(i);
        holder.tarih.setText(oruc.tarih);
        holder.imsak.setText(oruc.imsak);
        holder.gunes.setText(oruc.gunes);
        holder.ogle.setText(oruc.ogle);
        holder.ikindi.setText(oruc.ikindi);
        holder.iftar.setText(oruc.iftar);
        holder.yatsi.setText(oruc.yatsi);
        if (!TextUtils.isEmpty(oruc.tarih)) {
            String[] parts = oruc.tarih.split("\\.");
            if (parts.length > 0) {
                if (parts[0].length() == 1) {
                    parts[0] = "0" + parts[0];
                }
                holder.tarih.setText(String.format("%s.%s", parts[0], parts[1]));
            }
        }
        Date tarih = oruc.tarih();

        final int textColor = new TextView(context).getCurrentTextColor();
        int background = ContextCompat.getColor(context, R.color.white);
        int text = textColor;
        if (tarih != null && DateUtils.isToday(tarih.getTime())) {
            text = background;
            background = ContextCompat.getColor(context, R.color.colorAccent);
        }
        holder.itemView.setBackgroundColor(background);
        holder.tarih.setTextColor(text);
        holder.imsak.setTextColor(text);
        holder.gunes.setTextColor(text);
        holder.ogle.setTextColor(text);
        holder.ikindi.setTextColor(text);
        holder.iftar.setTextColor(text);
        holder.yatsi.setTextColor(text);
    }

    @Override
    public int getItemCount() {
        return oruclar.size();
    }

    int today() {
        Date date;
        for (Oruc oruc : oruclar) {
            if ((date = oruc.tarih()) != null && DateUtils.isToday(date.getTime())) {
                return oruclar.indexOf(oruc);
            }
        }
        return 0;
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView tarih, imsak, gunes, ogle, ikindi, iftar, yatsi;

        Holder(View itemView) {
            super(itemView);

            tarih = itemView.findViewById(R.id.tarih);
            imsak = itemView.findViewById(R.id.imsak);
            gunes = itemView.findViewById(R.id.gunes);
            ogle = itemView.findViewById(R.id.ogle);
            ikindi = itemView.findViewById(R.id.ikindi);
            iftar = itemView.findViewById(R.id.iftar);
            yatsi = itemView.findViewById(R.id.yatsi);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Oruc oruc = oruclar.get(getAdapterPosition());
            int index = oruclar.indexOf(oruc) + 1;
            Locale l = Util.locale();
            final String message = String.format(l, "%d. gün - %s", index, oruc.tarih);
            new AlertDialog.Builder(context)
                    .setCancelable(true)
                    .setTitle(message)
                    .setMessage(oruc.toString())
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
    }
}