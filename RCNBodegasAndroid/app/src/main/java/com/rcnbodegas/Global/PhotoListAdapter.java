package com.rcnbodegas.Global;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rcnbodegas.R;

import java.util.ArrayList;

/**
 * Created by windows 8 on 13/05/14.
 */
public class PhotoListAdapter extends BaseAdapter
{
    ArrayList<String> listaNombresImagenes;
    ArrayList<Bitmap> images;
    LayoutInflater inflater;
/*
    public PhotoListAdapter(ArrayList<Bitmap> Images, Context c)
    {
        images = Images;
        inflater= LayoutInflater.from(c);

    }
*/
    public PhotoListAdapter(ArrayList<Bitmap> Images, ArrayList<String> listaNombresImagenes, Context c)
    {
        this.images = Images;
        this.listaNombresImagenes = listaNombresImagenes;
        this.inflater = LayoutInflater.from(c);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return images.size();
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_foto, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.imgDano);
            holder.tvNombreFichero = (TextView) convertView.findViewById(R.id.tvNombreFichero);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.iv.setImageBitmap(images.get(position));
        holder.iv.setContentDescription(listaNombresImagenes.get(position));
        holder.tvNombreFichero.setText(listaNombresImagenes.get(position));
        return convertView;
    }

}

class ViewHolder
{
    ImageView iv;
    TextView tvNombreFichero;
}