package com.coffee.seller.ksa;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.tami.seller.ksa.R;
import com.utils.ConnectionClass;
import com.utils.Functions;
import com.utils.SharePrefsEntry;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity implements View.OnTouchListener {
    ConnectionClass cc;
    static SharePrefsEntry sp;
    Functions fun;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    public static int screenwidth, screenheight;
    RelativeLayout cart_rl;
    View rootView;
    public static FragmentManager fragmentManager;
    public JSONObject addressJSON;

    public DrawerLayout mDrawer;
    public static Dialog spinner;
    SwitchCompat switchComb;

    public static String currentLocSelected, currentCatSelected, currentSubCatSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cc = new ConnectionClass(this);
        sp = new SharePrefsEntry(this);
        fun = new Functions(this);


        fragmentManager = getSupportFragmentManager();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenheight = displayMetrics.heightPixels;
        screenwidth = displayMetrics.widthPixels;

        rootView = getWindow().getDecorView().getRootView();

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        currentLocSelected = null;
        currentCatSelected = null;
        currentSubCatSelected = null;


    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        } catch (Exception e) {
        }


        sp.loadLocalLanguage();


    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_entry_anim, R.anim.activity_exit_anim);
    }

    public ConnectionClass getConnection() {
        return cc;
    }

    public static void globalLog(String msg) {
        Log.e("AndroidRuntime", msg);
    }


    public SharePrefsEntry getSharePrefs() {
        return sp;
    }

    public Functions getFunction() {
        return fun;
    }

    public String getIntialDefault() {
        if (sp.getLanguage().equals(""))
            return Locale.getDefault().getLanguage();
        else
            return null;
    }

    public static String getDefaultLang() {
        if (sp.getLanguage().equals(""))
            return "_" + Locale.getDefault().getLanguage();
        else
            return "_" + sp.getLanguage();
    }

    public static String getDefLang(Context ctx) {
        String abc = "";
        if (sp == null)
            sp = new SharePrefsEntry(ctx);
        return getDefaultLang();
    }


    public static void appLog(String msg) {
        Log.e("AndroidRuntime", msg);
    }


    public BaseActivity getBaseActivity() {
        return this;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                int y = (int) event.getY();
                int x = (int) event.getX();

                cart_rl.getLayoutParams().height = 20;
                cart_rl.getLayoutParams().width = 20;

                cart_rl.layout(x, y, x + 48, y + 48);
                break;
        }
        return true;
    }


    public SharePrefsEntry getSharePref() {
        return sp;
    }

    public void toggleMenu(View v) {
        if (getSharePref().getLanguage() == "ar") {
            try {
                if (mDrawer.isDrawerOpen(Gravity.RIGHT))
                    mDrawer.closeDrawer(Gravity.RIGHT);
                else
                    mDrawer.openDrawer(Gravity.RIGHT);
            } catch (Exception e) {
                if (mDrawer.isDrawerOpen(Gravity.LEFT))
                    mDrawer.closeDrawer(Gravity.LEFT);
                else
                    mDrawer.openDrawer(Gravity.LEFT);
            }

        } else {
            try {
                if (mDrawer.isDrawerOpen(Gravity.LEFT))
                    mDrawer.closeDrawer(Gravity.LEFT);
                else
                    mDrawer.openDrawer(Gravity.LEFT);
            } catch (Exception e) {
                if (mDrawer.isDrawerOpen(Gravity.RIGHT))
                    mDrawer.closeDrawer(Gravity.RIGHT);
                else
                    mDrawer.openDrawer(Gravity.RIGHT);
            }

        }

    }

    public void menuClicked(View v) {
        int tag = -1;
        try {
            tag = Integer.parseInt(v.getTag().toString());
        } catch (Exception e) {
        }

        if (tag == 1)
            getFunction().logout();
        else if (tag == 2)
            gotoAccountPage();
        else if (tag == 3) {
            if (getDefaultLang().contains("_ar"))
                getSharePrefs().setLanguage("en");
            else
                getSharePrefs().setLanguage("ar");


            getSharePrefs().loadLocalLanguage();

            recreate();
        } else if (tag == 4) {
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setChooserTitle("Chooser title")
                    .setText("http://play.google.com/store/apps/details?id=" + this.getPackageName())
                    .startChooser();
        } else if (tag == 10) {
            gotoOrders(null);
        } else if (tag == 11) {
            gototLocation(null);
        } else if (tag == 12) {
            gotoCategories(null);
        } else if (tag == 13) {
            gotoSubCategories(null);
        } else if (tag == 14) {
            gotoProducts(null);
        } else if (tag == 202) {
            getFunction().confirm(CatSubProducts.jsonObject, 1);
        } else if (tag == 563) {
            Intent intent = new Intent(this, GotoOffers.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (tag == 37) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dalilalqahwa.com/app/help.php?ref=t_" + sp.getUid()));
            startActivity(browserIntent);
        }

        try {
            mDrawer.closeDrawer(Gravity.RIGHT);
        } catch (Exception e) {
        }
        try {
            mDrawer.closeDrawer(Gravity.LEFT);
        } catch (Exception e) {
        }

    }

    public void gotoOrders(View v) {
        Intent intent = new Intent(this, Orders.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void gotoAccountPage() {
        Intent intent = new Intent(this, EditProfile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void gototLocation(View v) {
        Intent intent = new Intent(this, Locations.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void addLocations(View v) {
        Intent intent = new Intent(this, LocationsManipulate.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void gotoCategories(View v) {
        Intent intent = new Intent(this, Categories.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void addCategories(View v) {
        Intent intent = new Intent(this, CategoriesMani.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void gotoSubCategories(View v) {
        Intent intent = new Intent(this, SubCategories.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void addSubCategories(View v) {
        Intent intent = new Intent(this, SubCategoriesMani.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void gotoProducts(View v) {
        Intent intent = new Intent(this, Products.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void addProducts(View v) {
        Intent intent = new Intent(this, ProductsMani.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void finishMeReload(Activity activity) {
        getSharePref().setBooleanValue("needreload", true);
        activity.finish();
    }

    public void goBack(View v) {
        this.finish();
    }

    @Override
    public void onBackPressed() {

        try {
            if (mDrawer.isDrawerOpen(Gravity.LEFT) || mDrawer.isDrawerOpen(Gravity.RIGHT)) {
                if (getDefLang(this).equals("_en"))
                    mDrawer.closeDrawer(Gravity.LEFT);
                else
                    mDrawer.closeDrawer(Gravity.LEFT);

            } else
                super.onBackPressed();
        } catch (Exception e) {
            super.onBackPressed();
        }

    }

    public static void callActivity(Context context, Intent intent) {
        context.startActivity(intent);
        try {
            ((Activity) context).overridePendingTransition(R.anim.activity_entry_anim, R.anim.activity_exit_anim);
        } catch (Exception e) {
        }
    }

    public void changeAppLocale() {
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

}
