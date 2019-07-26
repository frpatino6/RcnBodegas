package com.rcnbodegas.Fragments;


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
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rcnbodegas.Activities.CustomActivity;
import com.rcnbodegas.Activities.ListItemAddedActivity;
import com.rcnbodegas.Activities.ProductionListActivity;
import com.rcnbodegas.Activities.ResponsibleListActivity;
import com.rcnbodegas.Activities.TypeElementListActivity;
import com.rcnbodegas.Activities.WareHouseListActivity;
import com.rcnbodegas.Global.DateTimeUtilities;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.IObserver;
import com.rcnbodegas.Global.PhotoListAdapter;
import com.rcnbodegas.Global.PhotosAdapter;
import com.rcnbodegas.Global.ScannerFactory;
import com.rcnbodegas.Global.TScanner;
import com.rcnbodegas.Global.onRecyclerProductionListItemClick;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.ProductionViewModel;
import com.symbol.emdk.barcode.Scanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class WarehouseFragment extends CustomActivity implements IObserver, DatePickerDialog.OnDateSetListener {
    private static final int REQUEST_PRODUCTION = 1;
    private static final int REQUEST_RESPONSIBLE = 2;
    private static final int REQUEST_TYPE_ELEMENT = 3;
    private static final int REQUEST_WAREHOUSE = 4;
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1900;

    public PhotoListAdapter adapterPhotos;

    private Button warehouse_btn_ok;
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

    private LinearLayout warehouse_element;
    private LinearLayout warehouse_data;
    private LinearLayoutManager layoutManager;
    private DateTimeUtilities dateTimeUtilities;

    private ArrayList<String> ListFotos;
    private ArrayList<Bitmap> ListaImagenes;
    private ArrayList<String> listaNombresImagenes;
    private ArrayList<MaterialViewModel> dataMaterial;

    private Scanner scanner = null;
    private TScanner Scanner_manager = null;

    public String mCurrentPhotoPath;
    private GlobalClass globalVariable;
    private Uri file;
    private Integer consecutive = 1;
    private RecyclerView photos_recycler_view;
    private PhotosAdapter adapter;
    private MenuItem menuReview;

    public WarehouseFragment() {
        // Required empty public constructor
    }

    private MenuItem mnuCamera;

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
        return view;
    }

    @Override
    public void DataRecived(final String BarcodeData) {
        final String _barcodeData = BarcodeData;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                warehouse_element_barcode_edit.setText(BarcodeData);
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

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mnuCamera = menu.findItem(R.id.menu_camera);
        mnuCamera.setVisible(false);
        menuReview = menu.findItem(R.id.menu_review);
        menuReview.setVisible(false);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ingreso_elemento, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menu_camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    showCamera();
                else
                    dispatchTakePictureIntent();
                return true;
            case R.id.menu_review:
                globalVariable.setListMaterialForAdd(dataMaterial);
                intent = null;
                intent = new Intent(getActivity(), ListItemAddedActivity.class);
                startActivity(intent);
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

    private void addMaterialToList() {
        if (dataMaterial == null)
            dataMaterial = new ArrayList<>();


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


            int ratio;
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

            LoadPhoto(bitmap, nombreFichero);


        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream is;
    }

    private void InitializeControls(View v) {

        warehouse_btn_ok = v.findViewById(R.id.warehouse_btn_ok);
        warehouse_btn_new_element = v.findViewById(R.id.warehouse_btn_new_element);

        warehouse_option = v.findViewById(R.id.warehouse_option);
        warehouse_program_option = v.findViewById(R.id.warehouse_program_option);
        warehouse_legalizedBy_option = v.findViewById(R.id.warehouse_legalizedBy_option);
        warehouse_date_option = v.findViewById(R.id.warehouse_date_option);
        warehouse_element = v.findViewById(R.id.warehouse_element);
        warehouse_element_barcode_edit = v.findViewById(R.id.warehouse_element_barcode_edit);
        warehouse_element_desc_edit = v.findViewById(R.id.warehouse_element_desc_edit);
        warehouse_element_type_edit = v.findViewById(R.id.warehouse_element_type_edit);
        warehouse_element_edit = v.findViewById(R.id.warehouse_element_edit);
        warehouse_element_price_edit = v.findViewById(R.id.warehouse_element_price_edit);
        warehouse_element_value_edit = v.findViewById(R.id.warehouse_element_value_edit);
        warehouse_data = v.findViewById(R.id.warehouse_data);

        warehouse_date_option.setText(dateTimeUtilities.parseDateTurno());

        photos_recycler_view = (RecyclerView) v.findViewById(R.id.photos_recycler_view);
        photos_recycler_view.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(getActivity());
        photos_recycler_view.setLayoutManager(layoutManager);
        photos_recycler_view.setItemAnimator(new DefaultItemAnimator());


    }

    private void InitializeEvents() {
        globalVariable.setQueryByInventory(false);
        warehouse_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFieldsHeader();
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
        warehouse_date_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new InventoryFragment.DatePickerFragment();
                ((DatePickerFragment) newFragment).txtDate = warehouse_date_option;
                ((DatePickerFragment) newFragment).dateTimeUtilities = dateTimeUtilities;

                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
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
    }

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
            warehouse_element.setVisibility(View.VISIBLE);
            warehouse_data.setVisibility(View.GONE);
            mnuCamera.setVisible(true);
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
            warehouse_element_value_edit.setError(getString(R.string.error_responsible_empty));
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

    private void addElement() {
        MaterialViewModel newElement = new MaterialViewModel();
        newElement.setBarCode(warehouse_element_barcode_edit.getText().toString());
        newElement.setWareHouseId(globalVariable.getIdSelectedWareHouseWarehouse());
        newElement.setProductionId(Integer.valueOf(globalVariable.getIdSelectedProductionWarehouse()));
        newElement.setResponsibleId(globalVariable.getIdSelectedResponsibleWarehouse());
        newElement.setMaterialName(warehouse_element_desc_edit.getText().toString());
        newElement.setMarca(warehouse_element_edit.getText().toString());
        newElement.setUnitPrice(warehouse_element_price_edit.getText().toString().equals("") ? 0 : Double.valueOf(warehouse_element_price_edit.getText().toString()));
        newElement.setTypeElementId(String.valueOf(globalVariable.getIdSelectedTypeElementWarehouse()));
        if (ListaImagenes != null && ListaImagenes.size() > 0)
            newElement.setListaImagenes(ListaImagenes);

        if (dataMaterial == null)
            dataMaterial = new ArrayList<>();
        menuReview.setVisible(true);
        dataMaterial.add(newElement);
        clearFields();

    }

    private void clearFields() {
        warehouse_element_barcode_edit.setText("");
        warehouse_element_desc_edit.setText("");
        warehouse_element_type_edit.setText("");
        warehouse_element_edit.setText("");
        warehouse_element_price_edit.setText("");
        warehouse_element_value_edit.setText("");

        if (ListaImagenes != null) {
            ListaImagenes.clear();
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
                //fragmentAddFoto.adapterPhotos = new PhotoListAdapter(fragmentAddFoto.ListaImagenes, fragmentAddFoto.listaNombresImagenes, Edit_partesActivity.this);
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
