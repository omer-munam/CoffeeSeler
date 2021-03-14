package com.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.balram.library.FotTextView;
import com.tami.seller.ksa.R;
import com.utils.ConnectionClass;
import com.utils.Functions;
import com.utils.SharePrefsEntry;

import org.json.JSONObject;

import java.util.ArrayList;


public class OffersAdapter extends ArrayAdapter<JSONObject>
{
    ArrayList<JSONObject> list = new ArrayList<JSONObject>();
    Activity context;
    Functions function;
    ConnectionClass cc;
    SharePrefsEntry sp;
    public OffersAdapter(Activity con, ArrayList<JSONObject> l)
    {
        super(con, R.layout.row_categories);

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
        FotTextView name , dated;
        ImageView deleteData;

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
                convertView 				= inflater.inflate(R.layout.row_offers, parent, false);
                viewHolder 					= new ViewHolderItem();
                viewHolder.dated			= (FotTextView)convertView.findViewById(R.id.dated);
                viewHolder.name			    = (FotTextView)convertView.findViewById(R.id.text);
                viewHolder.deleteData		= (ImageView)convertView.findViewById(R.id.delete_data);

                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder 					= (ViewHolderItem) convertView.getTag();
            }

            final JSONObject jsonObject		= list.get(paramInt);
            viewHolder.name.setText(jsonObject.getString("data"));
            viewHolder.dated.setText(jsonObject.getString("dated"));


            viewHolder.deleteData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    function.confirm(jsonObject,8);
                }
            });


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }





}