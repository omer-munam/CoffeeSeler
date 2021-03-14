package com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.balram.library.FotButton;
import com.balram.library.FotTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.coffee.seller.ksa.BaseActivity;
import com.coffee.seller.ksa.CatSubProducts;
import com.coffee.seller.ksa.LocationsManipulate;
import com.coffee.seller.ksa.Orders;
import com.tami.seller.ksa.R;
import com.utils.ConnectionClass;
import com.utils.Functions;
import com.utils.SharePrefsEntry;

import org.json.JSONObject;

import java.util.ArrayList;


public class LocationsAdapater extends ArrayAdapter<JSONObject>
{
    ArrayList<JSONObject> list = new ArrayList<JSONObject>();
    Activity context;
    Functions function;
    ConnectionClass cc;
    SharePrefsEntry sp;
    public LocationsAdapater(Activity con, ArrayList<JSONObject> l)
    {
        super(con, R.layout.row_locations);

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
        FotTextView locname;
        ImageView deleteLoc;
        FotButton edit_location, view_orders;
        SwitchCompat switchCompat;
        ImageView img;

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
                convertView 				= inflater.inflate(R.layout.row_locations, parent, false);
                viewHolder 					= new ViewHolderItem();
                viewHolder.locname			= (FotTextView)convertView.findViewById(R.id.locname);
                viewHolder.deleteLoc		= (ImageView)convertView.findViewById(R.id.delete_data);
                viewHolder.edit_location    = (FotButton)convertView.findViewById(R.id.edit_location);
                viewHolder.view_orders		= (FotButton)convertView.findViewById(R.id.view_orders);
                viewHolder.switchCompat		= (SwitchCompat)convertView.findViewById(R.id.switchCompat);
                viewHolder.img              = (ImageView)convertView.findViewById(R.id.img);

                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder 					= (ViewHolderItem) convertView.getTag();
            }

            final JSONObject jsonObject		= list.get(paramInt);

            String title                    = "title"+ BaseActivity.getDefLang(context);
            final String latng              = jsonObject.getString("location");
            viewHolder.locname.setText(jsonObject.getString(title));

            if(jsonObject.getString("status").equals("1"))
                viewHolder.switchCompat.setChecked(true);
            else
                viewHolder.switchCompat.setChecked(false);

            final ImageView location_img     = viewHolder.img;

            Glide.with(context)
                    .load(jsonObject.getString("image"))
                    .placeholder(R.drawable.logo)
                    .into(new GlideDrawableImageViewTarget(viewHolder.img) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            location_img.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {}
                    });


            viewHolder.switchCompat.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    changeStatus(((SwitchCompat) v).isChecked(),jsonObject);


                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?saddr="+latng+"&daddr="+latng));
//                    context.startActivity(intent);
                    Intent intent = new Intent(context, CatSubProducts.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("data",jsonObject+"");
                    context.startActivity(intent);
                }
            });

            viewHolder.deleteLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.confirm(jsonObject,1);
                }
            });


            viewHolder.edit_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LocationsManipulate.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("data",jsonObject+"");
                    context.startActivity(intent);
                }
            });

            viewHolder.view_orders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Orders.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("data",jsonObject+"");
                    context.startActivity(intent);
                }
            });


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    void changeStatus(final boolean isChecked , final JSONObject jobj2)
    {
        function.showDialog();
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... voids) {
                String res  = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("myid",sp.getUid());
                    jobj.put("status",isChecked ? "1":"0");
                    jobj.put("data",jobj2+"");
                    jobj.put("caller","updateStatus");

                    String encrypted        = cc.getEncryptedString(jobj.toString());
                    res                     = cc.sendPostData(encrypted,null);

                }catch (Exception e){
                    e.printStackTrace();
                }



                return res;
            }


            @Override
            protected void onPostExecute(String paramString) {
                super.onPostExecute(paramString);
                function.dismissDialog();
                try
                {
                    JSONObject jobj = new JSONObject(paramString);
                    Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_SHORT).show();


                }catch (Exception e){
                    Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}