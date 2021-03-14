package com.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.balram.library.FotButton;
import com.balram.library.FotTextView;

import com.github.ornolfr.ratingview.RatingView;
import com.coffee.seller.ksa.BaseActivity;
import com.tami.seller.ksa.R;
import com.coffee.seller.ksa.SelectDeliveryGuy;
import com.utils.ConnectionClass;
import com.utils.Constants;
import com.utils.Functions;
import com.utils.SharePrefsEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.coffee.seller.ksa.BaseActivity.appLog;
import static com.coffee.seller.ksa.BaseActivity.globalLog;
import static com.tami.seller.ksa.R.id.status;
import static com.utils.Constants.DEF_CURRENCY;
import static com.utils.Constants.ORDERS_RELOAD;


public class OrderMainAdapter extends ArrayAdapter<JSONObject>
{
    ArrayList<JSONObject>    list = new ArrayList<JSONObject>();

    Activity context;
    Functions function;
    LayoutInflater inflater;
    long currentTime;
    String defaultLang;
    SharePrefsEntry sp;
    ConnectionClass cc;
    Dialog details;
    int selectedIndex;
    int currentStatus = -1;
    public OrderMainAdapter(Activity con, ArrayList<JSONObject> l)
    {
        super(con, R.layout.row_order_main);

        context             = con;
        list                = l;
        function            = new Functions(con);
        sp                  = new SharePrefsEntry(con);
        cc                  = new ConnectionClass(con);
        inflater 	        = context.getLayoutInflater();
        defaultLang         = BaseActivity.getDefLang(context);

        details             = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
        details.setCanceledOnTouchOutside(false);
        details.setCancelable(true);
        details.setContentView(R.layout.dialog_order_details);
    }

    @Override
    public int getCount() {
        return list.size();
    }




    public class ViewHolderItem {
        FotTextView orderid,ordered_date,status,deldate ,qty , usrname , location;
    }

