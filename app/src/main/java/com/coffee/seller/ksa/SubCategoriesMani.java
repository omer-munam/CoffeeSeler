package com.coffee.seller.ksa;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tami.seller.ksa.R;

import org.json.JSONObject;

public class SubCategoriesMani extends BaseActivity {

    TextView name_en,name_ar;
    public static TextView cat;
    String nametxt,nameartxt;
    public static String catid;

    JSONObject jsonObject;
    boolean editing;
    String subcatid = "";
    SwitchCompat status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcat_mani);

        catid           = "";
        name_en         = (TextView)findViewById(R.id.name_en);
        name_ar         = (TextView)findViewById(R.id.name_ar);
        cat             = (TextView)findViewById(R.id.cat);
        status          = (SwitchCompat)findViewById(R.id.status);
        Log.d("orsers", "SubCategoriesMani");

        try
        {
            jsonObject  = new JSONObject(getIntent().getExtras().getString("data"));

//            if(jsonObject.getString("status").equals("1"))
//                status.setChecked(true);
//            else
//                status.setChecked(false);
            status.setVisibility(View.GONE);

            globalLog("jsonobj :: "+jsonObject);
            subcatid = jsonObject.getString("id");
            name_en.setText(jsonObject.getString("title_en"));
            name_ar.setText(jsonObject.getString("title_ar"));
            cat.setText(jsonObject.getString("cat"+getDefaultLang()));
            catid       = jsonObject.getString("catid");
            editing = true;

        }catch (Exception e){
            e.printStackTrace();
        }

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
                    jobj.put("data",jsonObject);
                    jobj.put("id",subcatid);
                    jobj.put("locid",CatSubProducts.jsonObject.getString("id"));
                    jobj.put("status",status.isChecked() ? "1" : "0");
                    jobj.put("myid",getSharePrefs().getUid()+"");
                    jobj.put("caller","saveSubCategory");

                    globalLog("subcatid :: "+jobj);

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
                        finishMeReload(SubCategoriesMani.this);//gotoSubCategories(null);
                    }
                }catch (Exception e){
                    Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }

            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void selectCategories(View v)
    {
            getFunction().getCurrentCategoris(false,2);
    }

    public void getCurrent(View v)
    {
        getFunction().getCurrentSubCategories(true,1);
    }

}
