package com.rcnbodegas.Global;

import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rcnbodegas.R;
import com.rcnbodegas.ViewModels.ProductionViewModel;

import java.util.ArrayList;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.MyViewHolder> {
    private ArrayList<Bitmap> dataSet;
    ArrayList<String> listaNombresImagenes;

    private onRecyclerProductionListItemClick _event;
    private int row_index;
    private boolean isClicked = false;
    private View _view;
    private Integer SelectedIncidencia;

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

    public PhotosAdapter(ArrayList<Bitmap> data, ArrayList<String> _listaNombresImagenes, onRecyclerProductionListItemClick event) {
        this.dataSet = data;
        this._event = event;
        this.listaNombresImagenes = _listaNombresImagenes;

    }

    List<RelativeLayout> layoutViewList = new ArrayList<>();

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

        if (listaNombresImagenes != null)
            tvNombreFichero.setText(listaNombresImagenes.get(listPosition));

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

