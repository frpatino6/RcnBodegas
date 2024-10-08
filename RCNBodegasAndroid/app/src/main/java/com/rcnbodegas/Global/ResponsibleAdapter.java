package com.rcnbodegas.Global;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rcnbodegas.CustomEvents.onRecyclerResponsibleListItemClick;
import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.ResponsibleViewModel;

import java.util.ArrayList;
import java.util.List;

public class ResponsibleAdapter extends RecyclerView.Adapter<ResponsibleAdapter.MyViewHolder> {
    List<RelativeLayout> layoutViewList = new ArrayList<>();
    private Integer SelectedIncidencia;
    private onRecyclerResponsibleListItemClick _event;
    private View _view;
    private ArrayList<ResponsibleViewModel> dataSet;
    private boolean isClicked = false;
    private int row_index;

    public ResponsibleAdapter(ArrayList<ResponsibleViewModel> data, onRecyclerResponsibleListItemClick event) {
        this.dataSet = data;
        this._event = event;

    }

    @Override
    public int getItemCount() {

        if (dataSet != null)
            return dataSet.size();
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView txtId = holder.txtId;
        TextView txtName = holder.txtName;


        txtName.setText(dataSet.get(listPosition).getName());
        txtId.setText(dataSet.get(listPosition).getId().toString());

        holder.layoutCompanyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = listPosition;
                isClicked = true;
                if (_event != null)
                    _event.onClick(dataSet.get(listPosition));
                notifyDataSetChanged();
            }
        });
    }

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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtId;
        TextView txtName;
        ConstraintLayout layoutCompanyView;

        public MyViewHolder(View itemView) {
            super(itemView);
            layoutCompanyView = itemView.findViewById(R.id.layoutCompany);
            this.txtName = (TextView) itemView.findViewById(R.id.txtName);
            this.txtId = (TextView) itemView.findViewById(R.id.txtId);

        }
    }
}

