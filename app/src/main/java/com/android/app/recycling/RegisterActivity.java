package com.android.app.recycling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    Button bRegister;
    EditText etFirstName, etLastName, etEmail, etUsername, etAddress;
    TextInputLayout tilFirstName, tilLastName, tilEmail, tilUsername, tilAddress;
    private RequestQueue ReqQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etAddress = (EditText) findViewById(R.id.etAddress);

        tilFirstName = (TextInputLayout) findViewById(R.id.tilFirstName);
        tilLastName = (TextInputLayout) findViewById(R.id.tilLastName);
        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        tilUsername = (TextInputLayout) findViewById(R.id.tilUsername);
        tilAddress = (TextInputLayout) findViewById(R.id.tilAddress);

        bRegister = (Button) findViewById(R.id.bRegister);
        ReqQueue = Volley.newRequestQueue(RegisterActivity.this);


        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm();
            }
        });
    }
    private void validateForm(){
        //Si se anidan-> probar que tire error en todos los campos si se quiere guardar
        if(!validate(etFirstName,tilFirstName,R.string.err_FN)){
            return;
        }
        if(!validate(etLastName,tilLastName,R.string.err_LN)){
            return;
        }
        if(!validate(etUsername,tilUsername,R.string.err_UN)){
            return;
        }
        if(!validate(etAddress,tilAddress,R.string.err_A)){
            return;
        }
        if(!validate(etEmail,tilEmail,R.string.err_E)){
            return;
        }
        registrar();
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

    private void registrar()
    {
        //CREO OBJETO JSON
        JSONObject j = new JSONObject();
        try {
            j.put("firstName",etFirstName.getText().toString());
            j.put("lastName",etLastName.getText().toString());
            j.put("userName", etUsername.getText().toString());
            j.put("address",etAddress.getText().toString());
            j.put("mail", etEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // CREO LA PETICION PARA ENVIAR AL SERVIDOR
        String url = getResources().getString(R.string.path_service) +"personas";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, j,
                new Response.Listener<JSONObject>() {

                    //SI LA PETICION FUE EXITOSA, SE PASA AL MENU PPAL DEL USUARIO.
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        String username="";
                        try {
                            username = response.getString("userName");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String ToastText = "El usuario "+username+" fue creado exitosamente";
                        Toast.makeText(RegisterActivity.this,ToastText,Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    //EN CASO DE ERROR, SE VUELVE A HABILITAR EL BOTON
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this,error.toString(), Toast.LENGTH_SHORT).show();
                bRegister.setEnabled(true);
            }
        });
        ReqQueue.add(req);
    }
}
