package com.rcnbodegas.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.LayerDrawable;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rcnbodegas.Activities.CustomActivity;
import com.rcnbodegas.Activities.ListItemAddedActivity;
import com.rcnbodegas.Activities.ProductionListActivity;
import com.rcnbodegas.Activities.ResponsibleListActivity;
import com.rcnbodegas.Activities.TypeElementListActivity;
import com.rcnbodegas.Activities.WareHouseListActivity;
import com.rcnbodegas.Activities.WarehouseUserActivity;
import com.rcnbodegas.Global.DateTimeUtilities;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.IObserver;
import com.rcnbodegas.Global.PhotoListAdapter;
import com.rcnbodegas.Global.PhotosAdapter;
import com.rcnbodegas.Global.ScannerFactory;
import com.rcnbodegas.Global.TScanner;
import com.rcnbodegas.Global.Utils;
import com.rcnbodegas.Global.onRecyclerProductionListItemClick;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.ProductionViewModel;
import com.symbol.emdk.barcode.Scanner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.app.Activity.RESULT_OK;


public class WarehouseFragment extends CustomActivity implements IObserver, DatePickerDialog.OnDateSetListener {
    private static final int REQUEST_PRODUCTION = 1;
    private static final int REQUEST_RESPONSIBLE = 2;
    private static final int REQUEST_TYPE_ELEMENT = 3;
    private static final int REQUEST_WAREHOUSE = 4;
    private static final int REQUEST_USER_WAREHOUSE = 5;
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1900;

    public PhotoListAdapter adapterPhotos;

    private Button warehouse_btn_ok;
    private CheckBox chkIsAdmin;
    private FloatingActionButton warehouse_btn_camera;
    private FloatingActionButton warehouse_btn_new_element;
    private EditText warehouse_date_option;
    private EditText warehouse_program_option;
    private EditText warehouse_option;
    private EditText warehouse_legalizedBy_option;

    private EditText warehouse_element_barcode_edit;
    private EditText warehouse_element_desc_edit;
    private EditText warehouse_element_type_edit;
    private EditText warehouse_element_edit;
    private EditText warehouse_element_price_edit;
    private EditText warehouse_element_value_edit;
    private EditText warehouse_user_option;

    private LinearLayout warehouse_element_layout;
    private LinearLayout warehouse_data;
    private LinearLayoutManager layoutManager;
    private DateTimeUtilities dateTimeUtilities;

    private ArrayList<String> ListFotos;
    private ArrayList<Bitmap> ListaImagenes;
    private ArrayList<String> listaNombresImagenes;

    private View mIncidenciasFormView;
    private View mProgressView;
    private Scanner scanner = null;
    private TScanner Scanner_manager = null;

    public String mCurrentPhotoPath;
    private GlobalClass globalVariable;
    private Uri file;
    private Integer consecutive = 1;
    private RecyclerView photos_recycler_view;
    private PhotosAdapter adapter;
    private MenuItem menuReview;
    private MenuItem menuSave;
    private MenuItem iconScanMenu;
    private String lastCreatedNUmberDocument = "";
    private boolean isOk;
    private MenuItem mnuCancel;
    private int mYear;
    private int mMonth;
    private int mDay;

    public WarehouseFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static WarehouseFragment newInstance(String param1, String param2) {
        WarehouseFragment fragment = new WarehouseFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(getString(R.string.title_element_legalization));

        dateTimeUtilities = new DateTimeUtilities(getActivity());

        globalVariable = (GlobalClass) getActivity().getApplicationContext();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_warehouse, container, false);
        InitializeControls(view);
        InitializeEvents();

