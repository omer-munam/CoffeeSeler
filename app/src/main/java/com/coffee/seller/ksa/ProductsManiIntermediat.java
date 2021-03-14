package com.coffee.seller.ksa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.adapter.PreProductAdapter;
import com.tami.seller.ksa.R;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import pl.droidsonroids.gif.GifImageView;


public class ProductsManiIntermediat extends BaseActivity   {
    RecyclerView items;

    ArrayList<JSONObject> listItem                              = new ArrayList<>();

    StaggeredGridLayoutManager stagLayoutManager;

    PreProductAdapter productAdapter;

    GifImageView loader_img;

    LinearLayout menu_delete;

    String locid;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try
        {
            listItem           = new ArrayList<>();

            setContentView(R.layout.activity_productsinter);
            items               = (RecyclerView) findViewById(R.id.items);
            loader_img          = (GifImageView) findViewById(R.id.loader_img);
            menu_delete         = (LinearLayout)findViewById(R.id.menu_delete);

            locid               = CatSubProducts.jsonObject.getString("id");


            stagLayoutManager   = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
            productAdapter      = new PreProductAdapter(this,listItem);

            items.setLayoutManager(stagLayoutManager);
            items.setAdapter(productAdapter);


            menu_delete.setVisibility(View.VISIBLE);


            OverScrollDecoratorHelper.setUpOverScroll(items,OverScrollDecoratorHelper.ORIENTATION_VERTICAL);


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loader_img.setVisibility(View.VISIBLE);
        getProducts();
    }

    void getProducts()
    {
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... params) {
                String res = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("screenwidth",BaseActivity.screenwidth);
                    jobj.put("screenheight",BaseActivity.screenheight);
                    jobj.put("locid",locid);
                    jobj.put("myid",getSharePrefs().getUid());
                    jobj.put("caller","getAllProducts");


                    String encrypted        = getConnection().getEncryptedString(jobj.toString());
                    res                     = getConnection().sendPostData(encrypted,null);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try
                {
                    appLog("productmani :: "+s);
                    listItem.clear();
                    JSONArray jArray                                = new JSONArray(s);
                    for(int i=0;i<jArray.length();i++)
                    {
                        try
                        {
                            JSONObject jobj                         = jArray.getJSONObject(i);
                            listItem.add(jobj);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    items.getRecycledViewPool().clear();
                    productAdapter.notifyDataSetChanged();
                    loader_img.setVisibility(View.GONE);

                    if(listItem.size() == 0)
                    {
                        addNew(null);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void addNew(View v)
    {
        try
        {
            Intent intentX      = new Intent(this, ProductsMani.class);
            try{intentX.putExtra("catid",CatSubProducts.currentCatId.toString());}catch (Exception e){}
            try{intentX.putExtra("subid",CatSubProducts.currentSubId.toString());}catch (Exception e){}
            startActivity(intentX);
            this.finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
