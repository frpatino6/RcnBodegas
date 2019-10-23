package com.rcnbodegas.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.rcnbodegas.Global.ProductionAdapter;
import com.rcnbodegas.Global.onRecyclerProductionListItemClick;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.ProductionViewModel;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ProductionListActivity extends AppCompatActivity {


    private View mIncidenciasFormView;
    private View mProgressView;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<ProductionViewModel> data;
    private ProductionAdapter adapter;
    private ArrayList<ProductionViewModel> sortEmpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_list);
        ((AppCompatActivity) this).getSupportActionBar().setTitle(getString(R.string.title_bar_production));
        InitializeControls();
        asyncListProductions();

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
            public boolean onQueryTextSubmit(String s) {

                //clear the previous data in search arraylist if exist

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                FilterListView(s);
                return true;
            }
        });


        return true;

    }

    private void InitializeControls() {

        mIncidenciasFormView = findViewById(R.id.production_recycler_view);
        mProgressView = findViewById(R.id.production_progress);
        recyclerView = (RecyclerView) findViewById(R.id.production_recycler_view);
        recyclerView.setHasFixedSize(true);



        layoutManager = new LinearLayoutManager(ProductionListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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

    private void shwoMessage(String res) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ProductionListActivity.this);

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

    private void asyncListProductions() {

        String responsable=GlobalClass.getInstance().getQueryByInventory()? GlobalClass.getInstance().getIdSelectedWareHouseInventory(): GlobalClass.getInstance().getIdSelectedWareHouseWarehouse();


        String urlIncidencias = GlobalClass.getInstance().getUrlServices() + "Inventory/GetListProduction/" + responsable;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();
        showProgress(true);
        client.get(urlIncidencias, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<List<ProductionViewModel>> token = new TypeToken<List<ProductionViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            data = gson.fromJson(res, token.getType());
                            adapter = new ProductionAdapter(data, new onRecyclerProductionListItemClick() {
                                @Override
                                public void onClick(ProductionViewModel result) {
                                    final Intent _data = new Intent();
                                    _data.putExtra("productionName", result.getProductionName());
                                    _data.putExtra("productionId", result.getProductionCode().toString());

                                    setResult(RESULT_OK, _data);

                                    finish();
                                }
                            });
                            recyclerView.setAdapter(adapter);
                            showProgress(false);

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        shwoMessage(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        showProgress(false);

                    }
                }
        );
    }

    private void FilterListView(String query) {

        //mStatusView.setText("Query = " + query + " : submitted");
        try {
            Filter<ProductionViewModel, String> filter = new Filter<ProductionViewModel, String>() {
                public boolean isMatched(ProductionViewModel object, String text) {

                    boolean result = false;


                    result = object.getProductionName().toString().toLowerCase().contains(String.valueOf(text));


                    if (result)
                        return true;
                    else
                        return false;
                }
            };


            sortEmpList = (ArrayList<ProductionViewModel>) new FilterList().filterList(data, filter, query);

            adapter = new ProductionAdapter(sortEmpList, new onRecyclerProductionListItemClick() {
                @Override
                public void onClick(ProductionViewModel result) {
                    final Intent _data = new Intent();
                    _data.putExtra("productionName", result.getProductionName());
                    _data.putExtra("productionId", result.getProductionCode().toString());

                    setResult(RESULT_OK, _data);

                    finish();
                }
            });
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
