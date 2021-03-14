package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.balram.library.FotTextView;
import com.coffee.seller.ksa.BaseActivity;
import com.coffee.seller.ksa.PreProductsManiExplicit;
import com.coffee.seller.ksa.ProductsMani;
import com.coffee.seller.ksa.ProductsManiExplicit;
import com.tami.seller.ksa.R;
import com.coffee.seller.ksa.SubCategoriesMani;
import com.utils.ConnectionClass;
import com.utils.Functions;
import com.utils.SharePrefsEntry;

import org.json.JSONObject;

import java.util.ArrayList;


public class PresentSubListAdapter extends ArrayAdapter<JSONObject>
{
    ArrayList<JSONObject> list = new ArrayList<JSONObject>();
    Context context;
    Functions function;
    ConnectionClass cc;
    SharePrefsEntry sp;
    int callingCat;
    public PresentSubListAdapter(Context con, ArrayList<JSONObject> l, int c)
    {
        super(con, R.layout.row_locations);

        callingCat          = c;
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

    }

    @Override
    public View getView(final int paramInt, View convertView, ViewGroup parent)
    {
        try
        {
            ViewHolderItem viewHolder;
            if(convertView==null)
            {
                LayoutInflater inflater 	= ((Activity)context).getLayoutInflater();
                convertView 				= inflater.inflate(R.layout.row_present_list, parent, false);
                viewHolder 					= new ViewHolderItem();
                viewHolder.locname			= (FotTextView)convertView.findViewById(R.id.locname);

                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder 					= (ViewHolderItem) convertView.getTag();
            }

            final JSONObject jsonObject		= list.get(paramInt);

            final String title                    = "title"+ BaseActivity.getDefLang(context);
            viewHolder.locname.setText(jsonObject.getString(title));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {

                        if(Integer.parseInt(jsonObject.getString("id")) > 0 )
                        {
                            if(callingCat == 1)
                            {
                                Intent intent       = new Intent(context,SubCategoriesMani.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("data",jsonObject+"");
                                context.startActivity(intent);
                            }
                            else if(callingCat == 2)
                            {
                                BaseActivity.currentSubCatSelected = jsonObject.getString("id");
                                try
                                {
                                    ProductsMani.subcat.setText(jsonObject.getString(title));
                                    ProductsMani.subcatid = jsonObject.getString("id");
                                }catch (Exception e){}

                                try
                                {
                                    ProductsManiExplicit.subcat.setText(jsonObject.getString(title));
                                    ProductsManiExplicit.subcatid = jsonObject.getString("id");
                                }catch (Exception e){}

                                try
                                {
                                    PreProductsManiExplicit.subcat.setText(jsonObject.getString(title));
                                    PreProductsManiExplicit.subcatid = jsonObject.getString("id");
                                }catch (Exception e){}
                            }
                        }


                    }catch (Exception e){}

                    BaseActivity.spinner.dismiss();
                }
            });


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

}