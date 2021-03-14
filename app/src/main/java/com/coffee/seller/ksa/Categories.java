package com.coffee.seller.ksa;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.adapter.CategoriesAdapter;
import com.tami.seller.ksa.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class Categories extends BaseActivity {

    ListView listview;
    CategoriesAdapter adapter;
    ArrayList<JSONObject> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat);

        listview            = (ListView)findViewById(R.id.listview);

        adapter             = new CategoriesAdapter(this,list);
        listview.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocations();
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
                    jobj.put("excludemine","1");
                    jobj.put("caller","getCategories");
                    Log.d("cat", jobj.toString());

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
                Log.d("cat", paramString);
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
