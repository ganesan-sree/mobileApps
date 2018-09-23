package com.vegfreshbox.ecommerce.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.vegfreshbox.ecommerce.R;
import com.vegfreshbox.ecommerce.pojo.OrderHistoryProductsDetailsPojo;
import com.squareup.picasso.Picasso;
import com.vegfreshbox.ecommerce.pojo.ProductPojo;
import com.vegfreshbox.ecommerce.utils.VegUtils;

import java.util.ArrayList;

/**
 * Created by ganesh on 01/02/17.
 */

public class OrderHistoryDetailsAdapter extends ArrayAdapter<ProductPojo> {

    ArrayList<ProductPojo> contactlist;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    ImageLoader imageLoader;
    Context context;


    public OrderHistoryDetailsAdapter(Context context, int resource, ArrayList<ProductPojo> objects) {
        super(context, resource, objects);

        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        contactlist = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.img_product = (ImageView) v.findViewById(R.id.img_product);
            holder.product_list_name = (TextView) v.findViewById(R.id.product_list_name);
            holder.price = (TextView) v.findViewById(R.id.price);
            holder.productQuantity = (TextView) v.findViewById(R.id.product_quantity);
            holder.wgt = (TextView) v.findViewById(R.id.wgt);
            
           
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();

        }

        if(contactlist.get(position).getImagelocal() !=null){
        	Picasso.with(context).load("file:///android_asset/"+contactlist.get(position).getImagelocal()).resize(100, 100).into(holder.img_product);
        }else{
        	Picasso.with(context).load(contactlist.get(position).getImage()).resize(100, 100).into(holder.img_product);
        }

       
       holder.product_list_name.setText(contactlist.get(position).getName().toString());
       
       holder.price.setText(VegUtils.getSubTotal(contactlist.get(position).getPrice(),contactlist.get(position).getQuantity()));
       // holder.productQuantity.setText(contactlist.get(position).getQuantity());
        holder.wgt.setText(contactlist.get(position).getWgt());
    




        return v;

    }

    static class ViewHolder {

        public TextView product_list_name,price,productQuantity,wgt;
        public ImageView img_product;

    }
}