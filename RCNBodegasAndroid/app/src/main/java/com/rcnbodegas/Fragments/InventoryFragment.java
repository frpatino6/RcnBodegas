package com.rcnbodegas.Fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.LinearLayout;

import com.rcnbodegas.Activities.MainActivity;
import com.rcnbodegas.Activities.ProductionListActivity;
import com.rcnbodegas.Activities.ResponsibleListActivity;
import com.rcnbodegas.Activities.SelectParametersActivity;
import com.rcnbodegas.Global.DateTimeUtilities;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.R;

import java.util.Calendar;

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
    // TODO: Rename and change types of parameters
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
    private DatePickerDialog datePickerDialog = null;
    private DateTimeUtilities dateTimeUtilities;


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
    }

    private void InitializeControls(View v) {

        inventory_element = v.findViewById(R.id.inventory_element);
        inventory_data = v.findViewById(R.id.inventory_data);
        inventory_warehouse_option = v.findViewById(R.id.inventory_warehouse_option);
        inventory_program_option = v.findViewById(R.id.inventory_program_option);
        inventory_date_option = v.findViewById(R.id.inventory_date_option);
        inventory_responsible_option = v.findViewById(R.id.inventory_responsible_option);
        inventory_btn_ok = v.findViewById(R.id.inventory_btn_ok);

        inventory_date_option.setText(dateTimeUtilities.parseDateTurno());
        inventory_warehouse_option.setText(globalVariable.getNameSelectedWareHouse());
    }

    private void InitializeEvents() {

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
                }
                else
                    inventory_program_option.setError(null);

                startActivityForResult(intent, REQUEST_RESPONSIBLE);
            }
        });
        inventory_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFiels();
            }
        });
    }

    private void validateFiels() {
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
        } else {
            inventory_element.setVisibility(View.VISIBLE);
            inventory_data.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

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
