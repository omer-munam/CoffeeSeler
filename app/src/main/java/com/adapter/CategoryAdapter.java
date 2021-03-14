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
import com.coffee.seller.ksa.Categories;
import com.coffee.seller.ksa.CategoriesMani;
import com.tami.seller.ksa.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.utils.Constants.PRODUCT_PRESENT;
import static com.utils.Constants.SUBCAT_PRESENT;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CustomViewHolder>
{
	ArrayList<JSONObject>    list = new ArrayList<JSONObject>();

	int selectedIndex;
    BaseActivity context;
	public CategoryAdapter(BaseActivity con, ArrayList<JSONObject> l)
	{
		context             = con;
		list                = l;
        selectedIndex       = -1;
	}


    @Override
    public int getItemCount() {
        return list.size();
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v                  = inflater.inflate(R.layout.row_cat, parent, false);

        CustomViewHolder vh     = new CustomViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        try
        {
            final JSONObject jsonObject		= list.get(position);
            boolean addcategory             = false;
            try{addcategory                 = jsonObject.getString("addcat").equals("1");}catch (Exception e){}

            if(selectedIndex == position)
                holder.parent.setBackgroundResource(R.drawable.rounded_cat_selected);
            else
                holder.parent.setBackgroundResource(R.drawable.rounded_cat_unselected);

            if(!addcategory)
            {
                holder.namelr.setVisibility(View.VISIBLE);
                holder.addcat.setVisibility(View.GONE);
                holder.name.setText(jsonObject.getString("title"+BaseActivity.getDefaultLang()));
                final JSONArray subCatList2      = new JSONArray(jsonObject.getString("subcat"));
                final JSONArray subCatList       = new JSONArray();

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





                if(position > 1)
                {
                    holder.seprate.setVisibility(View.VISIBLE);
                    holder.editicon.setVisibility(View.VISIBLE);
                    JSONObject addPr = new JSONObject();
                    addPr.put("addsub","1");
                    subCatList.put(addPr);
                }
                else
                {
                    holder.seprate.setVisibility(View.GONE);
                    holder.editicon.setVisibility(View.GONE);
                }


                for(int i=0;i<subCatList2.length();i++)
                {

                    subCatList.put(subCatList2.get(i));
                }


                holder.editicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,CategoriesMani.class);
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
                            if(position > 1)
                                CatSubProducts.currentCatId = jsonObject;
                            else
                            {
                                CatSubProducts.currentCatId = null;
                                CatSubProducts.currentSubId = null;
                            }

                        }catch (Exception e){}

                        selectedIndex = position;
                        notifyDataSetChanged();
                        if(subCatList.length() == 0)
                        {

                        }

                        Intent intent = new Intent(PRODUCT_PRESENT);
                        intent.setAction(PRODUCT_PRESENT);
                        intent.putExtra("cat",jsonObject.toString());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


                        Intent intent2 = new Intent(SUBCAT_PRESENT);
                        intent2.setAction(SUBCAT_PRESENT);
                        intent2.putExtra("data",subCatList.toString());
                        intent2.putExtra("cat",jsonObject.toString());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
                        SubCategoryAdapter. notifyDataChange();
                    }
                });
            }
            else
            {
                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intentX      = new Intent(context, Categories.class);
                        context.startActivity(intentX);

                        JSONArray subCatList       = new JSONArray();
                        Intent intent = new Intent(SUBCAT_PRESENT);
                        intent.setAction(SUBCAT_PRESENT);
                        intent.putExtra("data",subCatList.toString());
                        intent.putExtra("cat",jsonObject.toString());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
                holder.namelr.setVisibility(View.GONE);
                holder.addcat.setVisibility(View.VISIBLE);
            }

            holder.parent2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.parent.performClick();
                }
            });


        }catch (Exception e){e.printStackTrace();}


    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        FotTextView name ;
        View parent;
        LinearLayout parent2 , namelr;
        ImageView addcat , editicon;
        TextView seprate;


        public CustomViewHolder(View convertView) {
            super(convertView);
            name        = (FotTextView) convertView.findViewById(R.id.name);
            parent      = (View) convertView.findViewById(R.id.parent);
            parent2     = (LinearLayout) convertView.findViewById(R.id.parent2);
            namelr      = (LinearLayout) convertView.findViewById(R.id.namelr);
            addcat      = (ImageView)convertView.findViewById(R.id.addcat);
            editicon    = (ImageView)convertView.findViewById(R.id.editicon);
            seprate     = (TextView) convertView.findViewById(R.id.seprate);
        }


    }
}