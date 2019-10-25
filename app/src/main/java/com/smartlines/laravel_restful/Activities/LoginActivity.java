package com.smartlines.laravel_restful.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.smartlines.laravel_restful.R;
import com.smartlines.laravel_restful.Utils.Connectivity;
import com.smartlines.laravel_restful.Utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences prefs;

    private EditText mUsuarioView;
    private EditText mPasswordView;

    //private String url = String.format("http://%s/android-RESTFUL/public/api/login", Connectivity.ip());
    private String url = "http://example.smartlines.mx/laravelrest/public/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences("Preference", MODE_PRIVATE);

        mUsuarioView = (EditText) findViewById(R.id.usuario);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.btn_entrar);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        mUsuarioView.setError(null);
        mPasswordView.setError(null);

        String user = mUsuarioView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Porfavor introduzca su contraseña");
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(user)) {
            mUsuarioView.setError("Introduzca su usuario!");
            focusView = mUsuarioView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            logIn();
            //irMain();
            //guardarPreference();
        }
    }

    private void logIn() {
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Response", response);
                            JSONObject jsonObject = new JSONObject(response);
//                            if (jsonObject.names().get(0).toString().equals("logged")) {
//                                guardarPreference();
//                                irMain();
//                            } else {
//                                mPasswordView.setError("Contraseña Erronea");
//                            }
                            if (jsonObject.names().get(0).toString().equals("message")) {
                                mPasswordView.setError("Contraseña Erronea y/o Correo Incorrecto");
                            } else {
                                String key = jsonObject.getString("token_type") + " " + jsonObject.getString("access_token");
                                guardarPreference(key);
                                irMain();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Usuario y/o Contraseña Erroneas!", Toast.LENGTH_LONG).show();
                            //Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(LoginActivity.this, "No me puedo comunicar con el servidor", Toast.LENGTH_LONG).show();

                //NetworkResponse networkResponse = error.networkResponse;
                //if (networkResponse != null && networkResponse.statusCode == 401) {
                    // HTTP Status Code: 401 Unauthorized
                   //mPasswordView.setError("Contraseña Erronea");
                //}

                // Log the error
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    String errorString = new String(response.data);
                    Log.i("ERROR", errorString);
                    mPasswordView.setError("Contraseña Erronea y/o Correo Incorrecto");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", mUsuarioView.getText().toString().trim());
                params.put("password", mPasswordView.getText().toString().trim());
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

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//        MySingleton.getmInstance(LoginActivity.this).addToRequestQue(stringRequest);
        MySingleton.getInstance().addToRequestQueue(loginRequest, "loginRequest");
    }


    private void irMain() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void guardarPreference(String key) throws JSONException {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user", mUsuarioView.getText().toString());
        editor.putString("pass", mPasswordView.getText().toString());
        editor.putString("key", key);
        editor.apply();
    }

    public void registrar(View view) {
        //Toast.makeText(this, "Creare tu usuario", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

//    public void recuperar(View view) {
//        //Toast.makeText(this, "Recuperare contraseña", Toast.LENGTH_SHORT).show();
//        correo();
//    }
//
//    boolean cancel = false;
//    private String email;
//
//    public void correo() {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        View dialogView = getLayoutInflater().inflate(R.layout.alert_correo, null);
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.setPositiveButton("Aceptar", null);
//        dialogBuilder.setNegativeButton("Cerrar", null);
//
//        final EditText etemail = dialogView.findViewById(R.id.email);
//
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.setTitle("Desea recuperar su contraseña?");
//        alertDialog.setCancelable(false);
//        alertDialog.show();
//        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//        theButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                email = etemail.getText().toString();
//                if (TextUtils.isEmpty(email) || !email.contains("@")) {
//                    etemail.setError("Debe especificar un email valido");
//                    cancel = true;
//                    view = etemail;
//                }
//                if (cancel)
//                    view.requestFocus();
//                else {
//                    alertDialog.dismiss();
//                    //inicia_respaldo();
//                }
//            }
//        });
//        Button back = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//            }
//        });
//
//    }
}

