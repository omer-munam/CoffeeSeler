package com.coffee.seller.ksa;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.tami.seller.ksa.R;
import com.utils.MarshMallowPermission;

public class Splash extends BaseActivity{

    public static Splash splash;
    boolean allPermissionSet;
    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash              = this;
        loadActivity();

//        changeAppLocale();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    public void checkPermissions() {

        globalLog("abc");
        if (!marshMallowPermission.checkPermissionForCamera())
        {
            globalLog("def");
            marshMallowPermission.requestPermissionForCamera();
        }
        else
        {
            globalLog("ghi");
            if (!marshMallowPermission.checkPermissionForExternalStorage())
            {
                globalLog("jkl");
                marshMallowPermission.requestPermissionForExternalStorage();
            }
            else
            {
                globalLog("mno");
                if (!marshMallowPermission.checkPermissionForLocation())
                {
                    globalLog("pqr");
                    marshMallowPermission.requestPermissionForLocation();
                }
                else
                {
                    globalLog("stu");
                    allPermissionSet = true;
                    loadActivity();
                }

            }

        }
    }


    void loadActivity()
    {
        if(allPermissionSet)
        {
            new CountDownTimer(3000,1000)
            {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    Intent intent = new Intent(getBaseActivity(),Locations.class);

                    if(!getSharePrefs().isLoggedin())
                        intent    = new Intent(getBaseActivity(),SignInActivity.class);
//                        intent    = new Intent(getBaseActivity(),SignupSignin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }.start();
        }

    }

}
