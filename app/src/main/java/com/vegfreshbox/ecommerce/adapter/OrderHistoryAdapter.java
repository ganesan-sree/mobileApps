package com.vegfreshbox.ecommerce.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.vegfreshbox.ecommerce.R;
import com.vegfreshbox.ecommerce.pojo.AddressPojo;
import com.vegfreshbox.ecommerce.pojo.OrderHistoryPojo;
import com.vegfreshbox.ecommerce.pojo.ProductPojo;

import java.util.ArrayList;

/**
 * Created by ganesh on 09/01/17.
 */

public class OrderHistoryAdapter extends ArrayAdapter<OrderHistoryPojo> {

    public ArrayList<OrderHistoryPojo> getOrderlist() {
        return orderlist;
    }

    public void setOrderlist(ArrayList<OrderHistoryPojo> orderlist) {
        this.orderlist = orderlist;
    }

    ArrayList<OrderHistoryPojo> orderlist;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    ImageLoader imageLoader;
    Context context;



    public OrderHistoryAdapter(Context context, int resource, ArrayList<OrderHistoryPojo> objects) {
        super(context, resource, objects);

        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        orderlist = objects;

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
//            holder.image = (ImageView) v.findViewById(R.id.img_product);
            holder.orderNo = (TextView) v.findViewById(R.id.orderNo);
            holder.date = (TextView) v.findViewById(R.id.orderdate);
            holder.amount = (TextView) v.findViewById(R.id.ordertotal);
            holder.status = (TextView) v.findViewById(R.id.orderstatus);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();

        }


        holder.date.setText("Order Date  : "+orderlist.get(position).getOrderdate());
        holder.amount.setText("Order Total : "+orderlist.get(position).getOrderamount());
        holder.status.setText("Order Status :"+orderlist.get(position).getOrderstatus());
        holder.orderNo.setText("Order no :"+orderlist.get(position).getId());

        return v;

    }



    static class ViewHolder {

        public TextView date,amount,status,orderNo;

    }

}