        if (!validateInventoryProcess()) {
            warehouse_element_layout.setVisibility(View.GONE);
            warehouse_data.setVisibility(View.VISIBLE);
        } else {
            warehouse_element_layout.setVisibility(View.VISIBLE);
            warehouse_data.setVisibility(View.GONE);

        }
        return view;
    }

    @Override
    public void DataRecived(final String BarcodeData) {
        final String _barcodeData = BarcodeData;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!chkIsAdmin.isChecked())
                    warehouse_element_barcode_edit.setText(BarcodeData);
                else {
                    showMessageDialog("El elemento es administrativo, lo cual no se asignará codigo de barras");
                    warehouse_element_barcode_edit.setText("");
                }
            }
        });

    }

    @Override
    public void ScannerReady() {
        Scanner_manager.ScannerON(true);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Scanner_manager.ScannerOFF();
    }

    @Override
    public void onPause() {
        super.onPause();
        Scanner_manager.ScannerOFF();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            SetImage(data);
        }
        if (requestCode == REQUEST_PRODUCTION) {
            if (resultCode == -1) {
                String result = data.getStringExtra("productionName");
                globalVariable.setIdSelectedProductionWarehouse(data.getStringExtra("productionId"));
                this.warehouse_program_option.setText(result);

            }
        }
        if (requestCode == REQUEST_RESPONSIBLE) {
            if (resultCode == -1) {
                String result = data.getStringExtra("responsibleName");
                globalVariable.setIdSelectedResponsibleWarehouse(Integer.valueOf(data.getStringExtra("responsibleId")));
                this.warehouse_legalizedBy_option.setText(result);

            }
        }
        if (requestCode == REQUEST_TYPE_ELEMENT) {
            if (resultCode == -1) {
                String result = data.getStringExtra("typeElementName");
                globalVariable.setIdSelectedTypeElementWarehouse(Integer.valueOf(data.getStringExtra("typeElementId")));
                this.warehouse_element_type_edit.setText(result);

                if (globalVariable.getIdSelectedTypeElementWarehouse().toString().equals(globalVariable.getAdminTypeElementId()))
                    this.warehouse_element_barcode_edit.setText("");

            }
        }
        if (requestCode == REQUEST_WAREHOUSE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("wareHouseName");
                this.warehouse_option.setText(result);
                globalVariable.setIdSelectedWareHouseWarehouse(data.getStringExtra("wareHouseId"));
                globalVariable.setNameSelectedWareHouseWarehouse(data.getStringExtra("wareHouseName"));
            }
        }
        if (requestCode == REQUEST_USER_WAREHOUSE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("responsibleWarehouseName");
                globalVariable.setIdSelectedUserWarehouse(Integer.valueOf(data.getStringExtra("responsibleWarehouseId")));
                this.warehouse_user_option.setText(result);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuReview = menu.findItem(R.id.menu_review);
        menuSave = menu.findItem(R.id.mnu_save);
        mnuCancel = menu.findItem(R.id.mnu_cancel);

        if (!validateInventoryProcess()) {
            menuReview.setVisible(false);
            menuSave.setVisible(false);
            mnuCancel.setVisible(false);
            warehouse_btn_camera.setVisibility(View.GONE);
            warehouse_btn_new_element.setVisibility(View.GONE);


        } else {
            menuReview.setVisible(true);
            menuSave.setVisible(true);
            mnuCancel.setVisible(true);
            warehouse_btn_camera.setVisibility(View.VISIBLE);
            warehouse_btn_new_element.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_ingreso_elemento, menu);
        iconScanMenu = menu.findItem(R.id.menu_review);
        LayerDrawable icon = (LayerDrawable) iconScanMenu.getIcon();
        Utils.setBadgeCount(getActivity(), icon, globalVariable.getDataMaterial().size());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        Intent intent = null;
        switch (item.getItemId()) {
            /**/
            case R.id.menu_review:
                globalVariable.setListMaterialForAdd(globalVariable.getDataMaterial());
                intent = null;
                intent = new Intent(getActivity(), ListItemAddedActivity.class);
                startActivity(intent);
                return true;
            case R.id.mnu_save:
                confirmAddNewELement();
                return true;
            case R.id.mnu_cancel:
                confirmCancelProcess();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                    showCamera();
                }
                if (!canUseExternalStorage) {
                    Toast.makeText(getActivity(), "No se puede usar esta función sin el permiso solicitado", Toast.LENGTH_SHORT).show();
                } else {
                    // user now provided permission
                    // perform function for what you want to achieve
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    private void InitializaNewAddElement() {

        try {

            asyncListMaterialsByProduction();

        } catch (Exception ex) {
            showMessageDialog(ex.getMessage());
        }

    }

    //Valida si hay un proceso de inventario en proceso
    private boolean validateInventoryProcess() {

        return globalVariable.getCurrentAddElementActiveProcess();
    }


    private void addMaterialToList() {
        if (globalVariable.getDataMaterial() == null)
            globalVariable.setDataMaterial(new ArrayList<MaterialViewModel>());
    }


    public void SetImage(Intent data) {


        if (ListFotos == null)
            ListFotos = new ArrayList<String>();

        try {

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels / 2; //dividimos por 2 pues no se ven a pantalla completa.
            int width = metrics.widthPixels / 2; //dividimos por 2 pues no se ven a pantalla completa.
            File file = new File(globalVariable.getmCurrentPhotoPath());
            Bitmap bitmap;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(globalVariable.getmCurrentPhotoPath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / width, photoH / height);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

            //f.delete();
            String timeStamp = Long.toString(Calendar.getInstance().getTimeInMillis());
            String prefijo = warehouse_element_barcode_edit.getText().toString() + consecutive;
            prefijo = prefijo.replace(' ', '_');
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fechaActual = sdf.format(new Date());
            String nombreFichero = prefijo + "_" + fechaActual;
            ListFotos.add(globalVariable.getmCurrentPhotoPath());
            String srSignature = "";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();


            srSignature = Base64.encodeToString(b, Base64.DEFAULT);


            LoadPhoto(bitmap, nombreFichero);


        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream is;
    }

    private void InitializeControls(View v) {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);

        warehouse_btn_ok = v.findViewById(R.id.warehouse_btn_ok);
        warehouse_btn_camera = v.findViewById(R.id.warehouse_btn_camera);
        warehouse_btn_new_element = v.findViewById(R.id.warehouse_btn_new_element);

        warehouse_option = v.findViewById(R.id.warehouse_option);
        warehouse_program_option = v.findViewById(R.id.warehouse_program_option);
        warehouse_legalizedBy_option = v.findViewById(R.id.warehouse_legalizedBy_option);
        warehouse_date_option = v.findViewById(R.id.warehouse_date_option);
        warehouse_element_layout = v.findViewById(R.id.warehouse_element_layout);
        warehouse_element_barcode_edit = v.findViewById(R.id.warehouse_element_barcode_edit);
        warehouse_element_desc_edit = v.findViewById(R.id.warehouse_element_desc_edit);
        warehouse_element_type_edit = v.findViewById(R.id.warehouse_element_type_edit);
        warehouse_element_edit = v.findViewById(R.id.warehouse_element_edit);
        warehouse_element_price_edit = v.findViewById(R.id.warehouse_element_price_edit);
        warehouse_element_value_edit = v.findViewById(R.id.warehouse_element_value_edit);
        warehouse_user_option = v.findViewById(R.id.warehouse_user_option);
        warehouse_data = v.findViewById(R.id.warehouse_data);
        chkIsAdmin = v.findViewById(R.id.chkIsAdmin);
        warehouse_date_option.setText(dateTimeUtilities.parseDateTurno());
        warehouse_option.setText(globalVariable.getNameSelectedWareHouseWarehouse());

        photos_recycler_view = (RecyclerView) v.findViewById(R.id.photos_recycler_view);
        photos_recycler_view.setHasFixedSize(true);

        mIncidenciasFormView = v.findViewById(R.id.layout_header);
        mProgressView = v.findViewById(R.id.warehouse_progress);
        layoutManager = new LinearLayoutManager(getActivity());
        photos_recycler_view.setLayoutManager(layoutManager);
        photos_recycler_view.setItemAnimator(new DefaultItemAnimator());

        if (ListFotos == null)
            ListFotos = new ArrayList<>();

    }

    private void InitializeEvents() {
        globalVariable.setQueryByInventory(false);
        warehouse_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFieldsHeader();
            }
        });

        warehouse_btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    showCamera();
                else
                    dispatchTakePictureIntent();
            }
        });
        warehouse_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenListWareHouse();
            }
        });

        warehouse_btn_new_element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });

        warehouse_program_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(getActivity(), ProductionListActivity.class);
                startActivityForResult(intent, REQUEST_PRODUCTION);
            }
        });
        warehouse_user_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;

                if (warehouse_option.getText().toString().equals("")) {
                    warehouse_option.setError(getString(R.string.error_warehouse_empty));
                    return;
                }
                warehouse_option.setError(null);
                intent = new Intent(getActivity(), WarehouseUserActivity.class);
                startActivityForResult(intent, REQUEST_USER_WAREHOUSE);
            }
        });
        warehouse_date_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new InventoryFragment.DatePickerFragment();
                /*((DatePickerFragment) newFragment).txtDate = warehouse_date_option;
                ((DatePickerFragment) newFragment).dateTimeUtilities = dateTimeUtilities;

                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");*/
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                warehouse_date_option.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                mYear = year;
                                mMonth = (monthOfYear + 1);
                                mDay = dayOfMonth;

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        warehouse_legalizedBy_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ResponsibleListActivity.class);
                intent.putExtra("title_option", R.string.title_legalized_by);

                if (warehouse_program_option.getText().toString().equals("")) {
                    warehouse_program_option.setError(getString(R.string.error_program_empty));
                    return;
                } else
                    warehouse_program_option.setError(null);
                globalVariable.setResponsable(false);
                startActivityForResult(intent, REQUEST_RESPONSIBLE);
            }
        });

        warehouse_element_type_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(getActivity(), TypeElementListActivity.class);
                startActivityForResult(intent, REQUEST_TYPE_ELEMENT);
            }
        });

        chkIsAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    warehouse_element_barcode_edit.setText("");
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private boolean validateFieldsHeader() {
        warehouse_option.setError(null);
        warehouse_program_option.setError(null);
        warehouse_legalizedBy_option.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(warehouse_option.getText().toString())) {
            warehouse_option.setError(getString(R.string.error_warehouse_empty));
            focusView = warehouse_option;
            cancel = true;
        }
        if (TextUtils.isEmpty(warehouse_program_option.getText().toString())) {
            warehouse_program_option.setError(getString(R.string.error_program_empty));
            focusView = warehouse_program_option;
            cancel = true;
        }
        if (TextUtils.isEmpty(warehouse_date_option.getText().toString())) {
            warehouse_date_option.setError(getString(R.string.error_fecha_empty));
            focusView = warehouse_date_option;
            cancel = true;
        }
        if (TextUtils.isEmpty(warehouse_legalizedBy_option.getText().toString())) {
            warehouse_legalizedBy_option.setError(getString(R.string.error_responsible_empty));
            focusView = warehouse_legalizedBy_option;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            warehouse_element_layout.setVisibility(View.VISIBLE);
            warehouse_data.setVisibility(View.GONE);
            globalVariable.setCurrentAddElementActiveProcess(true);
            warehouse_btn_camera.setVisibility(View.VISIBLE);
            warehouse_btn_new_element.setVisibility(View.VISIBLE);
            menuSave.setVisible(true);
            menuReview.setVisible(true);
            mnuCancel.setVisible(true);
            LayerDrawable icon = (LayerDrawable) iconScanMenu.getIcon();
            Utils.setBadgeCount(getActivity(), icon, globalVariable.getDataMaterial().size());
        }
        return true;
    }

    private boolean validateFields() {
        warehouse_element_barcode_edit.setError(null);
        warehouse_element_desc_edit.setError(null);
        warehouse_element_type_edit.setError(null);
        warehouse_element_edit.setError(null);
        warehouse_element_price_edit.setError(null);
        warehouse_element_value_edit.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (!chkIsAdmin.isChecked())
            if (TextUtils.isEmpty(warehouse_element_barcode_edit.getText().toString())) {
                warehouse_element_barcode_edit.setError(getString(R.string.error_warehouse_empty));
                focusView = warehouse_element_barcode_edit;
                cancel = true;
            }

        if (TextUtils.isEmpty(warehouse_element_desc_edit.getText().toString())) {
            warehouse_element_desc_edit.setError(getString(R.string.error_program_empty));
            focusView = warehouse_element_desc_edit;
            cancel = true;
        }
        if (TextUtils.isEmpty(warehouse_element_type_edit.getText().toString())) {
            warehouse_element_type_edit.setError(getString(R.string.error_fecha_empty));
            focusView = warehouse_element_type_edit;
            cancel = true;
        }
        if (TextUtils.isEmpty(warehouse_element_edit.getText().toString())) {
            warehouse_element_edit.setError(getString(R.string.error_responsible_empty));
            focusView = warehouse_element_edit;
            cancel = true;
        }
        if (TextUtils.isEmpty(warehouse_element_price_edit.getText().toString())) {
            warehouse_element_price_edit.setError(getString(R.string.error_fecha_empty));
            focusView = warehouse_element_price_edit;
            cancel = true;
        }
        if (TextUtils.isEmpty(warehouse_element_value_edit.getText().toString())) {
            warehouse_element_value_edit.setError(getString(R.string.error_valor_materia_empty));
            focusView = warehouse_element_value_edit;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            addElement();
        }
        return true;
    }

    @SuppressLint("RestrictedApi")
    private void addElement() {
        showProgress(true);
        MaterialViewModel newElement = new MaterialViewModel();
        newElement.setBarCode(warehouse_element_barcode_edit.getText().toString());
        newElement.setWareHouseId(globalVariable.getIdSelectedWareHouseWarehouse());
        newElement.setProductionId(Integer.valueOf(globalVariable.getIdSelectedProductionWarehouse()));
        newElement.setResponsibleId(globalVariable.getIdSelectedResponsibleWarehouse());
        newElement.setMaterialName(warehouse_element_desc_edit.getText().toString());
        newElement.setMarca(warehouse_element_edit.getText().toString());
        newElement.setLegalizedBy(String.valueOf(globalVariable.getIdSelectedResponsibleWarehouse()));
        newElement.setUnitPrice(warehouse_element_price_edit.getText().toString().equals("") ? 0 : Double.valueOf(warehouse_element_price_edit.getText().toString()));
        newElement.setTypeElementId(String.valueOf(globalVariable.getIdSelectedTypeElementWarehouse()));
        newElement.setTypeElementName(warehouse_element_type_edit.getText().toString());
        newElement.setPurchaseValue(warehouse_element_value_edit.getText().toString().equals("") ? 0 : Double.valueOf(warehouse_element_value_edit.getText().toString()));
        newElement.setSaleDate(dateTimeUtilities.parseDateTurno(mYear, mMonth - 1, mDay));
        newElement.setTerceroActual(globalVariable.getIdSelectedUserWarehouse());
        newElement.setAdmin(chkIsAdmin.isChecked());


        if (ListaImagenes == null) ListaImagenes = new ArrayList<>();

        if (ListFotos != null)
            for (String photo : ListFotos) {
                newElement.getListaImagenesStr().add(parseImage(photo));
            }

        if (globalVariable.getDataMaterial() == null)
            globalVariable.setDataMaterial(new ArrayList<MaterialViewModel>());

        menuReview.setVisible(true);
        menuSave.setVisible(true);


        globalVariable.getDataMaterial().add(newElement);
        clearFields();

        LayerDrawable icon = (LayerDrawable) iconScanMenu.getIcon();
        Utils.setBadgeCount(getActivity(), icon, globalVariable.getDataMaterial().size());
        showProgress(false);

    }

    private String parseImage(String photo) {
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/bodegas_images");
        File f = new File(folder, photo);
        byte[] b = readPhotoAndRezise(photo, 2);
        String srSignature = Base64.encodeToString(b, Base64.DEFAULT);

        return srSignature;
    }


    private void confirmAddNewELement() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("Está seguro de guardar los elementos agregados?");
        builder.setPositiveButton(getString(R.string.btn_confirm),
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InitializaNewAddElement();
                        dialog.dismiss();
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

    private void confirmCancelProcess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.message_confirm_cancel));
        builder.setPositiveButton(getString(R.string.btn_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InitializeNewProcess();
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

    private void clearFields() {
        warehouse_element_barcode_edit.setText("");
        warehouse_element_desc_edit.setText("");
        warehouse_element_type_edit.setText("");
        warehouse_element_edit.setText("");
        warehouse_element_price_edit.setText("");
        warehouse_element_value_edit.setText("");
        chkIsAdmin.setChecked(false);
        globalVariable.setIdSelectedTypeElementWarehouse(-1);

        if (ListaImagenes != null) {
            ListaImagenes.clear();
            ListFotos.clear();
            setListImagesAdapter();
        }
    }

    private void OpenListWareHouse() {
        Intent intent = new Intent(getActivity(), WareHouseListActivity.class);
        startActivityForResult(intent, REQUEST_WAREHOUSE);
    }

    private void showCamera() {
        try {

            if (warehouse_element_barcode_edit.getText().toString().equals("")) {
                showMessageDialog(getString(R.string.message_not_barcode));
                return;
            }

            List<String> listPermissionsNeeded = new ArrayList<>();
            int camera = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA);
            int storage = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (storage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray
                        (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);

            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String destination = Environment.getExternalStorageDirectory().getPath() + "/image.jpg";
                Uri outputUri = Uri.fromFile(new File(destination));
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {

                        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                "com.rcnbodegas.fileprovider",
                                photoFile);
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessageDialog(String res) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());

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

    private byte[] readPhotoAndRezise(String f, int ToScale) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        File fdelete = new File(f);
        Bitmap bMap = BitmapFactory.decodeFile(f.toString(), options);
        if (ToScale > 1) bMap = scaleBitmap(bMap, ToScale);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bMap.getByteCount());
        bMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        bMap.recycle();
        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        baos = null;

        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + f);
            } else {
                System.out.println("file not Deleted :" + f);
            }
        }
        return b;
    }

    private Bitmap scaleBitmap(Bitmap bm, int ToScale) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        int maxWidth = width / ToScale;
        int maxHeight = height / ToScale;

        if (width > height) {
            // landscape
            int ratio = width / maxWidth;
            width = maxWidth;
            height = height / ratio;
        } else if (height > width) {
            // portrait
            int ratio = height / maxHeight;
            height = maxHeight;
            width = width / ratio;
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }


        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String prefijo = "GENERAL";
        prefijo = prefijo.replace(' ', '_');
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fechaActual = sdf.format(new Date());
        String nombreFichero = prefijo + "_" + fechaActual;
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                nombreFichero,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        globalVariable.setmCurrentPhotoPath(mCurrentPhotoPath);
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.gestpark.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getActivity().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                getActivity().startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private void LoadPhoto(Bitmap photo, String nombreFichero) {

        try {
            if (ListaImagenes == null) {
                ListaImagenes = new ArrayList<Bitmap>();
                listaNombresImagenes = new ArrayList<>();
                ListaImagenes.add(photo);
                listaNombresImagenes.add(nombreFichero);
            } else {
                ListaImagenes.add(photo);
                listaNombresImagenes.add(nombreFichero);
            }

            setListImagesAdapter();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListImagesAdapter() {
        adapter = new PhotosAdapter(ListaImagenes, listaNombresImagenes, new onRecyclerProductionListItemClick() {
            @Override
            public void onClick(ProductionViewModel wareHouseViewModel) {

            }
        });
        photos_recycler_view.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void asyncListMaterialsByProduction() {

        showProgress(true);
        String wareHouse = globalVariable.getQueryByInventory() ? globalVariable.getIdSelectedWareHouseInventory() : globalVariable.getIdSelectedWareHouseWarehouse();

        String url = globalVariable.getUrlServices() + "warehouse/CreateElement/" + wareHouse;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        String tipo = "application/json";

        StringEntity entity = null;
        Gson json = new Gson();

        String resultJson = json.toJson(globalVariable.getDataMaterial());

        entity = new StringEntity(resultJson, StandardCharsets.UTF_8);

        client.post(getActivity().getApplicationContext(), url, entity, tipo, new TextHttpResponseHandler() {

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
                showProgress(false);
                showMessageDialog(responseBody);
                isOk = false;
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onFinish() {
                super.onFinish();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isOk == true) {
                            InitializeNewProcess();
                            showProgress(false);
                            showMessageDialog(lastCreatedNUmberDocument);
                        }
                    }
                });

            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void InitializeNewProcess() {
        globalVariable.setIdSelectedProductionWarehouse("");
        warehouse_program_option.setText("");
        globalVariable.setIdSelectedResponsibleWarehouse(-1);
        warehouse_legalizedBy_option.setText("");
        globalVariable.setIdSelectedTypeElementWarehouse(-1);
        warehouse_element_type_edit.setText("");
        globalVariable.setCurrentAddElementActiveProcess(false);
        warehouse_btn_camera.setVisibility(View.GONE);
        warehouse_btn_new_element.setVisibility(View.GONE);
        menuSave.setVisible(false);
        menuReview.setVisible(false);
        mnuCancel.setVisible(false);
        warehouse_element_layout.setVisibility(View.GONE);
        warehouse_data.setVisibility(View.VISIBLE);
        globalVariable.getDataMaterial().clear();
        chkIsAdmin.setChecked(false);

        if (ListFotos != null) ListFotos.clear();
        if (ListaImagenes != null) ListaImagenes.clear();
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
}
