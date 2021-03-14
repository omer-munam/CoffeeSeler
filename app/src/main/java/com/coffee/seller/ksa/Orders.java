package com.coffee.seller.ksa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.adapter.OrderMainAdapter;
import com.adapter.OrdersStatusAdapter;
import com.balram.library.FotTextView;
import com.tami.seller.ksa.R;
import com.utils.Constants;
import com.utils.EndlessAdapter;
import com.utils.FetchDataTask;
import com.utils.IItemsReadyListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

import static com.utils.Constants.ORDERS_RELOAD;


public class Orders extends BaseActivity {

    EndlessLoading adapter;
    ListView lv;
    public ArrayList<JSONObject> list           = new ArrayList<JSONObject>();
    public ArrayList<String> idvalues           = new ArrayList<String>();

    public ArrayList<JSONObject> listNew                    = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> listAccepted               = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> listReady                  = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> listDispatched             = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> listCompleted              = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> listCancelled              = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> listReturn                 = new ArrayList<JSONObject>();

    OrdersStatusAdapter  adapterNew , adapterAccepted , adapterReady , adapterDispatched , adapterCompleted , adapterCancelled, adapterReturn;


    boolean executing;
    boolean completed;
    String lastJson;


    JSONObject incomingData;
    ListView listview2;
    FotTextView tabs[]      = new FotTextView[7];
    GifImageView loader;
    ImageView reload;

    public static Orders orders;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        try
        {
            orders              = this;
            lv                  = (ListView)findViewById(R.id.listview);
            listview2           = (ListView) findViewById(R.id.listview2);
            tabs[0]             = (FotTextView) findViewById(R.id.neworders);
            tabs[1]             = (FotTextView) findViewById(R.id.accepted);
            tabs[2]             = (FotTextView) findViewById(R.id.ready);
            tabs[3]             = (FotTextView) findViewById(R.id.dispatched);
            tabs[4]             = findViewById(R.id.returns);
            tabs[5]             = (FotTextView) findViewById(R.id.completed);
            tabs[6]             = (FotTextView) findViewById(R.id.cancelled);
            loader              = (GifImageView) findViewById(R.id.loader);
            reload              = (ImageView) findViewById(R.id.refresh);

            adapter             = new EndlessLoading();
            incomingData        = new JSONObject(getIntent().getExtras().getString("data"));
            adapterNew          = new OrdersStatusAdapter(this,listNew,Constants.PLACED);
            adapterAccepted     = new OrdersStatusAdapter(this,listAccepted,Constants.ACCEPTED);
            adapterReady        = new OrdersStatusAdapter(this,listReady,Constants.READY);
            adapterDispatched   = new OrdersStatusAdapter(this,listDispatched,Constants.DELIVERED);
            adapterCompleted    = new OrdersStatusAdapter(this,listCompleted,Constants.CLOSED);
            adapterCancelled    = new OrdersStatusAdapter(this,listCancelled,Constants.CANCELLED);
            adapterReturn       = new OrdersStatusAdapter(this, listReturn,"6");

//            Log.d("orsers", incomingData.toString());
            reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();
                }
            });


            for(int i=0;i<tabs.length;i++)
            {
                tabs[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int tag =  Integer.parseInt(v.getTag().toString());
                        selectTab(tag);
                    }
                });
            }

            LocalBroadcastManager.getInstance(this).registerReceiver(loadList,new IntentFilter(ORDERS_RELOAD));
            lv.setAdapter(adapter);
            loadList();

            selectTab(0);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void selectTab(int j)
    {
        for(int i=0;i<tabs.length;i++)
        {
            if(i == j)
            {
                tabs[i].setBackgroundResource(R.drawable.rounded_cat_selected);
                tabs[i].setTextColor(Color.WHITE);

                if(i == 0)
                    listview2.setAdapter(adapterNew);
                else if(i == 1)
                    listview2.setAdapter(adapterAccepted);
                else if(i == 2)
                    listview2.setAdapter(adapterReady);
                else if(i == 3)
                    listview2.setAdapter(adapterDispatched);
                else if(i == 4)
                    listview2.setAdapter(adapterReturn);
                else if(i == 5)
                    listview2.setAdapter(adapterCompleted);
                else if(i == 6)
                    listview2.setAdapter(adapterCancelled);
            }
            else
            {
                tabs[i].setBackgroundResource(R.drawable.rounded_cat_unselected);
                tabs[i].setTextColor(getResources().getColor(R.color.orange));
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        orders              = null;
        try{LocalBroadcastManager.getInstance(getBaseActivity()).unregisterReceiver(loadList);}catch (Exception e){}

    }

    public BroadcastReceiver loadList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            globalLog("broadcast :: rcv");
            if(intent.getAction().equals(ORDERS_RELOAD))
            {
                loadList();
            }
        }
    };


    void loadList()
    {
        listNew.clear();listAccepted.clear();listReady.clear();listDispatched.clear();listCompleted.clear();listCancelled.clear();listReturn.clear();
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
            appLog("broadcast :: "+executing+" -- "+completed);
            if(!executing && !completed) {
                executing   = true;

                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("myid",getSharePrefs().getUid());
                    jobj.put("lastjson",lastJson+"");
                    jobj.put("incoming",incomingData+"");
                    jobj.put("caller","getMyOrders");
                    appLog("broadcast :: "+jobj);
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
            loader.setVisibility(View.GONE);
            for(int i=0;i<d.size();i++)
            {
                try
                {
                    jobjGlobal 						= d.get(i);
                    if(!idvalues.contains(jobjGlobal.getString("threadid")))
                    {


                        if(jobjGlobal.getString("status").equals(Constants.ACCEPTED))
                            listAccepted.add(jobjGlobal);
                        else if(jobjGlobal.getString("status").equals(Constants.READY))
                            listReady.add(jobjGlobal);
                        else if(jobjGlobal.getString("status").equals(Constants.DELIVERED))
                            listDispatched.add(jobjGlobal);
                        else if(jobjGlobal.getString("status").equals(Constants.CLOSED))
                            listCompleted.add(jobjGlobal);
                        else if(jobjGlobal.getString("status").equals(Constants.PLACED))
                            listNew.add(jobjGlobal);
                        else if(jobjGlobal.getString("status").equals(Constants.CANCELLED))
                            listCancelled.add(jobjGlobal);
                        else if(jobjGlobal.getString("status").equals("6"))
                            listReturn.add(jobjGlobal);

                        globalLog("responsevalue :: "+jobjGlobal.getString("status")+" -- "+listReady.size());

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

            notifyAllAdapters();

            executing   = false;

        }

        @Override
        protected void appendCachedData() {

        }
    }

    void notifyAllAdapters()
    {
        adapterNew.notifyDataSetChanged();
        adapterAccepted.notifyDataSetChanged();
        adapterReady.notifyDataSetChanged();
        adapterDispatched.notifyDataSetChanged();
        adapterCompleted.notifyDataSetChanged();
        adapterCancelled.notifyDataSetChanged();
        adapterReturn.notifyDataSetChanged();
    }
}
