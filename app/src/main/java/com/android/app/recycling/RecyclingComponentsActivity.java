package com.android.app.recycling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class RecyclingComponentsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerComponents;
    EditText etQuantity;
    PieChart pieChart;
    Button bAdd, bSend, bLogout;
    private Integer total_cans, total_paperboard, total_bottles, total_glass, total_tetrabriks;
    PieDataSet dataSet;
    PieData pieData;
    ArrayList<PieEntry> residues;
    private RequestQueue ReqQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling_components);
        total_bottles = 0;
        total_cans = 0;
        total_glass = 0;
        total_paperboard = 0;
        total_tetrabriks = 0;

        bAdd = findViewById(R.id.bAdd);
        bLogout = findViewById(R.id.bLogout);
        bSend = findViewById(R.id.bSend);
        spinnerComponents = findViewById(R.id.spinnerComponents);
        etQuantity = findViewById(R.id.etQuantity);
        pieChart = findViewById(R.id.pieChart);

        // PIE CHART
        residues = new ArrayList<PieEntry>();

        dataSet =  new PieDataSet(residues,"residues");
        dataSet.setValueTextSize(12f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // SPINNER RESIDUES

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.residues, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerComponents.setAdapter(adapter);
        spinnerComponents.setOnItemSelectedListener(this);
        SharedPreferences preferencias = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        Boolean openSession = true;

        SharedPreferences.Editor editor = preferencias.edit();
        editor.putBoolean("UserInSession",openSession);
        editor.commit();


        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Arrays.asList(getResources().getStringArray(R.array.residues)).contains(spinnerComponents.getSelectedItem().toString())){
                    if(etQuantity != null && etQuantity.getText().toString().trim().length() != 0){
                        switch (spinnerComponents.getSelectedItem().toString()){
                            case "Cans":
                                total_cans += Integer.parseInt(etQuantity.getText().toString());
                                setDrawPieChart(total_cans,"Cans");
                                break;
                            case "Glass":
                                total_glass += Integer.parseInt(etQuantity.getText().toString());
                                setDrawPieChart(total_glass,"Glass");
                                break;
                            case "Tetrabriks":
                                total_tetrabriks += Integer.parseInt(etQuantity.getText().toString());
                                setDrawPieChart(total_tetrabriks,"Tetrabriks");
                                break;
                            case "Bottles":
                                total_bottles += Integer.parseInt(etQuantity.getText().toString());
                                setDrawPieChart(total_bottles,"Bottles");
                                break;
                            case "Paperboard":
                                total_paperboard += Integer.parseInt(etQuantity.getText().toString());
                                setDrawPieChart(total_paperboard,"Paperboard");
                                break;
                        }
                    }
                }
            }
        });

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecycled();
            }
        });

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecyclingComponentsActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences preferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

    }

    public void setDrawPieChart(Integer total, String label){

        if(!residues.isEmpty()){
            for(PieEntry p: residues){
                if (p != null && p.getLabel() == label){
                    residues.remove(p);
                    p.setY(total);
                    residues.add(p);
                } else {
                    residues.add(new PieEntry(total.floatValue(),label));
                }
            }
        } else {
            residues.add(new PieEntry(total.floatValue(),label));
        }

        dataSet.setValues(residues);

        pieData.notifyDataChanged();
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        if (!text.equals("Select a residue")){
            Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    public void addRecycled(){
        JSONObject j = new JSONObject();
        try {
            j.put("userName",getIntent().getExtras().getString("userName"));
            j.put("cans",total_cans.toString());
            j.put("bottles",total_bottles.toString());
            j.put("tetrabriks", total_tetrabriks.toString());
            j.put("glass",total_glass.toString());
            j.put("paperboard", total_paperboard.toString());
            j.put("date",new Date());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.path_service) +"reciclados";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, j,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        String username="";
                        try {
                            username = response.getString("userName");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String ToastText = "El reciclado fue almacenado exitosamente";
                        Toast.makeText(RecyclingComponentsActivity.this,ToastText,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RecyclingComponentsActivity.this,error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        ReqQueue.add(req);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
