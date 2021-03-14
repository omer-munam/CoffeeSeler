package com.coffee.seller.ksa;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.CategoryAdapter;
import com.adapter.ProductAdapter;
import com.adapter.SubCategoryAdapter;
import com.balram.library.FotButton;
import com.tami.seller.ksa.R;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import pl.droidsonroids.gif.GifImageView;

import static com.utils.Constants.PRODUCT_PRESENT;
import static com.utils.Constants.SUBCAT_PRESENT;


public class CatSubProducts extends BaseActivity {
    RecyclerView categories, subcategories, items;
    public static CatSubProducts catSubProducts;

    HashMap<String, ArrayList<JSONObject>> itemListing = new HashMap<>();
    HashMap<String, ArrayList<JSONObject>> catitemListing = new HashMap<>();
    ArrayList<JSONObject> listCat = new ArrayList<>();
    ArrayList<JSONObject> listBlockedCats = new ArrayList<>();
    ArrayList<JSONObject> listSub = new ArrayList<>();
    ArrayList<JSONObject> listItem = new ArrayList<>();
    ArrayList<JSONObject> listSelectedItem = new ArrayList<>();

    LinearLayoutManager catLayoutManager, subCatLayoutManager;
    StaggeredGridLayoutManager stagLayoutManager;

    CategoryAdapter categoryAdapter;
    SubCategoryAdapter subCategoryAdapter;
    ProductAdapter productAdapter
            ;

    LinearLayout subcatlr;
    GifImageView loader_img;

    int storeid = 0;
    int locid = 0;
    String searchTxt;
    int currentCatid, currentSubcatid;
    int enabled = 0;
    public static JSONObject jsonObject;
    TextView action_title;
    public static JSONObject currentCatId;
    public static JSONObject currentSubId;
    ImageView edit_loc;
    LinearLayout menu_delete;
    Dialog coupon;
    EditText couponCode, coupondiscount;
    FotButton done, cancel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            itemListing = new HashMap<>();
            catitemListing = new HashMap<>();
            listCat = new ArrayList<>();
            listSub = new ArrayList<>();
            listItem = new ArrayList<>();
            listSelectedItem = new ArrayList<>();

            catSubProducts = this;
            setContentView(R.layout.activity_catsubproducts);
            enabled = 0;
            action_title = (TextView) findViewById(R.id.action_title);
            categories = (RecyclerView) findViewById(R.id.categories);
            subcategories = (RecyclerView) findViewById(R.id.subcategories);
            items = (RecyclerView) findViewById(R.id.items);
            subcatlr = (LinearLayout) findViewById(R.id.subcatlr);
            loader_img = (GifImageView) findViewById(R.id.loader_img);
            edit_loc = (ImageView) findViewById(R.id.edit_loc);
            menu_delete = (LinearLayout) findViewById(R.id.menu_delete);

            coupon             = new Dialog(CatSubProducts.this,android.R.style.Theme_Translucent_NoTitleBar);
            coupon.setCanceledOnTouchOutside(false);
            coupon.setCancelable(true);
            coupon.setContentView(R.layout.dialog_coupon_add);

