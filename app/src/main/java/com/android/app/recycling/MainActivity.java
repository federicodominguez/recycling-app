package com.android.app.recycling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button bLogin;
    EditText etUsername;
    TextView tvRegisterLink;
    TextInputLayout tilUsername;

    public static final String USER_PREF = "UserPreferences" ;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        etUsername = (EditText) findViewById(R.id.etUsername);
        tilUsername = (TextInputLayout) findViewById(R.id.tilUsername);
        tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        SharedPreferences pref_session = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        //Comprobando si ya hay una sesion abierta por un usuario
        if (pref_session.getBoolean("UserInSession",false)) {
            startActivity(new Intent(MainActivity.this,RecyclingComponentsActivity.class));
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
        final String url = getResources().getString(R.string.path_service)+"personas/"+username;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Si la respuesta es vacia es porque no existe el usuario
                        if (response.isEmpty()) {
                            String ToastText ="El usuario "+username+" no existe";
                            Toast.makeText(MainActivity.this,ToastText,Toast.LENGTH_SHORT).show();
                            etUsername.setText("");
                            tilUsername.setEnabled(true);
                        } else {
                            String ToastText =username+" ha iniciado sesion";
                            Toast.makeText(MainActivity.this,ToastText,Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this, NavDrawerActivity.class);
                            i.putExtra("userName",username);
                            startActivity(i);
                            finish();
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorText= "No existe el usuario";
                Toast.makeText(MainActivity.this,errorText,Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //Antes de salir de la activity registramos la sesion
    @Override
    protected void onPause()
    {
        super.onPause();
        SharedPreferences preferencias = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        String user = etUsername.getText().toString();

        SharedPreferences.Editor editor = preferencias.edit();

        editor.putString("user",user);

        editor.commit();

    }
}
