package com.coffee.seller.ksa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tami.seller.ksa.R;

import static com.utils.Constants.LOCATION_RADIUS;


public class LocationRadius extends BaseActivity implements OnMapReadyCallback {

    MapView mapView;
    SeekBar seekbar;
    private GoogleMap gmap;
    double latitude,longitude;
    TextView current_range;
    Button done;
    Circle circle;
    float currentCoverage;
    int denominator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_circle);

        mapView                 = (MapView) findViewById(R.id.mapview);
        seekbar                 = (SeekBar)findViewById(R.id.seekbar);
        current_range           = (TextView)findViewById(R.id.current_range);
        done                    = (Button)findViewById(R.id.done);

        seekbar.setEnabled(false);


        latitude                = getIntent().getExtras().getDouble("latitude");
        longitude               = getIntent().getExtras().getDouble("longitude");
        currentCoverage         = getIntent().getExtras().getFloat("current");
        denominator             = 2;

        globalLog("latitudelongi :: "+currentCoverage);

        seekbar.setMax(100000);
        int progressX = (int)((float)Math.ceil((currentCoverage*1000)/denominator));
        current_range.setText((currentCoverage*1000)+"meters");
        seekbar.setProgress(progressX);



        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                float progressX = (seekbar.getProgress()/denominator);
                intent.putExtra("radius",(progressX/1000));
                setResult(LOCATION_RADIUS,intent);
                finish();
            }
        });


    }

    void intializeSeekBar()
    {
        seekbar.setEnabled(true);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if(circle!=null){
                    circle.remove();
                }
                int progressX = (int)((float)Math.ceil(seekbar.getProgress()/denominator));
                globalLog("mapready :: "+progressX);
                current_range.setText(progressX+"meters");
                circle = gmap.addCircle(new CircleOptions()
                        .center(new LatLng(latitude, longitude))
                        .radius(progressX)
                        .strokeColor(Color.parseColor("#22aa0000"))
                        .fillColor(Color.parseColor("#330000aa")));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        try{mapView.onDestroy();}catch (Exception e){}
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        globalLog("mapready :: "+googleMap);
        gmap = googleMap;
        LatLng coordinates = new LatLng(latitude, longitude);
        gmap.addMarker(new MarkerOptions().position(coordinates).title("Site"));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
        mapView.onResume();
        gmap.getUiSettings().setAllGesturesEnabled(true);
        if(currentCoverage > 0)
        {
            int progressX = (int)((float)Math.ceil((currentCoverage*1000)/denominator));
            circle = gmap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(progressX)
                    .strokeColor(Color.parseColor("#22aa0000"))
                    .fillColor(Color.parseColor("#330000aa")));
        }

        intializeSeekBar();


    }
}
