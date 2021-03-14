package com.coffee.seller.ksa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tami.seller.ksa.R;

import org.json.JSONObject;

public class AddOffers extends BaseActivity {

    EditText text;
    String textTxt;
    String locid  , storeid;
    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addoffers);
        text                    = (EditText)findViewById(R.id.text);

        try
        {
            jsonObject          = new JSONObject(getIntent().getExtras().getString("data"));

            locid               = jsonObject.getString("id");
            storeid             = jsonObject.getString("storeid");
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void saveData(View v)
    {
            textTxt     = text.getText().toString();

            if(textTxt.equals(""))
            {
                text.requestFocus();
                Toast.makeText(this,getString(R.string.text_blank),Toast.LENGTH_SHORT).show();
                return;
            }

            getFunction().showDialog();
            new AsyncTask<Void,Void,String>()
            {
                @Override
                protected String doInBackground(Void... voids) {
                    String res  = null;
                    try
                    {
                        JSONObject jobj         = new JSONObject();
                        jobj.put("textdata", textTxt);
                        jobj.put("data",jsonObject+"");
                        jobj.put("myid",getSharePrefs().getUid()+"");
                        jobj.put("caller","saveOffer");
                        globalLog("datasent :: "+jobj);


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
                            Intent intent = new Intent(AddOffers.this,GotoOffers.class);
                            intent.putExtra("data",jsonObject+"");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }catch (Exception e){
                        Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                    }

                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


}
