package com.rcnbodegas.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.WareHouseAdapter;
import com.rcnbodegas.Global.onRecyclerWarehouseListItemClick;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.WareHouseViewModel;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class WareHouseListActivity extends AppCompatActivity {


    private WareHouseAdapter adapter;
    private ArrayList<WareHouseViewModel> data;
    private LinearLayoutManager layoutManager;
    private View mIncidenciasFormView;
    private View mProgressView;
    private RecyclerView recyclerView;
    private ArrayList<WareHouseViewModel> sortEmpList;

    private void FilterListView(String query) {

        //mStatusView.setText("Query = " + query + " : submitted");
        try {
            Filter<WareHouseViewModel, String> filter = new Filter<WareHouseViewModel, String>() {
                public boolean isMatched(WareHouseViewModel object, String text) {

                    boolean result = false;


                    result = object.getWareHouseName().toString().toLowerCase().contains(String.valueOf(text));

                    if (result)
                        return true;
                    else
                        return false;
                }
            };

            sortEmpList = (ArrayList<WareHouseViewModel>) new FilterList().filterList(data, filter, query);

            adapter = new WareHouseAdapter(sortEmpList, new onRecyclerWarehouseListItemClick() {
                @Override
                public void onClick(WareHouseViewModel result) {
                    final Intent _data = new Intent();
                    _data.putExtra("wareHouseName", result.getWareHouseName());
                    _data.putExtra("wareHouseId", result.getId());

                    setResult(RESULT_OK, _data);

                    finish();
                }
            });
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void InitializeControls() {

        mIncidenciasFormView = findViewById(R.id.company_recycler_view);
        mProgressView = findViewById(R.id.company_progress);
        recyclerView = (RecyclerView) findViewById(R.id.company_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(WareHouseListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void asyncListWareHouse() {


        String urlIncidencias = GlobalClass.getInstance().getUrlServices() + "WareHouse/GetLisWareHouse/" + GlobalClass.getInstance().getUserName() + "/" + GlobalClass.getInstance().getIdSelectedCompanyInventory();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();
        showProgress(true);
        client.get(urlIncidencias, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        showMessage(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        showProgress(false);

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<List<WareHouseViewModel>> token = new TypeToken<List<WareHouseViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            data = gson.fromJson(res, token.getType());
                            adapter = new WareHouseAdapter(data, new onRecyclerWarehouseListItemClick() {
                                @Override
                                public void onClick(WareHouseViewModel result) {
                                    final Intent _data = new Intent();
                                    _data.putExtra("wareHouseName", result.getWareHouseName());
                                    _data.putExtra("wareHouseId", result.getId());

                                    setResult(RESULT_OK, _data);

                                    finish();
                                }
                            });
                            recyclerView.setAdapter(adapter);
                            GlobalClass.getInstance().setListWareHouseGlobal(data);
                            showProgress(false);

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();

                        }
                    }
                }
        );
    }

    private void returnListOffLine() {


        SharedPreferences pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
        String res = pref.getString("key_list_warehouse", "");
        TypeToken<List<WareHouseViewModel>> token = new TypeToken<List<WareHouseViewModel>>() {
        };
        Gson gson = new GsonBuilder().create();
        // Define Response class to correspond to the JSON response returned
        data = gson.fromJson(res, token.getType());

        if (data != null) {
            adapter = new WareHouseAdapter(data, new onRecyclerWarehouseListItemClick() {
                @Override
                public void onClick(WareHouseViewModel result) {
                    final Intent _data = new Intent();
                    _data.putExtra("wareHouseName", result.getWareHouseName());
                    _data.putExtra("wareHouseId", result.getId());

                    setResult(RESULT_OK, _data);

                    finish();
                }
            });
            recyclerView.setAdapter(adapter);
            GlobalClass.getInstance().setListWareHouseGlobal(data);
        } else {
            showMessage(getString(R.string.message_not_sync_data));

        }
    }

    private void showMessage(String res) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(WareHouseListActivity.this);

        dlgAlert.setMessage(res);
        dlgAlert.setTitle(getString(R.string.app_name));
        //dlgAlert.setPositiveButton(getString(R.string.Texto_Boton_Ok), null);
        dlgAlert.setPositiveButton(R.string.Texto_Boton_Ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close
                // current activity

            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mIncidenciasFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mIncidenciasFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIncidenciasFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mIncidenciasFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);
        ((AppCompatActivity) this).getSupportActionBar().setTitle(getString(R.string.title_bar_warehouse));
        InitializeControls();
        //asyncListWareHouse();

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }

        if (GlobalClass.getInstance().isNetworkAvailable())
            asyncListWareHouse();
        else
            returnListOffLine();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_warehouselist, menu);

        MenuItem search_item = menu.findItem(R.id.search_warehouse);

        SearchView searchView = (SearchView) search_item.getActionView();
        searchView.setFocusable(false);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextChange(String s) {
                FilterListView(s);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {

                //clear the previous data in search arraylist if exist

                return false;
            }
        });


        return true;

    }
}
