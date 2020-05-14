package com.android.app.recycling.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Arrays;
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
    private boolean hashEmpty;
    TextView tvTons;
    BottomNavigationView bottomNavigationView;
    Button bSend,bDelete;
    SharedPreferences sharedpreferences;
    Float tons;
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
        bDelete = view.findViewById(R.id.bDelete);
        tvTons = (TextView) view.findViewById(R.id.tvTons);
        tvTons.setVisibility(View.INVISIBLE);
        pieChart.setVisibility(View.INVISIBLE);
        bSend.setVisibility(View.INVISIBLE);
        bDelete.setVisibility(View.INVISIBLE);
        this.actual = sharedViewModel.getResidues();
        tons = new Float(0.0);

        if(!this.actual.isEmpty()){
            pieChartActual();
            bSend.setVisibility(View.VISIBLE);
            bDelete.setVisibility(View.VISIBLE);
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
                        if(!actual.isEmpty()){
                            bSend.setVisibility(View.VISIBLE);
                            bDelete.setVisibility(View.VISIBLE);
                        }
                        tvTons.setVisibility(View.INVISIBLE);

                        pieChartActual();
                        return true;
                    case R.id.nav_historical:
                        getTotalAndDraw();
                        if (historical.isEmpty()){
                        }
                        bSend.setVisibility(View.INVISIBLE);
                        bDelete.setVisibility(View.INVISIBLE);
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

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Set a title for alert dialog
                builder.setTitle("¡Hola!");
                // Show a message on alert dialog
                builder.setMessage("¿Quiere borrar su reciclado actual?");
                // Set the positive button
                builder.setPositiveButton("Si, claro",yesDelete());
                // Set the negative button
                builder.setNegativeButton("No",noDelete());
                // Create the alert dialog
                AlertDialog dialog = builder.create();
                // Finally, display the alert dialog
                dialog.show();
                // Change the alert dialog background color to transparent
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            }
        });

    }
    private Dialog.OnClickListener yesDelete(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                actual.clear();
                Toast.makeText(requireContext(), "Reciclaje Eliminado", Toast.LENGTH_SHORT).show();
                pieChartActual();
                bSend.setVisibility(View.INVISIBLE);
                bDelete.setVisibility(View.INVISIBLE);
            }
        };
    }
    private Dialog.OnClickListener noDelete(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(),"Ok, ¡Sigamos reciclando!",Toast.LENGTH_SHORT).show();
            }
        };
    }
    private void pieChartHistorical() {
        System.out.println(historical.toString());
        if(tons > 0){
            residuesTotals.clear();

            for (HashMap.Entry<String, Integer> h : historical.entrySet()) {
                if(h.getValue() > 0){
                    residuesTotals.add(new PieEntry(h.getValue().floatValue(),translate(h.getKey())));
                }
            }
            description.setText("HISTORICO");
            pieChart.setDescription(description);
            setDataPieChart(residuesTotals);

        } else {
            pieChart.setVisibility(View.INVISIBLE);
            tvTons.setVisibility(View.INVISIBLE);
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
            description.setText("ACTUAL");
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
                            String[] tonsString= res.getString("tons").split("\\.");
                            String beforeComma=tonsString[0];
                            String afterComma=tonsString[1];
                            if(afterComma.length()>3) {
                                afterComma= afterComma.substring(0,2);
                            }
                            String textTons= "Total: "+beforeComma+"."+afterComma+" tons.";
                            tons=Float.parseFloat(res.getString("tons"));
                            tvTons.setText(textTons);
                            tvTons.setVisibility(View.VISIBLE);
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
                        bSend.setVisibility(View.INVISIBLE);
                        bDelete.setVisibility(View.INVISIBLE);
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
