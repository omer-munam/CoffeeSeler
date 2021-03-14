package com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.coffee.seller.ksa.services.MyFirebaseInstanceIDService;

import org.json.JSONObject;

import java.util.Locale;

import static com.coffee.seller.ksa.BaseActivity.globalLog;

/**
 * Created by mac on 23/09/2017.
 */

public class SharePrefsEntry {

    Context context;
    SharedPreferences prefs , pushPref;
    SharedPreferences.Editor editor;


    public static final String UID          = "id";
    public static final String NAME         = "title_en";
    public static final String THUMB        = "images";
    public static final String EMAIL        = "email";
    public static final String PASSWORD     = "password";
    public static final String PHONE 		= "phonenumber";
    public static final String NAME_AR      = "title_ar";
    public static final String DATA         = "data";
    public static final String LANG         = "language";


    public SharePrefsEntry(Context cont)
    {
        context = cont;
        prefs   = context.getSharedPreferences("applicationta",Context.MODE_PRIVATE);
        editor  = prefs.edit();
        pushPref= context.getSharedPreferences(MyFirebaseInstanceIDService.SHARED_PREF, 0);
    }


    public String getDeviceId()
    {
        return "";
    }

    public String getUid()
    {
        return prefs.getString(UID,"");
    }

    public void setLanguage(String value)
    {
        editor.putString(LANG,value);
        editor.commit();
    }

    public void loadLocalLanguage()
    {
        String lang             = getLanguage();
        Locale locale           = new Locale(lang);
        Locale.setDefault(locale);

        globalLog("local :: "+lang);

        Resources res           = context.getResources();
        Configuration config    = new Configuration(res.getConfiguration());
        config.locale           = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public String getLanguage()
    {
        String lang = prefs.getString(LANG,"");
        if(lang.equals(""))
        {
            lang    = Locale.getDefault().getLanguage();
        }

        return lang;
    }

    public void setGeneralString(String key,String value)
    {
        editor.putString(key,value);
        editor.commit();
    }

    public String getGeneralString(String key)
    {
        return prefs.getString(key,"");
    }






    public void setKeyValInt(String key, int val)
    {
        editor.putInt(key, val);
        editor.commit();
    }

    public int getKeyValInt(String key)
    {
        return prefs.getInt(key,-1);
    }


    public void setKeyValLong(String key, long val)
    {
        editor.putLong(key, val);
        editor.commit();
    }




    public void setBooleanValue(String key , boolean b)
    {
        editor.putBoolean(key, b);
        editor.commit();
    }


    public boolean getBooleanValue(String key)
    {
        return prefs.getBoolean(key,false);
    }

    public long getKeyValLong(String key)
    {
        return prefs.getLong(key,-1l);
    }

    public void setKeyValString(String key, String val)
    {
        editor.putString(key, val);
        editor.commit();
    }



    public String getPushID()
    {
        return pushPref.getString("regId", null);
    }


    public boolean onFinish(JSONObject jobj)
    {
        try
        {
            editor.putString(DATA, jobj + "");
            editor.commit();
        }catch(Exception e){
            e.printStackTrace();
        }

        try
        {
            editor.putString(THUMB, jobj.optString(THUMB));
            editor.putString(UID, jobj.optString(UID));
            editor.putString(PHONE, jobj.optString(PHONE));
            editor.putString(NAME, jobj.optString(NAME));
            editor.putString(NAME_AR,jobj.optString(NAME_AR));
            editor.putString(PASSWORD, jobj.optString(PASSWORD));
            editor.putString(EMAIL, jobj.optString(EMAIL));
            editor.commit();

            return true;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
            return true;
        }
    }

    public String getEmail()
    {
        return prefs.getString(EMAIL,"");
    }
    public String getNameEn()
    {
        return prefs.getString(NAME,"");
    }


    public String getNameAr()
    {
        return prefs.getString(NAME_AR,"");
    }

    public String getPhone()
    {
        return prefs.getString(PHONE,"");
    }

    public String getProfilePic() {
        String pp = prefs.getString(THUMB,"");
        return getImageUrl(pp);
    }

    public String getImageUrl(String s)
    {
        String pp = s;
        try
        {
            if(pp.equals(""))
                pp = ConnectionClass.PLACEHOLDER;
            else
                pp = ConnectionClass.THUMB_URL+pp;
        }catch(Exception e){
            pp = ConnectionClass.PLACEHOLDER;
        }
        return pp;
    }

    public boolean isLoggedin()
    {
        try
        {
            String s=prefs.getString(UID,null);
            int i=Integer.parseInt(s);
            if(i>0)
                return true;
            else
                return false;
        } catch (NumberFormatException e) {
            return false;
        }


    }

    public void logout()
    {
        editor.clear();
        editor.commit();
    }

    public void updatePassword(String s) {
        editor.putString(PASSWORD, s);
        editor.commit();
    }

    public String getData() {
        return prefs.getString(DATA,"");
    }
}
