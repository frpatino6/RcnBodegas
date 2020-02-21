package com.rcnbodegas.Fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.rcnbodegas.Global.DateTimeUtilities;
import com.rcnbodegas.R;

public class AddElementFragment extends Fragment {

    private EditText inventory_date_option;
    private DateTimeUtilities dateTimeUtilities;
    public AddElementFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AddElementFragment newInstance(String param1, String param2) {
        AddElementFragment fragment = new AddElementFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        dateTimeUtilities = new DateTimeUtilities(getActivity());
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_inventory, container, false);
        InitializeControls(view);
        return view;
    }
    private void InitializeControls(View v) {


        inventory_date_option = v.findViewById(R.id.inventory_date_option);

        inventory_date_option.setText(dateTimeUtilities.parseDateTurno());

    }

}
