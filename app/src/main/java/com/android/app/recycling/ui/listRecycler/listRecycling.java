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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link listRecycling#newInstance} factory method to
 * create an instance of this fragment.
 */
public class listRecycling extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static  final int LISTA_NUMEROS = 10;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private RecyclerView mRvListRecycling;
    private RecyclerAdapter mAdapter;
    private ArrayList<RecyclerItem> mRecyclerList;
    private RequestQueue mRequestQueue;

    public listRecycling() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment listRecycling.
     */
    // TODO: Rename and change types and number of parameters
    public static listRecycling newInstance(String param1, String param2) {
        listRecycling fragment = new listRecycling();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        final String url = getResources().getString(R.string.path_service)+"reciclados/"+username;
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
