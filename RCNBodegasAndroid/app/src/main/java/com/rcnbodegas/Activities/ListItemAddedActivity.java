package com.rcnbodegas.Activities;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rcnbodegas.Fragments.WarehouseFragment;
import com.rcnbodegas.Global.AddedElementListAdapter;
import com.rcnbodegas.Global.GlobalClass;
import com.rcnbodegas.CustomEvents.onRecyclerReviewListDeleteItemClick;
import com.rcnbodegas.CustomEvents.onRecyclerReviewListEditItemClick;
import com.rcnbodegas.R;
import com.rcnbodegas.Repository.MaterialHeaderRepository;
import com.rcnbodegas.Repository.MaterialImagesRepository;
import com.rcnbodegas.Repository.MaterialRepository;
import com.rcnbodegas.ViewModels.MaterialImagesViewModel;
import com.rcnbodegas.ViewModels.MaterialViewModel;
import com.rcnbodegas.ViewModels.MaterialViewmodelHeader;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListItemAddedActivity extends AppCompatActivity {
    private MaterialRepository materialRepository;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<MaterialViewModel> listMaterialByReview;
    private ArrayList<MaterialViewModel> sortEmpList;
    private MaterialHeaderRepository materialHeaderRepository;
    private AddedElementListAdapter adapter;
    private View mIncidenciasFormView;
    private View mProgressView;
    private TextView txtResumen;
    private TextView txtTotal;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_legalization);

        InitializeControls();
        InitializeEvents();
        filterListByNotReview();
        setRecyclerViewData();

    }


    private void InitializeControls() {

        txtResumen = findViewById(R.id.txtResumen);
        mIncidenciasFormView = findViewById(R.id.review_recycler_view);
        mProgressView = findViewById(R.id.review_progress);
        recyclerView = (RecyclerView) findViewById(R.id.review_recycler_view);
        txtTotal = findViewById(R.id.txtTotal);

        recyclerView.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(ListItemAddedActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
         materialRepository = new MaterialRepository(getApplicationContext());

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

    private void InitializeEvents() {

    }

    private void OpenEditDialog(MaterialViewModel wareHouseViewModel) {
        try {
            MaterialImagesRepository materialImagesRepository = new MaterialImagesRepository(getApplicationContext());
            List<MaterialImagesViewModel> materialImagesViewModels = materialImagesRepository.getByMaterialDetailId(wareHouseViewModel.getIdDetail());
            wareHouseViewModel.setListaImagenesStr(null);

            for (MaterialImagesViewModel materialImagesViewModel : materialImagesViewModels) {
                wareHouseViewModel.getListaImagenesStr().add(materialImagesViewModel.getParsePhoto());
            }

            FragmentManager fm = getSupportFragmentManager();
            DialogFragment warehouseFragment = WarehouseFragment.newInstance(wareHouseViewModel);
            warehouseFragment.show(fm, "Editar");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterListByNotReview() {
        materialHeaderRepository = new MaterialHeaderRepository(getApplicationContext());
        MaterialViewmodelHeader materialViewmodelHeader = materialHeaderRepository.getLegalizationPendingProcess();
        listMaterialByReview = (ArrayList<MaterialViewModel>) materialRepository.getMaterialLegalizationDetail(materialViewmodelHeader.getId());
        txtResumen.setText(getString(R.string.message_resume_legalization_list) + listMaterialByReview.size());
        SumPurchaseValue();
    }

    private void setRecyclerViewData() {
        adapter = new AddedElementListAdapter(listMaterialByReview, new onRecyclerReviewListEditItemClick() {
            @Override
            public void onClick(MaterialViewModel wareHouseViewModel) {
                OpenEditDialog(wareHouseViewModel);
            }
        }, new onRecyclerReviewListDeleteItemClick() {
            @Override
            public void onClick(MaterialViewModel wareHouseViewModel) {
                showConfirmDialog("Seguro de eliminar este elemento?", wareHouseViewModel);
            }
        });
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

            adapter = new AddedElementListAdapter(sortEmpList, new onRecyclerReviewListEditItemClick() {
                @Override
                public void onClick(MaterialViewModel wareHouseViewModel) {
                    OpenEditDialog(wareHouseViewModel);
                }
            }, new onRecyclerReviewListDeleteItemClick() {
                @Override
                public void onClick(MaterialViewModel wareHouseViewModel) {
                    showConfirmDialog("Seguro de eliminar este elemento_", wareHouseViewModel);
                }
            });
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showConfirmDialog(String message, final MaterialViewModel wareHouseViewModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListItemAddedActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Bodegas");
        builder.setMessage(message);
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DropElement(wareHouseViewModel);
                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            setResult(RESULT_OK, null);
            finish();
            return true; //I have tried here true also
        }
        return super.onKeyDown(keyCode, event);
    }

    private void DropElement(MaterialViewModel dropElement) {

        int index = listMaterialByReview.indexOf(dropElement);

        //materialRepository.delete(dropElement);

        if (index != -1) {
            listMaterialByReview.remove(dropElement);
            GlobalClass.getInstance().getDataMaterial().remove(dropElement);
            SumPurchaseValue();
        }
        adapter.notifyDataSetChanged();
        adapter.notifyItemRemoved(index);


    }

}
