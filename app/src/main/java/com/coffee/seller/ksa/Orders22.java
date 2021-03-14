package com.coffee.seller.ksa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;

import com.adapter.OrderMainAdapter;
import com.tami.seller.ksa.R;
import com.utils.EndlessAdapter;
import com.utils.FetchDataTask;
import com.utils.IItemsReadyListener;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.utils.Constants.ORDERS_RELOAD;


public class Orders22 extends BaseActivity {

    EndlessLoading adapter;
    ListView lv;
    public ArrayList<JSONObject> list           = new ArrayList<JSONObject>();
    public ArrayList<String> idvalues           = new ArrayList<String>();
    boolean executing;
    boolean completed;
    String lastJson;


    JSONObject incomingData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        try
        {
            lv                  = (ListView)findViewById(R.id.listview);
            adapter             = new EndlessLoading();
            incomingData        = new JSONObject(getIntent().getExtras().getString("data"));

            lv.setAdapter(adapter);
            loadList();



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{LocalBroadcastManager.getInstance(getBaseActivity()).unregisterReceiver(loadList);}catch (Exception e){}

    }

    public BroadcastReceiver loadList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ORDERS_RELOAD))
            {
                loadList();
            }
        }
    };


    void loadList()
    {
        completed           = false;
        executing           = false;
        list.clear();
        lastJson            = "";
        lv.setAdapter(null);
        adapter             = new EndlessLoading();
        idvalues.clear();
        adapter.cacheInBackground();
    }


    class EndlessLoading extends EndlessAdapter implements IItemsReadyListener {
        JSONObject jobjGlobal;
        EndlessLoading()
        {
            super(getBaseActivity(),new OrderMainAdapter(getBaseActivity(),list), R.layout.row_zero);

        }



        @Override
        protected boolean cacheInBackground(){
            appLog("calledcache :: "+executing+" -- "+completed);
            if(!executing && !completed) {
                executing   = true;

                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("myid",getSharePrefs().getUid());
                    jobj.put("lastjson",lastJson+"");
                    jobj.put("incoming",incomingData+"");
                    jobj.put("  ","getMyOrders");
                    appLog("ordrjobj :: "+jobj);
                    String encrypted        = getConnection().getEncryptedString(jobj.toString());
                    FetchDataTask fetchs = new FetchDataTask(getBaseActivity(),this,list.size(),encrypted);
                    fetchs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return !completed;
        }

        @Override
        public void onItemsReady(ArrayList<JSONObject> d) {
            for(int i=0;i<d.size();i++)
            {
                try
                {
                    jobjGlobal 						= d.get(i);
                    if(!idvalues.contains(jobjGlobal.getString("threadid")))
                    {
                        list.add(d.get(i));
                        lastJson             = jobjGlobal+"";
                        idvalues.add(jobjGlobal.getString("threadid"));
                    }

                }catch (Exception e){}

            }


            if(lv.getAdapter() == null)
                lv.setAdapter(adapter);

            if(d.size() < 10)
                completed = true;
            else
            {
                adapter.onDataReady();
                adapter.notifyDataSetChanged();
            }

            executing   = false;

        }

        @Override
        protected void appendCachedData() {

        }
    }
}
