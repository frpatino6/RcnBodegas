package com.rcnbodegas.Global;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.MaterialViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddedElementListAdapter extends RecyclerView.Adapter<AddedElementListAdapter.MyViewHolder> {
    private ArrayList<MaterialViewModel> dataSet;
    public onRecyclerReviewListItemClick _event;
    private int row_index;
    private boolean isClicked = false;
    private View _view;
    private Integer SelectedIncidencia;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtMaterial;
        TextView txtMarca;
        TextView txtBarcode;
        TextView txtPrecion;
        Toolbar toolbarCard;
        ImageButton btnEdit;
        RelativeLayout layoutWareHouseView;

        public MyViewHolder(View itemView) {
            super(itemView);
            layoutWareHouseView = itemView.findViewById(R.id.layoutReview);
            this.txtMaterial = (TextView) itemView.findViewById(R.id.txtMaterial);
            this.txtMarca = (TextView) itemView.findViewById(R.id.txtMarca);
            this.txtBarcode = (TextView) itemView.findViewById(R.id.txtBarcode);
            this.txtPrecion = (TextView) itemView.findViewById(R.id.txtPrecion);
            this.btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    public AddedElementListAdapter(ArrayList<MaterialViewModel> data, onRecyclerReviewListItemClick event) {
        this.dataSet = data;
        this._event=event;
    }

    List<RelativeLayout> layoutViewList = new ArrayList<>();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        MyViewHolder myViewHolder = null;

        try {

            _view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_list_item_added_item, parent, false);
            myViewHolder = new MyViewHolder(_view);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView txtMaterial = holder.txtMaterial;
        TextView txtMarca = holder.txtMarca;
        TextView txtBarcode = holder.txtBarcode;
        TextView txtPrecion = holder.txtPrecion;
        ImageButton btnEdit = holder.btnEdit;
        Toolbar toolbarCard = holder.toolbarCard;

        txtMaterial.setText(dataSet.get(listPosition).getMaterialName());
        txtMarca.setText(dataSet.get(listPosition).getMarca().toString());
        txtBarcode.setText(dataSet.get(listPosition).getBarCode().toString());
        txtPrecion.setText(dataSet.get(listPosition).getUnitPrice().toString());

        holder.layoutWareHouseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = listPosition;
                isClicked = true;

                notifyDataSetChanged();
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_event!=null){
                    _event.onClick(dataSet.get(listPosition));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

