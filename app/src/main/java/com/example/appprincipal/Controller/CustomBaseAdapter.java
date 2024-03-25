package com.example.appprincipal.Controller;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.appprincipal.Models.Fotografia;
import com.example.appprincipal.R;

import java.util.List;

public class CustomBaseAdapter extends ArrayAdapter<Fotografia> {

    private List<Fotografia> mList;
    private Context mContext;
    private int resourceLayout;

    public CustomBaseAdapter(@NonNull Context context, int resource, List<Fotografia> objects) {
        super(context, resource, objects);
        this.mList = objects;
        this.mContext = context;
        this.resourceLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(resourceLayout, parent, false);
            holder = new ViewHolder();
            holder.imagen = view.findViewById(R.id.image_View);
            holder.description = view.findViewById(R.id.description);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Fotografia foto = mList.get(position);

        // Establecer la imagen si tienes el recurso de imagen en tu clase Fotografia
        // Decodificar la cadena Base64 en un objeto Bitmap
        Bitmap bitmap = decodeBase64(foto.getImagen());
        holder.imagen.setImageBitmap(bitmap);

        holder.description.setText(foto.getDescription());

        return view;
    }

    // Método para decodificar una cadena Base64 en un objeto Bitmap
    private Bitmap decodeBase64(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    static class ViewHolder {
        ImageView imagen;
        TextView description;
    }
}
