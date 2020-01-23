package com.rcnbodegas.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import com.rcnbodegas.Global.ResponsibleAdapter;
import com.rcnbodegas.Global.onRecyclerResponsibleListItemClick;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.ResponsibleViewModel;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class WarehouseUserActivity extends AppCompatActivity {


    private ResponsibleAdapter adapter;
    private ArrayList<ResponsibleViewModel> data;
    private LinearLayoutManager layoutManager;
    private View mIncidenciasFormView;
    private View mProgressView;
    private RecyclerView recyclerView;
    private ArrayList<ResponsibleViewModel> sortEmpList;

    private void FilterListByTipoBodega(String query) {

        //mStatusView.setText("Query = " + query + " : submitted");
        try {
            Filter<ResponsibleViewModel, String> filter = new Filter<ResponsibleViewModel, String>() {
                public boolean isMatched(ResponsibleViewModel object, String text) {

                    boolean result = false;


                    result = object.getTipoBodega().toString().toLowerCase().contains(String.valueOf(text));

                    if (result)
                        return true;
                    else
                        return false;
                }
            };

            data = (ArrayList<ResponsibleViewModel>) new FilterList().filterList(data, filter, query);

            adapter = new ResponsibleAdapter(sortEmpList, new onRecyclerResponsibleListItemClick() {
                @Override
                public void onClick(ResponsibleViewModel result) {
                    final Intent _data = new Intent();
                    _data.putExtra("responsibleWarehouseName", result.getName());
                    _data.putExtra("responsibleWarehouseId", result.getId().toString());

                    setResult(RESULT_OK, _data);

                    finish();
                }
            });
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void FilterListView(String query) {

        //mStatusView.setText("Query = " + query + " : submitted");
        try {
            Filter<ResponsibleViewModel, String> filter = new Filter<ResponsibleViewModel, String>() {
                public boolean isMatched(ResponsibleViewModel object, String text) {

                    boolean result = false;

                    result = object.getName().toString().toLowerCase().contains(String.valueOf(text));

                    if (result)
                        return true;
                    else
                        return false;
                }
            };

            data = (ArrayList<ResponsibleViewModel>) new FilterList().filterList(data, filter, query);

            adapter = new ResponsibleAdapter(data, new onRecyclerResponsibleListItemClick() {
                @Override
                public void onClick(ResponsibleViewModel result) {
                    final Intent _data = new Intent();
                    _data.putExtra("responsibleWarehouseName", result.getName());
                    _data.putExtra("responsibleWarehouseId", result.getId().toString());

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

        mIncidenciasFormView = findViewById(R.id.responsible_recycler_view);
        mProgressView = findViewById(R.id.responsible_progress);
        recyclerView = (RecyclerView) findViewById(R.id.responsible_recycler_view);
        recyclerView.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(WarehouseUserActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void asyncListWarehouseUser() {

        String tipoBodega = GlobalClass.getInstance().getQueryByInventory() ? GlobalClass.getInstance().getIdSelectedWareHouseInventory() : GlobalClass.getInstance().getIdSelectedWareHouseWarehouse();

        String urlIncidencias = GlobalClass.getInstance().getUrlServices() + "Inventory/GetListWarehouseUser/" + tipoBodega;
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

                            TypeToken<List<ResponsibleViewModel>> token = new TypeToken<List<ResponsibleViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            data = gson.fromJson(res, token.getType());
                            adapter = new ResponsibleAdapter(data, new onRecyclerResponsibleListItemClick() {
                                @Override
                                public void onClick(ResponsibleViewModel result) {
                                    final Intent _data = new Intent();
                                    _data.putExtra("responsibleWarehouseName", result.getName());
                                    _data.putExtra("responsibleWarehouseId", result.getId().toString());

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
                }
        );
    }

    private void returnListOffLine() {

        try {
            String tipoBodega = GlobalClass.getInstance().getQueryByInventory() ? GlobalClass.getInstance().getIdSelectedWareHouseInventory() : GlobalClass.getInstance().getIdSelectedWareHouseWarehouse();

            showProgress(true);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
            String res = pref.getString("key_list_users_warehouse", "");
            TypeToken<List<ResponsibleViewModel>> token = new TypeToken<List<ResponsibleViewModel>>() {
            };
            Gson gson = new GsonBuilder().create();
            // Define Response class to correspond to the JSON response returned
            data = gson.fromJson(res, token.getType());

            if (data != null) {
                FilterListByTipoBodega(tipoBodega);

                adapter = new ResponsibleAdapter(data, new onRecyclerResponsibleListItemClick() {
                    @Override
                    public void onClick(ResponsibleViewModel result) {
                        final Intent _data = new Intent();
                        _data.putExtra("responsibleWarehouseName", result.getName());
                        _data.putExtra("responsibleWarehouseId", result.getId().toString());

                        setResult(RESULT_OK, _data);

                        finish();
                    }
                });
                recyclerView.setAdapter(adapter);
            } else {
                showMessage(getString(R.string.message_not_sync_data));

            }
            showProgress(false);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showMessage(e.getMessage());
        }


    }

    private void showMessage(String res) {
        try {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(WarehouseUserActivity.this);

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
        } catch (Exception e) {
            e.printStackTrace();

        }
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
        setContentView(R.layout.activity_tercero_actual);


        if (GlobalClass.getInstance().isResponsable())
            ((AppCompatActivity) this).getSupportActionBar().setTitle(getString(R.string.title_bar_responsible));
        else
            ((AppCompatActivity) this).getSupportActionBar().setTitle(getString(R.string.title_legalized_by));

        InitializeControls();

        if (GlobalClass.getInstance().isNetworkAvailable())
            asyncListWarehouseUser();
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
