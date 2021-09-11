package com.example.bdayreminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>{
    private  Context context;
    private ArrayList person_id, name, msg, phone, date, time;
    Activity activity;

    CustomAdapter(Activity activity,
                  Context context,
                  ArrayList id,
                  ArrayList name,
                  ArrayList msg,
                  ArrayList phone,
                  ArrayList date,
                  ArrayList time ){
        this.activity = activity;
        this.context = context;
        this.person_id = id;
        this.name= name;
        this.msg = msg;
        this.phone = phone;
        this.date = date;
        this.time = time;

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.person_id.setText(String.valueOf(person_id.get(position)));
        holder.name.setText(String.valueOf(name.get(position)));
        holder.msg.setText(String.valueOf(msg.get(position)));
        holder.phone.setText(String.valueOf(phone.get(position)));
        holder.date.setText(String.valueOf(date.get(position)));
        holder.time.setText(String.valueOf(time.get(position)));
        holder.mainLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,UpdateActivity.class);
                intent.putExtra("id",String.valueOf(person_id.get(position)));
                intent.putExtra("name",String.valueOf(name.get(position)));
                intent.putExtra("msg",String.valueOf(msg.get(position)));
                intent.putExtra("phone",String.valueOf(phone.get(position)));
                intent.putExtra("date",String.valueOf(date.get(position)));
                intent.putExtra("time",String.valueOf(time.get(position)));
                activity.startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return person_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView person_id,name,msg,phone,date,time;
        LinearLayout mainLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            person_id = itemView.findViewById(R.id.person_id_text);
            name = itemView.findViewById(R.id.person_name);
            msg = itemView.findViewById(R.id.bday_msg);
            phone = itemView.findViewById(R.id.person_phone);
            date = itemView.findViewById(R.id.bday_date);
            time = itemView.findViewById(R.id.wishing_time);
            mainLayout = itemView.findViewById(R.id.mainlayout);
        }
    }
}

