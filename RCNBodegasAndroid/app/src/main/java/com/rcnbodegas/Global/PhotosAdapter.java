package com.rcnbodegas.Global;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.rcnbodegas.CustomEvents.onRecyclerProductionListItemClick;
import com.rcnbodegas.CustomEvents.onRecyclerProductionListLongItemClick;
import com.rcnbodegas.R;

import java.util.ArrayList;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.MyViewHolder> {
    private Integer SelectedIncidencia;
    private onRecyclerProductionListItemClick _event;
    private onRecyclerProductionListLongItemClick _onRecyclerProductionListLongItemClick;
    private View _view;
    private ArrayList<Bitmap> dataSet;
    private boolean isClicked = false;
    private ArrayList<String> listaNombresImagenes;
    private int row_index;

    public ArrayList<Bitmap> getDataSet() {
        return dataSet;
    }

    public void setDataSet(ArrayList<Bitmap> dataSet) {
        this.dataSet = dataSet;
    }

    public ArrayList<String> getListaNombresImagenes() {
        return listaNombresImagenes;
    }

    public void setListaNombresImagenes(ArrayList<String> listaNombresImagenes) {
        this.listaNombresImagenes = listaNombresImagenes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        MyViewHolder myViewHolder = null;

        try {

            _view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_foto, parent, false);
            myViewHolder = new MyViewHolder(_view);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        ImageView iv = holder.iv;
        TextView tvNombreFichero = holder.tvNombreFichero;

        iv.setImageBitmap(dataSet.get(listPosition));

        if (listaNombresImagenes != null && listaNombresImagenes.size() > 0)
            tvNombreFichero.setText(listaNombresImagenes.get(listPosition));

        iv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (_onRecyclerProductionListLongItemClick != null) {
                    _onRecyclerProductionListLongItemClick.onLongClick(listPosition);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    List<RelativeLayout> layoutViewList = new ArrayList<>();

    public PhotosAdapter(ArrayList<Bitmap> data, ArrayList<String> _listaNombresImagenes, onRecyclerProductionListItemClick event, onRecyclerProductionListLongItemClick _onRecyclerProductionListLongItemClick) {
        this.dataSet = data;
        this._event = event;
        this._onRecyclerProductionListLongItemClick = _onRecyclerProductionListLongItemClick;
        this.listaNombresImagenes = _listaNombresImagenes;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tvNombreFichero;
        ConstraintLayout layoutCompanyView;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.iv = itemView.findViewById(R.id.imgDano);
            this.tvNombreFichero = (TextView) itemView.findViewById(R.id.tvNombreFichero);

        }
    }
}

