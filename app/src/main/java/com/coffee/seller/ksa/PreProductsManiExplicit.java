package com.coffee.seller.ksa;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.tami.seller.ksa.R;

import org.json.JSONObject;

public class PreProductsManiExplicit extends BaseActivity {

    TextView name_en,name_ar , price;
    public static TextView cat , subcat , location;
    String nametxt,nameartxt , pricetxt;
    public static String catid , subcatid , locid;


    boolean editing;
    ImageView img;
    Bitmap bitmap;
    String encoded;
    SwitchCompat status;
    EditText offerPrice;
    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_mani);

        encoded         = "";
        catid           = "";
        subcatid        = "";
        locid           = "";
        price           = (TextView)findViewById(R.id.price);
        name_en         = (TextView)findViewById(R.id.name_en);
        name_ar         = (TextView)findViewById(R.id.name_ar);
        cat             = (TextView)findViewById(R.id.cat);
        subcat          = (TextView)findViewById(R.id.subcat);
        location        = (TextView)findViewById(R.id.location);
        img             = (ImageView) findViewById(R.id.img);
        offerPrice      = (EditText)findViewById(R.id.discount);
        status          = (SwitchCompat)findViewById(R.id.status);



        status.setChecked(true);
        try
        {
            jsonObject      = new JSONObject(getIntent().getExtras().getString("data"));

            name_en.setText(jsonObject.getString("title_en"));
            name_ar.setText(jsonObject.getString("title_ar"));
            name_ar.setEnabled(false);
            name_en.setEnabled(false);

            

            try
            {
                Glide.with(this)
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
            }catch (Exception e){}

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try
        {
            try{cat.setText(CatSubProducts.currentCatId.getString("title"+getDefaultLang()));
                catid           = CatSubProducts.currentCatId.getString("id");
                currentCatSelected = catid;;}catch (Exception e){}

            try{subcat.setText(CatSubProducts.currentSubId.getString("title"+getDefaultLang()));
                subcatid       = CatSubProducts.currentSubId.getString("subid");
                currentSubCatSelected = subcatid;}catch (Exception e){}


            try{location.setText(CatSubProducts.jsonObject.getString("title"+getDefaultLang()));
                locid           = CatSubProducts.jsonObject.getString("id");
                currentLocSelected = locid;}catch (Exception e){}

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public void saveData(View v)
    {
            nametxt     = name_en.getText().toString();
            nameartxt   = name_ar.getText().toString();
            pricetxt    = price.getText().toString();

            if(nametxt.equals(""))
            {
                name_en.requestFocus();
                Toast.makeText(this,getString(R.string.en_blank),Toast.LENGTH_SHORT).show();
                return;
            }

            if(nameartxt.equals(""))
            {
                name_ar.requestFocus();
                Toast.makeText(this,getString(R.string.ar_blank),Toast.LENGTH_SHORT).show();
                return;
            }

            try
            {
                if(Float.parseFloat(pricetxt) < 1)
                {
                    price.requestFocus();
                    Toast.makeText(this,getString(R.string.invalid_price),Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch (Exception e){
                price.requestFocus();
                Toast.makeText(this,getString(R.string.invalid_price),Toast.LENGTH_SHORT).show();
                return;
            }

            if(locid.equals(""))
            {
                Toast.makeText(this,getString(R.string.location_blank),Toast.LENGTH_SHORT).show();
                return;
            }

            if(catid.equals(""))
            {
                Toast.makeText(this,getString(R.string.category_blank),Toast.LENGTH_SHORT).show();
                return;
            }

            proceed();

    }

    void proceed()
    {
        getFunction().showDialog();
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... voids) {
                String res  = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("name_en",nametxt);
                    jobj.put("name_ar",nameartxt);
                    jobj.put("catid",catid);
                    jobj.put("image",encoded);
                    jobj.put("price",pricetxt);
                    jobj.put("locid",locid);
                    jobj.put("offer",offerPrice.getText().toString());
                    jobj.put("subcatid",subcatid);
                    jobj.put("direct","1");
                    jobj.put("status",status.isChecked() ? "1" : "0");
                    jobj.put("myid",getSharePrefs().getUid()+"");
                    jobj.put("productid",jsonObject.getString("id"));
                    jobj.put("caller","savePreProduct");

                    globalLog("addproduct :: request "+jobj);

                    String encrypted        = cc.getEncryptedString(jobj.toString());
                    res                     = cc.sendPostData(encrypted,null);

                    globalLog("addproduct :: "+res);

                }catch (Exception e){
                    e.printStackTrace();
                }



                return res;
            }


            @Override
            protected void onPostExecute(String paramString) {
                super.onPostExecute(paramString);
                getFunction().dismissDialog();
                try
                {
                    JSONObject json     = new JSONObject(paramString);
                    Toast.makeText(getBaseActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    if(json.getString("status").equals("1"))
                    {
                        finishMeReload(PreProductsManiExplicit.this);//gotoProducts(null);
                    }
                }catch (Exception e){
                    Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }

            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void selectCategories(View v)
    {
            if(BaseActivity.currentLocSelected == null)
            {
                Toast.makeText(this,this.getString(R.string.please_select_location_first),Toast.LENGTH_SHORT).show();
                return;
            }
            getFunction().getCurrentCategoris(false,3);
    }
    public void selectSubCategories(View v)
    {
        if(BaseActivity.currentCatSelected == null)
        {
            Toast.makeText(this,this.getString(R.string.please_select_cat_first),Toast.LENGTH_SHORT).show();
            return;
        }
        getFunction().getCurrentSubCategories(false,2);
    }
    public void selectLocations(View v){getFunction().getCurrentLocations(false,2);}

    public void getCurrent(View v)
    {
        getFunction().getCurrentProducts(true,1);
    }

}
