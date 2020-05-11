package com.android.app.recycling.ui.listRecycler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.app.recycling.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class listRecycling extends Fragment {



    private RecyclerView mRvListRecycling;
    private RecyclerAdapter mAdapter;
    private ArrayList<RecyclerItem> mRecyclerList;
    private RequestQueue mRequestQueue;

    public listRecycling() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_recycling, container, false);
        mRvListRecycling = view.findViewById(R.id.rvRecyclingList);
        mRvListRecycling.setHasFixedSize(true);
        mRvListRecycling.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRvListRecycling.setLayoutManager(linearLayoutManager);
        mRecyclerList = new ArrayList<RecyclerItem>();
        mRequestQueue = Volley.newRequestQueue(getContext());
        parseJSON();

        return view;
    }

    private void parseJSON(){
        SharedPreferences sharedpreferences = this.getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        final String username = sharedpreferences.getString("user","");
        final String url = getResources().getString(R.string.path_service)+username+"/reciclados";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length()==0){
                        Toast.makeText(getContext(),"No tenes reciclados",Toast.LENGTH_LONG).show();
                    }
                    for (int i = 0; i<response.length();i++){
                        JSONObject item = response.getJSONObject(i);
                        createRecyclerItem(item);
                    }
                    mAdapter = new RecyclerAdapter(getContext(),mRecyclerList);
                    mRvListRecycling.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }

    private void createRecyclerItem(JSONObject item){
        try {
            String bottles = item.getString("bottles");
            String tetrabriks=item.getString("tetrabriks");
            String glass=item.getString("glass");
            String paperboard=item.getString("paperboard");
            String cans=item.getString("cans");
            String date = item.getString("date");
            RecyclerItem mRecyclerItem = new RecyclerItem(bottles,tetrabriks,glass,paperboard,cans,date);
            mRecyclerList.add(mRecyclerItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
