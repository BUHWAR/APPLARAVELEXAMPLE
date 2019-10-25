//package com.smartlines.laravel_restful.Providers;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.smartlines.laravel_restful.Activities.MainActivity;
//import com.smartlines.laravel_restful.Adapter.RecyclerAdapter;
//import com.smartlines.laravel_restful.Models.Article;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
/*
    CLASES AUXILIAR PARA FUNCIONES REQUEST VOLLEY

 */
//public class ArticlesProvider {
//    private Context context;
//    private ProgressDialog progressDialog;
//    ArrayList<Article> articles;
//
//    String urlindex="http://192.168.0.2/android-RESTFUL/public/api/articles";
//
//    public ArticlesProvider(Context context,ProgressDialog progressDialog){
//        this.context=context;
//        this.progressDialog=progressDialog;
//        this.articles=new ArrayList<>();
//    }
//
//    //URL of the request we are sending
//    //String url = "api.openweathermap.org/data/2.5/weather?q=London";
//    String url = "http://192.168.0.2/android-RESTFUL/public/api/articles";
//    /*
//     * JsonObjectRequest takes in five paramaters
//     * Request Type - This specifies the type of the request eg: GET,POST
//     *
//     * URL - This String param specifies the Request URL
//     *
//     * JSONObject - This parameter takes in the POST parameters."null" in
//     * case of GET request.
//     *
//     * Listener -This parameter takes in a implementation of Response.Listener()
//     * interface which is invoked if the request is successful
//     *
//     * Listener -This parameter takes in a implementation of Error.Listener()
//     * interface which is invoked if any error is encountered while processing
//     * the request
//     */
//    public JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
//            urlindex, null,
//            new Response.Listener<JSONArray>() {
//                @Override
//                public void onResponse(JSONArray response) {
//                    Log.i("IVANAAA", response.toString());
//                    try {
//                        for (int i = 0; i < response.length(); i++) {
//                            JSONObject articleObject = response.getJSONObject(i);
//                            articles.add(new Article(
//                                    Integer.parseInt(articleObject.getString("id")),
//                                    articleObject.getString("title"),
//                                    articleObject.getString("body")
//                            ));
//                        }
////                        for (int i = 0; i < articles.size(); i++) {
////                            s.add(articles.get(i).getTitle());
////                        }
////                        for (int i = 0; i < articles.size(); i++) {
////                            s.add(articles.get(i).getBody());
////                        }
////                        sugerenciasList.addAll(s);
////                        sugerenciasAdapter = new ArrayAdapter<String>(MainActivity.this,
////                                android.R.layout.simple_dropdown_item_1line, sugerenciasList);
////
////                        adapter = new RecyclerAdapter(MainActivity.this, articles);
////                        rv.setAdapter(adapter);
////                        adapter.setOnItemClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View v) {
////                                Toast.makeText(MainActivity.this, adapter.mFilteredList.get(rv.getChildAdapterPosition(v)).getId() + "", Toast.LENGTH_LONG).show();
////                            }
////                        });
////                        adapter.setOnCreateContextMenuListener(MainActivity.this);
//
//                        progressDialog.dismiss();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //Failure Callback
//                    Log.e("ERRORJSON", error.getMessage());
//                    Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
//                }
//            });
//
////    {
////        @Override
////        protected Map<String, String> getParams() throws AuthFailureError {
////            Map<String, String> params = new HashMap<>();
////            //Llenar Revision
////            params.put("title", "");
////            params.put("description", "");
////            return params;
////        }
////        /** Passing some request headers* */
////        @Override
////        public Map<String,String> getHeaders() throws AuthFailureError {
////            Map<String,String> headers = new HashMap<>();
////            headers.put("Content-Type", "application/json");
////            headers.put("Authorization", "Bearer xxxxxxxxxxxxxxx");
////            return headers;
////        }
////
////    };
//// Adding the request to the queue along with a unique string tag
////MyApplication.getInstance().addToRequestQueue(jsonObjectReq, "headerRequest");
//}
