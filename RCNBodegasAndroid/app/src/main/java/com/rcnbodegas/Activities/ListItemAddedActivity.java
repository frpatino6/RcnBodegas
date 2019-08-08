package com.rcnbodegas.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.ReviewListAdapter;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.util.ArrayList;

public class ListItemAddedActivity extends AppCompatActivity   implements SearchView.OnQueryTextListener{
    private RecyclerView recyclerView;
    private GlobalClass globalVariable;
    private LinearLayoutManager layoutManager;
    private ArrayList<MaterialViewModel> listMaterialByReview;

    private ReviewListAdapter adapter;
    private View mIncidenciasFormView;
    private View mProgressView;
    private TextView txtResumen;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_review);
        globalVariable = (GlobalClass) getApplicationContext();
        InitializeControls();
        filterListByNotReview();
        setRecyclerViewData();

    }


    private void InitializeControls() {

        txtResumen = findViewById(R.id.txtResumen);
        txtResumen.setVisibility(View.GONE);
        mIncidenciasFormView = findViewById(R.id.review_recycler_view);
        mProgressView = findViewById(R.id.review_progress);
        recyclerView = (RecyclerView) findViewById(R.id.review_recycler_view);
        recyclerView.setHasFixedSize(true);

        globalVariable = (GlobalClass) getApplicationContext();

        layoutManager = new LinearLayoutManager(ListItemAddedActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());



    }

    private void filterListByNotReview() {

        if (listMaterialByReview == null)
            listMaterialByReview = new ArrayList<>();

        for (MaterialViewModel materialViewModel : globalVariable.getListMaterialForAdd()) {
            if (!materialViewModel.isReview())
                listMaterialByReview.add(materialViewModel);
        }

        txtResumen.setText(getString(R.string.message_resume_review_list) +listMaterialByReview.size()) ;

    }

    private void setRecyclerViewData() {
        adapter = new ReviewListAdapter(listMaterialByReview);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_review, menu);


        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;

    }



    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
