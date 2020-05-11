package com.android.app.recycling.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private PieChart pieChart;
    private PieDataSet dataSet;
    private PieData pieData;
    private ArrayList<PieEntry> residues = new ArrayList<PieEntry>();
    private ArrayList<PieEntry> residuesTotals = new ArrayList<PieEntry>();
    private HashMap<String,Integer> historical = new HashMap<String,Integer>();
    private HashMap<String,Integer> actual = new HashMap<String,Integer>();
    private RequestQueue ReqQueue;
    private Description description = new Description();
    BottomNavigationView bottomNavigationView;
    Button bSend;
    SharedPreferences sharedpreferences;
    Snackbar snackbar;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ReqQueue = Volley.newRequestQueue(this.getActivity());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataSet =  new PieDataSet(residues,"");
        pieData = new PieData(dataSet);

        pieChart = view.findViewById(R.id.pieChart);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bSend = view.findViewById(R.id.bSend);

        pieChart.setVisibility(View.INVISIBLE);

        this.actual = sharedViewModel.getResidues();

        if(!this.actual.isEmpty()){
            pieChartActual();
        } else {
            Toast t = Toast.makeText(requireContext(), "No existen reciclajes actualmente", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }

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

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedViewModel.getResidues().isEmpty()){
                    Toast t = Toast.makeText(requireContext(), "Debe agregar un reciclaje", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                } else {
                    sendRecycled();
                }
            }
        });

    }

    private void pieChartHistorical() {
        if(!this.historical.isEmpty()){
            residuesTotals.clear();

            for (HashMap.Entry<String, Integer> h : historical.entrySet()) {
                if(h.getValue() > 0){
                    residuesTotals.add(new PieEntry(h.getValue().floatValue(),translate(h.getKey())));
                }
            }
            description.setText("RECICLADO HISTORICO");
            pieChart.setDescription(description);
            setDataPieChart(residuesTotals);

        } else {
            pieChart.setVisibility(View.INVISIBLE);
            Toast t = Toast.makeText(requireContext(), "No existe historial de reciclaje", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }

    }
    private void pieChartActual() {

        if(!this.actual.isEmpty()){
            residues.clear();

            for (HashMap.Entry<String, Integer> a : actual.entrySet()) {
                if(a.getValue() > 0) {
                    residues.add(new PieEntry(a.getValue().floatValue(), translate(a.getKey())));
                }
            }
            description.setText("RECICLADO ACTUAL");
            pieChart.setDescription(description);
            setDataPieChart(residues);
        } else {
            pieChart.setVisibility(View.INVISIBLE);
            Toast t = Toast.makeText(requireContext(), "No existen reciclajes actualmente", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }
    }

    public void setDataPieChart(ArrayList<PieEntry> data){
        dataSet.setLabel("Reciclados");
        dataSet.setValues(data);
        dataSet.setValueTextSize(12f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        pieData.setDataSet(dataSet);
        pieChart.setData(pieData);

        pieData.notifyDataChanged();
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();

        pieChart.setVisibility(View.VISIBLE);

    }

    private void getTotalAndDraw(){
        sharedpreferences = this.getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userName = sharedpreferences.getString("user","");

        final String url = getResources().getString(R.string.path_service)+userName+"/totalreciclado";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);

                            historical.put("bottles",Integer.parseInt(res.getString("bottles")));
                            historical.put("cans",Integer.parseInt(res.getString("cans")));
                            historical.put("tetrabriks",Integer.parseInt(res.getString("tetrabriks")));
                            historical.put("glass",Integer.parseInt(res.getString("glass")));
                            historical.put("paperboard",Integer.parseInt(res.getString("paperboard")));

                            pieChartHistorical();

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String responseBody = new String( error.networkResponse.data, "utf-8" );
                    JSONObject jsonObject = new JSONObject( responseBody );
                    Toast.makeText(requireContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                } catch ( JSONException e ) {
                    //Handle a malformed json response
                } catch (UnsupportedEncodingException e){

                }
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
            j.put("cans",sharedViewModel.getResidues().get("cans").toString());
            j.put("bottles",sharedViewModel.getResidues().get("bottles").toString());
            j.put("tetrabriks", sharedViewModel.getResidues().get("tetrabriks").toString());
            j.put("glass",sharedViewModel.getResidues().get("glass").toString());
            j.put("paperboard", sharedViewModel.getResidues().get("paperboard").toString());

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
                        actual.clear();
                        pieChartActual();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(),error.toString(), Toast.LENGTH_SHORT).show();
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
