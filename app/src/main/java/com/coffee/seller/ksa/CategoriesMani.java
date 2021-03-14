package com.coffee.seller.ksa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class CategoriesMani extends BaseActivity {

    TextView name_en,name_ar;
    String nametxt,nameartxt;

    ImageView img;
    Bitmap bitmap;
    String encoded;
    JSONObject jsonObject;
    boolean editing;
    LinearLayout oring;
    SwitchCompat status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_mani);
        encoded         = "";
        img             = (ImageView) findViewById(R.id.img);
        name_en         = (TextView)findViewById(R.id.name_en);
        name_ar         = (TextView)findViewById(R.id.name_ar);
        oring           = (LinearLayout)findViewById(R.id.oring);
        status          = (SwitchCompat)findViewById(R.id.status);
        status.setVisibility(View.GONE);

        try
        {
            jsonObject  = new JSONObject(getIntent().getExtras().getString("data"));

            if(jsonObject.getString("status").equals("1"))
                status.setChecked(true);
            else
                status.setChecked(false);

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

            name_en.setText(jsonObject.getString("title_en"));
            name_ar.setText(jsonObject.getString("title_ar"));
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
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
                    jobj.put("image",encoded);
                    jobj.put("locid",CatSubProducts.jsonObject.getString("id"));
                    jobj.put("data",jsonObject);
                    jobj.put("status",status.isChecked() ? "1" : "0");
                    jobj.put("myid",getSharePrefs().getUid()+"");
                    jobj.put("caller","saveCategory");

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
                        finishMeReload(CategoriesMani.this);
                    }
                }catch (Exception e){
                    Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }

            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void getCurrent(View v)
    {
        getFunction().getCurrentCategoris(true,1);
    }

}