            findViewById(R.id.fabCouponAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    coupon.show();
                }
            });

            done = coupon.findViewById(R.id.done);
            cancel = coupon.findViewById(R.id.cancel);
            couponCode = coupon.findViewById(R.id.couponAdd);
            coupondiscount = coupon.findViewById(R.id.couponDiscount);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    coupon.dismiss();
                }
            });

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String code = couponCode.getText().toString();
                    String dic = coupondiscount.getText().toString();
                    if (code.equals("") || dic.equals("")){
                        Toast.makeText(getApplicationContext(), "All Fields are Mandatory", Toast.LENGTH_LONG).show();
                    }
                    else if (Double.valueOf(dic) <= 0 || Double.valueOf(dic) >= 100){
                        Toast.makeText(getApplicationContext(), "Percentage Should be Between 1-99", Toast.LENGTH_LONG).show();
                    }
                    else{
                        addCoupon(code, dic);
                        coupon.dismiss();
                    }

                }
            });

            jsonObject = new JSONObject(getIntent().getExtras().getString("data"));

            try {
                storeid = Integer.parseInt(jsonObject.getString("storeid"));
            } catch (Exception ee) {
            }
            try {
                locid = Integer.parseInt(jsonObject.getString("id"));
            } catch (Exception ee) {
            }
            try {
                enabled = Integer.parseInt(jsonObject.getString("status"));
            } catch (Exception ee) {
            }

            action_title.setText(jsonObject.getString("title" + getDefaultLang()));

            catLayoutManager = new LinearLayoutManager(this);
            subCatLayoutManager = new LinearLayoutManager(this);
            stagLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);

            categoryAdapter = new CategoryAdapter(this, listCat);
            subCategoryAdapter = new SubCategoryAdapter(this, listSub);
            productAdapter = new ProductAdapter(this, listSelectedItem, enabled);

            catLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            subCatLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            categories.setLayoutManager(catLayoutManager);
            subcategories.setLayoutManager(subCatLayoutManager);
            items.setLayoutManager(stagLayoutManager);

            items.setAdapter(productAdapter);
            categories.setAdapter(categoryAdapter);
            subcategories.setAdapter(subCategoryAdapter);

            edit_loc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CatSubProducts.this, LocationsManipulate.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("data", jsonObject + "");
                    startActivity(intent);
                }
            });

            menu_delete.setVisibility(View.VISIBLE);


            OverScrollDecoratorHelper.setUpOverScroll(categories, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
            OverScrollDecoratorHelper.setUpOverScroll(subcategories, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
            OverScrollDecoratorHelper.setUpOverScroll(items, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            LocalBroadcastManager.getInstance(this).registerReceiver(categoryClicked, new IntentFilter(SUBCAT_PRESENT));
            LocalBroadcastManager.getInstance(this).registerReceiver(productClicked, new IntentFilter(PRODUCT_PRESENT));

            getCategories();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCoupon(final String code, final String dic) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String res = null;
                try {
                    JSONObject jobj = new JSONObject();
                    jobj.put("locid", locid);
                    jobj.put("code", code);
                    jobj.put("disc", dic);
                    jobj.put("caller", "addCoupon");

                    appLog("jobj :: 22 " + jobj);
                    String encrypted = getConnection().getEncryptedString(jobj.toString());
                    res = getConnection().sendPostData(encrypted, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    appLog("onpost :: " + s);
                    JSONObject jobj = new JSONObject(s);
                    if (jobj.getString("success").equals("1")){
                        Toast.makeText(getApplicationContext(),jobj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),R.string.error_network, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (getSharePref().getBooleanValue("needreload")) {
            getSharePref().setBooleanValue("needreload", false);
            recreate();
        }

    }

    public void gotoOffers(View v) {
        Intent intent = new Intent(this, GotoOffers.class);
        intent.putExtra("data", jsonObject + "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void getCategories() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String res = null;
                try {
                    JSONObject jobj = new JSONObject();
                    jobj.put("pagenumber", listCat.size());
                    jobj.put("screenwidth", BaseActivity.screenwidth);
                    jobj.put("screenheight", BaseActivity.screenheight);
                    jobj.put("data", jsonObject + "");
                    jobj.put("locid", locid);
                    jobj.put("myid", getSharePrefs().getUid());
                    jobj.put("caller", "getStoreCategories");

                    appLog("jobj :: 22 " + jobj);
                    String encrypted = getConnection().getEncryptedString(jobj.toString());
                    res = getConnection().sendPostData(encrypted, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    appLog("onpost :: " + s);
                    listCat.clear();
                    JSONObject addPr = new JSONObject();
                    addPr.put("addcat", "1");
                    listCat.add(addPr);
                    JSONArray jArray = new JSONArray(s);
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            listCat.add(jArray.getJSONObject(i));
                        } catch (Exception e) {
                        }

                    }

                    categories.getRecycledViewPool().clear();
                    categoryAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }


                getProducts();
                loader_img.setVisibility(View.GONE);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    void getProducts() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String res = null;
                try {
                    JSONObject jobj = new JSONObject();
                    jobj.put("pagenumber", listCat.size());
                    jobj.put("screenwidth", BaseActivity.screenwidth);
                    jobj.put("screenheight", BaseActivity.screenheight);
                    jobj.put("data", jsonObject + "");
                    jobj.put("myid", getSharePrefs().getUid());
                    jobj.put("caller", "getStoreProducts");


                    String encrypted = getConnection().getEncryptedString(jobj.toString());
                    res = getConnection().sendPostData(encrypted, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    listBlockedCats = new ArrayList<>();
                    for (int i = 0; i < listCat.size(); i++) {
                        int status = 1;
                        try {
                            status = Integer.parseInt(listCat.get(i).getString("status"));
                        } catch (Exception e) {
                        }

                        if (status == 0) {

                            // means off
                            listBlockedCats.add(listCat.get(i));
                        }
                    }

                    appLog("tmp :: " + s);
                    itemListing.clear();
                    listItem.clear();
                    JSONObject addPr = new JSONObject();
                    addPr.put("addproduct", "1");
                    //listItem.add(addPr);

                    JSONArray jArray = new JSONArray(s);
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            JSONObject jobj = jArray.getJSONObject(i);
                            int keyAll[] = getFunction().getKeys(jobj);
                            String key = getFunction().createKeyHash(keyAll);
                            ArrayList<JSONObject> tmp = itemListing.get(key);
                            appLog("tmp :: " + key + " -- " + tmp);
                            if (tmp == null)
                                tmp = new ArrayList<JSONObject>();

                            tmp.add(jobj);
//                            itemListing.put(key, tmp);
                            Log.d("listItem", jobj.toString());


                            String keyCatOnly = getFunction().createKeyHashCat(keyAll);
                            ArrayList<JSONObject> tmp2 = catitemListing.get(keyCatOnly);
                            if (tmp2 == null)
                                tmp2 = new ArrayList<JSONObject>();

                            tmp2.add(jobj);
//                            catitemListing.put(keyCatOnly, tmp2);

                            if (jobj.getString("admin_approved").equals("0") ){
                                continue;
                            }

                            if (listBlockedCats.size() == 0) {
                                listItem.add(jobj);
                                itemListing.put(key, tmp);
                            } else {
                                boolean isFound = false;
                                for (int j = 0; j < listBlockedCats.size(); j++) {
                                    // Add after comparing catid
                                    try {
                                        int catidBlocked = Integer.parseInt(listBlockedCats.get(j).getString("catid"));
                                        int catidJobj = Integer.parseInt(jobj.getString("catid"));
                                        if (catidBlocked == catidJobj) {
                                            // do not add in this case to list
                                            isFound = true;
                                            break;
                                        }
                                    } catch (Exception e) {
                                    }

                                }

                                if (!isFound) {
                                    listItem.add(jobj);
                                    itemListing.put(key, tmp);
                                    catitemListing.put(keyCatOnly, tmp2);
                                }

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    listSelectedItem.clear();
                    listSelectedItem.addAll(listItem);
                    items.getRecycledViewPool().clear();
                    productAdapter.notifyDataSetChanged();
                    try {
                        categories.findViewHolderForAdapterPosition(1).itemView.performClick();
                    } catch (Exception e) {
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 500);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(categoryClicked);
        } catch (Exception e) {
        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(productClicked);
        } catch (Exception e) {
        }

    }


    public BroadcastReceiver categoryClicked = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SUBCAT_PRESENT)) {
                try {
                    JSONObject singleObj = null;
                    listSub.clear();
                    try {

                        JSONArray jsonArray = new JSONArray(intent.getExtras().getString("data"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            singleObj = jsonArray.getJSONObject(i);
                            try {
                                listSub.add(jsonArray.getJSONObject(i));
                            } catch (Exception e) {
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (listSub.size() > 0 && subcatlr.getVisibility() == View.GONE)
                        subcatlr.setVisibility(View.VISIBLE);
                    if (listSub.size() == 0 && subcatlr.getVisibility() == View.VISIBLE)
                        subcatlr.setVisibility(View.GONE);

                    subcategories.getRecycledViewPool().clear();
                    subCategoryAdapter.notifyDataSetChanged();


                    int catid = 0;
                    try {
                        appLog("cat :: sub " + singleObj.getString("catid"));
                        catid = Integer.parseInt(singleObj.getString("catid"));
                    } catch (Exception e) {
                    }
                    int ids[] = new int[]{0, storeid, locid, catid, 0};
                    String key = getFunction().createKeyHashCat(ids);

                    appLog("cat  :: sub  key " + key);
                    currentCatid = catid;
                    currentSubcatid = 0;

                    if (catid > 0) {
                        listSelectedItem.clear();
                        if (catitemListing.get(key) != null) {
                            if (catitemListing.get(key).size() > 0) {
                                JSONObject addPr = new JSONObject();
                                addPr.put("addproduct", "1");
                                listSelectedItem.add(addPr);
                                listSelectedItem.addAll(catitemListing.get(key));
                            }

                        }
                        productAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        }
    };

    public BroadcastReceiver productClicked = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PRODUCT_PRESENT)) {
                try {
                    int catid = 0;
                    int subcatid = 0;
                    int status = 1;

                    try {
                        JSONObject jobj = new JSONObject(intent.getExtras().getString("cat"));
                        appLog("cat :: " + jobj);
                        catid = Integer.parseInt(jobj.getString("id"));
                        status                       = Integer.parseInt(jobj.getString("status"));
                    } catch (Exception e) {
                    }

                    try {
                        JSONObject jobj = new JSONObject(intent.getExtras().getString("subcat"));
                        appLog("cat :: sub " + jobj);
                        catid = Integer.parseInt(jobj.getString("catid"));
                        subcatid = Integer.parseInt(jobj.getString("subid"));
                    } catch (Exception e) {
                    }

                    int ids[] = new int[]{0, storeid, locid, catid, subcatid};
                    String key = getFunction().createKeyHash(ids);

                    appLog("cat :: key " + key);

                    currentCatid = catid;
                    currentSubcatid = subcatid;
                    listSelectedItem.clear();
                    JSONObject addPr = new JSONObject();
                    addPr.put("addproduct", "1");
                    listSelectedItem.add(addPr);

                    globalLog("category :: " + catid);
                    if (catid > 0)
                        listSelectedItem.addAll(itemListing.get(key));
                    else
                        listSelectedItem.addAll(listItem);

                    if (status == 0)
                        listSelectedItem.clear();

                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                productAdapter.notifyDataSetChanged();
            }

        }
    };


}
