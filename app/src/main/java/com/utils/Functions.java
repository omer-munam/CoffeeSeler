package com.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.adapter.PresentListAdapter;
import com.adapter.PresentLocatioinList;
import com.adapter.PresentProductList;
import com.adapter.PresentSubListAdapter;
import com.tami.seller.ksa.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.graphics.Bitmap.createBitmap;
import static com.coffee.seller.ksa.BaseActivity.appLog;
import static com.coffee.seller.ksa.BaseActivity.currentCatSelected;
import static com.coffee.seller.ksa.BaseActivity.currentLocSelected;
import static com.coffee.seller.ksa.BaseActivity.spinner;

/**
 * Created by mac on 1/01/2018.
 */

public class Functions {

    Context context;
    Dialog dialog;
    SharePrefsEntry sp;
    ConnectionClass cc;
    ArrayList<JSONObject> listData = new ArrayList<>();
    ArrayList<JSONObject> listDataSub = new ArrayList<>();
    ArrayList<JSONObject> listLoc = new ArrayList<>();
    ArrayList<JSONObject> listProduct = new ArrayList<>();
    ListView catList;
    PresentListAdapter adapter;
    PresentSubListAdapter adapter2;
    PresentLocatioinList adapter3;
    PresentProductList adapter4;
    public Functions(Context baseActivity) {

        context         = baseActivity;
        sp              = new SharePrefsEntry(baseActivity);
        cc              = new ConnectionClass(baseActivity);
    }



