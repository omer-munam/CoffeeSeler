package com.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.balram.library.FotTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.coffee.seller.ksa.BaseActivity;
import com.tami.seller.ksa.R;
import com.utils.ConnectionClass;
import com.utils.Functions;
import com.utils.SharePrefsEntry;

import org.json.JSONObject;

import java.util.ArrayList;


public class OrdersAdapter extends ArrayAdapter<JSONObject>
{
    ArrayList<JSONObject> list = new ArrayList<JSONObject>();
    Activity context;
    Functions function;
    ConnectionClass cc;
    SharePrefsEntry sp;
    public OrdersAdapter(Activity con, ArrayList<JSONObject> l)
    {
        super(con, R.layout.row_orders);

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
        FotTextView name , qty , address , status;
        CircularImageView img;
        Button mark_delivered;

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
                convertView 				= inflater.inflate(R.layout.row_orders, parent, false);
                viewHolder 					= new ViewHolderItem();
                viewHolder.img			    = (CircularImageView)convertView.findViewById(R.id.img);
                viewHolder.name			    = (FotTextView)convertView.findViewById(R.id.name);
                viewHolder.qty			    = (FotTextView)convertView.findViewById(R.id.qty);
                viewHolder.status			= (FotTextView)convertView.findViewById(R.id.status);
                viewHolder.address			= (FotTextView)convertView.findViewById(R.id.address);
                viewHolder.mark_delivered   = (Button)convertView.findViewById(R.id.mark_delivered);

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
            viewHolder.qty.setText(jsonObject.getString("qty"));
            viewHolder.address.setText(jsonObject.getString("address"));


            if(jsonObject.getString("status").equals(View.VISIBLE)) // pending
            {
                viewHolder.mark_delivered.setVisibility(View.VISIBLE);
                viewHolder.status.setText(context.getResources().getString(R.string.pending));
                viewHolder.status.setTextColor(Color.parseColor("#ff5555"));
            }
            else
            {
                viewHolder.mark_delivered.setVisibility(View.GONE);
                viewHolder.status.setText(context.getResources().getString(R.string.delivered));
                viewHolder.status.setTextColor(Color.parseColor("#005500"));
            }

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


            viewHolder.mark_delivered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDelivery(jsonObject,paramInt);
                }
            });


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    void confirmDelivery(final JSONObject jobj,final int param)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        markDelivered(jobj,param);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.are_you_sure)).setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();

    }


    void markDelivered(final JSONObject jobjw,final int param)
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
                        jobj.put("data",jobjw);
                        jobj.put("myid",sp.getUid()+"");
                        jobj.put("caller","markDelivered");

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
                        JSONObject json             = new JSONObject(paramString);
                        Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                        if(json.getString("status").equals("1"))
                        {
                            JSONObject jsonObject   = new JSONObject(json.getString("data"));
                          //  Orders.list.set(param,jsonObject);
                            notifyDataSetChanged();
                        }
                    }catch (Exception e){
                        Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                    }

                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }





}