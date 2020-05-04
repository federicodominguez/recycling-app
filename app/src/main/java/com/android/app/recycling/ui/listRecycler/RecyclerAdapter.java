package com.android.app.recycling.ui.listRecycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.app.recycling.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
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
        String bottles = "Botellas:"+recyclerItem.getBottles();
        String tetrabriks = " Tetrabricks:"+recyclerItem.getTetrabriks();
        String glass = " Vidrio:"+recyclerItem.getGlass();
        String paperboard = " Cartón:"+recyclerItem.getPaperboard();
        String cans = " Latas:"+recyclerItem.getCans();
        holder.mTvListRecycling.setText(bottles);
        holder.mTvListRecycling.append(tetrabriks);
        holder.mTvListRecycling.append(glass);
        holder.mTvListRecycling.append(paperboard);
        holder.mTvListRecycling.append(cans);
        holder.mDateRecycling.setText(getDateTransformation(recyclerItem.getDate()));
    }

    @Override
    public int getItemCount() {
        return mListRecycling.size();
    }

    private String getDateTransformation(String s){
        String date="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            Date d = sdf.parse(s);
            Calendar mCalendar = new GregorianCalendar();
            mCalendar.setTime(d);
            date = mCalendar.get(Calendar.DAY_OF_MONTH) +"/"+(mCalendar.get(Calendar.MONTH)+1)+"/"+mCalendar.get(Calendar.YEAR)+" "+mCalendar.get(Calendar.HOUR_OF_DAY)+":"+mCalendar.get(Calendar.MINUTE);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        return date;
    }

    class RecyclingViewHolder extends RecyclerView.ViewHolder{
        TextView mTvListRecycling,mDateRecycling;
        public RecyclingViewHolder(View itemView){
            super(itemView);
            mTvListRecycling = itemView.findViewById(R.id.tv_itemRecycling);
            mDateRecycling = itemView.findViewById(R.id.tv_DateRecycling);
        }

        void bind (int position) {
            mTvListRecycling.setText("");
        }
    }
}
