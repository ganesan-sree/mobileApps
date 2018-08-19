package com.vegfreshbox.ecommerce.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.vegfreshbox.ecommerce.R;
import com.vegfreshbox.ecommerce.database.MyCart;
import com.vegfreshbox.ecommerce.pojo.ProductPojo;
import com.vegfreshbox.ecommerce.utils.BitmapLruCache;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ganesh on 03/01/17.
 */

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    private List<ProductPojo> verticalList;
    ImageLoader imageLoader;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView,price,ourprice;
        ImageView profilePic,addtocart,removefromcart;



        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.product_list_name);
            profilePic = (ImageView)view.findViewById(R.id.img_product);
            addtocart=(ImageView)view.findViewById(R.id.addtocart);
            removefromcart=(ImageView)view.findViewById(R.id.removefromcart);
            price=(TextView)view.findViewById(R.id.price);
            //ourprice=(TextView)view.findViewById(R.id.ourprice);

        }
    }


    public MyCartAdapter(Context context,List<ProductPojo> verticalList) {
        this.verticalList = verticalList;
        this.context=context;
//        imageLoader = AppController.getInstance().getImageLoader();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_table_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyCartAdapter.MyViewHolder holder, final int position) {

        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(context), imageCache);

        holder.txtView.setText(verticalList.get(position).getName());
        Picasso.with(context).load(verticalList.get(position).getImage()).resize(250, 250).into(holder.profilePic);
        holder.price.setText(verticalList.get(position).getPrice());
        //holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //holder.ourprice.setText(verticalList.get(position).getOurprice());
//        CHECK PRODUCT AVAILABE IN DATABASE OR NOT
        MyCart myCartdb=new MyCart(context);
        myCartdb.open();
        int i=myCartdb.checkAvailable(verticalList.get(position).getId());
        if(i==0){
            holder.addtocart.setVisibility(View.VISIBLE);
            holder.removefromcart.setVisibility(View.GONE);
        }else{
            holder.removefromcart.setVisibility(View.VISIBLE);
            holder.addtocart.setVisibility(View.GONE);

        }
        myCartdb.close();
//        END PRODUCT CHECK


        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Added to cart successfully.",Toast.LENGTH_SHORT).show();
                MyCart myCartdb=new MyCart(context);
                myCartdb.open();
                myCartdb.insertData(verticalList.get(position).getId(), verticalList.get(position).getName(), verticalList.get(position).getImage(),
                		verticalList.get(position).getImagelocal(),verticalList.get(position).getPrice(), verticalList.get(position).getWgt(), "1");
                myCartdb.close();
                holder.removefromcart.setVisibility(View.VISIBLE);
                holder.addtocart.setVisibility(View.GONE);
//                new ProductsActivity().controllcountincrease();
            }
        });
        holder.removefromcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Product deleted from your cart.",Toast.LENGTH_SHORT).show();
                MyCart myCartdb=new MyCart(context);
                myCartdb.open();
                myCartdb.deleteproduct(verticalList.get(position).getId());
                myCartdb.close();
                holder.addtocart.setVisibility(View.VISIBLE);
                holder.removefromcart.setVisibility(View.GONE);
//                new ProductsActivity().controllcountincrease();
            }
        });
    }

    @Override
    public int getItemCount() {
        return verticalList.size();
    }
}