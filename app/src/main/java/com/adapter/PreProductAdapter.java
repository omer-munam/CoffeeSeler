package com.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.balram.library.FotTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.coffee.seller.ksa.BaseActivity;
import com.coffee.seller.ksa.PreProductsManiExplicit;
import com.tami.seller.ksa.R;
import com.utils.ConnectionClass;
import com.utils.Functions;
import com.utils.SharePrefsEntry;
import com.widget.RoundedImg;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.coffee.seller.ksa.BaseActivity.globalLog;


public class PreProductAdapter extends RecyclerView.Adapter<PreProductAdapter.CustomViewHolder>
{
	ArrayList<JSONObject>    list = new ArrayList<JSONObject>();

    BaseActivity context;
    Functions function;
    SharePrefsEntry sp;
    ConnectionClass cc;
	public PreProductAdapter(BaseActivity con, ArrayList<JSONObject> l)
	{
		context             = con;
		list                = l;
        function            = new Functions(context);
        cc                  = new ConnectionClass(context);
        sp                  = new SharePrefsEntry(context);
	}


    @Override
    public int getItemCount() {
        return list.size();
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v                  = inflater.inflate(R.layout.row_product, parent, false);

        CustomViewHolder vh     = new CustomViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        try
        {
            final JSONObject jsonObject		= list.get(position);

            globalLog("jsonobject :: "+jsonObject);
            holder.name.setVisibility(View.VISIBLE);
            holder.price.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.VISIBLE);
            holder.imgadd.setVisibility(View.GONE);
            holder.status.setVisibility(View.GONE);
            holder.discounted.setVisibility(View.GONE);


            holder.name.setText(jsonObject.getString("title_en"));
            holder.price.setText(jsonObject.getString("title_ar"));
            holder.price.setTextColor(Color.parseColor("#333333"));

            Glide.with(context)
                    .load(jsonObject.getString("images"))
                    .placeholder(R.mipmap.ic_launcher)
                    .into(new GlideDrawableImageViewTarget(holder.img) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            holder.img.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {}
                    });


            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        Intent intent = new Intent(context,PreProductsManiExplicit.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("data",jsonObject+"");
                        context.startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            holder.img.setBorderWidth(function.dpToPx(1));
            holder.img.setBorderColor(Color.parseColor("#eeeeee"));

        }catch (Exception e){e.printStackTrace();}


    }


    public class CustomViewHolder extends RecyclerView.ViewHolder{
        FotTextView name , price , discounted ;
        RoundedImg img;
        RelativeLayout parent;
        RelativeLayout imglr;
        ImageView imgadd;
        SwitchCompat status;


        public CustomViewHolder(View convertView) {
            super(convertView);
            name            = (FotTextView) convertView.findViewById(R.id.name);
            price           = (FotTextView) convertView.findViewById(R.id.price);
            discounted      = (FotTextView) convertView.findViewById(R.id.discounted);
            img             = (RoundedImg) convertView.findViewById(R.id.img);
            imgadd          = (ImageView) convertView.findViewById(R.id.imgadd);
            parent          = (RelativeLayout) convertView.findViewById(R.id.parent);
            imglr		    = (RelativeLayout) convertView.findViewById(R.id.imglr);
            status          = (SwitchCompat) convertView.findViewById(R.id.status);

        }


    }
}