package com.coffee.seller.ksa;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tami.seller.ksa.R;

import org.json.JSONObject;

public class SubCategoriesManiExplicit extends BaseActivity {

    TextView name_en,name_ar;
    public static TextView cat;
    String nametxt,nameartxt;
    public static String catid;

    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcat_mani);

        catid           = "";
        name_en         = (TextView)findViewById(R.id.name_en);
        name_ar         = (TextView)findViewById(R.id.name_ar);
        cat             = (TextView)findViewById(R.id.cat);
        try
        {

            try{cat.setText(CatSubProducts.currentCatId.getString("title"+getDefaultLang()));
                catid           = CatSubProducts.currentCatId.getString("id");}catch (Exception e){}

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
                    jobj.put("direct","1");
                    jobj.put("locid",CatSubProducts.jsonObject.getString("id"));
                    jobj.put("myid",getSharePrefs().getUid()+"");
                    jobj.put("caller","saveSubCategory");

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
                        finishMeReload(SubCategoriesManiExplicit.this);//gotoSubCategories(null);
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
