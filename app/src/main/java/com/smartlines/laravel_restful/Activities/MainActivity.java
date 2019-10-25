package com.smartlines.laravel_restful.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smartlines.laravel_restful.Adapter.RecyclerAdapter;
import com.smartlines.laravel_restful.Models.Article;
import com.smartlines.laravel_restful.R;
import com.smartlines.laravel_restful.Utils.Connectivity;
import com.smartlines.laravel_restful.Utils.MySingleton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.smartlines.laravel_restful.Utils.Connectivity.setConnectivityReceiver;

public class MainActivity extends AppCompatActivity implements Connectivity.ConnectivityReceiverListener {

    private RecyclerView rv;
    ArrayList<Article> articles;
    private RecyclerAdapter adapter;

    Set<String> s = new HashSet<>();
    ArrayAdapter<String> sugerenciasAdapter;
    ArrayList<String> sugerenciasList;

    private ProgressDialog progressDialog;
    private SearchView searchView;
    public static TextView tv;

    private Connectivity connectivity;

    private SharedPreferences prefs;

//    String url = String.format("http://%s/android-RESTFUL/public/api/articles", Connectivity.ip());
//    String url2 = String.format("http://%s/android-RESTFUL/public/api/logout", Connectivity.ip());

    String url ="http://example.smartlines.mx/laravelrest/public/api/articles";
    String url2 = "http://example.smartlines.mx/laravelrest/public/api/logout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("Preference", Context.MODE_PRIVATE);

        connectivity = new Connectivity();

        tv = (TextView) findViewById(R.id.tv);
        rv = (RecyclerView) findViewById(R.id.recyclerview);
        articles = new ArrayList<>();
        sugerenciasList = new ArrayList<>();
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        DefaultItemAnimator df = new DefaultItemAnimator();
        df.setAddDuration(1000);
        rv.setItemAnimator(df);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                llm.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        articles.clear();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Consultando Articulos");
        progressDialog.setMessage("Porfavor espere...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        MySingleton.getInstance().addToRequestQueue(jsonObjReq, "headerRequest");

        registerReceiver(connectivity, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        setConnectivityReceiver(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) item.getActionView();

//        final AutoCompleteTextView searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
//        searchAutoComplete.setHint("Buscar");
//        //searchAutoComplete.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.ic_search, 0, 0, 0);
//        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);
//
//        searchAutoComplete.setAdapter(sugerenciasAdapter);
//        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                searchView.setQuery(searchAutoComplete.getAdapter().getItem(i).toString(), true);
//                searchView.clearFocus();
//            }
//        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Cerrando Sesion", Toast.LENGTH_LONG).show();
            MySingleton.getInstance().addToRequestQueue(jsonObjReq2, "sesionRequest");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    int ide, pos;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

//        menu.setHeaderTitle("Select The Action");
//        menu.add(0, v.getId(), 0, "Call");//groupId, itemId, order, title
//        menu.add(0, v.getId(), 0, "SMS");

        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose your option");
        getMenuInflater().inflate(R.menu.opciones_menu, menu);

        ide = adapter.mFilteredList.get(rv.getChildAdapterPosition(v)).getId();
        pos = rv.getChildAdapterPosition(v);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_1:
                Toast.makeText(this, "Editar " + ide, Toast.LENGTH_SHORT).show();
//              Toast.makeText(this, "Option 1 selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("id", ide);
                intent.putExtra("articulo", adapter.mFilteredList.get(pos));
                startActivity(intent);
                return true;
            case R.id.option_2:
                Toast.makeText(this, "Eliminar " + ide, Toast.LENGTH_SHORT).show();

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿ Al eliminar este articulo ya no podra recuperarlo ?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        adapter.borrar(pos);
                        //adapter.borrararticle(ide);
                        delete(ide);
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();

//              Toast.makeText(this, "Option 2 selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void delete(final int id) {
        StringRequest dr = new StringRequest(Request.Method.DELETE, url +"/"+ id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        Toast.makeText(MainActivity.this, "articulo eliminado!", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.
                        Toast.makeText(MainActivity.this, "ERROR AL ELIMINAR ARTICULO", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
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
        MySingleton.getInstance().addToRequestQueue(dr, "deleteRequest");
    }

    JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
            url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i("IVANAAA", response.toString());

                      Gson gson = new Gson();
                      Type type = new TypeToken<ArrayList<Article>>() {}.getType();
                      articles=gson.fromJson(response.toString(), type);

//                    articles = Arrays.asList(gson.fromJson(response.toString(), Article[].class));

//                    try {
//                        for (int i = 0; i < response.length(); i++) {
//                            JSONObject articleObject = response.getJSONObject(i);
////                            articles.add(new Article(
////                                    articleObject.getInt("id"),
////                                    articleObject.getString("title"),
////                                    articleObject.getString("body")
////                            ));
//                            articles.add(gson.fromJson(articleObject.toString(), Article.class));
//                        }

                        for (int i = 0; i < articles.size(); i++) {
                            s.add(articles.get(i).getTitle());
                        }
                        for (int i = 0; i < articles.size(); i++) {
                            s.add(articles.get(i).getBody());
                        }
                        sugerenciasList.addAll(s);
                        sugerenciasAdapter = new ArrayAdapter<String>(MainActivity.this,
                                android.R.layout.simple_dropdown_item_1line, sugerenciasList);

                        adapter = new RecyclerAdapter(MainActivity.this, articles);
                        rv.setAdapter(adapter);
                        adapter.setOnItemClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, adapter.mFilteredList.get(rv.getChildAdapterPosition(v)).getId() + "", Toast.LENGTH_LONG).show();
                            }
                        });
                        adapter.setOnCreateContextMenuListener(MainActivity.this);

                        progressDialog.dismiss();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Failure Callback
                    Log.e("ERRORJSON", error.toString());
                    Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }) {
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

    JsonObjectRequest jsonObjReq2 = new JsonObjectRequest(Request.Method.GET,
            url2, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("Response", response.toString());
                    removeSharedPreference();
                    logOut();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Failure Callback
                    Log.e("ERRORCERRARSESION", error.toString());
                    Toast.makeText(MainActivity.this, "ERRORCERRARSESION", Toast.LENGTH_SHORT).show();
                }
            }) {
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

    public void showSnack(boolean isConnected) {
        if (isConnected) {
           //Snackbar.make(rv, "Conneccion exitosa", Snackbar.LENGTH_LONG).show();
        } else {
            progressDialog.dismiss();
            Snackbar.make(rv, "No hay conexion", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Verificar Conexion", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    }).setActionTextColor(Color.RED)
                    .show();
        }
    }

    private void logOut() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void removeSharedPreference() {
        prefs.edit().clear().apply();
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnack(isConnected);
    }
}
