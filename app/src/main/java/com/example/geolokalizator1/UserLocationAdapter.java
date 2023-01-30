package com.example.geolokalizator1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geolokalizator1.db.Loc;

import java.util.List;

public class UserLocationAdapter extends RecyclerView.Adapter<UserLocationAdapter.MyViewHolder> {

    private final Context context;
    private List<Loc> locList;
    static String lat, lon;
    public UserLocationAdapter(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setLocList(List<Loc> list) {
        locList = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.recycler_row, parent, false);

       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String lat = locList.get(position).locLatitude;
        String lon = locList.get(position).locLongitude;
        String add = locList.get(position).locAddress;
        String dat = locList.get(position).locDate;
        String tim = locList.get(position).locTime;
        Log.d("adres", add +" "+ lat +" "+locList.get(position).lid);
        holder.tvLocLatitude.setText(Html.fromHtml("<b>Szerokość geograficzna:</b><br>"+lat));
        holder.tvLocLatitude.setVisibility(View.VISIBLE);
        holder.tvLocLongitude.setText(Html.fromHtml("<b>Długość geograficzna: </b><br>" + lon));
        holder.tvLocLongitude.setVisibility(View.VISIBLE);
        holder.tvLocAddress.setText(add);
        holder.tvLocAddress.setVisibility(View.VISIBLE);
        holder.tvLocDate.setText(Html.fromHtml("<b>Data i godzina: </b><br>" + dat));
        holder.tvLocDate.setVisibility(View.VISIBLE);
        holder.tvLocTime.setText(Html.fromHtml("<b>Czas w dany miejscu: </b><br> &gt;"+tim+" minut(y)"));
        holder.tvLocTime.setVisibility(View.VISIBLE);
        if (add.equals("Zatrzymano pobieranie lokalizacji")) {
            holder.tvLocLatitude.setVisibility(View.GONE);
            holder.tvLocLongitude.setVisibility(View.GONE);
            holder.tvLocDate.setVisibility(View.GONE);
            holder.tvLocTime.setVisibility(View.GONE);
            holder.btLoc.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return  locList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvLocLatitude;
        TextView tvLocLongitude;
        TextView tvLocAddress;
        TextView tvLocDate;
        TextView tvLocTime;
        ImageButton btLoc;


        public MyViewHolder(View view) {
             super(view);
             tvLocLatitude = view.findViewById(R.id.tvLocLatitude);
             tvLocLongitude = view.findViewById(R.id.tvLocLongitude);
             tvLocAddress = view.findViewById(R.id.tvLocAddress);
             tvLocDate = view.findViewById(R.id.tvLocDate);
             tvLocTime = view.findViewById(R.id.tvLocTime);
             btLoc = view.findViewById(R.id.btloc);

             btLoc.setOnClickListener(view1 -> {
                 String theurl = "https://www.google.com/maps/place/"+lat+","+lon;
                 Uri urlstr = Uri.parse(theurl);
                 Intent urlintent = new Intent(Intent.ACTION_VIEW, urlstr);
                 view1.getContext().startActivity(urlintent);
             });
        }
    }
}
