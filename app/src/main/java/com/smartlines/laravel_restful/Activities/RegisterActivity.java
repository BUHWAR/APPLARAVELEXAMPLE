package com.smartlines.laravel_restful.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.smartlines.laravel_restful.R;
import com.smartlines.laravel_restful.Utils.Connectivity;
import com.smartlines.laravel_restful.Utils.MySingleton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private EditText name, email, password, password_confirmation;
    private Button btn_registrar;

    //private String url = String.format("http://%s/android-RESTFUL/public/api/signup", Connectivity.ip());
    private String url = "http://example.smartlines.mx/laravelrest/public/api/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        password_confirmation = (EditText) findViewById(R.id.confirm_password);
        btn_registrar=(Button)findViewById(R.id.btn_registrar);

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSave();
            }
        });
    }

    private void attemptSave() {
        name.setError(null);
        email.setError(null);
        password.setError(null);
        password_confirmation.setError(null);

        String sname = name.getText().toString();
        String semail = email.getText().toString();
        String spassword = password.getText().toString();
        String spasswordc = password_confirmation.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(sname)) {
            name.setError("Porfavor introduzca su nombre");
            focusView = name;
            cancel = true;
        }

        if (TextUtils.isEmpty(semail) || !semail.contains("@")) {
            email.setError("Ingrese un email valido");
            focusView = email;
            cancel = true;
        }

        if (TextUtils.isEmpty(spassword)) {
            password.setError("Porfavor introduzca una contraseña");
            focusView = password;
            cancel = true;
        }

        if (TextUtils.isEmpty(spasswordc)) {
            password_confirmation.setError("Porfavor introduzca una contraseña");
            focusView = password_confirmation;
            cancel = true;
        }

        if (!(spassword.equals(spasswordc))) {
            password_confirmation.setError("Contraseña de confirmacion no coincide");
            focusView = password_confirmation;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            registrar();
        }
    }


    private void registrar() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creando Usuario");
        progressDialog.setMessage("Porfavor espere...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest registerRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Response", response);
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.names().get(0).toString().equals("message")) {
                                Toast.makeText(RegisterActivity.this, "Usuario Creado Exitosamente", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                String semail=jsonObject.getString("email");
                                if(!TextUtils.isEmpty(semail))
                                    email.setError("Este correo ya existe en nuestros registros");
                                //Toast.makeText(RegisterActivity.this, email, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Error al crear usuario", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(LoginActivity.this, "No me puedo comunicar con el servidor", Toast.LENGTH_LONG).show();

                //NetworkResponse networkResponse = error.networkResponse;
                //if (networkResponse != null && networkResponse.statusCode == 421) {
                // HTTP Status Code: 422 Unauthorized
                //email.setError("Este Correo ya existe");
                //}

                // Log the error
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    String errorString = new String(response.data);
                    Log.i("ERROR", errorString);
                    progressDialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("password", password.getText().toString().trim());
                params.put("password_confirmation", password_confirmation.getText().toString().trim());
                return params;
            }

            /** Passing some request headers* */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        registerRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        MySingleton.getmInstance(LoginActivity.this).addToRequestQue(stringRequest);
        MySingleton.getInstance().addToRequestQueue(registerRequest, "loginRequest");
    }

}
