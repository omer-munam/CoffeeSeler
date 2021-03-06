package com.coffee.seller.ksa;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.adapter.ProductsAdapter;
import com.tami.seller.ksa.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class Products extends BaseActivity {

    ListView listview;
    ProductsAdapter adapter;
    ArrayList<JSONObject> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        listview            = (ListView)findViewById(R.id.listview);

        adapter             = new ProductsAdapter(this,list);
        listview.setAdapter(adapter);



    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubCategories();
    }

    void loadSubCategories()
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
                    jobj.put("caller","getProducts");

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
