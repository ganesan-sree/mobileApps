package com.vegfreshbox.ecommerce.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.vegfreshbox.ecommerce.MainActivity;
import com.vegfreshbox.ecommerce.ProductsActivity;
import com.vegfreshbox.ecommerce.R;
import com.vegfreshbox.ecommerce.pojo.MasterCategory;
import com.vegfreshbox.ecommerce.utils.BitmapLruCache;
import com.vegfreshbox.ecommerce.utils.VegUtils;

import java.util.ArrayList;

/**
 * Created by ganesh on 11/12/16.
 */

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder>{
         private ArrayList<MasterCategory> masterCategories;
         private Context context;



        public HomeCategoryAdapter(Context context, ArrayList<MasterCategory> masterCategories) {
            this.masterCategories = masterCategories;
            this.context = context;
        }

        @Override
        public HomeCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_category, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HomeCategoryAdapter.ViewHolder viewHolder, final int i) {

            ImageLoader.ImageCache imageCache = new BitmapLruCache();
            ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(context), imageCache);

            viewHolder.tv_android.setText(masterCategories.get(i).getName());
            //viewHolder.img_android.setImageUrl(android.get(i).getAndroid_image_url(),imageLoader);
            
            if(masterCategories.get(i).getImageLocal() !=null){
                Picasso.with(context).load("file:///android_asset/"+masterCategories.get(i).getImageLocal()).resize(500, 240).into(viewHolder.img_android);
            }
            else{
                Picasso.with(context).load(masterCategories.get(i).getImage()).resize(500, 240).into(viewHolder.img_android);
            }
            
            //Picasso.with(context).load(masterCategories.get(i).getImage()).resize(240, 120).into(viewHolder.img_android);
            
            

            viewHolder.img_android.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(VegUtils.isOnline(context)){
                        Intent intent = new Intent(context, ProductsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id",masterCategories.get(i).getId());
                        intent.putExtra("Name",masterCategories.get(i).getName());
                        context.startActivity(intent);
                    }else{
                        Toast.makeText(context, "You are not connected to Internet please go online", Toast.LENGTH_SHORT).show();
                    }



                }
            });


        }




        @Override
        public int getItemCount() {
            return masterCategories.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView tv_android;
            private ImageView img_android;
            public ViewHolder(View view) {
                super(view);

                tv_android = (TextView)view.findViewById(R.id.tv_android);
                img_android = (ImageView) view.findViewById(R.id.img_android);
            }
        }
}
