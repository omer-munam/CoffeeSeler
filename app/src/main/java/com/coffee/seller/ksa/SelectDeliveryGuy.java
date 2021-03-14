package com.coffee.seller.ksa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tami.seller.ksa.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import pl.droidsonroids.gif.GifImageView;


public class SelectDeliveryGuy extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    HashMap<String,JSONObject> driversHash          = new HashMap<>();
    SupportMapFragment fragmentMap;
    GoogleMap googleMap;
    JSONArray jsonArray;
    JSONObject currentDriver , myData;
    Dialog dialog_delivery;
    TextView name , totalorders , myorders , distance , totalpending , mypendingords;
    Button callDelivery,selectDelivery;
    String allDelivered , myDelivered , allPending , myPending , distance_away;

    TextView orderid;
    GifImageView loader;

    Button manualdelivery;
    String orderIdString;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_delivery_guys);
            fragmentMap         = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            manualdelivery      = (Button)findViewById(R.id.manualdelivery);
            loader              = (GifImageView)findViewById(R.id.loader);

            fragmentMap.getMapAsync(this);

            orderIdString        = getIntent().getExtras().getString("orderid");//"824359";//


            dialog_delivery     = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog_delivery.setContentView(R.layout.dialog_delivery_details);
            name                = (TextView)dialog_delivery.findViewById(R.id.name);
            totalorders         = (TextView)dialog_delivery.findViewById(R.id.totalorders);
            myorders            = (TextView)dialog_delivery.findViewById(R.id.myorders);
            distance            = (TextView)dialog_delivery.findViewById(R.id.distance);
            totalpending        = (TextView)dialog_delivery.findViewById(R.id.totalpending);
            mypendingords       = (TextView)dialog_delivery.findViewById(R.id.mypendingords);
            callDelivery        = (Button)dialog_delivery.findViewById(R.id.callstore);
            selectDelivery      = (Button)dialog_delivery.findViewById(R.id.cancelorder);
            orderid             = (TextView)dialog_delivery.findViewById(R.id.orderid);

            allDelivered        = getResources().getString(R.string.total_orders);
            myDelivered         = getResources().getString(R.string.my_orders_carried);
            allPending          = getResources().getString(R.string.total_pending);
            myPending           = getResources().getString(R.string.my_orders_pending);
            distance_away       = getResources().getString(R.string.distance_away);

            orderid.setText(orderIdString);


            manualdelivery.setOnClickListener(this);

            loadCurrentDeliveryGuys();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap gmap) {
        googleMap   = gmap;
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                try
                {
                    String idValue  = marker.getSnippet();
                    currentDriver   = driversHash.get(idValue);
                    if(currentDriver != null)
                    {

                        name.setText(currentDriver.getString("name"));
                        totalorders.setText(allDelivered+" "+currentDriver.getString("alldel"));
                        totalpending.setText(allPending+" "+currentDriver.getString("pending"));
                        myorders.setText(myDelivered+" "+currentDriver.getString("mydel"));
                        mypendingords.setText(myPending+" "+currentDriver.getString("mypending"));
                        distance.setText(distance_away+" "+currentDriver.getString("distance_in_km"));

                        callDelivery.setText(currentDriver.getString("contact"));
                        callDelivery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try
                                {
                                    Intent callIntent       = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + currentDriver.getString("contact")));
                                    startActivity(callIntent);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                        selectDelivery.setOnClickListener(SelectDeliveryGuy.this);
                        dialog_delivery.show();
                    }
                }catch (Exception e){}


                return true;
            }
        });

        loadMarker();

    }

    void loadMarker()
    {
        try
        {
            LatLng position = null;
            globalLog("onpostexec :: 00 "+jsonArray.length());
            for(int i=0;i<jsonArray.length();i++)
            {
                try
                {
                    JSONObject jobj         = jsonArray.getJSONObject(i);
                    driversHash.put(jobj.getString("id"),jobj);
                    position         = new LatLng(jobj.getDouble("last_lati"), jobj.getDouble("last_longi"));
                    MarkerOptions options   = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
                    options.position(position);
                    options.title(jobj.getString("name"));
                    options.snippet(jobj.getString("id"));

                    googleMap.addMarker(options);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            try
            {
                CameraUpdate center = CameraUpdateFactory.newLatLng(position);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
            }catch (Exception e){}
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.manualdelivery)
        {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            setDeliveryStatus(null,0);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.no_delivery_sure)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).show();
        }
        else if(id == R.id.cancelorder)
        {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            setDeliveryStatus(currentDriver,1);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(this.getString(R.string.are_you_sure)).setPositiveButton(this.getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(this.getString(R.string.no), dialogClickListener).show();

        }
    }

    void loadCurrentDeliveryGuys()
    {
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("orderid",orderIdString);
                    jobj.put("myid",getSharePrefs().getUid());
                    jobj.put("caller","getDeliveryGuys");
                    String encrypted        = getConnection().getEncryptedString(jobj.toString());
                    res                     = getConnection().sendPostData(encrypted,null);
                    Log.d("orsers", jobj.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }

                return res;
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try
                {
                    globalLog("onpostexec :: "+s);
                    jsonArray   = new JSONArray(s);
                    loadMarker();
                    loader.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    void setDeliveryStatus(final JSONObject jsonObject ,final  int type)
    {
            getFunction().showDialog();
            new AsyncTask<Void,Void,String>()
            {
                @Override
                protected String doInBackground(Void... voids) {
                    String res = null;
                    try
                    {
                        JSONObject jobj         = new JSONObject();
                        jobj.put("orderid",orderIdString);
                        jobj.put("deliveryperson",jsonObject+"");
                        jobj.put("deliverytype",type+"");
                        jobj.put("myid",getSharePrefs().getUid());
                        jobj.put("caller","updateDelivery");
                        String encrypted        = getConnection().getEncryptedString(jobj.toString());
                        res                     = getConnection().sendPostData(encrypted,null);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    return res;
                }


                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    try
                    {
                            globalLog("exoecc :: "+s);
                            getFunction().dismissDialog();
                            JSONObject jsonObject = new JSONObject(s);
                            Toast.makeText(SelectDeliveryGuy.this,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                            if(jsonObject.getString("success").equals("1"))
                            {
                                try{Orders.orders.recreate();}catch (Exception e){}
                                SelectDeliveryGuy.this.finish();
                            }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
