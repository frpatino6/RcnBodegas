package com.rcnbodegas.Activities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
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
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rcnbodegas.Fragments.InventoryFragment;
import com.rcnbodegas.Fragments.WarehouseFragment;
import com.rcnbodegas.Global.GlobalClass;
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
    private static final int ID_NOTIFICACION_NUMERODOCUMENTOS = 1;
    private static final String TAG = "MainActivity";
    private static ArrayList<String> materialSync;
    NotificationManager notificationManager;
    private int controlRequestMateriales = 0;
    private long coutnMateriales;
    private ArrayList<Integer> data;
    private ArrayList<MaterialViewModel> dataMaterialELemento;
    private ArrayList<ProductionViewModel> dataProductions;
    private ArrayList<ResponsibleViewModel> dataResponsable;
    private ArrayList<TypeElementViewModel> dataTipoELemento;
    private ArrayList<ResponsibleViewModel> dataUser;
    private ArrayList<WareHouseViewModel> dataWarehouse;
    private ProgressDialog dialog;
    private ProgressDialog dialogo;
    private SharedPreferences.Editor editor;
    private android.support.v4.app.FragmentManager fragmentManager;
    private boolean isOk;
    private String lastCreatedNUmberDocument;
    private View mLoginFormView;
    private View mProgressView;
    private NetworkStateReceiver networkStateReceiver;
    private SharedPreferences pref;
    private TextView txtUser;

    private void AddsharedPreferenceConfig(boolean clear) {

        if (materialSync == null)
            materialSync = new ArrayList<>();
        else
            materialSync.clear();

        new asyncBasicTables().execute();

    }

    private void SendPendingMaterials(boolean showMessage) {
        ArrayList<ArrayList<MaterialViewModel>> materialViewModels = GlobalClass.getInstance().getListMaterialForSync();

        if (materialViewModels.size() == 0) {
            if (showMessage) {
                showMessage("No tiene documentos de legalización pendientes por sincronizar");
                return;
            }
        }

        if (materialViewModels.size() > 0)
            new asyncMaterialBackGround().execute(materialViewModels);
    }

    private void SyncALlResponsibleData() {
        // Create URL

        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetListAllResponsible/";

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;


                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }

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

                        }
                    }
                }
        );
    }

    private void SyncAllMaterialData(Long offSet) {


        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetListAllMaterial/" + offSet;

        SyncHttpClient client = new SyncHttpClient();

        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        controlRequestMateriales++;

                        if ((coutnMateriales / 3000) < controlRequestMateriales) {
                            parseArrayMaterial(materialSync);
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            Log.d(TAG, "Respuesta recibida");
                            //TypeToken<List<MaterialViewModel>> token = new TypeToken<List<MaterialViewModel>>() {
                            //} ;
                            //Gson gson = new GsonBuilder().create();

                            //editor.putString("key_list_material", res);
                            //editor.commit();
                            materialSync.add(res);

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();

                        }
                    }

                }
        );
    }

    private void SyncAllProductionsData() {

        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetAllListProductions/";

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;


                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }

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
                        }
                    }
                }
        );
    }

    private void SyncAllTipoElementoData() {

        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetAllListTipoElemento/";

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;


                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();


                    }

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

                        }
                    }
                }
        );
    }

    private void SyncAllTipoPrendaData() {

        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetAllListTipoPrenda/";

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }

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

                        }
                    }
                }
        );
    }

    private void SyncAllUserWarehousesData() {
        // Create URL

        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetListAllWArehouseUser/";

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();


                    }

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
                }
        );
    }

    private void SyncAllWarehousesData() {
        // Create URL
        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetListAllWarehouse/";

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;


                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }

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
                }
        );
    }

    private void SyncCountMaterialData() {

        String url = GlobalClass.getInstance().getUrlServices() + "sync/GetCountMateriales/";

        SyncHttpClient client = new SyncHttpClient();

        client.setTimeout(60000);
        RequestParams params = new RequestParams();

        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        int resultCode = statusCode;


                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();


                    }

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

                        }
                    }
                }
        );
    }

    private void parseArrayMaterial(ArrayList<String> materialSync) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("materialbodegasPreferences", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("key_list_material_count", String.valueOf(materialSync.size()));
        editor.commit();
        int materilaSyncIndex = 0;
        for (String res : materialSync) {
            TypeToken<List<MaterialViewModel>> token = new TypeToken<List<MaterialViewModel>>() {
            };
            Gson gson = new GsonBuilder().create();

            editor.putString("key_list_material_" + materilaSyncIndex, res);
            materilaSyncIndex++;
        }
        editor.commit();
    }

    private void sendLocalNotification(String text) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent nIntent = new Intent();
        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nIntent, ID_NOTIFICACION_NUMERODOCUMENTOS);
        Notification notiBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Rcn")
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentIntent(pendingIntent).build();

        //notiBuilder.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(ID_NOTIFICACION_NUMERODOCUMENTOS, notiBuilder);
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

    private void showMessage(String res) {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void networkAvailable() {
        SendPendingMaterials(false);
    }

    @Override
    public void networkUnavailable() {
        Log.d("tommydevall", "I'm dancing with myself");
        /* TODO: Your disconnection-oriented stuff here */
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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

    private class asyncBasicTables extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            SharedPreferences pref1 = getApplicationContext().getSharedPreferences("bodegasPreferences", 0); // 0 - for private mode
            editor = pref1.edit();
            editor.clear().commit();


            String list_warehouse = pref1.getString("key_list_warehouse", "");
            String list_responsable = pref1.getString("key_list_responsables", "");
            String list_productions = pref1.getString("key_list_productions", "");
            String list_tipo_elemento = pref1.getString("key_list_tipo_elemento", "");
            String list_tipo_prenda = pref1.getString("key_list_tipo_prenda", "");
            String list_material = pref1.getString("key_list_material", "");
            String list_warehouser_users = pref1.getString("key_list_users_warehouse", "");


            try {
                if (list_warehouse.equals("")) {
                    publishProgress("Tarea 1 de 7. Sincronizando Bodegas");
                    SyncAllWarehousesData();
                }

                if (list_responsable.equals("")) {
                    publishProgress("Tarea 2 de 7. Sincronizando Responsables");
                    SyncALlResponsibleData();
                }

                if (list_productions.equals("")) {
                    publishProgress("Tarea 3 de 7. Sincronizando Producciones");
                    SyncAllProductionsData();
                }

                if (list_tipo_elemento.equals("")) {
                    publishProgress("Tarea 4 de 7. Sincronizando Elementos");
                    SyncAllTipoElementoData();
                }

                if (list_tipo_prenda.equals("")) {
                    publishProgress("Tarea 5 de 7. Sincronizando Prendas");
                    SyncAllTipoPrendaData();
                }

                if (list_material.equals("")) {
                    publishProgress("Tarea 6 de 7. Sincronizando Materiales");
                    SyncCountMaterialData();
                }

                if (list_warehouser_users.equals("")) {
                    publishProgress("Tarea 7 de 7. Sincronizando Usuarios por Bodega");
                    SyncAllUserWarehousesData();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean)
                showMessage("Sincronización ejecutada con éxito");
            else
                showMessage("Error sincrnonizando, intente de nuevo");
            dialogo.dismiss();
        }

        @Override
        protected void onPreExecute() {
            // super.onPreExecute();
            dialogo = new ProgressDialog(MainActivity.this);
            dialogo.setMessage("Cargando datos...");
            dialogo.setIndeterminate(false);
            dialogo.setCancelable(false);
            dialogo.show();

        }

        @Override
        protected void onProgressUpdate(String... values) {
            dialogo.setMessage(values[0]);
            super.onProgressUpdate(values);
        }
    }

    private class asyncMaterialBackGround extends AsyncTask<ArrayList<ArrayList<MaterialViewModel>>, String, Boolean> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(ArrayList<ArrayList<MaterialViewModel>>... params) {

            final boolean isOkResult = true;
            ArrayList<ArrayList<MaterialViewModel>> listForSync = params[0];

            try {
                String wareHouse = GlobalClass.getInstance().getQueryByInventory() ? GlobalClass.getInstance().getIdSelectedWareHouseInventory() : GlobalClass.getInstance().getIdSelectedWareHouseWarehouse();

                String url = GlobalClass.getInstance().getUrlServices() + "warehouse/CreateElements/" + wareHouse;
                SyncHttpClient client = new SyncHttpClient();
                client.setTimeout(60000);
                String tipo = "application/json";

                StringEntity entity = null;
                Gson json = new Gson();

                for (ArrayList<MaterialViewModel> materialViewModel : listForSync) {
                    for (MaterialViewModel viewModel : materialViewModel) {
                        viewModel.getListaImagenesBmp().clear();
                    }

                }
                String resultJson = json.toJson(listForSync);

                entity = new StringEntity(resultJson, StandardCharsets.UTF_8);

                client.post(getApplicationContext(), url, entity, tipo, new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                        isOk = false;
                        showMessage("Error generando el documento de legalización" + responseBody);
                        sendLocalNotification("Error generando el documento de legalización" + responseBody);
                    }

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (isOk) {
                            GlobalClass.getInstance().getListMaterialForSync().clear();
                            stopService(new Intent(MainActivity.this, SyncService.class));
                            GlobalClass.getInstance().getDataMaterial().clear();
                            sendLocalNotification("Se genero documentos de legalización  " + lastCreatedNUmberDocument);
                            showMessage("Sincronización ejecutada con éxito. Documento número: " + lastCreatedNUmberDocument);
                        }
                    }

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        isOk = true;
                        TypeToken<List<Integer>> token = new TypeToken<List<Integer>>() {
                        };
                        Gson gson = new GsonBuilder().create();
                        // Define Response class to correspond to the JSON response returned
                        data = gson.fromJson(responseString, token.getType());

                        // Define Response class to correspond to the JSON response returned
                        lastCreatedNUmberDocument = android.text.TextUtils.join(",", data);//gson.fromJson(responseString, String.class);
                    }
                });
                return isOkResult;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dialogo.dismiss();
        }

        @Override
        protected void onPreExecute() {
            // super.onPreExecute();
            dialogo = new ProgressDialog(MainActivity.this);
            dialogo.setMessage("Cargando datos...");
            dialogo.setIndeterminate(false);
            dialogo.setCancelable(false);
            dialogo.show();

        }

        @Override
        protected void onProgressUpdate(String... values) {
            dialogo.setMessage(values[0]);
            super.onProgressUpdate(values);

        }
    }

}
