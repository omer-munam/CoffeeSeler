package com.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balram.library.FotTextView;
import com.coffee.seller.ksa.BaseActivity;
import com.coffee.seller.ksa.CatSubProducts;
import com.coffee.seller.ksa.SubCategories;
import com.tami.seller.ksa.R;
import com.coffee.seller.ksa.SubCategoriesMani;
import com.coffee.seller.ksa.SubCategoriesManiExplicit;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.coffee.seller.ksa.BaseActivity.globalLog;
import static com.utils.Constants.PRODUCT_PRESENT;


public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.CustomViewHolder>
{
	ArrayList<JSONObject>    list = new ArrayList<JSONObject>();

    BaseActivity context;
    public static int selectedIndex;
    public static SubCategoryAdapter thisAdapter;
	public SubCategoryAdapter(BaseActivity con, ArrayList<JSONObject> l)
	{
		context             = con;
		list                = l;
        selectedIndex       = -1;
        thisAdapter         = this;
	}

	public static void notifyDataChange()
    {
        selectedIndex   = -1;
        thisAdapter.notifyDataSetChanged();

    }


    @Override
    public int getItemCount() {
        return list.size();
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v                  = inflater.inflate(R.layout.row_catsub, parent, false);

        CustomViewHolder vh     = new CustomViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        try
        {
            final JSONObject jsonObject		= list.get(position);
            boolean addcategory             = false;
            globalLog("addcat :: "+jsonObject);
            try{addcategory                 = jsonObject.getString("addsub").equals("1");}catch (Exception e){}

            globalLog("positionselec :: "+selectedIndex+" -- "+position);
            if(selectedIndex == position)
                holder.parent.setBackgroundResource(R.drawable.rounded_cat_selected);
            else
                holder.parent.setBackgroundResource(R.drawable.rounded_cat_unselected);



            if(!addcategory)
            {

                holder.namelr.setVisibility(View.VISIBLE);
                holder.addcat.setVisibility(View.GONE);
                holder.name.setText(jsonObject.getString("title" + BaseActivity.getDefaultLang()));

                int status                       = 1;
                try{status                       = Integer.parseInt(jsonObject.getString("status"));}catch (Exception e){}

                if(selectedIndex == position)
                {
                    holder.editicon.setColorFilter(Color.WHITE);
                    holder.name.setTextColor(Color.WHITE);
                    if(status == 0)
                        holder.parent.setBackgroundResource(R.drawable.rounded_cat_selected_disabled);
                }
                else
                {
                    if(status == 1)
                    {
                        holder.name.setTextColor(context.getResources().getColor(R.color.orange));
                        holder.editicon.setColorFilter(context.getResources().getColor(R.color.orange));
                    }
                    else
                    {
                        holder.name.setTextColor(Color.parseColor("#aaaa0000"));
                        holder.editicon.setColorFilter(Color.parseColor("#aaaa0000"));
                        holder.parent.setBackgroundResource(R.drawable.rounded_cat_unselected_disabled);
                    }
                }

                if(position > 0)
                {
                    holder.seprate.setVisibility(View.VISIBLE);
                    holder.editicon.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.seprate.setVisibility(View.GONE);
                    holder.editicon.setVisibility(View.GONE);
                }

                holder.editicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,SubCategoriesMani.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("data",jsonObject+"");
                        context.startActivity(intent);
                    }
                });


                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try
                        {
                            if(position > 0)
                            {
                                CatSubProducts.currentSubId = jsonObject;
                            }

                            else
                                CatSubProducts.currentSubId = null;
                        }catch (Exception e){}
                        selectedIndex = position;
                        notifyDataSetChanged();

                        Intent intent = new Intent(PRODUCT_PRESENT);
                        intent.setAction(PRODUCT_PRESENT);
                        intent.putExtra("subcat", jsonObject.toString());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
            }
            else
            {
                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try
                        {
                            Intent intentX      = new Intent(context, SubCategories.class);
                            intentX.putExtra("catid", CatSubProducts.currentCatId.toString());
                            context.startActivity(intentX);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
                holder.namelr.setVisibility(View.GONE);
                holder.addcat.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){e.printStackTrace();}


    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        FotTextView name ;
        ImageView addcat ,editicon;
        View parent;
        LinearLayout parent2 , namelr;
        TextView seprate;


        public CustomViewHolder(View convertView) {
            super(convertView);
            name        = (FotTextView) convertView.findViewById(R.id.name);
            addcat      = (ImageView) convertView.findViewById(R.id.addcat);
            parent      = (View) convertView.findViewById(R.id.parent);
            parent2     = (LinearLayout) convertView.findViewById(R.id.parent2);
            namelr      = (LinearLayout) convertView.findViewById(R.id.namelr);
            editicon    = (ImageView)convertView.findViewById(R.id.editicon);
            seprate     = (TextView) convertView.findViewById(R.id.seprate);
        }


    }
}