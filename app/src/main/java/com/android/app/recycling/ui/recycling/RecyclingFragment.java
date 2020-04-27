package com.android.app.recycling.ui.recycling;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.android.app.recycling.R;
import com.android.app.recycling.SharedViewModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RecyclingFragment extends Fragment {

    private RecyclingViewModel recyclingViewModel;
    private SharedViewModel sharedViewModel;
    Button bAdd,bSend;
    EditText etCans,etBottles,etGlass,etTetrabriks,etPaperboard;
    private HashMap<String,Integer> residues;
    private RequestQueue ReqQueue;
    SharedPreferences sharedpreferences;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recyclingViewModel = ViewModelProviders.of(this).get(RecyclingViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        residues = new HashMap<String,Integer>();
        ReqQueue = Volley.newRequestQueue(this.getActivity());


        View root = inflater.inflate(R.layout.fragment_recycling, container, false);

        return root;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bAdd = view.findViewById(R.id.bAdd);
        bSend = view.findViewById(R.id.bSend);

        etBottles = view.findViewById(R.id.etBottles);
        etCans = view.findViewById(R.id.etCans);
        etPaperboard = view.findViewById(R.id.etPaperboard);
        etGlass = view.findViewById(R.id.etGlass);
        etTetrabriks = view.findViewById(R.id.etTetrabriks);

        bAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                residues.put("Cans",Integer.parseInt(!etCans.getText().toString().isEmpty() ? etCans.getText().toString() : "0"));
                residues.put("Glass",Integer.parseInt(!etGlass.getText().toString().isEmpty() ? etGlass.getText().toString() : "0"));
                residues.put("Paperboard",Integer.parseInt(!etPaperboard.getText().toString().isEmpty() ? etPaperboard.getText().toString() : "0"));
                residues.put("Tetrabriks",Integer.parseInt(!etTetrabriks.getText().toString().isEmpty() ? etTetrabriks.getText().toString() : "0"));
                residues.put("Bottles",Integer.parseInt(!etBottles.getText().toString().isEmpty() ? etBottles.getText().toString() : "0"));
                sharedViewModel.setResidues(residues);

                Toast.makeText(requireContext(), "Reciclaje agregado", Toast.LENGTH_SHORT).show();

            }
        });
        
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

    private void sendRecycled() {

        sharedpreferences = this.getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userName = sharedpreferences.getString("user","");

        JSONObject j = new JSONObject();
        try {
            j.put("userName",userName);
            j.put("cans",sharedViewModel.getResidues().getValue().get("Cans").toString());
            j.put("bottles",sharedViewModel.getResidues().getValue().get("Bottles").toString());
            j.put("tetrabriks", sharedViewModel.getResidues().getValue().get("Tetrabriks").toString());
            j.put("glass",sharedViewModel.getResidues().getValue().get("Glass").toString());
            j.put("paperboard", sharedViewModel.getResidues().getValue().get("Paperboard").toString());
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
                Toast.makeText(requireContext(),error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        ReqQueue.add(req);
    }
}
