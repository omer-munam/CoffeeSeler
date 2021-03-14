package com.coffee.seller.ksa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.adapter.OffersAdapter;
import com.tami.seller.ksa.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class GotoOffers extends BaseActivity {

    ListView listview;
    OffersAdapter adapter;
    ArrayList<JSONObject> list = new ArrayList<>();
    JSONObject jsonObject;
    String locid  , storeid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        try
        {
            listview            = (ListView)findViewById(R.id.listview);
            adapter             = new OffersAdapter(this,list);
            listview.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        try
        {
            jsonObject          = new JSONObject(getIntent().getExtras().getString("data"));
            locid               = jsonObject.getString("id");
            storeid             = jsonObject.getString("storeid");
        }catch (Exception e){}





    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocations();
    }

    public void addOffers(View v)
    {
        Intent intent = new Intent(this,AddOffers.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data",jsonObject+"");
        startActivity(intent);
    }

    void loadLocations()
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
                    jobj.put("myid",getSharePrefs().getUid()+"");
                    jobj.put("location",locid+"");
                    jobj.put("storeid",storeid+"");
                    jobj.put("caller","getMyOffers");

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
                        JSONArray jsonArray = new JSONArray(paramString);
                        list.clear();
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            list.add(jsonArray.getJSONObject(i));
                        }

                }catch (Exception e){
                    Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
