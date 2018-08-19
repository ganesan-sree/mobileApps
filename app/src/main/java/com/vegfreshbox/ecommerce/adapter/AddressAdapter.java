package com.vegfreshbox.ecommerce.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.vegfreshbox.ecommerce.AddressAdd;
import com.vegfreshbox.ecommerce.CheckoutActivity;
import com.vegfreshbox.ecommerce.R;
import com.vegfreshbox.ecommerce.pojo.AddressPojo;
import com.vegfreshbox.ecommerce.utils.BitmapLruCache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ganesh on 06/01/17.
 */

public class AddressAdapter extends ArrayAdapter<AddressPojo> {

    ArrayList<AddressPojo> contactlist;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    ImageLoader imageLoader;
    Context context;
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;


    public AddressAdapter(Context context, int resource, ArrayList<AddressPojo> objects) {
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
//            holder.image = (ImageView) v.findViewById(R.id.img_product);
            holder.name = (TextView) v.findViewById(R.id.name);
            holder.address = (TextView) v.findViewById(R.id.address);
            holder.pincode = (TextView) v.findViewById(R.id.pincode);
            holder.mobile=(TextView)v.findViewById(R.id.mobile);
            holder.mRadioButton=(RadioButton)v.findViewById(R.id.selectedadd);


            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();

        }


//        Picasso.with(context).load(contactlist.get(position).getImage()).resize(100, 100).into(holder.image);
        holder.name.setText(contactlist.get(position).getName());
        holder.address.setText(contactlist.get(position).getFlat()+" - "+contactlist.get(position).getColony());
        holder.pincode.setText(contactlist.get(position).getPincode());
        holder.mobile.setText(contactlist.get(position).getMobile());
        holder.mRadioButton.setId(Integer.parseInt(contactlist.get(position).getId()));
        holder.mRadioButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position != mSelectedPosition && mSelectedRB != null){
                    mSelectedRB.setChecked(false);
                }
                CheckoutActivity.chooseaddr=1;
                mSelectedPosition = position;
                mSelectedRB = (RadioButton) view;
                CheckoutActivity.addressId=""+mSelectedRB.getId();

                Log.e("Addres selected Id===", ""+mSelectedRB.getId());
                Log.e("Addres selected Id===", ""+mSelectedRB.getText());
            }
        });

        return v;

    }
    static class ViewHolder {

        public TextView name,address,pincode;
        public TextView mobile,ourprice;
        public ImageView image,product_quantity_dec,product_quantity_inc;
        public TextView total;
        RadioButton mRadioButton;
    }
    public String checkNull(String s){
        if(s.equals("null"))
            return "N/A";
        else
            return s;
    }
}
