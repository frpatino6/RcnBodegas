package com.rcnbodegas.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rcnbodegas.Fragments.InventoryFragment;
import com.rcnbodegas.Fragments.WarehouseFragment;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.KeepLiveApp;
import com.rcnbodegas.Global.NetworkStateReceiver;
import com.rcnbodegas.Global.SyncService;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.ProductionViewModel;
import com.rcnbodegas.ViewModels.ResponsibleViewModel;
import com.rcnbodegas.ViewModels.TypeElementViewModel;
import com.rcnbodegas.ViewModels.WareHouseViewModel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NetworkStateReceiver.NetworkStateReceiverListener {
    private static final String TAG = "MainActivity";

    private View mLoginFormView;
    private View mProgressView;
    private ProgressDialog dialogo;
    private SharedPreferences.Editor editor;

    private TextView txtUser;
    private android.support.v4.app.FragmentManager fragmentManager;
    private SharedPreferences pref;
    private long coutnMateriales;
    private ArrayList<ProductionViewModel> dataProductions;
    private ArrayList<TypeElementViewModel> dataTipoELemento;
    private ArrayList<MaterialViewModel> dataMaterialELemento;
    private ArrayList<WareHouseViewModel> dataWarehouse;
    private ArrayList<ResponsibleViewModel> dataResponsable;
    private ArrayList<ResponsibleViewModel> dataUser;
    private NetworkStateReceiver networkStateReceiver;
    private boolean isOk;
    private String lastCreatedNUmberDocument;
    private ProgressDialog dialog;
    private int controlRequestMateriales = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        txtUser = headerView.findViewById(R.id.txtUserName);
        txtUser.setText(GlobalClass.getInstance().getUserName());
        drawer.openDrawer(Gravity.LEFT);


        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));


        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Sincronizando...");
        AddsharedPreferenceConfig(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showConfirmDialog("Está seguro de salir del menú principal");

        }
    }

    private void showConfirmDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Bodegas");
        builder.setMessage(message);
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void networkAvailable() {


        SendPendingMaterials(false);


    }

    private void SendPendingMaterials(boolean showMessage) {
        ArrayList<ArrayList<MaterialViewModel>> materialViewModels = GlobalClass.getInstance().getListMaterialForSync();

        if (materialViewModels.size() == 0) {
            if (showMessage) {
                showLoginError("No tiene documentos de legalización pendientes por sincronizar");
                return;
            }
        }

        for (ArrayList<MaterialViewModel> materialViewModel : materialViewModels) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                asyncListMaterialsByProduction(materialViewModel);
            }
        }
    }

    @Override
    public void networkUnavailable() {
        Log.d("tommydevall", "I'm dancing with myself");
        /* TODO: Your disconnection-oriented stuff here */
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.navBodegasVestuario) {
            fragment = new WarehouseFragment();

        } else if (id == R.id.navInventario) {
            fragment = new InventoryFragment();

        } else if (id == R.id.navSyncLDataBasica) {

            AddsharedPreferenceConfig(true);

        } else if (id == R.id.navSyncLegalizacion) {
            SendPendingMaterials(true);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (fragment != null)
            transaction.replace(R.id.content_frame, fragment).commit();
        return true;

    }

    private void AddsharedPreferenceConfig(boolean clear) {


        SharedPreferences pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
        editor = pref.edit();
        if (clear)
            editor.clear().commit();
        // pref.edit().remove("key_list_responsables").commit();

        String list_warehouse = pref.getString("key_list_warehouse", "");
        String list_responsable = pref.getString("key_list_responsables", "");
        String list_productions = pref.getString("key_list_productions", "");
        String list_tipo_elemento = pref.getString("key_list_tipo_elemento", "");
        String list_tipo_prenda = pref.getString("key_list_tipo_prenda", "");
        String list_material = pref.getString("key_list_material", "");
        String list_warehouser_users = pref.getString("key_list_users_warehouse", "");


        if (list_warehouse.equals(""))
            SyncAllWarehousesData();

        if (list_responsable.equals(""))
            SyncALlResponsibleData();

        if (list_productions.equals(""))
            SyncAllProductionsData();

        if (list_tipo_elemento.equals(""))
            SyncAllTipoElementoData();

        if (list_tipo_prenda.equals(""))
            SyncAllTipoPrendaData();

        if (list_material.equals(""))
            SyncCountMaterialData();

        if (list_warehouser_users.equals(""))
            SyncAllUserWarehousesData();

    }

    private void showLoginError(String res) {
        android.app.AlertDialog.Builder dlgAlert = new android.app.AlertDialog.Builder(MainActivity.this);

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

    private void SyncALlResponsibleData() {
        // Create URL
        dialog.show();
        dialog.setTitle("Sincronizando Responsables");
        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetListAllResponsible/";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<List<ResponsibleViewModel>> token = new TypeToken<List<ResponsibleViewModel>>() {
                            };

                            Gson gson = new GsonBuilder().create();
                            dataResponsable = gson.fromJson(res, token.getType());
                            // Define Response class to correspond to the JSON response returned
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("key_list_responsables", res);
                            editor.apply();


                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            dialogo.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                        showLoginError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }
                }
        );
    }

    private void SyncAllWarehousesData() {
        // Create URL

        dialog.setTitle("Sincronizando bodegas");
        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetListAllWarehouse/";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<List<WareHouseViewModel>> token = new TypeToken<List<WareHouseViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            dataWarehouse = gson.fromJson(res, token.getType());

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("key_list_warehouse", res);
                            editor.apply();


                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                        showLoginError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }
                }
        );
    }

    private void SyncAllProductionsData() {


        dialog.setTitle("Sincronizando producciones");
        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetAllListProductions/";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {


                            TypeToken<List<ProductionViewModel>> token = new TypeToken<List<ProductionViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            dataProductions = gson.fromJson(res, token.getType());

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("key_list_productions", res);
                            editor.apply();


                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            dialogo.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                        showLoginError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }
                }
        );
    }

    private void SyncAllTipoElementoData() {
        // Create URL
        dialog.setTitle("Sincronizando tipos de elementos");
        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetAllListTipoElemento/";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {


                            TypeToken<List<TypeElementViewModel>> token = new TypeToken<List<TypeElementViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            dataTipoELemento = gson.fromJson(res, token.getType());

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("key_list_tipo_elemento", res);
                            editor.apply();


                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            dialogo.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                        showLoginError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();


                    }
                }
        );
    }

    private void SyncAllTipoPrendaData() {
        dialog.setTitle("Sincronizando tipos de prendas");
        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetAllListTipoPrenda/";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {


                            TypeToken<List<TypeElementViewModel>> token = new TypeToken<List<TypeElementViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            dataTipoELemento = gson.fromJson(res, token.getType());

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("key_list_tipo_prenda", res);
                            editor.apply();


                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            dialogo.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                        showLoginError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }
                }
        );
    }

    private void SyncCountMaterialData() {
        dialog.setTitle("Sincronizando Material c");
        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetCountMateriales/";

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {


                            TypeToken<List<Long>> token = new TypeToken<List<Long>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            coutnMateriales = Long.valueOf(res);
                            //Log.d(TAG, "Total materiales  " + coutnMateriales);
                            long offSet = 0L;
                            int countRequest = 0;

                            while (offSet < coutnMateriales) {
                                Log.d(TAG, "Iteracion " + offSet);
                                dialog.show();
                                SyncAllMaterialData(offSet);

                                if ((coutnMateriales - offSet) < 3000) {
                                    //Log.d(TAG, "!!!!!!ULTIMA ITERACION!!!!! " + (coutnMateriales - offSet));
                                    offSet += (coutnMateriales - offSet);
                                } else {
                                    offSet += 3000;
                                }
                                countRequest++;
                            }

                            Log.d(TAG, "!!!!!REQUEST !!!!!!!!!!" + countRequest);

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            dialogo.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                        showLoginError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();


                    }
                }
        );
    }

    private void SyncAllMaterialData(Long offSet) {
        dialog.setTitle("Sincronizando Material");
        dialog.show();
        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetListAllMaterial/" + offSet;

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            Log.d(TAG, "Respuesta recibida");
                            TypeToken<List<MaterialViewModel>> token = new TypeToken<List<MaterialViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            dataMaterialELemento = gson.fromJson(res, token.getType());
                            editor.putString("key_list_material", res);
                            editor.apply();


                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            dialogo.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                        showLoginError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        controlRequestMateriales++;

                        if ((coutnMateriales / 3000) < controlRequestMateriales) {
                            dialog.dismiss();
                            showLoginError("Proceso de sincronización de data básica, ejecutada con éxito!!!");
                        }
                    }

                }
        );
    }

    private void SyncAllUserWarehousesData() {
        // Create URL
        dialog.setTitle("Sincronizando usuarios");
        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetListAllWArehouseUser/";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<List<ResponsibleViewModel>> token = new TypeToken<List<ResponsibleViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            dataUser = gson.fromJson(res, token.getType());

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("key_list_users_warehouse", res);
                            editor.apply();


                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            dialogo.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                        showLoginError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();


                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void asyncListMaterialsByProduction(ArrayList<MaterialViewModel> listForSync) {
        dialog.show();
        dialog.setTitle("Sincronizando docuemtnos de legalización pendientes");
        String wareHouse = GlobalClass.getInstance().getQueryByInventory() ? GlobalClass.getInstance().getIdSelectedWareHouseInventory() : GlobalClass.getInstance().getIdSelectedWareHouseWarehouse();

        String url = GlobalClass.getInstance().getUrlServices() + "warehouse/CreateElement/" + wareHouse;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        String tipo = "application/json";

        StringEntity entity = null;
        Gson json = new Gson();

        for (MaterialViewModel materialViewModel : listForSync) {
            materialViewModel.getListaImagenesBmp().clear();
        }
        String resultJson = json.toJson(listForSync);

        entity = new StringEntity(resultJson, StandardCharsets.UTF_8);

        client.post(getApplicationContext(), url, entity, tipo, new TextHttpResponseHandler() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                isOk = true;
                Gson gson = new GsonBuilder().create();
                // Define Response class to correspond to the JSON response returned
                lastCreatedNUmberDocument = gson.fromJson(responseString, String.class);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                isOk = false;
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onFinish() {
                super.onFinish();
                if (isOk) {
                    GlobalClass.getInstance().getListMaterialForSync().clear();
                    stopService(new Intent(MainActivity.this, SyncService.class));
                    dialog.dismiss();
                    showLoginError("Sincronización de documentos finalizada correctamente!!!");
                }

            }
        });
    }


}
