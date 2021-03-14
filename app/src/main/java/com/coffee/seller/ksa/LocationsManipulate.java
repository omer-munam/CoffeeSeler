package com.coffee.seller.ksa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mvc.imagepicker.ImagePickerLib;
import com.tami.seller.ksa.R;
import com.utils.ImagePicker;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import static com.utils.Constants.FROM_IMAGE;
import static com.utils.Constants.LOCATION_RADIUS;
import static com.utils.Constants.PLACE_PICKER_REQUEST;

public class LocationsManipulate extends BaseActivity {

    double latitude;
    double longitude;
    EditText mincost;
    TextView loc , name_en,name_ar;
    String nametxt,nameartxt;
    boolean locSelected;
    ImageView select_img;
    Bitmap bitmap;
    String encoded;
    JSONObject jsonObject;
    int storeid , locid;
    TextView coverage;
    float coverageAre;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_locations_mani);
            latitude        = 0;
            longitude       = 0;

            ImagePickerLib.setMinQuality(600, 600);

            select_img      = (ImageView)findViewById(R.id.select_img);
            loc             = (TextView)findViewById(R.id.loc);
            name_en         = (TextView)findViewById(R.id.name_en);
            name_ar         = (TextView)findViewById(R.id.name_ar);
            coverage        = (TextView)findViewById(R.id.coverage);
            mincost         = (EditText)findViewById(R.id.mincost);

            try{jsonObject  = new JSONObject(getIntent().getExtras().getString("data"));}catch (Exception e){}

            try{storeid     = Integer.parseInt(jsonObject.getString("storeid"));}catch (Exception ee){}
            try{locid       = Integer.parseInt(jsonObject.getString("id"));}catch (Exception ee){}
            try{coverageAre = Float.parseFloat(jsonObject.getString("radius"));}catch (Exception ee){}
            try
            {
                mincost.setText(jsonObject.getString("minimum_del"));
                globalLog("datavalue :: "+jsonObject);
                addressJSON         = new JSONObject();
                latitude            = Double.parseDouble(jsonObject.getString("latitude"));
                longitude           = Double.parseDouble(jsonObject.getString("longitude"));
                String address      = jsonObject.getString("address");
                addressJSON.put("latitude",latitude);
                addressJSON.put("longitude",longitude);
                addressJSON.put("address",address);
                loc.setText(address);
                locSelected         = true;
            }catch (Exception e){}

            try
            {
                name_en.setText(jsonObject.getString("title_en"));
                name_ar.setText(jsonObject.getString("title_ar"));
                coverage.setText(jsonObject.getString("radius"));
                Glide.with(this)
                        .load(jsonObject.getString("image"))
                        .placeholder(R.drawable.logo)
                        .into(new GlideDrawableImageViewTarget(select_img) {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                select_img.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {

                            }
                        });
            }catch (Exception e){}


            coverage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(latitude == 0 && longitude == 0)
                    {
                        Toast.makeText(LocationsManipulate.this,getResources().getString(R.string.location_loading),Toast.LENGTH_SHORT).show();
                    }
                    else if(!locSelected)
                    {
                        Toast.makeText(LocationsManipulate.this,getResources().getString(R.string.select_location_plz),Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(LocationsManipulate.this,LocationRadius.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude",longitude);
                        intent.putExtra("current",coverageAre);
                        startActivityForResult(intent,LOCATION_RADIUS);
                    }
                }
            });

            mLocationManager                = (LocationManager) getSystemService(LOCATION_SERVICE);

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, mLocationListener);




            select_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chooseImageIntent = ImagePicker.getPickImageIntent(getBaseActivity());
                    startActivityForResult(chooseImageIntent, FROM_IMAGE);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            globalLog("XLOCATION :: "+location);
            if(latitude == 0 && longitude == 0)
            {
                latitude           = location.getLatitude();
                longitude          = location.getLongitude();
            }
            try{mLocationManager.removeUpdates(mLocationListener);}catch (Exception e){}
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{mLocationManager.removeUpdates(mLocationListener);}catch (Exception e){}
    }


    @Override
    protected void onResume() {
        super.onResume();
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }



    public void selectLocation(View v)
    {
        try
        {
            if(latitude == 0 && longitude == 0)
            {
                Toast.makeText(LocationsManipulate.this,getResources().getString(R.string.location_loading),Toast.LENGTH_SHORT).show();
            }
            else
            {
                PlacePicker.IntentBuilder builder   = new PlacePicker.IntentBuilder();
                builder.setLatLngBounds(new LatLngBounds(new LatLng(latitude,longitude),new LatLng(latitude,longitude)));
                startActivityForResult(builder.build(getBaseActivity()), PLACE_PICKER_REQUEST);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        appLog("onactivity :: "+requestCode+" -- "+resultCode);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                try
                {
                    Place place         = PlacePicker.getPlace(data, this);
                    addressJSON         = new JSONObject();
                    latitude            = place.getLatLng().latitude;
                    longitude           = place.getLatLng().longitude;
                    String address      = String.format("%s", place.getAddress());
                    addressJSON.put("latitude",latitude);
                    addressJSON.put("longitude",longitude);
                    addressJSON.put("address",address);
                    loc.setText(address);
                    locSelected         = true;
                    globalLog("latitudelongi :: "+latitude+" -- "+longitude);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        else if(requestCode == FROM_IMAGE)
        {
            try
            {
                bitmap                  = ImagePicker.getImageFromResult(this, resultCode, data);
                int h                   = 300;
                int w                   = 300;
                bitmap                  = getFunction().scaleCenterCrop(bitmap, w, h);

                bitmapToString();
            } catch (Exception e) {
                encoded = "";
            }
        }
        else if(requestCode == LOCATION_RADIUS)
        {
            try
            {
                coverageAre = getFunction().round2decimal(data.getExtras().getFloat("radius"));
                coverage.setText(coverageAre+"");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void bitmapToString()
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            byte[] byteArray        = byteArrayOutputStream .toByteArray();
            encoded                 = Base64.encodeToString(byteArray, Base64.DEFAULT);

            Glide.with(this)
                    .load(byteArrayOutputStream.toByteArray())
                    .asBitmap()
                    .into(select_img);
        }catch (Exception e){}

    }


    public void saveLocation(View v)
    {
            nametxt     = name_en.getText().toString();
            nameartxt   = name_ar.getText().toString();
            coverageAre     = 0;
            try{coverageAre = getFunction().round2decimal(Float.parseFloat(coverage.getText().toString()));}catch(Exception e){}

            if(nametxt.equals(""))
            {
                name_en.requestFocus();
                Toast.makeText(this,getString(R.string.en_blank),Toast.LENGTH_SHORT).show();
                return;
            }

            if(nameartxt.equals(""))
            {
                name_ar.requestFocus();
                Toast.makeText(this,getString(R.string.ar_blank),Toast.LENGTH_SHORT).show();
                return;
            }

            if(!locSelected)
            {
                Toast.makeText(this,getString(R.string.location_blank),Toast.LENGTH_SHORT).show();
                return;
            }

            if(coverageAre <= 0)
            {
                Toast.makeText(this,getString(R.string.invalid_coverage_radius),Toast.LENGTH_SHORT).show();
                return;
            }

            saveLocations();

    }

    void saveLocations()
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
                    jobj.put("name_en",nametxt);
                    jobj.put("name_ar",nameartxt);
                    jobj.put("locations",addressJSON);
                    jobj.put("bitmap",encoded);
                    jobj.put("storeid",storeid);
                    jobj.put("locid",locid);
                    jobj.put("mincost",mincost.getText().toString().trim());
                    jobj.put("radius",coverageAre);
                    jobj.put("myid",getSharePrefs().getUid()+"");
                    jobj.put("caller","saveLocation");

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
                    JSONObject json     = new JSONObject(paramString);
                    Toast.makeText(getBaseActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    if(json.getString("status").equals("1"))
                    {
                        gototLocation(null);
                    }
                }catch (Exception e){
                    Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }

            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
