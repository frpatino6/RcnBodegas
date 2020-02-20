package com.rcnbodegas.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.LastDocumentsAdapter;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.WareHouseViewModel;

import java.util.ArrayList;


public class ListDocumentsFragment extends Fragment {

    private LastDocumentsAdapter adapter;
    private ArrayList<Integer> data;
    private LinearLayoutManager layoutManager;
    private View mIncidenciasFormView;
    private View mProgressView;
    private RecyclerView recyclerView;
    private ArrayList<WareHouseViewModel> sortEmpList;
    public ListDocumentsFragment() {
        // Required empty public constructor
    }

    private void InitializeControls(View v ) {

        mIncidenciasFormView = v.findViewById(R.id.documents_recycler_view);
        mProgressView = v.findViewById(R.id.documents_progress);
        recyclerView = (RecyclerView) v.findViewById(R.id.documents_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new LastDocumentsAdapter(GlobalClass.getInstance().getLastDocuments());
        recyclerView.setAdapter(adapter);
    }

    public static ListDocumentsFragment newInstance() {
        ListDocumentsFragment fragment = new ListDocumentsFragment();

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.title_list_last_documents_created));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_documents, container, false);
        InitializeControls(view);
        return view;




    }

}
