package com.coffee.seller.ksa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tami.seller.ksa.R;
import com.utils.ImagePicker;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import static com.utils.Constants.FROM_IMAGE;

public class ProductsMani extends BaseActivity {

    TextView name_en,name_ar , price;
    public static TextView cat , subcat , location;
    String nametxt,nameartxt , pricetxt;
    public static String catid , subcatid , locid;

    JSONObject jsonObject;

    boolean editing;
    ImageView img;
    Bitmap bitmap;
    String encoded;
    SwitchCompat status;
    EditText offerPrice;
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
        offerPrice      = (EditText) findViewById(R.id.discount);
        img             = (ImageView) findViewById(R.id.img);
        status          = (SwitchCompat)findViewById(R.id.status);


        try
        {
            jsonObject  = new JSONObject(getIntent().getExtras().getString("data"));
            name_en.setText(jsonObject.getString("title_en"));
            name_ar.setText(jsonObject.getString("title_ar"));
            offerPrice.setText(jsonObject.getString("discount_price"));
            globalLog("jsonobject :: "+jsonObject);

            if(jsonObject.getString("status").equals("1"))
                status.setChecked(true);
            else
                status.setChecked(false);

            if(!jsonObject.getString("price").equals("0"))
            {
                price.setText(jsonObject.getString("price"));
            }


            if(!jsonObject.getString("catid").equals("0"))
            {
                cat.setText(jsonObject.getString("ca"+getDefaultLang()));
                catid           = jsonObject.getString("catid");
                currentCatSelected = catid;
            }

            if(!jsonObject.getString("subcatid").equals("0"))
            {
                subcat.setText(jsonObject.getString("s"+getDefaultLang()));
                subcatid       = jsonObject.getString("subcatid");
            }

            Glide.with(this)
                    .load(jsonObject.getString("images"))
                    .asBitmap()
                    .placeholder(R.drawable.logo)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            img.setImageBitmap(resource);
                        }
                    });


            if(!jsonObject.getString("locid").equals("0"))
            {
                location.setText(jsonObject.getString("loc"+getDefaultLang()));
                locid           = jsonObject.getString("locid");
                currentLocSelected = locid;
            }



            editing = true;

        }catch (Exception e){
            e.printStackTrace();
        }

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getBaseActivity());
                startActivityForResult(chooseImageIntent, FROM_IMAGE);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode)
        {
            case FROM_IMAGE:

                try
                {
                    bitmap                  = ImagePicker.getImageFromResult(this, resultCode, data);
                    int h                   = 300;
                    int w                   = 300;
                    bitmap                  = getFunction().scaleCenterCrop(bitmap, w, h);

                    bitmapToString();
                } catch (Exception e) {
                    encoded = "";
                }


                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    void bitmapToString()
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray        = byteArrayOutputStream .toByteArray();
            encoded                 = Base64.encodeToString(byteArray, Base64.DEFAULT);

            Glide.with(this)
                    .load(byteArrayOutputStream.toByteArray())
                    .asBitmap()
                    .into(img);
        }catch (Exception e){}

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

            if(encoded.equals("") && !editing)
            {
                Toast.makeText(this,getString(R.string.image_blank),Toast.LENGTH_SHORT).show();
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
                    jobj.put("data",jsonObject);
                    jobj.put("image",encoded);
                    jobj.put("price",pricetxt);
                    jobj.put("offer",offerPrice.getText().toString());
                    jobj.put("locid",locid);
                    jobj.put("status",status.isChecked() ? "1" : "0");
                    jobj.put("subcatid",subcatid);
                    jobj.put("myid",getSharePrefs().getUid()+"");
                    jobj.put("caller","saveProduct");

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
                getFunction().dismissDialog();
                try
                {
                    JSONObject json     = new JSONObject(paramString);
                    Toast.makeText(getBaseActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    if(json.getString("status").equals("1"))
                    {
                        finishMeReload(ProductsMani.this);//gotoProducts(null);
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
