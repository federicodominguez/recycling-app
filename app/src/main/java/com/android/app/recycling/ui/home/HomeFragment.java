package com.android.app.recycling.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.app.recycling.R;
import com.android.app.recycling.SharedViewModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    PieChart pieChart;
    PieDataSet dataSet;
    PieData pieData;
    ArrayList<PieEntry> residues = new ArrayList<PieEntry>();
    ArrayList<PieEntry> residuesTotals = new ArrayList<PieEntry>();
    BottomNavigationView bottomNavigationView;
    Button bSend;
    private RequestQueue ReqQueue;
    SharedPreferences sharedpreferences;
    private HashMap<String,Integer> totals = new HashMap<String,Integer>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        dataSet =  new PieDataSet(residues,"");
        pieData = new PieData(dataSet);
        pieChart = root.findViewById(R.id.pieChart);

        bottomNavigationView = root.findViewById(R.id.bottom_navigation);

        ReqQueue = Volley.newRequestQueue(this.getActivity());

        sharedViewModel.getResidues().observe(getViewLifecycleOwner(), new Observer<HashMap<String, Integer>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, Integer> stringIntegerHashMap) {

                Log.d("ONCHANGED","====== ENTRO =====");

                Boolean set =  false;
                pieChart.setVisibility(View.VISIBLE);

                if(!residues.isEmpty()) {
                    for (HashMap.Entry<String, Integer> hm : stringIntegerHashMap.entrySet()) {
                        for (PieEntry p : residues) {
                            if (p != null && translate(hm.getKey()) == p.getLabel()) {
                                residues.remove(p);
                                p.setY(p.getValue() + hm.getValue());
                                residues.add(p);
                                set = true;
                            }
                        }
                        if (!set && hm.getValue() != 0){
                            residues.add(new PieEntry(hm.getValue().floatValue(),translate(hm.getKey())));
                        }
                    }
                }
                else {
                    for (HashMap.Entry<String, Integer> hm : stringIntegerHashMap.entrySet()){
                        if(hm.getValue() != 0 ) {
                            residues.add(new PieEntry(hm.getValue().floatValue(),translate(hm.getKey())));
                        }
                    }
                }

                dataSet.setValues(residues);

                pieData.notifyDataChanged();
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_actual:
                        bSend.setVisibility(View.VISIBLE);
                        pieChartActual();
                        return true;
                    case R.id.nav_historical:
                        bSend.setVisibility(View.INVISIBLE);
                        getTotalAndDraw();
                        return true;
                }
                return false;
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("On_View_Created_1","==============");

        if(residues.isEmpty()){
            pieChart.setVisibility(View.INVISIBLE);
            Toast.makeText(requireContext(),R.string.empty_recycled,Toast.LENGTH_SHORT).show();
        } else {
            if(pieChart.getVisibility() == View.INVISIBLE){
                Log.d("On_View_Created_2","==============");

                pieChart.setVisibility(View.VISIBLE);
            }

            Log.d("On_View_Created_3","==============");
            dataSet.setLabel("residues");
            dataSet.setValues(residues);
            //dataSet =  new PieDataSet(residues,"residues");
            dataSet.setValueTextSize(12f);
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            pieData.setDataSet(dataSet);
            //pieData = new PieData(dataSet);
            pieChart.setData(pieData);
        }

        bSend = view.findViewById(R.id.bSend);

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedViewModel.getResidues().getValue() == null){
                    Toast.makeText(requireContext(), "Debe agregar un reciclaje", Toast.LENGTH_SHORT).show();
                } else {
                    sendRecycled();
                }
            }
        });

    }

    private void pieChartHistorical() {
        residuesTotals.clear();

        for (HashMap.Entry<String, Integer> t : totals.entrySet()) {
            if(t.getValue() > 0){
                residuesTotals.add(new PieEntry(t.getValue().floatValue(),translate(t.getKey())));
            }
        }

        Log.d("Pie_Chart_Historical","==============");


        if(pieChart.getVisibility() == View.INVISIBLE){
            pieChart.setVisibility(View.VISIBLE);
        }

        dataSet.setLabel("residuesTotals");
        dataSet.setValues(residuesTotals);
        //dataSet =  new PieDataSet(residuesTotals,"residuesTotals");
        dataSet.setValueTextSize(12f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        pieData.setDataSet(dataSet);
        pieChart.setData(pieData);

        pieData.notifyDataChanged();
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();

    }
    private void pieChartActual() {

        if(residues.isEmpty()) {
            pieChart.setVisibility(View.INVISIBLE);
            Toast.makeText(requireContext(), R.string.empty_recycled, Toast.LENGTH_SHORT).show();
        } else {
            //dataSet =  new PieDataSet(residues,"residues");
            dataSet.setLabel("residues");
            dataSet.setValues(residues);
            dataSet.setValueTextSize(12f);
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            pieData.setDataSet(dataSet);
            pieChart.setData(pieData);

            pieData.notifyDataChanged();
            pieChart.notifyDataSetChanged();
            pieChart.invalidate();
        }
    }

    private void getTotalAndDraw(){
        sharedpreferences = this.getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userName = sharedpreferences.getString("user","");

        final String url = getResources().getString(R.string.path_service)+"totalreciclado/"+userName;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);

                            totals.put("bottles",Integer.parseInt(res.getString("bottles")));
                            totals.put("cans",Integer.parseInt(res.getString("cans")));
                            totals.put("tetrabriks",Integer.parseInt(res.getString("tetrabriks")));
                            totals.put("glass",Integer.parseInt(res.getString("glass")));
                            totals.put("paperboard",Integer.parseInt(res.getString("paperboard")));

                            pieChartHistorical();

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(),error.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });
       ReqQueue.add(stringRequest);
    }

    private void sendRecycled() {

        sharedpreferences = this.getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userName = sharedpreferences.getString("user","");

        JSONObject j = new JSONObject();
        try {
            j.put("userName",userName);
            j.put("cans",sharedViewModel.getResidues().getValue().get("cans").toString());
            j.put("bottles",sharedViewModel.getResidues().getValue().get("bottles").toString());
            j.put("tetrabriks", sharedViewModel.getResidues().getValue().get("tetrabriks").toString());
            j.put("glass",sharedViewModel.getResidues().getValue().get("glass").toString());
            j.put("paperboard", sharedViewModel.getResidues().getValue().get("paperboard").toString());
            j.put("date",null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.path_service) + "reciclados";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, j,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        String ToastText = "El reciclado fue almacenado exitosamente";
                        Toast.makeText(requireContext(),ToastText,Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(),error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        ReqQueue.add(req);
    }

    public String translate(String word){
        String translation;
        switch (word){
            case "glass":
                translation = "vidrio";
                break;
            case "paperboard":
                translation = "carton";
                break;
            case "cans":
                translation = "latas";
                break;
            case "tetrabriks":
                translation = "tetrabriks";
                break;
            case "bottles":
                translation = "botellas";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + word);
        }
        return translation;
    }

}
