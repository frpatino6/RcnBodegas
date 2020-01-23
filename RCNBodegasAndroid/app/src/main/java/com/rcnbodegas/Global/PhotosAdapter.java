package com.rcnbodegas.Global;

import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rcnbodegas.R;

import java.util.ArrayList;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.MyViewHolder> {
    List<RelativeLayout> layoutViewList = new ArrayList<>();
    private Integer SelectedIncidencia;
    private onRecyclerProductionListItemClick _event;
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

    public PhotosAdapter(ArrayList<Bitmap> data, ArrayList<String> _listaNombresImagenes, onRecyclerProductionListItemClick event) {
        this.dataSet = data;
        this._event = event;
        this.listaNombresImagenes = _listaNombresImagenes;

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        ImageView iv = holder.iv;
        TextView tvNombreFichero = holder.tvNombreFichero;

        iv.setImageBitmap(dataSet.get(listPosition));

        if (listaNombresImagenes != null)
            tvNombreFichero.setText(listaNombresImagenes.get(listPosition));

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

