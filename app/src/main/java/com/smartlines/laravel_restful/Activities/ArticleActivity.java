package com.smartlines.laravel_restful.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smartlines.laravel_restful.Models.Article;
import com.smartlines.laravel_restful.R;
import com.smartlines.laravel_restful.Utils.Connectivity;
import com.smartlines.laravel_restful.Utils.MySingleton;

import java.util.HashMap;
import java.util.Map;

public class ArticleActivity extends AppCompatActivity {

    private EditText title, body;
    private Button btn;
    private String tmpTitle, tmpBody;
    private int id;

    private SharedPreferences prefs;

    //String url = String.format("http://%s/android-RESTFUL/public/api/articles", Connectivity.ip());
    String url = "http://example.smartlines.mx/laravelrest/public/api/articles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
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

        prefs = getSharedPreferences("Preference", Context.MODE_PRIVATE);

        title = (EditText) findViewById(R.id.titulo);
        body = (EditText) findViewById(R.id.body);
        btn = (Button) findViewById(R.id.btn_accion);

        Article article = (Article) getIntent().getSerializableExtra("articulo");

        if (article != null) {
            Toast.makeText(this, article.getId() + "", Toast.LENGTH_LONG).show();
            id = article.getId();
            tmpTitle = article.getTitle();
            tmpBody = article.getBody();
            title.setText(tmpTitle);
            body.setText(tmpBody);
            btn.setText("Editar");
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSave();
            }
        });

    }

    private void attemptSave() {
        title.setError(null);
        body.setError(null);

        String titles = title.getText().toString();
        String bodys = body.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(titles)) {
            title.setError("Porfavor introduzca su contraseña");
            focusView = title;
            cancel = true;
        }

        if (TextUtils.isEmpty(bodys)) {
            body.setError("Introduzca su usuario!");
            focusView = body;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            save(titles, bodys);
        }
    }

    private void save(String title, String body) {
        if (btn.getText().equals("Guardar")) {
            guardar(title, body);
            finish();
        } else {
            if (title.equals(tmpTitle) && body.equals(tmpBody)) {
                finish();
            }
            editar(title, body);
            finish();
        }
    }

    private void guardar(final String title, final String body) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(ArticleActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title.trim());
                params.put("body", body.trim());
                return params;
            }

            /** Passing some request headers* */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                //headers.put("Authorization", "Bearer xxxxxxxxxxxxxxx");
                headers.put("Authorization", prefs.getString("key",""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        MySingleton.getInstance().addToRequestQueue(postRequest, "createRequest");
    }

    private void editar(final String title, final String body) {
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url + "/" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title.trim());
                params.put("body", body.trim());
                return params;
            }

            /** Passing some request headers* */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                //headers.put("Authorization", "Bearer xxxxxxxxxxxxxxx");
                headers.put("Authorization", prefs.getString("key",""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        MySingleton.getInstance().addToRequestQueue(putRequest, "putRequest");
    }
}
