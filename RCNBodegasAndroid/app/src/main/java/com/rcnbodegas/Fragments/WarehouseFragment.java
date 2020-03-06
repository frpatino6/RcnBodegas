package com.rcnbodegas.Fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.rcnbodegas.CustomEvents.onRecyclerProductionListItemClick;
import com.rcnbodegas.Global.DateTimeUtilities;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.NumberTextWatcher;
import com.rcnbodegas.Global.PhotosAdapter;
import com.rcnbodegas.Global.ScannerFactory;
import com.rcnbodegas.Global.SyncService;
import com.rcnbodegas.Global.TScanner;
import com.rcnbodegas.Global.Utils;
import com.rcnbodegas.Interfaces.IObserver;
import com.rcnbodegas.R;
import com.rcnbodegas.Repository.MaterialHeaderRepository;
import com.rcnbodegas.Repository.MaterialImagesRepository;
import com.rcnbodegas.Repository.MaterialRepository;
import com.rcnbodegas.ViewModels.MaterialImagesViewModel;
import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.MaterialViewmodelHeader;
import com.rcnbodegas.ViewModels.ProductionViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private static final int REQUEST_REVIEW = 6;
    private static MaterialViewModel _MaterialViewModel;
    public String mCurrentPhotoPath;
    private ArrayList<String> ListFotos;
    private ArrayList<Bitmap> ListaImagenes;
    private TScanner Scanner_manager = null;
    private CheckBox chkIsAdmin;
    private DateTimeUtilities dateTimeUtilities;
    private MenuItem iconScanMenu;
    private boolean isOk;
    private String lastCreatedNUmberDocument = "";
    private ArrayList<String> listaNombresImagenes;
    private int mDay;
    private View mIncidenciasFormView;
    private int mMonth;
    private View mProgressView;
    private int mYear;
    private MaterialHeaderRepository materialHeaderRepository;
    private MaterialImagesRepository materialImagesRepository;
    private MaterialRepository materialRepository;
    private MaterialViewmodelHeader materialViewmodelHeader;
    private MenuItem menuReview;
    private MenuItem menuSave;
    private MenuItem mnuCancel;
    private RecyclerView photos_recycler_view;
    private String selectedProductionName;
    private FloatingActionButton warehouse_btn_camera;
    private FloatingActionButton warehouse_btn_new_element;
    private Button warehouse_btn_ok;
    private LinearLayout warehouse_data;
    private EditText warehouse_date_option;
    private EditText warehouse_element_barcode_edit;
    private EditText warehouse_element_desc_edit;
    private EditText warehouse_element_edit;
    private LinearLayout warehouse_element_layout;
    private EditText warehouse_element_price_edit;
    private TextView warehouse_element_prod;
    private EditText warehouse_element_type_edit;
    private EditText warehouse_element_value_edit;
    private EditText warehouse_legalizedBy_option;
    private EditText warehouse_option;
    private EditText warehouse_program_option;
    private EditText warehouse_user_option;

    public WarehouseFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    private void InitializaNewAddElement() {

        try {
            if (GlobalClass.getInstance().isNetworkAvailable()) {
                asyncListMaterialsByProduction();
            } else {
                confirmAddForSyncAfter();
            }

        } catch (Exception ex) {
            showMessageDialog(ex.getMessage());
            confirmAddForSyncAfterByGenerateError("Se presentó un error");
        }

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
        warehouse_element_prod = v.findViewById(R.id.warehouse_element_prod);
        warehouse_element_value_edit = v.findViewById(R.id.warehouse_element_value_edit);
        warehouse_user_option = v.findViewById(R.id.warehouse_user_option);
        warehouse_data = v.findViewById(R.id.warehouse_data);
        chkIsAdmin = v.findViewById(R.id.chkIsAdmin);
        warehouse_date_option.setText(dateTimeUtilities.parseDateTurno());
        warehouse_option.setText(GlobalClass.getInstance().getNameSelectedWareHouseWarehouse());

        photos_recycler_view = v.findViewById(R.id.photos_recycler_view);
        photos_recycler_view.setHasFixedSize(true);

        warehouse_element_price_edit.addTextChangedListener(new NumberTextWatcher(warehouse_element_price_edit, "#,###"));
        warehouse_element_value_edit.addTextChangedListener(new NumberTextWatcher(warehouse_element_value_edit, "#,###"));

        mIncidenciasFormView = v.findViewById(R.id.layout_header);
        mProgressView = v.findViewById(R.id.warehouse_progress);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        photos_recycler_view.setLayoutManager(layoutManager);
        photos_recycler_view.setItemAnimator(new DefaultItemAnimator());

        if (ListFotos == null)
            ListFotos = new ArrayList<>();

    }

    private void InitializeEvents() {
        GlobalClass.getInstance().setQueryByInventory(false);
        warehouse_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFieldsHeader();
            }
        });

        warehouse_btn_camera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                Intent intent;
                intent = new Intent(getActivity(), ProductionListActivity.class);
                startActivityForResult(intent, REQUEST_PRODUCTION);
            }
        });
        warehouse_user_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

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
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                new InventoryFragment.DatePickerFragment();
                /*((DatePickerFragment) newFragment).txtDate = warehouse_date_option;
                ((DatePickerFragment) newFragment).dateTimeUtilities = dateTimeUtilities;

                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");*/
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                        new DatePickerDialog.OnDateSetListener() {

                            @SuppressLint("SetTextI18n")
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
                GlobalClass.getInstance().setResponsable(false);
                startActivityForResult(intent, REQUEST_RESPONSIBLE);
            }
        });

        warehouse_element_type_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), TypeElementListActivity.class);
                startActivityForResult(intent, REQUEST_TYPE_ELEMENT);
            }
        });

        warehouse_element_price_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    warehouse_element_price_edit.setText("");
            }
        });

        warehouse_element_value_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    warehouse_element_value_edit.setText("");
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
    private void InitializeNewProcess() {
        GlobalClass.getInstance().setIdSelectedProductionWarehouse("");
        warehouse_program_option.setText("");
        GlobalClass.getInstance().setIdSelectedResponsibleWarehouse(-1);
        warehouse_legalizedBy_option.setText("");
        GlobalClass.getInstance().setIdSelectedTypeElementWarehouse(-1);
        warehouse_element_type_edit.setText("");
        GlobalClass.getInstance().setCurrentAddElementActiveProcess(false);
        warehouse_btn_camera.setVisibility(View.GONE);
        warehouse_btn_new_element.setVisibility(View.GONE);
        menuSave.setVisible(false);
        menuReview.setVisible(false);
        mnuCancel.setVisible(false);
        warehouse_element_layout.setVisibility(View.GONE);
        warehouse_element_value_edit.setText("$0.0");
        warehouse_data.setVisibility(View.VISIBLE);
        GlobalClass.getInstance().getDataMaterial().clear();
        chkIsAdmin.setChecked(false);

        if (ListFotos != null) ListFotos.clear();
        if (ListaImagenes != null) ListaImagenes.clear();

        materialViewmodelHeader.setStatus(2);

        if (materialViewmodelHeader == null)
            materialViewmodelHeader = materialHeaderRepository.getLegalizationPendingProcess();

        materialViewmodelHeader.setStatus(2);
        materialHeaderRepository.update(materialViewmodelHeader);
    }

    private void LoadPhoto(Bitmap photo, String nombreFichero) {

        try {
            if (ListaImagenes == null) {
                ListaImagenes = new ArrayList<>();
                listaNombresImagenes = new ArrayList<>();
                ListaImagenes.add(photo);
                if (listaNombresImagenes != null)
                    listaNombresImagenes.add(nombreFichero);
            } else {
                ListaImagenes.add(photo);

                if (listaNombresImagenes != null)
                    listaNombresImagenes.add(nombreFichero);
            }

            setListImagesAdapter();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void OpenListWareHouse() {
        Intent intent = new Intent(getActivity(), WareHouseListActivity.class);
        startActivityForResult(intent, REQUEST_WAREHOUSE);
    }

    private void PrintCountElementes() {
        LayerDrawable icon = (LayerDrawable) iconScanMenu.getIcon();
        Utils.setBadgeCount(getActivity(), icon, GlobalClass.getInstance().getDataMaterial().size());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void SaveDocumentForSync() {

        Objects.requireNonNull(getActivity()).stopService(new Intent(getActivity(), SyncService.class));
        GlobalClass.getInstance().getListMaterialForSync().add((ArrayList<MaterialViewModel>) GlobalClass.getInstance().getDataMaterial().clone());
        InitializeNewProcess();
        getActivity().startService(new Intent(getActivity(), SyncService.class));

    }

    private View SetDialogConfig(LayoutInflater inflater, ViewGroup container) {
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
            getLegalizationMaterialDetail();
            setActionBarTittle();
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void SetImage() {


        if (ListFotos == null) {
            ListFotos = new ArrayList<>();
        }

        try {

            DisplayMetrics metrics = new DisplayMetrics();
            Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels / 2; //dividimos por 2 pues no se ven a pantalla completa.
            int width = metrics.widthPixels / 2; //dividimos por 2 pues no se ven a pantalla completa.
            File file = new File(GlobalClass.getInstance().getmCurrentPhotoPath());
            Bitmap bitmap;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(GlobalClass.getInstance().getmCurrentPhotoPath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / width, photoH / height);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);


            int consecutive = 1;
            String prefijo = warehouse_element_barcode_edit.getText().toString() + consecutive;
            prefijo = prefijo.replace(' ', '_');
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fechaActual = sdf.format(new Date());
            String nombreFichero = prefijo + "_" + fechaActual;
            ListFotos.add(GlobalClass.getInstance().getmCurrentPhotoPath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            bitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

            LoadPhoto(bitmap, nombreFichero);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean ValidateBarCode() {
        if (GlobalClass.getInstance().getDataMaterial().size() > 0)
            for (MaterialViewModel materialViewModel : GlobalClass.getInstance().getDataMaterial()) {
                if (materialViewModel.getBarCode().equals(warehouse_element_barcode_edit.getText().toString()))
                    return false;
            }
        return true;
    }

    @SuppressLint("RestrictedApi")
    private void addElement() {
        ProgressDialog dialogo = new ProgressDialog(getActivity());
        try {
            new DecimalFormat("0.00");

            dialogo.setMessage("Enviando...");
            MaterialViewModel newElement = new MaterialViewModel();
            newElement.setBarCode(warehouse_element_barcode_edit.getText().toString());
            newElement.setWareHouseId(materialViewmodelHeader.getWarehouseId());
            newElement.setProductionId(Integer.valueOf(materialViewmodelHeader.getProductionId()));
            newElement.setResponsibleId(materialViewmodelHeader.getResponsibleId());
            newElement.setMaterialName(warehouse_element_desc_edit.getText().toString());
            newElement.setMarca(warehouse_element_edit.getText().toString());
            newElement.setLegalizedBy(String.valueOf(materialViewmodelHeader.getResponsibleId()));


            String currencyUnitPriceString;
            currencyUnitPriceString = warehouse_element_price_edit.getText().toString()
                    .replace(",", "")
                    .replace(".", ".")
                    .replaceAll("[^\\d.-]", "");

            String currencyPurchaseString = warehouse_element_value_edit.getText().toString()
                    .replace(",", "")
                    .replace(".", ".")
                    .replaceAll("[^\\d.-]", "");

            newElement.setUnitPrice(Double.valueOf(currencyUnitPriceString));
            newElement.setTypeElementId(String.valueOf(GlobalClass.getInstance().getIdSelectedTypeElementWarehouse()));
            newElement.setTypeElementName(warehouse_element_type_edit.getText().toString());
            newElement.setPurchaseValue(Double.valueOf(currencyPurchaseString));
            newElement.setSaleDate(dateTimeUtilities.parseDateTurno(mYear, mMonth - 1, mDay));
            newElement.setTerceroActual(materialViewmodelHeader.getUserserWarehouse());
            newElement.setAdmin(chkIsAdmin.isChecked());
            newElement.setIdHeaderLegalization(materialViewmodelHeader.getId());


            long idMaterial = materialRepository.insert(newElement);

            if (ListaImagenes == null) ListaImagenes = new ArrayList<>();
            long id = 0;

            for (Bitmap photo : ListaImagenes) {
                newElement.getListaImagenesBmp().add(photo);
                byte[] byteArray = getByteArrayFromBitmap(photo);
                String str = Base64.encodeToString(byteArray, Base64.DEFAULT);
                materialImagesRepository.insert(new MaterialImagesViewModel(byteArray, idMaterial, str));
            }

            if (GlobalClass.getInstance().getDataMaterial() == null)
                GlobalClass.getInstance().setDataMaterial(new ArrayList<MaterialViewModel>());

            menuReview.setVisible(true);
            menuSave.setVisible(true);

            GlobalClass.getInstance().getDataMaterial().add(newElement);
            clearFields();
            PrintCountElementes();
            dialogo.dismiss();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            dialogo.dismiss();
            showMessageDialog(e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            dialogo.dismiss();
            showMessageDialog(e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void asyncListMaterialsByProduction() throws IOException {

        try {
            final ProgressDialog dialogo = new ProgressDialog(getActivity());
//            final ProgressDialog dialogo = null;
            dialogo.setMessage("Sincronizando materiales...");
            dialogo.setIndeterminate(false);
            dialogo.setCancelable(false);
            dialogo.show();

            String wareHouse = materialViewmodelHeader.getWarehouseId();//GlobalClass.getInstance().getQueryByInventory() ? GlobalClass.getInstance().getIdSelectedWareHouseInventory() : GlobalClass.getInstance().getIdSelectedWareHouseWarehouse();

            String url = GlobalClass.getInstance().getUrlServices() + "warehouse/CreateElement/" + wareHouse;
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(60000);
            String tipo = "application/json";

            StringEntity entity;
            Gson json = new Gson();

            for (MaterialViewModel materialViewModel : GlobalClass.getInstance().getDataMaterial()) {
                materialViewModel.getListaImagenesBmp().clear();
            }

            String resultJson = json.toJson(GlobalClass.getInstance().getDataMaterial());


            entity = new StringEntity(resultJson, StandardCharsets.UTF_8);

            client.post(Objects.requireNonNull(getActivity()).getApplicationContext(), url, entity, tipo, new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {

                    showMessageDialog(responseBody);
                    isOk = false;
                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onFinish() {
                    super.onFinish();
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isOk) {
                                InitializeNewProcess();
                                showMessageDialog(lastCreatedNUmberDocument);
                            }
                        }
                    });
                    dialogo.dismiss();
                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    isOk = true;
                    Gson gson = new GsonBuilder().create();
                    // Define Response class to correspond to the JSON response returned
                    lastCreatedNUmberDocument = gson.fromJson(responseString, String.class);
                    materialViewmodelHeader.setStatus(2);
                    materialHeaderRepository.update(materialViewmodelHeader);
                    materialImagesRepository.deleteALl();


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //confirmAddForSyncAfterByGenerateError(e.getMessage());
        }
    }

    private void clearFields() {
        warehouse_element_barcode_edit.setText("");
        warehouse_element_desc_edit.setText("");
        warehouse_element_type_edit.setText("");
        warehouse_element_edit.setText("");
        warehouse_element_price_edit.setText("");
        warehouse_element_value_edit.setText("");
        chkIsAdmin.setChecked(false);
        GlobalClass.getInstance().setIdSelectedTypeElementWarehouse(-1);

        if (ListaImagenes != null) {
            ListaImagenes.clear();
            ListFotos.clear();
            setListImagesAdapter();
        }
    }

    private void confirmAddForSyncAfter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.no_internet_for_sync));
        builder.setPositiveButton("Guardar y sincronizar cuando tenga conexión a internet",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaveDocumentForSync();
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

    private void confirmAddForSyncAfterByGenerateError(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(error);
        builder.setPositiveButton("Guardar y sincronizar en segundo plano",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaveDocumentForSync();
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
        builder.setMessage(getString(R.string.message_confirm_cancel_legalization));
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File createImageFile() throws IOException {
        // Create an image file name
        String prefijo = "GENERAL";
        prefijo = prefijo.replace(' ', '_');
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fechaActual = sdf.format(new Date());
        String nombreFichero = prefijo + "_" + fechaActual;
        File storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                nombreFichero,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        GlobalClass.getInstance().setmCurrentPhotoPath(mCurrentPhotoPath);
        return image;
    }

    private void createLegalizationHeader() {

        materialViewmodelHeader = new MaterialViewmodelHeader(GlobalClass.getInstance().getIdSelectedWareHouseWarehouse(),
                Integer.valueOf(GlobalClass.getInstance().getIdSelectedProductionWarehouse()),
                GlobalClass.getInstance().getIdSelectedResponsibleWarehouse(), GlobalClass.getInstance().getIdSelectedUserWarehouse(), this.warehouse_program_option.getText().toString());

        long materialViewmodelHeaderId = materialHeaderRepository.insert(materialViewmodelHeader);
        materialViewmodelHeader = materialHeaderRepository.getLegalizationById(materialViewmodelHeaderId);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
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

    private byte[] getByteArrayFromBitmap(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void getImagesByMaterial(long idMaterialDetail) {
        _MaterialViewModel.getListaImagenesBmp().clear();
        List<MaterialImagesViewModel> materialImagesViewModels = materialImagesRepository.getByMaterialDetailId(idMaterialDetail);
        for (MaterialImagesViewModel materialImagesViewModel : materialImagesViewModels) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(materialImagesViewModel.getImage(), 0, materialImagesViewModel.getImage().length);
            _MaterialViewModel.getListaImagenesBmp().add(bitmap);
        }
    }

    private void getLegalizationMaterialDetail() {

        GlobalClass.getInstance().setDataMaterial((ArrayList<MaterialViewModel>) materialRepository.getMaterialLegalizationDetail(materialViewmodelHeader.getId()));

        for (MaterialViewModel materialViewModel : GlobalClass.getInstance().getDataMaterial()) {

            List<MaterialImagesViewModel> materialImagesViewModels = materialImagesRepository.getByMaterialDetailId(materialViewModel.getIdDetail());
            for (MaterialImagesViewModel materialImagesViewModel : materialImagesViewModels) {
                materialViewModel.getListaImagenesStr().add(materialImagesViewModel.getParsePhoto());
            }
        }
    }

    private void loadEditData() {

        this.warehouse_element_barcode_edit.setText(_MaterialViewModel.getBarCode());
        this.warehouse_element_desc_edit.setText(_MaterialViewModel.getMaterialName());
        this.warehouse_element_type_edit.setText(_MaterialViewModel.getTypeElementName());
        this.warehouse_element_edit.setText(_MaterialViewModel.getMarca());
        this.warehouse_element_price_edit.setText(_MaterialViewModel.getUnitPrice().toString());
        this.warehouse_element_value_edit.setText(_MaterialViewModel.getPurchaseValue().toString());
        getImagesByMaterial(_MaterialViewModel.getIdDetail());

    }

    private void loadTest(MaterialViewModel newElement) {
        for (int i = 0; i < 10; i++) {
            GlobalClass.getInstance().getDataMaterial().add(newElement);
        }
    }

    public static WarehouseFragment newInstance(MaterialViewModel param1) {
        WarehouseFragment fragment = new WarehouseFragment();
        Bundle args = new Bundle();
        _MaterialViewModel = param1;
        fragment.setArguments(args);
        return fragment;
    }

    private String parseImage(String photo) {
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/bodegas_images");
        byte[] b = readPhotoAndRezise(photo);

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private byte[] readPhotoAndRezise(String f) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        File fdelete = new File(f);
        Bitmap bMap = BitmapFactory.decodeFile(f, options);
        bMap = scaleBitmap(bMap, 7);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bMap.getByteCount());
        bMap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        bMap.recycle();
        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private void setActionBarTittle() {

        warehouse_element_prod.setText(materialViewmodelHeader.getSelectedProductionName());
        dateTimeUtilities = new DateTimeUtilities(getActivity());

    }

    private void setListImagesAdapter() {
        PhotosAdapter adapter = new PhotosAdapter(ListaImagenes, listaNombresImagenes, new onRecyclerProductionListItemClick() {
            @Override
            public void onClick(ProductionViewModel wareHouseViewModel) {

            }
        });
        photos_recycler_view.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showCamera() {
        try {

            if (!chkIsAdmin.isChecked())
                if (warehouse_element_barcode_edit.getText().toString().equals("")) {
                    showMessageDialog(getString(R.string.message_not_barcode));
                    return;
                }

            List<String> listPermissionsNeeded = new ArrayList<>();
            int camera = ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), android.Manifest.permission.CAMERA);
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

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService((Context.INPUT_METHOD_SERVICE));
        imm.showSoftInput(warehouse_element_desc_edit, InputMethodManager.SHOW_IMPLICIT);
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

    private void validateFields() {
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
        if (ListaImagenes == null || ListaImagenes.size() == 0) {
            showMessageDialog("Debe ingresar al menos un imagen");
            focusView = warehouse_element_value_edit;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            addElement();
        }
    }

    @SuppressLint("RestrictedApi")
    private void validateFieldsHeader() {
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
        } else {
            warehouse_element_layout.setVisibility(View.VISIBLE);
            warehouse_data.setVisibility(View.GONE);
            GlobalClass.getInstance().setCurrentAddElementActiveProcess(true);
            warehouse_btn_camera.setVisibility(View.VISIBLE);
            warehouse_btn_new_element.setVisibility(View.VISIBLE);
            menuSave.setVisible(true);
            menuReview.setVisible(true);
            mnuCancel.setVisible(true);
            createLegalizationHeader();
            PrintCountElementes();
            setActionBarTittle();
        }
    }

    //Valida si hay un proceso de inventario en proceso
    private boolean validateInventoryProcess() {

        MaterialHeaderRepository materialHeaderRepository = new MaterialHeaderRepository(getActivity());
        materialViewmodelHeader = materialHeaderRepository.getLegalizationPendingProcess();


        return materialViewmodelHeader == null ? false : true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void DataRecived(final String BarcodeData) {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!chkIsAdmin.isChecked()) {
                    warehouse_element_barcode_edit.setText(BarcodeData);
                    if (!ValidateBarCode()) {
                        showMessageDialog("El código de barras " + BarcodeData + " ya ha sido agregado a este documento de legalización");
                        warehouse_element_barcode_edit.setText("");
                    } else {
                        warehouse_element_desc_edit.requestFocus();
                        showKeyBoard();
                    }
                } else {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            SetImage();
        }
        if (requestCode == REQUEST_PRODUCTION) {
            if (resultCode == -1) {
                selectedProductionName = data.getStringExtra("productionName");
                GlobalClass.getInstance().setIdSelectedProductionWarehouse(data.getStringExtra("productionId"));
                this.warehouse_program_option.setText(selectedProductionName);

            }
        }
        if (requestCode == REQUEST_RESPONSIBLE) {
            if (resultCode == -1) {
                String result = data.getStringExtra("responsibleName");
                GlobalClass.getInstance().setIdSelectedResponsibleWarehouse(Integer.valueOf(data.getStringExtra("responsibleId")));
                this.warehouse_legalizedBy_option.setText(result);

            }
        }
        if (requestCode == REQUEST_TYPE_ELEMENT) {
            if (resultCode == -1) {
                String result = data.getStringExtra("typeElementName");
                GlobalClass.getInstance().setIdSelectedTypeElementWarehouse(Integer.valueOf(data.getStringExtra("typeElementId")));
                this.warehouse_element_type_edit.setText(result);

                if (GlobalClass.getInstance().getIdSelectedTypeElementWarehouse().toString().equals(GlobalClass.getInstance().getAdminTypeElementId()))
                    this.warehouse_element_barcode_edit.setText("");

            }
        }
        if (requestCode == REQUEST_WAREHOUSE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("wareHouseName");
                this.warehouse_option.setText(result);
                GlobalClass.getInstance().setIdSelectedWareHouseWarehouse(data.getStringExtra("wareHouseId"));
                GlobalClass.getInstance().setNameSelectedWareHouseWarehouse(data.getStringExtra("wareHouseName"));
            }
        }
        if (requestCode == REQUEST_USER_WAREHOUSE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("responsibleWarehouseName");
                GlobalClass.getInstance().setIdSelectedUserWarehouse(Integer.valueOf(data.getStringExtra("responsibleWarehouseId")));
                this.warehouse_user_option.setText(result);
            }
        }
        if (requestCode == REQUEST_REVIEW) {
            if (resultCode == RESULT_OK) {
                PrintCountElementes();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.title_element_legalization));
        materialRepository = new MaterialRepository(getActivity());
        materialHeaderRepository = new MaterialHeaderRepository(getActivity());
        materialImagesRepository = new MaterialImagesRepository(getActivity());

        dateTimeUtilities = new DateTimeUtilities(getActivity());
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @SuppressLint("RestrictedApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.activity_warehouse, null, false);
        assert layout != null;
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setView(layout);

        InitializeControls(layout);
        InitializeEvents();


        if (!validateInventoryProcess()) {
            warehouse_element_layout.setVisibility(View.GONE);
            warehouse_data.setVisibility(View.VISIBLE);
        } else {
            warehouse_element_layout.setVisibility(View.VISIBLE);
            warehouse_data.setVisibility(View.GONE);

        }

        String title = getArguments().getString("title");
        b.setTitle(title);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _MaterialViewModel.setBarCode(warehouse_element_barcode_edit.getText().toString());
                _MaterialViewModel.setMaterialName(warehouse_element_desc_edit.getText().toString());
                _MaterialViewModel.setTypeElementName(warehouse_element_type_edit.getText().toString());
                _MaterialViewModel.setMarca(warehouse_element_edit.getText().toString());
                String currencyUnitPriceString = warehouse_element_price_edit.getText().toString()
                        .replace(",", "")
                        .replace(".", ".")
                        .replaceAll("[^\\d.-]", "");

                String currencyPurchaseString = warehouse_element_value_edit.getText().toString()
                        .replace(",", "")
                        .replace(".", ".")
                        .replaceAll("[^\\d.-]", "");

                _MaterialViewModel.setUnitPrice(Double.valueOf(currencyUnitPriceString));
                _MaterialViewModel.setPurchaseValue(Double.valueOf(currencyPurchaseString));
                materialRepository.update(_MaterialViewModel);

                if (ListFotos != null) {
                    for (String photo : ListFotos) {
                        String photoParsed = parseImage(photo);
                        _MaterialViewModel.getListaImagenesStr().add(photoParsed);


                    }
                }


            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        loadEditData();
        this.ListaImagenes = _MaterialViewModel.getListaImagenesBmp();
        setListImagesAdapter();
        warehouse_btn_new_element.setVisibility(View.GONE);
        return b.create();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_ingreso_elemento, menu);
        iconScanMenu = menu.findItem(R.id.menu_review);
        PrintCountElementes();


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        if (_MaterialViewModel == null) {
            view = SetDialogConfig(inflater, container);

        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _MaterialViewModel = null;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        Intent intent;
        switch (item.getItemId()) {
            /**/
            case R.id.menu_review:
                GlobalClass.getInstance().setListMaterialForAdd(GlobalClass.getInstance().getDataMaterial());
                intent = new Intent(getActivity(), ListItemAddedActivity.class);
                intent.putExtra("Inventory", "0");
                startActivityForResult(intent, REQUEST_REVIEW);
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
    public void onPause() {
        super.onPause();
        Scanner_manager.ScannerOFF();

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuReview = menu.findItem(R.id.menu_review);
        menuSave = menu.findItem(R.id.mnu_save);
        mnuCancel = menu.findItem(R.id.mnu_cancel);
        if (_MaterialViewModel == null)
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                canUseExternalStorage = true;
                showCamera();
            }
            if (!canUseExternalStorage) {
                Toast.makeText(getActivity(), "No se puede usar esta función sin el permiso solicitado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Scanner_manager = ScannerFactory.CreateScanner(getActivity().getApplicationContext(), getActivity());
        Scanner_manager.AddObserver(this);
        Scanner_manager.ScannerON(true);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public EditText txtDate;
        private DateTimeUtilities dateTimeUtilities;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {


            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            //btnDate.setText(ConverterDate.ConvertDate(year, month + 1, day));
            txtDate.setText(dateTimeUtilities.parseDateTurno(year, month + 1, day));
        }
    }
}
