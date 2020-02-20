package com.rcnbodegas.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rcnbodegas.Activities.CustomActivity;
import com.rcnbodegas.Activities.Filter;
import com.rcnbodegas.Activities.FilterList;
import com.rcnbodegas.Activities.ListItemReviewActivity;
import com.rcnbodegas.Activities.ProductionListActivity;
import com.rcnbodegas.Activities.ResponsibleListActivity;
import com.rcnbodegas.Activities.TypeElementListActivity;
import com.rcnbodegas.Activities.WareHouseListActivity;
import com.rcnbodegas.CustomEvents.onHttpRequestError;
import com.rcnbodegas.CustomEvents.onHttpRequestSuccess;
import com.rcnbodegas.CustomEvents.onRecyclerProductionListItemClick;
import com.rcnbodegas.Global.DateTimeUtilities;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.PhotosAdapter;
import com.rcnbodegas.Global.ScannerFactory;
import com.rcnbodegas.Global.TScanner;
import com.rcnbodegas.Interfaces.IObserver;
import com.rcnbodegas.R;
import com.rcnbodegas.Repository.InventoryHeaderRepository;
import com.rcnbodegas.Repository.MaterialRepository;
import com.rcnbodegas.ViewModels.InventroyHeaderViewModel;
import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.ProductionViewModel;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends CustomActivity implements IObserver, DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_PRODUCTION = 1;
    private static final int REQUEST_RESPONSIBLE = 2;
    private static final int REQUEST_TYPE_ELEMENT = 3;
    private static final int REQUEST_TYPE_ELEMENT_HEADER = 5;
    private static final int REQUEST_WAREHOUSE = 4;
    private static final String TAG = "InventoryFragment";
    private ArrayList<Bitmap> ListaImagenes;
    private TScanner Scanner_manager = null;
    private PhotosAdapter adapter;
    /* TODO: Rename and change types of parameter */
    private ImageButton btnSearch;
    private int countMaterialIndex;
    private ArrayList<MaterialViewModel> dataMaterial;
    private DateTimeUtilities dateTimeUtilities;
    private ProgressDialog dialogo;
    private InventoryHeaderRepository inventoryHeaderRepository;
    private int inventoryId;
    private Button inventory_btn_find;
    private FloatingActionButton inventory_btn_new_element;
    private Button inventory_btn_ok;
    private LinearLayout inventory_data;
    private EditText inventory_date_option;
    private EditText inventory_date_option2;
    private LinearLayout inventory_element;
    private EditText inventory_element_barcode_edit;
    private EditText inventory_element_brand_edit;
    private EditText inventory_element_edit;
    private EditText inventory_element_price_edit;
    private TextView inventory_element_prod;
    private EditText inventory_element_type_edit;
    private EditText inventory_element_value_edit;
    private EditText inventory_program_option;
    private EditText inventory_responsible_option;
    private EditText inventory_type_element_option;
    private EditText inventory_warehouse_option;
    private InventroyHeaderViewModel inventroyHeaderViewModel;
    private MaterialViewModel itemMaterialAdded;
    private View mIncidenciasFormView;
    private View mProgressView;
    private String m_Text;
    private MaterialRepository materialRepository;
    private MenuItem mnuCancel;
    private MenuItem mnuReview;
    private MenuItem mnuSave;
    private RecyclerView photos_recycler_view;

    public InventoryFragment() {


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void AddElementToReview() {

        GlobalClass.getInstance().getDataReviewMaterial().add(itemMaterialAdded);
        itemMaterialAdded.setReview(true);
        itemMaterialAdded.setFoundDate(getDate());
        itemMaterialAdded.setIdHeader(inventoryId);
        materialRepository.update(itemMaterialAdded);

        hideKeyboard(Objects.requireNonNull(getActivity()));
    }

    private void ClearFields() {
        inventory_element_barcode_edit.setText("");
        inventory_element_edit.setText("");
        inventory_element_type_edit.setText("");
        inventory_element_brand_edit.setText("");
        inventory_element_price_edit.setText("");
        inventory_element_value_edit.setText("");
        adapter.getDataSet().clear();
        adapter.notifyDataSetChanged();
    }

    private void FilterListByProduction(final String wharehouse, final String production) {

        //mStatusView.setText("Query = " + query + " : submitted");
        try {
            Filter<MaterialViewModel, String> filter = new Filter<MaterialViewModel, String>() {
                public boolean isMatched(MaterialViewModel object, String text) {
                    boolean result;
                    result =
                            object.getProductionId().toString().equals(String.valueOf(production)) &&
                                    object.getWareHouseId().toString().equals(String.valueOf(wharehouse));

                    if (result)
                        return true;
                    else
                        return false;
                }
            };

            dataMaterial = (ArrayList<MaterialViewModel>) new FilterList().filterList(dataMaterial, filter, "");
            Log.d(TAG, "Elementos a mostrar " + dataMaterial.size());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("CutPasteId")
    private void InitializeControls(View v) {

        materialRepository = new MaterialRepository(getActivity());

        btnSearch = v.findViewById(R.id.btnSearch);

        inventory_btn_new_element = v.findViewById(R.id.inventory_btn_new_element);
        inventory_element = v.findViewById(R.id.inventory_element);
        inventory_data = v.findViewById(R.id.inventory_data);
        inventory_warehouse_option = v.findViewById(R.id.inventory_warehouse_option);
        inventory_program_option = v.findViewById(R.id.inventory_program_option);
        inventory_type_element_option = v.findViewById(R.id.inventory_type_element_option);
        inventory_date_option = v.findViewById(R.id.inventory_date_option);
        inventory_date_option2 = v.findViewById(R.id.inventory_date_option2);
        inventory_responsible_option = v.findViewById(R.id.inventory_responsible_option);
        inventory_element_type_edit = v.findViewById(R.id.inventory_element_type_edit);
        inventory_element_barcode_edit = v.findViewById(R.id.inventory_element_barcode_edit);
        inventory_element_edit = v.findViewById(R.id.inventory_element_edit);
        inventory_element_brand_edit = v.findViewById(R.id.inventory_element_brand_edit);
        inventory_element_price_edit = v.findViewById(R.id.inventory_element_price_edit);
        inventory_element_value_edit = v.findViewById(R.id.inventory_element_value_edit);
        inventory_element_prod = v.findViewById(R.id.inventory_element_prod);
        inventory_btn_ok = v.findViewById(R.id.inventory_btn_ok);

        inventory_date_option.setText(dateTimeUtilities.parseDateTurno());
        inventory_date_option2.setText(dateTimeUtilities.parseDateTurno());

        inventory_warehouse_option.setText(GlobalClass.getInstance().getNameSelectedWareHouseInventory());

        mIncidenciasFormView = v.findViewById(R.id.inventory_element);
        mProgressView = v.findViewById(R.id.inventroy_progress);

        photos_recycler_view = v.findViewById(R.id.photos_recycler_view);
        photos_recycler_view.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        photos_recycler_view.setLayoutManager(layoutManager);
        photos_recycler_view.setItemAnimator(new DefaultItemAnimator());
    }

    private void InitializeEvents() {

        GlobalClass.getInstance().setQueryByInventory(true);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                itemMaterialAdded = findElementByBarCode();

                if (itemMaterialAdded == null) {
                    showMessageDialogError(getString(R.string.message_not_find_element));
                    return;
                }
                if (!itemMaterialAdded.isReview()) {
                    setMaterialData(itemMaterialAdded);
                } else
                    showMessageDialogError(getString(R.string.message_element_exist) + " " + itemMaterialAdded.getBarCode());
            }
        });


        inventory_btn_new_element.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (ValidaRequieredElementInfo()) {
                    ClearFields();
                    AddElementToReview();
                }
            }
        });


        inventory_program_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), ProductionListActivity.class);
                startActivityForResult(intent, REQUEST_PRODUCTION);
            }
        });

        inventory_type_element_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), TypeElementListActivity.class);
                startActivityForResult(intent, REQUEST_TYPE_ELEMENT_HEADER);
            }
        });

        inventory_date_option.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.txtDate = inventory_date_option;
                newFragment.dateTimeUtilities = dateTimeUtilities;

                newFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "datePicker");
            }
        });
        inventory_date_option2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.txtDate = inventory_date_option2;
                newFragment.dateTimeUtilities = dateTimeUtilities;

                newFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "datePicker");
            }
        });

        inventory_responsible_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), ResponsibleListActivity.class);

                if (inventory_program_option.getText().toString().equals("")) {
                    inventory_program_option.setError(getString(R.string.error_program_empty));
                    return;
                } else
                    inventory_program_option.setError(null);
                GlobalClass.getInstance().setResponsable(true);
                startActivityForResult(intent, REQUEST_RESPONSIBLE);
            }
        });

        inventory_element_type_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), TypeElementListActivity.class);


                startActivityForResult(intent, REQUEST_TYPE_ELEMENT);
            }
        });
        inventory_btn_ok.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (validateFiels()) {
                    LoadElements(true);
                }
            }
        });


        inventory_warehouse_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenListWareHouse();
            }
        });

    }

    @SuppressLint("RestrictedApi")
    private void InitializeNewInventroyProcess(boolean cancel) {
        GlobalClass.getInstance().setCurrentInventoryActiveProcess(false);

        inventory_element.setVisibility(View.GONE);
        inventory_data.setVisibility(View.VISIBLE);
        GlobalClass.getInstance().setIdSelectedProductionInventory("");
        GlobalClass.getInstance().setIdSelectedResponsibleInventory(-1);
        GlobalClass.getInstance().setIdSelectedTypeElementInventory(-1);
        inventory_program_option.setText("");
        inventory_responsible_option.setText("");
        inventory_btn_new_element.setVisibility(View.GONE);
        GlobalClass.getInstance().setDataMaterialInventory(new ArrayList<MaterialViewModel>());
        GlobalClass.getInstance().setListMaterialBYProduction(new ArrayList<MaterialViewModel>());
        GlobalClass.getInstance().setDataReviewMaterial(new ArrayList<MaterialViewModel>());
        mnuReview.setVisible(false);
        mnuSave.setVisible(false);
        mnuCancel.setVisible(false);

        if (ListaImagenes != null)
            ListaImagenes.clear();

        //if (!cancel)
        //  showMessageDialog("Inventario guardado correctamente. CONSECUTIVO: " + inventoryId);
        inventoryId = 0;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void LoadElements(boolean createHeader) {
        if (GlobalClass.getInstance().isNetworkAvailable())
            if (createHeader)
                asyncInventoryHeader();
            else {
                materialRepository.geDetailByDocumentNumber(inventoryId).observe(getActivity(), new Observer<List<MaterialViewModel>>() {
                    @Override
                    public void onChanged(List<MaterialViewModel> materialViewModels) {
                        GlobalClass.getInstance().setDataMaterialInventory((ArrayList<MaterialViewModel>) materialViewModels);
                        GlobalClass.getInstance().setListMaterialBYProduction((ArrayList<MaterialViewModel>) materialViewModels);
                        showElementInput();
                    }
                });
            }
        else {
            new asyncGetCountMaterial().execute();

        }
    }

    private void OpenListWareHouse() {
        Intent intent = new Intent(getActivity(), WareHouseListActivity.class);
        startActivityForResult(intent, REQUEST_WAREHOUSE);
    }

    private boolean ValidaRequieredElementInfo() {
        inventory_element_barcode_edit.setError(null);
        inventory_element_edit.setError(null);
        inventory_element_type_edit.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(inventory_element_barcode_edit.getText().toString())) {
            inventory_element_barcode_edit.setError(getString(R.string.error_barcode_empty));
            focusView = inventory_element_barcode_edit;
            cancel = true;
        }
        if (TextUtils.isEmpty(inventory_element_edit.getText().toString())) {
            inventory_element_edit.setError(getString(R.string.error_description_empty));
            focusView = inventory_element_edit;
            cancel = true;
        }
        /*if (TextUtils.isEmpty(inventory_element_type_edit.getText().toString())) {
            inventory_element_type_edit.setError(getString(R.string.error_type_empty));
            focusView = inventory_element_type_edit;
            cancel = true;
        }*/

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            inventory_element.setVisibility(View.VISIBLE);
            inventory_data.setVisibility(View.GONE);
        }
        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void asyncInventoryHeader() {

        inventroyHeaderViewModel = new InventroyHeaderViewModel();
        inventroyHeaderViewModel.setCompanyId(1);
        inventroyHeaderViewModel.setProductionId(Integer.valueOf(GlobalClass.getInstance().getIdSelectedProductionInventory()));
        inventroyHeaderViewModel.setWarehouseTypeId(GlobalClass.getInstance().getIdSelectedWareHouseInventory());
        inventroyHeaderViewModel.setResponsibleId(GlobalClass.getInstance().getIdSelectedResponsibleInventory());
        inventroyHeaderViewModel.setInventoryUser(GlobalClass.getInstance().getUserName());
        inventroyHeaderViewModel.setInitDate(inventory_date_option.getText().toString());
        inventroyHeaderViewModel.setTypeELement(GlobalClass.getInstance().getIdSelectedTypeElementHeader());
        inventroyHeaderViewModel.setFechaMovimiento(inventory_date_option2.getText().toString());


        String url = GlobalClass.getInstance().getUrlServices() + "Inventory/CreateInventoryHeader/";

        try {
            final ProgressDialog dialogo = new ProgressDialog(getActivity());
            dialogo.setMessage("Iniciando inventario...");
            dialogo.setIndeterminate(false);
            dialogo.setCancelable(false);
            dialogo.show();

            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(60000);
            String tipo = "application/json";

            StringEntity entity;
            Gson json = new Gson();

            String resultJson = json.toJson(inventroyHeaderViewModel);

            entity = new StringEntity(resultJson, StandardCharsets.UTF_8);


            client.post(Objects.requireNonNull(getActivity()).getApplicationContext(), url, entity, tipo, new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {

                    showMessageDialogError(responseBody);
                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onFinish() {
                    super.onFinish();

                    dialogo.dismiss();
                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    inventoryId = Integer.valueOf(responseString);
                    showMessageDialog("Usted va a iniciar el inventario de " + getTypewarehouseName() + " número " + inventoryId);

                    inventroyHeaderViewModel.setCodigo(inventoryId);
                    insertHeaderLocal(inventroyHeaderViewModel);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void asyncInventoryPending() {

        final boolean isOk = false;
        String url = GlobalClass.getInstance().getUrlServices() + "Inventory/GetPendingInventoryByUser/" + GlobalClass.getInstance().getUserName();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        final ProgressDialog dialogo = new ProgressDialog(getActivity());
        dialogo.setMessage("Buscando inventarios pendientes al usuario..." + GlobalClass.getInstance().getUserName());
        dialogo.setIndeterminate(false);
        dialogo.setCancelable(false);
        dialogo.show();
        client.get(url, new TextHttpResponseHandler() {
                    ArrayList<InventroyHeaderViewModel> listInventroyHeaderViewModels = null;

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        showMessageDialogError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dialogo.dismiss();

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<List<InventroyHeaderViewModel>> token = new TypeToken<List<InventroyHeaderViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            listInventroyHeaderViewModels = gson.fromJson(res, token.getType());

                            if (listInventroyHeaderViewModels != null && listInventroyHeaderViewModels.size() > 0) {
                                GlobalClass.getInstance().setIdSelectedProductionInventory(String.valueOf(listInventroyHeaderViewModels.get(0).getProductionId()));
                                GlobalClass.getInstance().setIdSelectedResponsibleInventory(listInventroyHeaderViewModels.get(0).getResponsibleId());
                                GlobalClass.getInstance().setIdSelectedWareHouseInventory(listInventroyHeaderViewModels.get(0).getWarehouseTypeId());
                                GlobalClass.getInstance().setCurrentproductionName(listInventroyHeaderViewModels.get(0).getProductionName());
                                inventoryId = listInventroyHeaderViewModels.get(0).getId();
                                setActionBarTittle();
                            }

                            if (listInventroyHeaderViewModels != null && listInventroyHeaderViewModels.size() > 0)
                                showConfirmInventoryContinueDialog("Inventario pendiente, seleccione una opción?");

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();

                        }
                    }
                }
        );
    }

    private void asyncInventoryPendingById(int id) {


        String url = GlobalClass.getInstance().getUrlServices() + "Inventory/GetPendingInventoryById/" + id;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        final ProgressDialog dialogo = new ProgressDialog(getActivity());
        dialogo.setMessage("Buscando inventarios con el código " + id);
        dialogo.setIndeterminate(false);
        dialogo.setCancelable(false);
        dialogo.show();
        client.get(url, new TextHttpResponseHandler() {
                    ArrayList<InventroyHeaderViewModel> listInventroyHeaderViewModels = null;

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        showMessageDialogError(res);

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dialogo.dismiss();


                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<List<InventroyHeaderViewModel>> token = new TypeToken<List<InventroyHeaderViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            listInventroyHeaderViewModels = gson.fromJson(res, token.getType());

                            if (listInventroyHeaderViewModels != null && listInventroyHeaderViewModels.size() > 0) {
                                GlobalClass.getInstance().setIdSelectedProductionInventory(String.valueOf(listInventroyHeaderViewModels.get(0).getProductionId()));
                                GlobalClass.getInstance().setIdSelectedResponsibleInventory(listInventroyHeaderViewModels.get(0).getResponsibleId());
                                GlobalClass.getInstance().setIdSelectedWareHouseInventory(listInventroyHeaderViewModels.get(0).getWarehouseTypeId());
                                GlobalClass.getInstance().setCurrentproductionName(listInventroyHeaderViewModels.get(0).getProductionName());
                                inventoryId = listInventroyHeaderViewModels.get(0).getId();
                                setActionBarTittle();
                            }
                            GlobalClass.getInstance().setContinueInventory(false);
                            showConfirmContinueDialog(listInventroyHeaderViewModels);

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();

                        }
                    }
                }
        );
    }

    private void asyncListMaterialsByProduction(boolean createHeader) {

        final ProgressDialog dialogo = new ProgressDialog(getActivity());
        dialogo.setMessage("Cargando elementos...");
        dialogo.setIndeterminate(false);
        dialogo.setCancelable(false);
        dialogo.show();

        String url = GlobalClass.getInstance().getUrlServices() + "Inventory/GetMaterialByProduction/" +
                GlobalClass.getInstance().getIdSelectedWareHouseInventory() + "/" +
                GlobalClass.getInstance().getIdSelectedProductionInventory() + "/" +
                GlobalClass.getInstance().getIdSelectedResponsibleInventory() + "/" +
                GlobalClass.getInstance().getIdSelectedTypeElementHeader() + "/" + (createHeader == true ? "0" : "1" + "/" + inventoryId) + "/" + inventory_date_option2.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(360000);
        showProgress(true);
        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        showMessageDialogError(res);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        showProgress(false);
                        dialogo.hide();
                    }

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<List<MaterialViewModel>> token = new TypeToken<List<MaterialViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            ArrayList<MaterialViewModel> dataMaterial = gson.fromJson(res, token.getType());
                            GlobalClass.getInstance().setDataMaterialInventory(dataMaterial);

                            if (dataMaterial != null)
                                GlobalClass.getInstance().setListMaterialBYProduction(dataMaterial);
                            else
                                showMessageDialogError("No se encontró elemento con el código de barras ingresado");

                            materialRepository.insertAllOElements(dataMaterial);

                            showElementInput();

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();

                        }
                    }
                }
        );
    }

    private void confirmCancelInventory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.message_confirm_cancel));
        builder.setPositiveButton(getString(R.string.btn_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InitializeNewInventroyProcess(true);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void confirmInventory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.message_confirm_save));
        builder.setPositiveButton(getString(R.string.btn_confirm),
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (GlobalClass.getInstance().isNetworkAvailable())
                            GlobalClass.getInstance().asyncUpdateElements(0, inventoryId, inventory_date_option.getText().toString(),
                                    getActivity(), true, new onHttpRequestSuccess() {
                                        @Override
                                        public void onSuccess(boolean result) {
                                            InitializeNewInventroyProcess(false);
                                        }
                                    }, new onHttpRequestError() {
                                        @Override
                                        public void onError(String responseBody) {
                                            showMessageDialogError(responseBody);
                                        }
                                    });
                        else {
                            showMessageDialog("No hay conexión a internet");
                        }
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createBitMapFromString(MaterialViewModel itemMaterialAdded) {

        if (ListaImagenes == null) ListaImagenes = new ArrayList<>();
        else
            ListaImagenes.clear();

        for (String encodedString : itemMaterialAdded.getListaImagenesStr()) {
            try {
                byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                ListaImagenes.add(bitmap);

            } catch (Exception e) {
                e.getMessage();
            }
        }

    }

    private MaterialViewModel findElementByBarCode() {
        return materialRepository.getMaterialByBarcode(inventory_element_barcode_edit.getText().toString());
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaActual = sdf.format(new Date());

        return fechaActual;

    }

    @SuppressLint("RestrictedApi")
    private void getLocalMagterialByProduction() {


        FilterListByProduction(GlobalClass.getInstance().getIdSelectedWareHouseInventory(),
                GlobalClass.getInstance().getIdSelectedProductionInventory()
        );
        GlobalClass.getInstance().setDataMaterialInventory(dataMaterial);

        if (dataMaterial != null)
            GlobalClass.getInstance().setListMaterialBYProduction(dataMaterial);
        else
            showMessageDialogError("No se encontró elemento con el código de barras ingresado");

        showProgress(false);
        mnuReview.setVisible(true);
        mnuSave.setVisible(true);
        mnuCancel.setVisible(true);
        GlobalClass.getInstance().setCurrentInventoryActiveProcess(true);
        inventory_btn_new_element.setVisibility(View.VISIBLE);
        inventory_element.setVisibility(View.VISIBLE);
        inventory_data.setVisibility(View.GONE);
        setActionBarTittle();
    }

    private String getTypewarehouseName() {

        if (GlobalClass.getInstance().getIdSelectedWareHouseInventory().toString().equals("V"))
            return "Vestuario";
        else
            return "Ambientación";
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void insertHeaderLocal(InventroyHeaderViewModel inventroyHeaderViewModel) {
        inventoryHeaderRepository = new InventoryHeaderRepository(getActivity());
        inventoryHeaderRepository.insert(inventroyHeaderViewModel);
    }

    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void setActionBarTittle() {


        if (GlobalClass.getInstance().getCurrentproductionName() == null)
            GlobalClass.getInstance().setCurrentproductionName("");

        inventory_element_prod.setText(GlobalClass.getInstance().getCurrentproductionName());
        dateTimeUtilities = new DateTimeUtilities(getActivity());

    }

    private void setListImagesAdapter() {
        ArrayList<String> listaNombresImagenes = new ArrayList<>();
        adapter = new PhotosAdapter(ListaImagenes, listaNombresImagenes, new onRecyclerProductionListItemClick() {
            @Override
            public void onClick(ProductionViewModel wareHouseViewModel) {

            }
        });
        photos_recycler_view.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setMaterialData(MaterialViewModel data) {
        inventory_element_edit.setText(data.getMaterialName());
        inventory_element_type_edit.setText(data.getTypeElementName());
        inventory_element_brand_edit.setText(data.getMarca().toString());
        inventory_element_price_edit.setText(data.getUnitPrice().toString());
        hideKeyboard(Objects.requireNonNull(getActivity()));
        createBitMapFromString(data);
        setListImagesAdapter();

    }

    private void showConfirmContinueDialog(final ArrayList<InventroyHeaderViewModel> listInventroyHeaderViewModels) {

        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());

        dlgAlert.setMessage("Usted va a continuar el inventario para el Programa: " +
                listInventroyHeaderViewModels.get(0).getProductionName() +
                " ,Fecha inventario: " + listInventroyHeaderViewModels.get(0).getInitDate() + " ¿Desea continuar?");
        dlgAlert.setTitle(getString(R.string.app_name));
        //dlgAlert.setPositiveButton(getString(R.string.Texto_Boton_Ok), null);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(DialogInterface dialog, int id) {
                if (listInventroyHeaderViewModels != null && listInventroyHeaderViewModels.size() > 0)
                    LoadElements(false);
            }
        });
        dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(DialogInterface dialog, int id) {


            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();


    }

    private void showConfirmInventoryContinueDialog(String res) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());

        dlgAlert.setMessage(res);
        dlgAlert.setTitle(getString(R.string.app_name));
        //dlgAlert.setPositiveButton(getString(R.string.Texto_Boton_Ok), null);
        dlgAlert.setPositiveButton("Realizar inventario nuevo", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        dlgAlert.setNegativeButton("Continuar inventario", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(DialogInterface dialog, int id) {
                LoadElements(false);
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void showElementInput() {
        showProgress(false);
        mnuReview.setVisible(true);
        mnuSave.setVisible(true);
        mnuCancel.setVisible(true);
        GlobalClass.getInstance().setCurrentInventoryActiveProcess(true);
        inventory_btn_new_element.setVisibility(View.VISIBLE);
        setActionBarTittle();
        inventory_element.setVisibility(View.VISIBLE);
        inventory_data.setVisibility(View.GONE);
    }

    private void showInputInventoryCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("RCN Bodegas");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.input_inventory_code, (ViewGroup) getView(), false);
// Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();
                inventoryId = Integer.valueOf(m_Text.toString());
                asyncInventoryPendingById(inventoryId);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showMessageDialog(String res) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());

        dlgAlert.setMessage(res);
        dlgAlert.setTitle(getString(R.string.app_name));
        //dlgAlert.setPositiveButton(getString(R.string.Texto_Boton_Ok), null);
        dlgAlert.setPositiveButton(R.string.Texto_Boton_Ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                asyncListMaterialsByProduction(false);

            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    private void showMessageDialogError(String res) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());

        dlgAlert.setMessage(res);
        dlgAlert.setTitle(getString(R.string.app_name));
        //dlgAlert.setPositiveButton(getString(R.string.Texto_Boton_Ok), null);
        dlgAlert.setPositiveButton(R.string.Texto_Boton_Ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


            }
        });
        dlgAlert.setCancelable(false);
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

    private boolean validaIsAddeddElement(String barcode) {

        if (GlobalClass.getInstance().getDataMaterialInventory() != null)
            for (MaterialViewModel materialViewModel : GlobalClass.getInstance().getDataMaterialInventory()) {
                if (materialViewModel.getBarCode().equals(barcode))
                    if (materialViewModel.isReview())
                        return true;
            }
        return false;
    }

    private boolean validateFiels() {
        inventory_warehouse_option.setError(null);
        inventory_program_option.setError(null);
        inventory_date_option.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(inventory_warehouse_option.getText().toString())) {
            inventory_warehouse_option.setError(getString(R.string.error_warehouse_empty));
            focusView = inventory_warehouse_option;
            cancel = true;
        }
        if (TextUtils.isEmpty(inventory_program_option.getText().toString())) {
            inventory_program_option.setError(getString(R.string.error_program_empty));
            focusView = inventory_program_option;
            cancel = true;
        }
        if (TextUtils.isEmpty(inventory_date_option.getText().toString())) {
            inventory_date_option.setError(getString(R.string.error_fecha_empty));
            focusView = inventory_date_option;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    //Valida si hay un proceso de inventario en proceso
    private boolean validateInventoryProcess() {

        return GlobalClass.getInstance().getCurrentInventoryActiveProcess();
    }

    public void DataRecived(String BarcodeData) {
        final String _barcodeData = BarcodeData;
        getActivity().runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                inventory_element_barcode_edit.setText(_barcodeData);

                itemMaterialAdded = findElementByBarCode();

                if (itemMaterialAdded == null) {
                    showMessageDialogError(getString(R.string.message_not_find_element));
                    return;
                }
                if (!validaIsAddeddElement(inventory_element_barcode_edit.getText().toString())) {
                    setMaterialData(itemMaterialAdded);
                } else
                    showMessageDialogError("El elemento - " + itemMaterialAdded.getBarCode() + " ya fue inventariado");
            }
        });

    }

    @Override
    public void ScannerReady() {
        Scanner_manager.ScannerON(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PRODUCTION) {
            if (resultCode == -1) {
                String result = data.getStringExtra("productionName");
                GlobalClass.getInstance().setIdSelectedProductionInventory(data.getStringExtra("productionId"));
                this.inventory_program_option.setText(result);
                GlobalClass.getInstance().setCurrentproductionName(result);

            }
        }
        if (requestCode == REQUEST_RESPONSIBLE) {
            if (resultCode == -1) {
                String result = data.getStringExtra("responsibleName");
                GlobalClass.getInstance().setIdSelectedResponsibleInventory(Integer.valueOf(data.getStringExtra("responsibleId")));
                this.inventory_responsible_option.setText(result);

            }
        }
        if (requestCode == REQUEST_TYPE_ELEMENT) {
            if (resultCode == -1) {
                String result = data.getStringExtra("typeElementName");
                GlobalClass.getInstance().setIdSelectedTypeElementInventory(Integer.valueOf(data.getStringExtra("typeElementId")));
                this.inventory_element_type_edit.setText(result);

            }
        }
        if (requestCode == REQUEST_WAREHOUSE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("wareHouseName");
                this.inventory_warehouse_option.setText(result);
                GlobalClass.getInstance().setIdSelectedWareHouseInventory(data.getStringExtra("wareHouseId"));
                GlobalClass.getInstance().setNameSelectedWareHouseInventory(data.getStringExtra("wareHouseName"));
            }
        }
        if (requestCode == REQUEST_TYPE_ELEMENT_HEADER) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("typeElementName");
                GlobalClass.getInstance().setIdSelectedTypeElementHeader(Integer.valueOf(data.getStringExtra("typeElementId")));
                this.inventory_type_element_option.setText(result);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.title_inventory_fragment));

        dateTimeUtilities = new DateTimeUtilities(getActivity());
        setHasOptionsMenu(true);
        dialogo = new ProgressDialog(getActivity());
        dialogo.setMessage("Sincronizando...");


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inventory, menu);
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_inventory, container, false);
        inventory_element = view.findViewById(R.id.inventory_element);
        inventory_data = view.findViewById(R.id.inventory_data);
        inventory_element.setVisibility(View.GONE);

        InitializeControls(view);
        InitializeEvents();

        if (GlobalClass.getInstance().isContinueInventory())
            showInputInventoryCode();
        else {
            if (!validateInventoryProcess()) {
                inventory_element.setVisibility(View.GONE);
                inventory_data.setVisibility(View.VISIBLE);

                if (GlobalClass.getInstance().isNetworkAvailable())
                    asyncInventoryPending();
            } else {
                inventory_element.setVisibility(View.VISIBLE);
                inventory_data.setVisibility(View.GONE);
                setActionBarTittle();
            }
        }
        showProgress(false);
        return view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onDestroy() {

        Scanner_manager.ScannerOFF();

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {

            case R.id.mnu_review:
                intent = null;
                intent = new Intent(getActivity(), ListItemReviewActivity.class);
                //intent.putExtra("Inventory", "1");
                startActivity(intent);
                return true;
            case R.id.mnu_save:
                confirmInventory();
                return true;
            case R.id.mnu_cancel:
                confirmCancelInventory();
                return true;
        }

        return super.onOptionsItemSelected(item); // important line
    }

    @Override
    public void onPause() {
        super.onPause();
        Scanner_manager.ScannerOFF();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mnuReview = menu.findItem(R.id.mnu_review);
        mnuSave = menu.findItem(R.id.mnu_save);
        mnuCancel = menu.findItem(R.id.mnu_cancel);

        if (validateInventoryProcess()) {
            mnuReview.setVisible(true);
            mnuSave.setVisible(true);
            mnuCancel.setVisible(true);
        } else {
            mnuReview.setVisible(false);
            mnuSave.setVisible(false);
            mnuCancel.setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Scanner_manager = ScannerFactory.CreateScanner(getActivity().getApplicationContext(), getActivity());
        Scanner_manager.AddObserver(this);
        Scanner_manager.ScannerON(true);


    }

    @Override
    public void onStart() {

        super.onStart();

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public EditText txtDate;
        private DateTimeUtilities dateTimeUtilities;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {


            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            int _month = month + 1;
            //btnDate.setText(ConverterDate.ConvertDate(year, month + 1, day));
            txtDate.setText(dateTimeUtilities.parseDateTurno(year, month + 1, day));
        }
    }

    class asyncGetCountMaterial extends AsyncTask {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Object doInBackground(Object[] objects) {
            String res = "";
            Integer result = 0;
            SharedPreferences pref = Objects.requireNonNull(getActivity()).getApplicationContext().getSharedPreferences("materialbodegasPreferences", 0); // 0 - for private mode
            if (GlobalClass.getInstance().getIdSelectedWareHouseInventory().equals("A"))
                res = pref.getString("key_list_material_ambientacion_count", "0");
            else
                res = pref.getString("key_list_material_vestuario_count", "0");

            assert res != null;
            if (res.equals("0")) {
                countMaterialIndex = 0;
            } else
                result = Integer.valueOf(res);

            countMaterialIndex = result;
            return true;
        }

        @Override
        protected void onPostExecute(Object o) {
            dialogo.dismiss();
            if (countMaterialIndex > 1)
                new asyncGetMaterial().execute();
            else
                showMessageDialogError("No existen elementos, conectese a internet y sincronice los datos básicos");


            super.onPostExecute(o);
        }

        @Override
        protected void onPreExecute() {
            dialogo.setMessage("Calculando cantiadad de elementos...");
            dialogo.setIndeterminate(false);
            dialogo.setCancelable(false);
            dialogo.show();

        }
    }

    class asyncGetMaterial extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            String res;
            SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("materialbodegasPreferences", 0); // 0 - for private mode

            for (int i = 0; i < countMaterialIndex; i++) {
                res = pref.getString("key_list_material_" + i, "");
                TypeToken<List<MaterialViewModel>> token = new TypeToken<List<MaterialViewModel>>() {
                };
                Gson gson = new GsonBuilder().create();
                // Define Response class to correspond to the JSON response returned

                ArrayList<MaterialViewModel> materialViewModel = gson.fromJson(res, token.getType());

                if (materialViewModel != null)
                    for (MaterialViewModel viewModel : materialViewModel) {
                        if (dataMaterial == null)
                            dataMaterial = new ArrayList<>();
                        dataMaterial.add(viewModel);
                    }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            getLocalMagterialByProduction();
            dialogo.dismiss();
            super.onPostExecute(o);
        }

        @Override
        protected void onPreExecute() {
            dialogo.setMessage("Cargando datos...");
            dialogo.setIndeterminate(false);
            dialogo.setCancelable(false);
            dialogo.show();

        }
    }

}
