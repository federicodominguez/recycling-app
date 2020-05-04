package com.android.app.recycling.ui.account;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.recycling.MainActivity;
import com.android.app.recycling.NavDrawerActivity;
import com.android.app.recycling.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Account#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Account extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button blogout;
    EditText etFirstName, etLastName, etEmail, etAddress;
    TextInputLayout tilFirstName, tilLastName, tilEmail, tilAddress;
    TextView tvUsername;
    private boolean userExist;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Account() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Account.
     */
    // TODO: Rename and change types and number of parameters
    public static Account newInstance(String param1, String param2) {
        Account fragment = new Account();
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
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_account, container, false);
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilFirstName = view.findViewById(R.id.tilAccFN);
        etFirstName = view.findViewById(R.id.etAccFN);
        tilLastName = view.findViewById(R.id.tilAccLN);
        etLastName = view.findViewById(R.id.etAccLN);
        tilAddress = view.findViewById(R.id.tilAccAd);
        etAddress = view.findViewById(R.id.etAccAd);
        tilEmail = view.findViewById(R.id.tilAccEm);
        etEmail = view.findViewById(R.id.etAccEm);
        tvUsername = view.findViewById(R.id.tVUN);
        blogout =  (Button) view.findViewById(R.id.btnLogout);
        getUserData();
        blogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // SharedPreferences preferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                //SharedPreferences.Editor editor = preferences.edit();
                //editor.clear();
                //editor.commit();
                //startActivity(new Intent(getActivity(),MainActivity.class));
                //getActivity().finish();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Set a title for alert dialog
                builder.setTitle("¡Hola!");
                // Show a message on alert dialog
                builder.setMessage("¿Quiere cerrar sesión?");
                // Set the positive button
                builder.setPositiveButton("Si, claro",yesLogout());
                // Set the negative button
                builder.setNegativeButton("No", notLogout());
                // Create the alert dialog
                AlertDialog dialog = builder.create();
                // Finally, display the alert dialog
                dialog.show();
                // Change the alert dialog background color to transparent
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

            }
        });


    }

    public Dialog.OnClickListener notLogout(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(),"Ok, ¡Sigamos reciclando!",Toast.LENGTH_LONG).show();
            }
        };
    }

    public Dialog.OnClickListener yesLogout(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(),"Cerrando Sesion",Toast.LENGTH_LONG).show();
                SharedPreferences preferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("UserInSession",false);
                editor.commit();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        };
    }

    private void llenarCampos(String request){

        try {
            JSONObject person = new JSONObject(request);
            tvUsername.setText(person.getString("userName"));
            etFirstName.setText(person.getString("firstName"));
            etLastName.setText(person.getString("lastName"));
            etAddress.setText(person.getString("address"));
            etEmail.setText(person.getString("mail"));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }
    private void getUserData(){
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(getContext());
            SharedPreferences sharedpreferences = this.getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            final String username = sharedpreferences.getString("user","");
            final String url = getResources().getString(R.string.path_service)+"personas/"+username;

            // Request a string response from the provided URL.
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Si la respuesta es vacia es porque no existe el usuario
                            if (response.isEmpty()) {
                                String ToastText ="El usuario "+username+" no existe";
                                Toast.makeText(getContext(),ToastText,Toast.LENGTH_SHORT).show();
                            } else {
                                llenarCampos(response);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorText= "Error en la peticion";
                    Toast.makeText(getContext(),errorText,Toast.LENGTH_SHORT).show();
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);

    }
}