    public void showDialog()
    {
        try
        {
            dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_loader);
            dialog.setCancelable(false);
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void dismissDialog()
    {
        try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
    }


    public void logout()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.logout_from_app)).setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();

    }

    public void confirm(final JSONObject jobj, final int type)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteLocation(jobj,type);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.are_you_sure)).setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();

    }


    void deleteLocation(final JSONObject jobjs,final int type)
    {
        try{showDialog();}catch (Exception e){}
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... voids) {
                String res  = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("data",jobjs);
                    jobj.put("type",type);
                    jobj.put("myid",sp.getUid()+"");
                    jobj.put("caller","deleteData");

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
                dismissDialog();
                try
                {
                    JSONObject json     = new JSONObject(paramString);
                    Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                    if(json.getString("status").equals("1"))
                    {
                        ((Activity)context).recreate();
                    }
                }catch (Exception e){
                    Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }

            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public Bitmap scaleCenterCrop(Bitmap bmp,int newHeight, int newWidth) {
        int sourceWidth = bmp.getWidth();
        int sourceHeight = bmp.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = createBitmap(newWidth, newHeight, bmp.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(bmp, null, targetRect, null);

        return dest;
    }


    public void getCurrentCategoris(final boolean excludeMine,int caller)
    {
        try{spinner.dismiss();}catch (Exception e){}
        spinner     = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
        spinner.setContentView(R.layout.dialog_choose_list);

        catList     = (ListView)spinner.findViewById(R.id.listview);
        adapter     = new PresentListAdapter(context,listData,caller);
        catList.setAdapter(adapter);
        listData.clear();
        if(listData.size() > 0)
        {
            spinner.show();
            return;
        }

        showDialog();
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... voids) {
                String res  = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("myid",sp.getUid()+"");
                    jobj.put("locid",currentLocSelected+"");
                    jobj.put("excludemine",(excludeMine ? 1 : 0));
                    jobj.put("caller","getCategories");

                    appLog("js :: "+jobj);

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
                dismissDialog();
                try
                {

                    JSONArray jsonArray = new JSONArray(paramString);
                    listData.clear();
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        listData.add(jsonArray.getJSONObject(i));
                    }

                    adapter.notifyDataSetChanged();
                    spinner.show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }



            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void getCurrentSubCategories(final boolean excludeMine,int caller)
    {
        try{spinner.dismiss();}catch (Exception e){}
        spinner     = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
        spinner.setContentView(R.layout.dialog_choose_list);

        catList     = (ListView)spinner.findViewById(R.id.listview);
        adapter2    = new PresentSubListAdapter(context,listDataSub,caller);
        catList.setAdapter(adapter2);

        listDataSub.clear();
        if(listDataSub.size() > 0)
        {
            spinner.show();
            return;
        }

        showDialog();
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... voids) {
                String res  = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("myid",sp.getUid()+"");
                    jobj.put("locid",currentLocSelected+"");
                    jobj.put("catid",currentCatSelected+"");
                    jobj.put("excludemine",(excludeMine ? 1 : 0));
                    jobj.put("caller","getSubCategories");

                    appLog("js :: "+jobj);

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
                dismissDialog();
                try
                {
                    JSONArray jsonArray = new JSONArray(paramString);
                    listDataSub.clear();
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        listDataSub.add(jsonArray.getJSONObject(i));
                    }

                    adapter2.notifyDataSetChanged();
                    spinner.show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }



            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void getCurrentLocations(final boolean excludeMine,int caller)
    {
        try{spinner.dismiss();}catch (Exception e){}
        spinner     = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
        spinner.setContentView(R.layout.dialog_choose_list);

        catList     = (ListView)spinner.findViewById(R.id.listview);
        adapter3    = new PresentLocatioinList(context,listLoc,caller);
        catList.setAdapter(adapter3);

//        if(listLoc.size() > 0)
//        {
//            spinner.show();
//            return;
//        }

        showDialog();
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... voids) {
                String res  = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("myid",sp.getUid()+"");
                    jobj.put("excludemine",(excludeMine ? 1 : 0));
                    jobj.put("caller","getLocations");

                    appLog("js :: "+jobj);

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
                dismissDialog();
                try
                {
                    JSONArray jsonArray = new JSONArray(paramString);
                    listLoc.clear();
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        listLoc.add(jsonArray.getJSONObject(i));
                    }

                    adapter3.notifyDataSetChanged();
                    spinner.show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }



            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    public void getCurrentProducts(final boolean excludeMine,int caller)
    {
        try{spinner.dismiss();}catch (Exception e){}
        spinner     = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
        spinner.setContentView(R.layout.dialog_choose_list);

        catList     = (ListView)spinner.findViewById(R.id.listview);
        adapter4    = new PresentProductList(context,listProduct,caller);
        catList.setAdapter(adapter4);

        if(listProduct.size() > 0)
        {
            spinner.show();
            return;
        }

        showDialog();
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... voids) {
                String res  = null;
                try
                {
                    JSONObject jobj         = new JSONObject();
                    jobj.put("myid",sp.getUid()+"");
                    jobj.put("excludemine",(excludeMine ? 1 : 0));
                    jobj.put("caller","getProducts");

                    appLog("js :: "+jobj);

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
                dismissDialog();
                try
                {
                    JSONArray jsonArray = new JSONArray(paramString);
                    listProduct.clear();
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        listProduct.add(jsonArray.getJSONObject(i));
                    }

                    adapter4.notifyDataSetChanged();
                    spinner.show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }



            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public String round2decimalString(float nTotal) {
        DecimalFormat form       = new DecimalFormat("0.00");
        return  form.format(nTotal);
    }

    public float round2decimal(float nTotal) {
        nTotal                   = nTotal*100;
        nTotal                   = Math.round(nTotal);
        nTotal                   = nTotal /100;

        return  nTotal;
    }

    public int[] getKeys(JSONObject jsonObject)
    {
        int returnArray[]       = new int[]{0,0,0,0,0};
        String keyarray[]       = new String[]{"id","storeid","locid","catid","subcatid"};

        for(int i=0;i<returnArray.length;i++)
        {
            try{returnArray[i]  = Integer.parseInt(jsonObject.getString(keyarray[i]));}catch (Exception e){}
        }

        return returnArray;
    }

    public String createKeyHashCat(int[] ids)
    {
        String key              = ids[2]+"@@"+ids[1]+"@@"+ids[3];
        return key;
    }

    public String createKeyHash(int[] ids)
    {
        String key              = ids[2]+"@@"+ids[1]+"@@"+ids[3]+"@@"+ids[4];
        return key;
    }
}
