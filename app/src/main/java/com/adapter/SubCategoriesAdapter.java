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
import com.coffee.seller.ksa.SubCategoriesManiExplicit;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.coffee.seller.ksa.BaseActivity;
import com.tami.seller.ksa.R;
import com.coffee.seller.ksa.SubCategoriesMani;
import com.utils.ConnectionClass;
import com.utils.Functions;
import com.utils.SharePrefsEntry;

import org.json.JSONObject;

import java.util.ArrayList;


public class SubCategoriesAdapter extends ArrayAdapter<JSONObject>
{
    ArrayList<JSONObject> list = new ArrayList<JSONObject>();
    Activity context;
    Functions function;
    ConnectionClass cc;
    SharePrefsEntry sp;
    public SubCategoriesAdapter(Activity con, ArrayList<JSONObject> l)
    {
        super(con, R.layout.row_subcat);

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
        FotTextView name , category;
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
                convertView 				= inflater.inflate(R.layout.row_subcat, parent, false);
                viewHolder 					= new ViewHolderItem();
                viewHolder.img			    = (CircularImageView)convertView.findViewById(R.id.img);
                viewHolder.name			    = (FotTextView)convertView.findViewById(R.id.name);
                viewHolder.category			= (FotTextView)convertView.findViewById(R.id.category);
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

            String title                    = "title"+ BaseActivity.getDefLang(context);
            viewHolder.name.setText(jsonObject.getString(title));
            viewHolder.category.setText(jsonObject.getString("cat"+BaseActivity.getDefLang(context)));

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
                    Intent intent = new Intent(context, SubCategoriesMani.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("data",jsonObject+"");
                    context.startActivity(intent);
                }
            });

            viewHolder.deleteData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.confirm(jsonObject,3);
                }
            });


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }





}