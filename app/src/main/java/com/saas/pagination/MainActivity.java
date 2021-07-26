package com.saas.pagination;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<MainData> dataArrayList = new ArrayList<>();

    MainAdapter adapter;
    public int page = 1;
    EditText search;
TextView total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidNetworking.initialize(getApplicationContext());

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                // .addNetworkInterceptor(new StethoInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

        AndroidNetworking.setParserFactory(new JacksonParserFactory());


        nestedScrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.recycle_view);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        search = findViewById(R.id.search);
        total = findViewById(R.id.total);
        total.setText("متاح : "+dataArrayList.size()+"");

        adapter = new MainAdapter(MainActivity.this, dataArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    private void getData(int page, String url, String query) {

        AndroidNetworking.get(url + "search.json?engine=google_maps&safe=active&start=" + page + "&type=search&ll=%4040.7455096%2C-74.0083012%2C15.1z&api_key=a89f39dd604227743dae5d1107484ab2b5826cc8552b7dcf974ed479512a9f11")
                .addQueryParameter("q", query)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() != 0) {
                            Log.d("errorapi", "response: " + response);

                            progressBar.setVisibility(View.GONE);
                            try {

                                parseResult(response.getJSONArray("local_results"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                try {
                                    Toast.makeText(MainActivity.this, response.getString("error"), Toast.LENGTH_LONG).show();
                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                }
                                Log.d("errorapi", "error1: " + e.getMessage());

                            }

                        } else {
                            Toast.makeText(MainActivity.this, "لا يوجد داتا !", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("errorapi", "error: " + anError.getErrorBody());
                        Toast.makeText(MainActivity.this, "" + anError.getErrorBody(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });


    }


    private void parseResult(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                MainData data = new MainData();
                data.setTitle(object.getString("title"));
                data.setPhone(object.getString("phone").replace("(", "").replace(")", "").replace(" ", "").replace("-", ""));
                data.setAddress(object.getString("address"));
                data.setWebsite(object.getString("website"));
                try {
                    data.setRating(object.getDouble("rating"));
                } catch (Exception e) {
                    data.setRating(0.0);

                }
                data.setReviews(object.getInt("reviews"));
                dataArrayList.add(data);
                total.setText("متاح : "+dataArrayList.size()+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new MainAdapter(MainActivity.this, dataArrayList);
            recyclerView.setAdapter(adapter);
            total.setText("متاح : "+dataArrayList.size()+"");

        }
    }

    public void search(View view) {
        progressBar.setVisibility(View.VISIBLE);
        page = 0;
        dataArrayList.clear();

        getData(page, "https://serpapi.com/", search.getText().toString().trim());
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                page += 20;
                progressBar.setVisibility(View.VISIBLE);
                getData(page, "https://serpapi.com/", search.getText().toString().trim());
            }

        });

    }
}