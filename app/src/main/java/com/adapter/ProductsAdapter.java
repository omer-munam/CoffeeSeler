package com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.balram.library.FotTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.coffee.seller.ksa.BaseActivity;
import com.coffee.seller.ksa.ProductsMani;
import com.tami.seller.ksa.R;
import com.utils.ConnectionClass;
import com.utils.Functions;
import com.utils.SharePrefsEntry;

import org.json.JSONObject;

import java.util.ArrayList;


public class ProductsAdapter extends ArrayAdapter<JSONObject>
{
    ArrayList<JSONObject> list = new ArrayList<JSONObject>();
    Activity context;
    Functions function;
    ConnectionClass cc;
    SharePrefsEntry sp;
    public ProductsAdapter(Activity con, ArrayList<JSONObject> l)
    {
        super(con, R.layout.row_products);

        context             = con;
        list                = l;
        sp                  = new SharePrefsEntry(con);
        function            = new Functions(con);
        cc                  = new ConnectionClass(con);
    }

    @Override
    public int getCount() {
        return list.size();
    }




    public class ViewHolderItem {
        FotTextView name , price , location , category;
        CircularImageView img;
        ImageView deleteData,editData;

    }

    @Override
    public View getView(final int paramInt, View convertView, ViewGroup parent)
    {
        try
        {
            ViewHolderItem viewHolder;
            if(convertView==null)
            {
                LayoutInflater inflater 	= context.getLayoutInflater();
                convertView 				= inflater.inflate(R.layout.row_products, parent, false);
                viewHolder 					= new ViewHolderItem();
                viewHolder.img			    = (CircularImageView)convertView.findViewById(R.id.img);
                viewHolder.name			    = (FotTextView)convertView.findViewById(R.id.name);
                viewHolder.price			= (FotTextView)convertView.findViewById(R.id.price);
                viewHolder.category			= (FotTextView)convertView.findViewById(R.id.category);
                viewHolder.location			= (FotTextView)convertView.findViewById(R.id.location);
                viewHolder.deleteData		= (ImageView)convertView.findViewById(R.id.delete_data);
                viewHolder.editData		    = (ImageView)convertView.findViewById(R.id.edit_cat);

                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder 					= (ViewHolderItem) convertView.getTag();
            }

            final JSONObject jsonObject		= list.get(paramInt);

            final CircularImageView img     = viewHolder.img;

            String dl                       = BaseActivity.getDefLang(context);
            String title                    = "title"+dl ;
            viewHolder.name.setText(jsonObject.getString(title));
            viewHolder.price.setText(jsonObject.getString("price"));
            viewHolder.location.setText(jsonObject.getString("location"+dl));


            if(jsonObject.getString("subcat"+dl).equals("null") || jsonObject.getString("subcat"+dl).equals(""))
                    viewHolder.category.setText(jsonObject.getString("cat"+dl));
            else
                    viewHolder.category.setText(jsonObject.getString("cat"+dl)+" , "+jsonObject.getString("subcat"+dl));

            Glide.with(context)
                    .load(jsonObject.getString("images"))
                    .placeholder(R.mipmap.ic_launcher)
                    .into(new GlideDrawableImageViewTarget(img) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            img.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {}
                    });


            viewHolder.editData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ProductsMani.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("data",jsonObject+"");
                    context.startActivity(intent);
                }
            });

            viewHolder.deleteData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.confirm(jsonObject,4);
                }
            });


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }





}