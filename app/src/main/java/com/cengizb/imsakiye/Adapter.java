package com.cengizb.imsakiye;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Cengiz Bayrak on 26.05.2017.
 * <p>
 * adaptör
 */
public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private static final String TAG = "Adapter";

    private Context context;
    private ArrayList<Oruc> oruclar;

    Adapter(Context context, ArrayList<Oruc> oruclar) {
        this.context = context;
        this.oruclar = oruclar;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int i) {
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

        Date tarih = oruc.getTarih();
        if (tarih != null && DateUtils.isToday(tarih.getTime())) {
            int green = ContextCompat.getColor(context, R.color.colorAccent);
            int white = ContextCompat.getColor(context, android.R.color.white);
            holder.itemView.setBackgroundColor(green);
            holder.tarih.setTextColor(white);
            holder.imsak.setTextColor(white);
            holder.gunes.setTextColor(white);
            holder.ogle.setTextColor(white);
            holder.ikindi.setTextColor(white);
            holder.iftar.setTextColor(white);
            holder.yatsi.setTextColor(white);

            active = i;
        }
    }

    @Override
    public int getItemCount() {
        return oruclar.size();
    }

    private int active = 0;

    int active() {
        return active;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tarih, imsak, gunes, ogle, ikindi, iftar, yatsi;

        Holder(View itemView) {
            super(itemView);

            tarih = (TextView) itemView.findViewById(R.id.tarih);
            imsak = (TextView) itemView.findViewById(R.id.imsak);
            gunes = (TextView) itemView.findViewById(R.id.gunes);
            ogle = (TextView) itemView.findViewById(R.id.ogle);
            ikindi = (TextView) itemView.findViewById(R.id.ikindi);
            iftar = (TextView) itemView.findViewById(R.id.iftar);
            yatsi = (TextView) itemView.findViewById(R.id.yatsi);
        }
    }
}