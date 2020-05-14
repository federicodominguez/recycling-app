package com.android.app.recycling.ui.listRecycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.app.recycling.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclingViewHolder>{
    private Context mContext;
    private ArrayList<RecyclerItem> mListRecycling;


    public RecyclerAdapter(Context context, ArrayList<RecyclerItem> listRecycling) {
        mContext = context;
        mListRecycling = listRecycling;
    }

    @NonNull
    @Override
    public RecyclingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdListItem = R.layout.list_recycling_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean attachToParent = false;
        View view =inflater.inflate(layoutIdListItem,parent,attachToParent);

        RecyclingViewHolder viewHolder = new RecyclingViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclingViewHolder holder,int position)  {
        holder.bind(position);
        RecyclerItem recyclerItem = mListRecycling.get(position);
        holder.tvIR_bottles.setText(recyclerItem.getBottles());
        holder.tvIR_tetra.setText(recyclerItem.getTetrabriks());
        holder.tvIR_glass.setText(recyclerItem.getGlass());
        holder.tvIR_paperboard.setText(recyclerItem.getPaperboard());
        holder.tvIR_cans.setText(recyclerItem.getCans());
        holder.mDateRecycling.setText(getDateTransformation(recyclerItem.getDate()));
    }

    @Override
    public int getItemCount() {
        return mListRecycling.size();
    }

    @SuppressLint("DefaultLocale")
    private String getDateTransformation(String s){
        String date="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            Date d = sdf.parse(s);
            Calendar mCalendar = new GregorianCalendar();
            mCalendar.setTime(d);
            date = mCalendar.get(Calendar.DAY_OF_MONTH) +"/"+(mCalendar.get(Calendar.MONTH)+1)+"/"+mCalendar.get(Calendar.YEAR)+" "+String.format("%02d:%02d",mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        return date;
    }

    class RecyclingViewHolder extends RecyclerView.ViewHolder{
        TextView tvIR_cans,tvIR_paperboard,tvIR_bottles,tvIR_glass,tvIR_tetra,mDateRecycling;
        public RecyclingViewHolder(View itemView){
            super(itemView);
            tvIR_cans = itemView.findViewById(R.id.tvIR_cans);
            tvIR_bottles = itemView.findViewById(R.id.tvIR_bottles);
            tvIR_paperboard = itemView.findViewById(R.id.tvIR_paperboard);
            tvIR_glass = itemView.findViewById(R.id.tvIR_glass);
            tvIR_tetra = itemView.findViewById(R.id.tvIR_tetra);
            mDateRecycling = itemView.findViewById(R.id.tv_DateRecycling);
        }

        void bind (int position) {
            tvIR_bottles.setText("");
            tvIR_tetra.setText("");
            tvIR_glass.setText("");
            tvIR_paperboard.setText("");
            tvIR_cans.setText("");
        }
    }
}
