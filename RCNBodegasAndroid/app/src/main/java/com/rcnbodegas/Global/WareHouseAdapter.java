package com.rcnbodegas.Global;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.WareHouseViewModel;
import com.rcnbodegas.ViewModels.WareHouseViewModel;

import java.util.ArrayList;
import java.util.List;

public class WareHouseAdapter extends RecyclerView.Adapter<WareHouseAdapter.MyViewHolder> {
    private ArrayList<WareHouseViewModel> dataSet;
    private onRecyclerItemClick _event;
    private int row_index;
    private boolean isClicked = false;
    private View _view;
    private Integer SelectedIncidencia;
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtId;
        TextView txtName;
        ConstraintLayout layoutWareHouseView;

        public MyViewHolder(View itemView) {
            super(itemView);
            layoutWareHouseView = itemView.findViewById(R.id.layoutWareHouse);
            this.txtName= (TextView) itemView.findViewById(R.id.txtName);
            this.txtId= (TextView) itemView.findViewById(R.id.txtId);

        }
    }

    public WareHouseAdapter(ArrayList<WareHouseViewModel> data, onRecyclerItemClick event) {
        this.dataSet = data;
        this._event = event;

    }

    List<RelativeLayout> layoutViewList = new ArrayList<>();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        MyViewHolder myViewHolder = null;

        try {

            _view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_warehouse_item, parent, false);
            myViewHolder = new MyViewHolder(_view);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView txtId = holder.txtId;
        TextView txtName= holder.txtName;


        txtName.setText(dataSet.get(listPosition).getWareHouseName());
        txtId.setText(dataSet.get(listPosition).getId().toString());

        holder.layoutWareHouseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = listPosition;
                isClicked = true;
                if (_event != null)
                    _event.onClick(dataSet.get(listPosition).getId());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

