package com.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.balram.library.FotTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.coffee.seller.ksa.BaseActivity;
import com.coffee.seller.ksa.CatSubProducts;
import com.coffee.seller.ksa.ProductsMani;
import com.coffee.seller.ksa.ProductsManiIntermediat;
import com.tami.seller.ksa.R;
import com.utils.ConnectionClass;
import com.utils.Functions;
import com.utils.SharePrefsEntry;
import com.widget.RoundedImg;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.coffee.seller.ksa.BaseActivity.globalLog;
import static com.utils.Constants.DEF_CURRENCY;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.CustomViewHolder> {
    ArrayList<JSONObject> list = new ArrayList<JSONObject>();

    BaseActivity context;
    Functions function;
    SharePrefsEntry sp;
    ConnectionClass cc;
    int isEnabled;
    boolean changeStatusProcess;

    public ProductAdapter(BaseActivity con, ArrayList<JSONObject> l, int ie) {
        context = con;
        list = l;
        isEnabled = ie;
        function = new Functions(context);
        cc = new ConnectionClass(context);
        sp = new SharePrefsEntry(context);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_product, parent, false);

        CustomViewHolder vh = new CustomViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        try {
            final JSONObject jsonObject = list.get(position);
            boolean addProduct = false;
            try {
                addProduct = jsonObject.getString("addproduct").equals("1");
            } catch (Exception eee) {
            }

            globalLog("jsonobject :: " + jsonObject);
            if (!addProduct) {
                holder.name.setVisibility(View.VISIBLE);
                holder.price.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.VISIBLE);
                holder.imgadd.setVisibility(View.GONE);

                final String storeid = jsonObject.getString("storeid");
                final String locid = jsonObject.getString("locid");
                final String catid = jsonObject.getString("catid");
                final String subcatid = jsonObject.getString("subcatid");
                final String key = jsonObject.getString("key");


                holder.name.setText(jsonObject.getString("title" + BaseActivity.getDefaultLang()));
                float discountPrice = 0;
                try {
                    discountPrice = Float.parseFloat(jsonObject.getString("discount_price"));
                } catch (Exception e) {
                }
                if (discountPrice > 0) {
                    holder.discounted.setVisibility(View.VISIBLE);
                    holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.price.setText(DEF_CURRENCY + jsonObject.getString("price"));
                    holder.price.setTextColor(context.getResources().getColor(R.color.red));
                    holder.discounted.setText(DEF_CURRENCY + function.round2decimalString(discountPrice));
                    holder.discounted.setTextColor(context.getResources().getColor(R.color.green));
                } else {
                    holder.price.setPaintFlags(holder.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.discounted.setVisibility(View.GONE);
                    holder.price.setVisibility(View.VISIBLE);
                    holder.price.setText(DEF_CURRENCY + jsonObject.getString("price"));
                    holder.price.setTextColor(context.getResources().getColor(R.color.green));
                }

                Glide.with(context)
                        .load(jsonObject.getString("images"))
                        .placeholder(R.mipmap.ic_launcher)
                        .into(new GlideDrawableImageViewTarget(holder.img) {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                holder.img.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            }
                        });


                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(context, ProductsMani.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("data", jsonObject + "");
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.img.setBorderWidth(function.dpToPx(1));
                if (jsonObject.getString("status").equals("0")) {
                    holder.status.setChecked(false);
                    holder.img.setBorderColor(Color.parseColor("#aaff5555"));
                } else {
                    holder.status.setChecked(true);
                    holder.img.setBorderColor(Color.WHITE);
                }

                holder.status.setVisibility(View.VISIBLE);
                holder.status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        globalLog("changestatus :: " + holder.status.isPressed());
                        if (holder.status.isPressed())
                            changeStatus(position, jsonObject, holder.status);
                    }
                });

            } else {
                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intentX = new Intent(context, ProductsManiIntermediat.class);
                            try {
                                intentX.putExtra("catid", CatSubProducts.currentCatId.toString());
                            } catch (Exception e) {
                            }
                            try {
                                intentX.putExtra("subid", CatSubProducts.currentSubId.toString());
                            } catch (Exception e) {
                            }
                            context.startActivity(intentX);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                holder.img.setVisibility(View.GONE);
                holder.imgadd.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.INVISIBLE);
                holder.price.setVisibility(View.INVISIBLE);
                holder.status.setVisibility(View.GONE);
                holder.discounted.setVisibility(View.INVISIBLE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    void changeStatus(final int position, final JSONObject jsonObject, final SwitchCompat status) {


        changeStatusProcess = true;
        function.showDialog();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    JSONObject jobj = new JSONObject();
                    jobj.put("data", jsonObject + "");
                    jobj.put("myid", sp.getUid() + "");
                    jobj.put("status", status.isChecked() ? "1" : "0");
                    jobj.put("caller", "changeAvailability");

                    String encrypted = cc.getEncryptedString(jobj.toString());
                    res = cc.sendPostData(encrypted, null);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return res;
            }


            @Override
            protected void onPostExecute(String paramString) {
                super.onPostExecute(paramString);
                function.dismissDialog();
                try {
                    JSONObject jobj = new JSONObject(paramString);
                    list.set(position, jobj);
                    notifyDataSetChanged();
                    if (!jobj.getString("message").equals("")) {
                        Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

//                    CatSubProducts.catSubProducts.recreate();


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }

                changeStatusProcess = false;
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        FotTextView name, price, discounted;
        RoundedImg img;
        RelativeLayout parent;
        RelativeLayout imglr;
        ImageView imgadd;
        SwitchCompat status;


        public CustomViewHolder(View convertView) {
            super(convertView);
            name = (FotTextView) convertView.findViewById(R.id.name);
            price = (FotTextView) convertView.findViewById(R.id.price);
            discounted = (FotTextView) convertView.findViewById(R.id.discounted);
            img = (RoundedImg) convertView.findViewById(R.id.img);
            imgadd = (ImageView) convertView.findViewById(R.id.imgadd);
            parent = (RelativeLayout) convertView.findViewById(R.id.parent);
            imglr = (RelativeLayout) convertView.findViewById(R.id.imglr);
            status = (SwitchCompat) convertView.findViewById(R.id.status);

        }


    }
}