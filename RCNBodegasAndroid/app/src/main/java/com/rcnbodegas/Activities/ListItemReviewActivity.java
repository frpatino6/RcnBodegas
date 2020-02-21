package com.rcnbodegas.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.Global.ReviewListAdapter;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListItemReviewActivity extends AppCompatActivity {
    private ReviewListAdapter adapter;
    private Bundle extras;
    private LinearLayoutManager layoutManager;
    private ArrayList<MaterialViewModel> listMaterialAdded;
    private ArrayList<MaterialViewModel> listMaterialByReview;
    private View mIncidenciasFormView;
    private View mProgressView;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private String selectedOption;
    private ArrayList<MaterialViewModel> sortEmpList;
    private TextView txtAdded;
    private TextView txtResumen;
    private TextView txtDiference;
    private TextView txtTotal;

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

    private void InitializeControls() {
        txtResumen = findViewById(R.id.txtResumen);
        txtAdded = findViewById(R.id.txtAdded);
        txtDiference= findViewById(R.id.txtDiference);

        mIncidenciasFormView = findViewById(R.id.review_recycler_view);
        mProgressView = findViewById(R.id.review_progress);
        recyclerView = (RecyclerView) findViewById(R.id.review_recycler_view);
        recyclerView.setHasFixedSize(true);
        txtTotal = findViewById(R.id.txtTotal);


        layoutManager = new LinearLayoutManager(ListItemReviewActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        /*if(!selectedOption.equals("1"))
            txtResumen.setVisibility(View.GONE);
        else
            txtResumen.setVisibility(View.VISIBLE);*/

    }

    private void SumPurchaseValue() {
        Double result = 0.0;
        for (MaterialViewModel materialViewModel : listMaterialByReview) {
            result += materialViewModel.getUnitPrice();
        }


        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CANADA);
        String currency = format.format(result);


        txtTotal.setText("Total valor compra: " + currency);
    }

    private void filterListByNotReview() {

        if (listMaterialByReview == null)
            listMaterialByReview = new ArrayList<>();

        for (MaterialViewModel materialViewModel : GlobalClass.getInstance().getListMaterialBYProduction()) {
            if (!materialViewModel.isReview())
                listMaterialByReview.add(materialViewModel);
        }

        txtResumen.setText(getString(R.string.message_resume_review_list) + " " + GlobalClass.getInstance().getListMaterialBYProduction().size());

        SumPurchaseValue();
    }

    private void filterListByReview() {

        if (listMaterialAdded == null)
            listMaterialAdded = new ArrayList<>();

        for (MaterialViewModel materialViewModel : GlobalClass.getInstance().getListMaterialBYProduction()) {
            if (materialViewModel.isReview())
                listMaterialAdded.add(materialViewModel);
        }

        txtAdded.setText(getString(R.string.message_resume_review_list_added) + " " + listMaterialAdded.size());
        txtDiference.setText(getString(R.string.message_resume_review_list_by_add) + " " + String.valueOf(listMaterialByReview.size()));


    }

    private void setRecyclerViewData() {
        adapter = new ReviewListAdapter(listMaterialByReview);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_review);

        ((AppCompatActivity) this).getSupportActionBar().setTitle(getString(R.string.title_bar_review));

        extras = getIntent().getExtras();

//        selectedOption = extras.getString("Inventory");
        InitializeControls();
        filterListByNotReview();
        filterListByReview();
        setRecyclerViewData();

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
                public boolean onQueryTextChange(String s) {
                    FilterListView(s);
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String s) {

                    //clear the previous data in search arraylist if exist

                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }
}
