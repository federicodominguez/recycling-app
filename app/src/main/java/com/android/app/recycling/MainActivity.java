package com.android.app.recycling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    Button bLogin;
    EditText etUsername;
    TextView tvRegisterLink;
    TextInputLayout tilUsername;
    SharedPreferences pref_session;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUsername = (EditText) findViewById(R.id.etUsername);
        tilUsername = (TextInputLayout) findViewById(R.id.tilUsername);
        tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        address="";
        pref_session = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        //Comprobando si ya hay una sesion abierta por un usuario
        if (pref_session.getBoolean("UserInSession",false)) {
            startActivity(new Intent(MainActivity.this,NavDrawerActivity.class));
            finish();
        } else {
            tvRegisterLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                    finish();
                }
            });


            bLogin = (Button) findViewById(R.id.bLogin);
            bLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validate(etUsername, tilUsername, R.string.err_UN)) {
                        requestLogin();
                    }

                }
            });
        }
    }
    private boolean validate(EditText et, TextInputLayout til, int errorString){
        if ( et.getText().toString().trim().isEmpty()){
            til.setError(getString(errorString));
            return false;
        } else {
            til.setEnabled(false);
        }
        return  true;
    }
    private void requestLogin(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        final String username = etUsername.getText().toString().trim();
        final String url = getResources().getString(R.string.path_service)+"usuarios/"+username;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            try {
                                JSONObject person = new JSONObject(response);
                                address = person.getString("address");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                            String ToastText =username+" ha iniciado sesion";
                            Toast.makeText(MainActivity.this,ToastText,Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = pref_session.edit();
                            editor.putBoolean("UserInSession",true);
                            editor.commit();
                            Intent i = new Intent(MainActivity.this, NavDrawerActivity.class);
                            i.putExtra("userName",username);
                            startActivity(i);
                            finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseVolleyError(error);
                etUsername.setText("");
                tilUsername.setEnabled(true);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.optString("message");
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }
    }
    //Antes de salir de la activity registramos la sesion
    @Override
    protected void onPause()
    {
        super.onPause();
        String user = etUsername.getText().toString();
        SharedPreferences.Editor editor = pref_session.edit();
        editor.putString("user",user);
        editor.putString("address",address);
        editor.commit();

    }
}
