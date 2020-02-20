package com.rcnbodegas.Global;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rcnbodegas.R;

import java.util.ArrayList;
import java.util.List;

public class LastDocumentsAdapter extends RecyclerView.Adapter<LastDocumentsAdapter.MyViewHolder> {
    private ArrayList<Integer> dataSet;


    private View _view;
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtId;
        TextView txtName;
        ConstraintLayout layoutCompanyView;

        public MyViewHolder(View itemView) {
            super(itemView);
            layoutCompanyView = itemView.findViewById(R.id.layoutCompany);
            this.txtName= (TextView) itemView.findViewById(R.id.txtName);
            this.txtId= (TextView) itemView.findViewById(R.id.txtId);

        }
    }

    public LastDocumentsAdapter(ArrayList<Integer> data) {
        this.dataSet = data;
    }

    List<RelativeLayout> layoutViewList = new ArrayList<>();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        MyViewHolder myViewHolder = null;

        try {

            _view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_company_item, parent, false);
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


        txtName.setText(dataSet.get(listPosition).toString());
        txtId.setText(dataSet.get(listPosition).toString());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

