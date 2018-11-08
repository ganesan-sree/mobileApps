package com.vegfreshbox.ecommerce.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vegfreshbox.ecommerce.CartActivity;
import com.vegfreshbox.ecommerce.HomeActivity;
import com.vegfreshbox.ecommerce.R;
import com.vegfreshbox.ecommerce.database.DatabaseUtil;
import com.vegfreshbox.ecommerce.pojo.ProductPojo;
import com.vegfreshbox.ecommerce.utils.VegUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ganesh on 02/01/17.
 */

public class CartListAdapter extends ArrayAdapter<ProductPojo> {

    ArrayList<ProductPojo> contactlist;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    Context context;

    public CartListAdapter(Context context, int resource,
                           ArrayList<ProductPojo> objects) {
        super(context, resource, objects);
        this.context = context;
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
            holder.image = (ImageView) v.findViewById(R.id.img_product);
            holder.name = (TextView) v.findViewById(R.id.product_list_name);
            holder.price = (TextView) v.findViewById(R.id.price);
            holder.wgt = (TextView) v.findViewById(R.id.wgt);
            holder.product_quantity = (TextView) v.findViewById(R.id.product_quantity);
            holder.product_quantity_dec = (ImageView) v.findViewById(R.id.product_quantity_dec);
            holder.product_quantity_inc = (ImageView) v.findViewById(R.id.product_quantity_inc);
            holder.totalprice = (TextView) v.findViewById(R.id.totalprice);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();

        }

        if (contactlist.get(position).getImagelocal() != null) {
            Picasso.with(context)
                    .load("file:///android_asset/"
                            + contactlist.get(position).getImagelocal())
                    .resize(100, 100).into(holder.image);
        } else {
            Picasso.with(context).load(contactlist.get(position).getImage())
                    .resize(100, 100).into(holder.image);
        }

        holder.name.setText(contactlist.get(position).getName());
        holder.price.setText(VegUtils.getSubTotal(contactlist.get(position).getPrice(),
                contactlist.get(position).getQuantity()));
        holder.wgt.setText(contactlist.get(position).getWgt());

        // holder.price.setPaintFlags(holder.price.getPaintFlags() |
        // Paint.STRIKE_THRU_TEXT_FLAG);
        // holder.ourprice.setText(contactlist.get(position).getOurprice());
        holder.product_quantity
                .setText(contactlist.get(position).getQuantity());
        holder.product_quantity_dec
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int qty = Integer.parseInt(contactlist.get(position).getQuantity());
                        if (qty == 1) {
                            DatabaseUtil.deleteProduct(context, contactlist.get(position).getId());
                            CartListAdapter.this.remove(CartListAdapter.this.getItem(position));
                            CartActivity.updatetotal(100);
                            HomeActivity.countproductoncart = HomeActivity.countproductoncart - 1;
                            notifyDataSetChanged();

                        } else {

                            String wgt = contactlist.get(position).getWgt();
                            wgt = VegUtils.getWeightDec(wgt, qty);
                            qty = qty - 1;

                            contactlist.get(position).setWgt(String.valueOf(wgt));
                            holder.wgt.setText(String.valueOf(wgt));
                            contactlist.get(position).setQuantity(String.valueOf(qty));
                            holder.product_quantity.setText(String.valueOf(qty));
                            holder.price.setText(VegUtils.getSubTotal(contactlist.get(position).getPrice(), "" + qty));
                            DatabaseUtil.updateQtyAndWgt(getContext(), contactlist.get(position).getId(), wgt, qty);
                            CartActivity.updatetotal(100);
                            notifyDataSetChanged();
                        }
                    }
                });

        holder.product_quantity_inc
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int qty = Integer.parseInt(contactlist.get(position)
                                .getQuantity());
                        String wgt = contactlist.get(position).getWgt();

                        wgt = VegUtils.getWeight(wgt, qty);
                        qty = qty + 1;

                        contactlist.get(position).setWgt(String.valueOf(wgt));
                        holder.wgt.setText(String.valueOf(wgt));
                        contactlist.get(position).setQuantity( String.valueOf(qty));
                        // Toast.makeText(getContext(),"tttt"+qty,Toast.LENGTH_SHORT).show();
                        holder.product_quantity.setText(String.valueOf(qty));
                        holder.price.setText(VegUtils.getSubTotal(contactlist.get(position).getPrice(), "" + qty));
                        DatabaseUtil.updateQtyAndWgt(getContext(), contactlist.get(position).getId(), wgt, qty);

                        CartActivity.updatetotal(100);
                        notifyDataSetChanged();

                    }
                });
        return v;

    }

    static class ViewHolder {

        public TextView name, product_quantity, totalprice;
        public TextView price, wgt;
        public ImageView image, product_quantity_dec, product_quantity_inc;
        public TextView total;
    }

    public String checkNull(String s) {
        if (s.equals("null"))
            return "N/A";
        else
            return s;
    }

}
