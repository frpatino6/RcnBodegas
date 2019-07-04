package com.rcnbodegas.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rcnbodegas.Activities.MainActivity;
import com.rcnbodegas.Activities.ProductionListActivity;
import com.rcnbodegas.Activities.ResponsibleListActivity;
import com.rcnbodegas.Activities.SelectParametersActivity;
import com.rcnbodegas.Activities.TypeElementListActivity;
import com.rcnbodegas.Global.DateTimeUtilities;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.ProductionAdapter;
import com.rcnbodegas.Global.onRecyclerProductionListItemClick;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.ProductionViewModel;

import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_PRODUCTION = 1;
    private static final int REQUEST_RESPONSIBLE = 2;
    private static final int REQUEST_TYPE_ELEMENT = 3;

    // TODO: Rename and change types of parameters
    private ImageButton btnSearch;
    private Button inventory_btn_new_element;
    private String mParam1;
    private String mParam2;
    private LinearLayout inventory_element;
    private LinearLayout inventory_data;
    private GlobalClass globalVariable;
    private Button inventory_btn_ok;
    private EditText inventory_warehouse_option;
    private EditText inventory_program_option;
    private EditText inventory_date_option;
    private EditText inventory_responsible_option;
    private EditText inventory_element_type_edit;
    private EditText inventory_element_barcode_edit;
    private EditText inventory_element_edit;
    private EditText inventory_element_brand_edit;
    private EditText inventory_element_price_edit;
    private EditText inventory_element_value_edit;
    private DatePickerDialog datePickerDialog = null;
    private DateTimeUtilities dateTimeUtilities;
    private View mIncidenciasFormView;
    private View mProgressView;
    private MaterialViewModel data;
    private List<MaterialViewModel> dataMaterial;


    public InventoryFragment() {
        // Required empty public constructor
    }


    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dateTimeUtilities = new DateTimeUtilities(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_inventory, container, false);
        inventory_element = view.findViewById(R.id.inventory_element);
        inventory_data = view.findViewById(R.id.inventory_data);

        inventory_element.setVisibility(View.GONE);

        globalVariable = (GlobalClass) getActivity().getApplicationContext();

        InitializeControls(view);
        InitializeEvents();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PRODUCTION) {
            if (resultCode == -1) {
                String result = data.getStringExtra("productionName");
                globalVariable.setIdSelectedProduction(data.getStringExtra("productionId"));
                this.inventory_program_option.setText(result);

            }
        }
        if (requestCode == REQUEST_RESPONSIBLE) {
            if (resultCode == -1) {
                String result = data.getStringExtra("responsibleName");
                globalVariable.setIdSelectedResponsible(Integer.valueOf(data.getStringExtra("responsibleId")));
                this.inventory_responsible_option.setText(result);

            }
        }
        if (requestCode == REQUEST_TYPE_ELEMENT) {
            if (resultCode == -1) {
                String result = data.getStringExtra("typeElementName");
                globalVariable.setIdSelectedTypeElement(Integer.valueOf(data.getStringExtra("typeElementId")));
                this.inventory_element_type_edit.setText(result);

            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    private void InitializeControls(View v) {

        btnSearch = v.findViewById(R.id.btnSearch);
        inventory_btn_new_element = v.findViewById(R.id.inventory_btn_new_element);
        inventory_element = v.findViewById(R.id.inventory_element);
        inventory_data = v.findViewById(R.id.inventory_data);
        inventory_warehouse_option = v.findViewById(R.id.inventory_warehouse_option);
        inventory_program_option = v.findViewById(R.id.inventory_program_option);
        inventory_date_option = v.findViewById(R.id.inventory_date_option);
        inventory_responsible_option = v.findViewById(R.id.inventory_responsible_option);
        inventory_element_type_edit = v.findViewById(R.id.inventory_element_type_edit);
        inventory_element_barcode_edit = v.findViewById(R.id.inventory_element_barcode_edit);
        inventory_element_edit = v.findViewById(R.id.inventory_element_edit);
        inventory_element_brand_edit = v.findViewById(R.id.inventory_element_brand_edit);
        inventory_element_price_edit = v.findViewById(R.id.inventory_element_price_edit);
        inventory_element_value_edit = v.findViewById(R.id.inventory_element_value_edit);

        inventory_btn_ok = v.findViewById(R.id.inventory_btn_ok);

        inventory_date_option.setText(dateTimeUtilities.parseDateTurno());
        inventory_warehouse_option.setText(globalVariable.getNameSelectedWareHouse());

        mIncidenciasFormView = v.findViewById(R.id.inventory_element);
        mProgressView = v.findViewById(R.id.inventroy_progress);
    }

    private void InitializeEvents() {

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncListMaterialsByBarCode();
            }
        });

        inventory_btn_new_element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidaRequieredElementInfo();
                ClearFIelds();
            }
        });

        inventory_program_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(getActivity(), ProductionListActivity.class);
                startActivityForResult(intent, REQUEST_PRODUCTION);
            }
        });

        inventory_date_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                ((DatePickerFragment) newFragment).txtDate = inventory_date_option;
                ((DatePickerFragment) newFragment).dateTimeUtilities = dateTimeUtilities;

                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        inventory_responsible_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(getActivity(), ResponsibleListActivity.class);

                if (inventory_program_option.getText().toString().equals("")) {
                    inventory_program_option.setError(getString(R.string.error_program_empty));
                    return;
                } else
                    inventory_program_option.setError(null);

                startActivityForResult(intent, REQUEST_RESPONSIBLE);
            }
        });

        inventory_element_type_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(getActivity(), TypeElementListActivity.class);


                startActivityForResult(intent, REQUEST_TYPE_ELEMENT);
            }
        });
        inventory_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFiels()) asyncListMaterialsByProduction();

            }
        });
    }


    private void ClearFIelds() {

        if (ValidaRequieredElementInfo()) {
            inventory_element_barcode_edit.setText("");
            inventory_element_edit.setText("");
            inventory_element_type_edit.setText("");
            inventory_element_brand_edit.setText("");
            inventory_element_price_edit.setText("");
            inventory_element_value_edit.setText("");
        }
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
        if (TextUtils.isEmpty(inventory_element_type_edit.getText().toString())) {
            inventory_element_type_edit.setError(getString(R.string.error_type_empty));
            focusView = inventory_element_type_edit;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            inventory_element.setVisibility(View.VISIBLE);
            inventory_data.setVisibility(View.GONE);
        }
        return true;

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
        if (TextUtils.isEmpty(inventory_responsible_option.getText().toString())) {
            inventory_responsible_option.setError(getString(R.string.error_responsible_empty));
            focusView = inventory_responsible_option;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            inventory_element.setVisibility(View.VISIBLE);
            inventory_data.setVisibility(View.GONE);
        }
        return true;
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

    private void asyncListMaterialsByBarCode() {


        String url = globalVariable.getUrlServices() + "Inventory/GetMaterialByBarcode/" + inventory_element_barcode_edit.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();
        showProgress(true);
        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<MaterialViewModel> token = new TypeToken<MaterialViewModel>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            data = gson.fromJson(res, token.getType());

                            if (data != null)
                                setMaterialData(data);
                            else
                                shwoMessage("No se encontró elemento con el código de barras ingresado");

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

    private void asyncListMaterialsByProduction() {


        String url = globalVariable.getUrlServices() + "Inventory/GetMaterialByProduction/" + globalVariable.getIdSelectedWareHouse() + "/" + globalVariable.getIdSelectedProduction() + "/" + globalVariable.getIdSelectedResponsible();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();
        showProgress(true);
        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            TypeToken<List<MaterialViewModel>> token = new TypeToken<List<MaterialViewModel>>() {
                            };
                            Gson gson = new GsonBuilder().create();
                            // Define Response class to correspond to the JSON response returned
                            dataMaterial = gson.fromJson(res, token.getType());

                            if (dataMaterial != null)
                                globalVariable.setListMaterialBYProduction(dataMaterial);
                            else
                                shwoMessage("No se encontró elemento con el código de barras ingresado");

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

    private void setMaterialData(MaterialViewModel data) {
        inventory_element_edit.setText(data.getMaterialName());
        inventory_element_type_edit.setText(data.getTypeElementName());
        inventory_element_brand_edit.setText(data.getMarca().toString());
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
