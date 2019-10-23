package com.rcnbodegas.Activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rcnbodegas.Global.AddedElementListAdapter;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.ReviewListAdapter;
import com.rcnbodegas.Global.WareHouseAdapter;
import com.rcnbodegas.Global.onRecyclerWarehouseListItemClick;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.ProductionViewModel;
import com.rcnbodegas.ViewModels.WareHouseViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListItemReviewActivity extends AppCompatActivity  {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<MaterialViewModel> listMaterialByReview;
    private ArrayList<MaterialViewModel> sortEmpList;
    private ReviewListAdapter adapter;
    private View mIncidenciasFormView;
    private View mProgressView;
    private TextView txtResumen;
    private TextView txtTotal;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_review);
        
        ((AppCompatActivity) this).getSupportActionBar().setTitle(getString(R.string.title_bar_review));

        InitializeControls();
        filterListByNotReview();
        setRecyclerViewData();

    }


    private void InitializeControls() {
        txtResumen = findViewById(R.id.txtResumen);
        mIncidenciasFormView = findViewById(R.id.review_recycler_view);
        mProgressView = findViewById(R.id.review_progress);
        recyclerView = (RecyclerView) findViewById(R.id.review_recycler_view);
        recyclerView.setHasFixedSize(true);
        txtTotal=findViewById(R.id.txtTotal);
        

        layoutManager = new LinearLayoutManager(ListItemReviewActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());



    }

    private void filterListByNotReview() {

        if (listMaterialByReview == null)
            listMaterialByReview = new ArrayList<>();

        for (MaterialViewModel materialViewModel : GlobalClass.getInstance().getListMaterialBYProduction()) {
            if (!materialViewModel.isReview())
                listMaterialByReview.add(materialViewModel);
        }

        txtResumen.setText(getString(R.string.message_resume_review_list) +listMaterialByReview.size()) ;

        SumPurchaseValue();
    }

    private void SumPurchaseValue(){
        Double result=0.0;
        for (MaterialViewModel materialViewModel : listMaterialByReview) {
            result+=materialViewModel.getUnitPrice();
        }


        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CANADA);
        String currency = format.format(result);


        txtTotal.setText("Total valor compra: " + currency);
    }
    private void setRecyclerViewData() {
        adapter = new ReviewListAdapter(listMaterialByReview);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_warehouselist, menu);

            MenuItem search_item = menu.findItem(R.id.search_warehouse);

            SearchView searchView = (SearchView) search_item.getActionView();
            searchView.setFocusable(false);
            searchView.setQueryHint(getString(R.string.search_hint));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


                @Override
                public boolean onQueryTextSubmit(String s) {

                    //clear the previous data in search arraylist if exist

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    FilterListView(s);
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }

    private void FilterListView(String query) {

        //mStatusView.setText("Query = " + query + " : submitted");
        try {
            Filter<MaterialViewModel, String> filter = new Filter<MaterialViewModel, String>() {
                public boolean isMatched(MaterialViewModel object, String text) {

                    boolean result1 = false;
                    boolean result2 = false;
                    boolean result3 = false;


                    result1 = object.getMaterialName().toString().toLowerCase().contains(String.valueOf(text));
                    result2 = object.getBarCode().toString().toLowerCase().contains(String.valueOf(text));
                    result3 = object.getMarca().toString().toLowerCase().contains(String.valueOf(text));


                    if (result1 || result2 || result3)
                        return true;
                    else
                        return false;
                }
            };


            sortEmpList = (ArrayList<MaterialViewModel>) new FilterList().filterList(listMaterialByReview, filter, query);

            adapter = new ReviewListAdapter(sortEmpList);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