    @Override
    public View getView(final int paramInt, View convertView, ViewGroup parent)
    {
        try
        {
            ViewHolderItem viewHolder;
            if(convertView==null)
            {
                convertView 				= inflater.inflate(R.layout.row_order_main, parent, false);
                viewHolder 					= new ViewHolderItem();
                viewHolder.orderid          = (FotTextView)convertView.findViewById(R.id.orderid);
                viewHolder.status           = (FotTextView)convertView.findViewById(status);
                viewHolder.ordered_date     = (FotTextView)convertView.findViewById(R.id.dated);
                viewHolder.qty              = (FotTextView)convertView.findViewById(R.id.itemcount);
                viewHolder.deldate          = (FotTextView)convertView.findViewById(R.id.deliverydate);
                viewHolder.usrname          = (FotTextView)convertView.findViewById(R.id.usrname);
                viewHolder.location          = (FotTextView)convertView.findViewById(R.id.location);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder 					= (ViewHolderItem) convertView.getTag();
            }


            final JSONObject productObj     = list.get(paramInt);

            viewHolder.orderid.setText(productObj.getString("orderid"));
            viewHolder.deldate.setText(context.getString(R.string.order_delivery_date)+" "+productObj.getString("deliverytime"));
            viewHolder.ordered_date.setText(context.getString(R.string.order_ordered_date)+" "+productObj.getString("dated"));
            viewHolder.qty.setText(context.getString(R.string.order_qty)+" "+productObj.getString("itemcount"));

            setStatusMessage(productObj,viewHolder.status);


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showOrderDetails(productObj,paramInt);
                }
            });


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    void showOrderDetails(final JSONObject productObj,int in)
    {

        try
        {
            selectedIndex                = in;
            FotTextView orderid          = (FotTextView)details.findViewById(R.id.orderid);
            Spinner status               = (Spinner)details.findViewById(R.id.status);
            FotTextView ordered_date     = (FotTextView)details.findViewById(R.id.dated);
            FotTextView usrName          = (FotTextView)details.findViewById(R.id.usrname);

            FotTextView deliverycost     = (FotTextView)details.findViewById(R.id.deliverycost);
            FotTextView totcost          = (FotTextView)details.findViewById(R.id.totcost);

            FotTextView qty              = (FotTextView)details.findViewById(R.id.itemcount);
            FotTextView deldate          = (FotTextView)details.findViewById(R.id.deliverydate);
            FotButton location           = (FotButton)details.findViewById(R.id.location);
            FotButton callstore          = (FotButton)details.findViewById(R.id.callstore);
            FotButton cancelorder        = (FotButton)details.findViewById(R.id.cancelorder);
            FotTextView total            = (FotTextView)details.findViewById(R.id.total);
            ListView itemslv             = (ListView)details.findViewById(R.id.itemslv);
            RatingView ratingbar         = (RatingView)details.findViewById(R.id.ratingbar);
            RatingView usrrated          = (RatingView)details.findViewById(R.id.usrrated);

            usrrated.setRating(0f);
            OrdersProductsAdapter oAdap  = new OrdersProductsAdapter(context,new JSONArray(productObj.getString("details")));
            itemslv.setAdapter(oAdap);

            String netTotal               = "0.00";
            try
            {
                float del                = Float.parseFloat(productObj.getString("deliverycost"));
                float to                 = Float.parseFloat(productObj.getString("total"));
                float nTotal             = del + to;
                nTotal                   = function.round2decimal(nTotal);
                netTotal                 = String.valueOf(nTotal);

            }catch (Exception e){}


            try{usrrated.setRating(Float.parseFloat(productObj.getString("avgrating")));}catch (Exception eee){}
            usrName.setText(productObj.getString("name"));
            ratingbar.setRating(productObj.getInt("myrating"));
            totcost.setText(DEF_CURRENCY+netTotal);
            deliverycost.setText(DEF_CURRENCY+productObj.getString("deliverycost"));
            total.setText(DEF_CURRENCY+productObj.getString("total"));
            location.setText(Uri.decode(productObj.getString("del_address")));
            setStatusMessage(productObj,status);
            callstore.setText(productObj.getString("phonenumber"));
            orderid.setText(productObj.getString("orderid"));
            Log.d("orsers", productObj.toString());
            if (productObj.getInt("del_type") == 3){
                deldate.setText("Pickup");
            }
            else if (productObj.getInt("del_type") == 2){
                deldate.setText("Deliver Now");
            }
            else{
                deldate.setText(context.getString(R.string.order_delivery_date)+" "+productObj.getString("deliverytime"));
            }
//            deldate.setText(context.getString(R.string.order_delivery_date)+" "+productObj.getString("deliverytime"));
            ordered_date.setText(context.getString(R.string.order_ordered_date)+" "+productObj.getString("dated"));
            qty.setText(context.getString(R.string.order_qty)+" "+productObj.getString("itemcount"));


            try
            {
                if(productObj.getString("status").equals(Constants.CLOSED))
                {
                    ratingbar.setVisibility(View.VISIBLE);
                    callstore.setVisibility(View.GONE);
                    cancelorder.setVisibility(View.GONE);

                    ratingbar.setOnRatingChangedListener(new RatingView.OnRatingChangedListener() {
                        @Override
                        public void onRatingChange(float oldRating, float newRating) {
                            if(newRating != oldRating)
                                changeRating(productObj,newRating);
                        }
                    });

                }
                else
                {
                    ratingbar.setVisibility(View.GONE);
                    callstore.setVisibility(View.VISIBLE);
                    cancelorder.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
                ratingbar.setVisibility(View.GONE);
            }

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        Intent callIntent       = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + productObj.getString("phonenumber")));
                        context.startActivity(callIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("https")
                                .authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1")
                                .appendQueryParameter("origin", Double.parseDouble(productObj.getString("store_lati")) + "," + Double.parseDouble(productObj.getString("store_longi")))
                                .appendQueryParameter("destination", Double.parseDouble(productObj.getString("del_lati")) + "," + Double.parseDouble(productObj.getString("del_longi")) );
                        String url = builder.build().toString();
                        Log.d("Directions", url);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        context.startActivity(i);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            if(productObj.getString("status").equals(Constants.PLACED))
                cancelorder.setVisibility(View.VISIBLE);
            else
                cancelorder.setVisibility(View.GONE);

            cancelorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    details.dismiss();
                                    cancelOrder(productObj);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(context.getString(R.string.cancel_order)).setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                            .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();
                }
            });

            details.show();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    void cancelOrder(final  JSONObject jsonObject)
    {
        function.showDialog();
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... params) {
                String res = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("data",jsonObject+"");
                    jobj.put("myid",sp.getUid());
                    jobj.put("caller","cancelOrder");
                    String encrypted        = cc.getEncryptedString(jobj.toString());
                    res                     = cc.sendPostData(encrypted,null);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                function.dismissDialog();
                try
                {
                    if(s != null)
                    {
                        JSONObject jsonObject                 = new JSONObject(s);
                        Toast.makeText(context,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                        if(jsonObject.getString("success").equals("1"))
                        {
                            Intent intent = new Intent(ORDERS_RELOAD);
                            intent.setAction(ORDERS_RELOAD);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    }
                    else
                        Toast.makeText(context, context.getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();


                }catch (Exception e){
                    Toast.makeText(context ,context.getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    void setStatusMessage(final JSONObject productObj, Spinner status)
    {
        try
        {
            List<String> list = new ArrayList<String>();
            list.add(context.getResources().getString(R.string.set_status));
            list.add(context.getResources().getString(R.string.order_accepted));
            list.add(context.getResources().getString(R.string.order_ready));
            list.add(context.getResources().getString(R.string.order_delivered));
            list.add(context.getResources().getString(R.string.order_closed));
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            status.setAdapter(dataAdapter);




            if(productObj.getString("status").equals(Constants.ACCEPTED))
            {
                currentStatus = 1;
            }
            else if(productObj.getString("status").equals(Constants.READY))
            {
                currentStatus = 2;
            }
            else if(productObj.getString("status").equals(Constants.DELIVERED))
            {
                currentStatus = 3;
            }
            else if(productObj.getString("status").equals(Constants.CLOSED))
            {
                currentStatus = 4;

            }

            status.setSelection(currentStatus);

            status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // 0 = sent , 1 = accepted , 2 = ready , 3 = delivered , 4 = finished , 5 = cancel
                    appLog("onItem :: "+position+" -- "+currentStatus);
                    if(position != currentStatus && position > 0)
                    {
                        changeStatus(productObj,position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void setStatusMessage(JSONObject productObj,TextView status)
    {
        try
        {
            if(productObj.getString("status").equals(Constants.PLACED))
            {
                status.setText(context.getResources().getString(R.string.order_placed));
                status.setTextColor(Color.parseColor("#c6b92a"));
            }
            else if(productObj.getString("status").equals(Constants.ACCEPTED))
            {
                status.setText(context.getResources().getString(R.string.order_accepted));
                status.setTextColor(Color.parseColor("#005500"));
            }
            else if(productObj.getString("status").equals(Constants.READY))
            {
                status.setText(context.getResources().getString(R.string.order_ready));
                status.setTextColor(Color.parseColor("#000055"));
            }
            else if(productObj.getString("status").equals(Constants.DELIVERED))
            {
                status.setText(context.getResources().getString(R.string.order_delivered));
                status.setTextColor(Color.parseColor("#005500"));
            }
            else if(productObj.getString("status").equals(Constants.CLOSED))
            {
                status.setText(context.getResources().getString(R.string.order_closed));
                status.setTextColor(Color.parseColor("#005500"));
            }
            else if(productObj.getString("status").equals(Constants.CANCELLED))
            {
                status.setText(context.getResources().getString(R.string.order_cancelled));
                status.setTextColor(Color.parseColor("#ff5555"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    void changeRating(final  JSONObject productObj,final float ratingValue)
    {
        function.showDialog();
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... params) {
                String res = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("data",productObj+"");
                    jobj.put("myid",sp.getUid());
                    jobj.put("rating",ratingValue);
                    jobj.put("caller","rateUser");
                    String encrypted        = cc.getEncryptedString(jobj.toString());
                    res                     = cc.sendPostData(encrypted,null);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                function.dismissDialog();
                try
                {
                    if(s != null)
                    {
                        JSONObject jobj                 = new JSONObject(s);
                        Toast.makeText(context,jobj.getString("message"),Toast.LENGTH_SHORT).show();
                        if(jobj.getString("success").equals("1"))
                        {
                            productObj.put("myrating",ratingValue);
                            productObj.put("avgrating",jobj.getString("avgrating"));
                            list.set(selectedIndex,productObj);
                            notifyDataSetChanged();
                            try{ details.dismiss();}catch (Exception ee){}

                        }
                    }
                    else
                        Toast.makeText(context, context.getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();


                }catch (Exception e){
                    Toast.makeText(context ,context.getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    void changeStatus(final  JSONObject productObj,final int status)
    {
        function.showDialog();
        globalLog("changestatus :: 00 "+status);
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... params) {
                String res = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("data",productObj+"");
                    jobj.put("myid",sp.getUid());
                    jobj.put("status",status);
                    jobj.put("caller","changeStatus");
                    String encrypted        = cc.getEncryptedString(jobj.toString());
                    res                     = cc.sendPostData(encrypted,null);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                function.dismissDialog();
                try
                {
                    globalLog("changestatus :: "+s.length());
                    if(s != null)
                    {
                        JSONObject jobj                 = new JSONObject(s);
                        Toast.makeText(context,jobj.getString("message"),Toast.LENGTH_SHORT).show();
                        if(jobj.getString("success").equals("1"))
                        {


                            try{if(status == 4)
                                details.dismiss();}catch (Exception ee){}


                            productObj.put("status",status);
                            list.set(selectedIndex,productObj);
                            notifyDataSetChanged();


                        }


                        try
                        {

                            globalLog("changestatus :: "+jobj.getString("nearby"));
                            JSONArray jsonArray     = new JSONArray(jobj.getString("nearby"));
                            if(jsonArray.length() > 0)
                            {
                                Intent intent = new Intent(context,SelectDeliveryGuy.class);
                                intent.putExtra("order",productObj+"");
                                intent.putExtra("delivery",jsonArray+"");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else
                        Toast.makeText(context, context.getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();


                }catch (Exception e){
                    Toast.makeText(context ,context.getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    void sendReminder(final JSONObject jobj)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        remindFunction(jobj);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.are_you_sure)).setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();
    }

    void remindFunction(final JSONObject jsonObject)
    {
        function.showDialog();
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... params) {
                String res = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("data",jsonObject+"");
                    jobj.put("myid",sp.getUid());
                    jobj.put("caller","sendreminder");
                    jobj.put("lang",BaseActivity.getDefLang(context));
                    appLog("jobj :: "+jobj);
                    String encrypted        = cc.getEncryptedString(jobj.toString());
                    res                     = cc.sendPostData(encrypted,null);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                function.dismissDialog();
                try
                {
                    if(s != null)
                    {
                        JSONObject jsonObject                 = new JSONObject(s);
                        Toast.makeText(context,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(context, context.getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();


                }catch (Exception e){
                    Toast.makeText(context ,context.getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

//    SimpleDateFormat simpleDateFormat   = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//    Date date                           = simpleDateFormat.parse(productObj.getString("deliverytime"));
//    long startDt                        = date.getTime();
}