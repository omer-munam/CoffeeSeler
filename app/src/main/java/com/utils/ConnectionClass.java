package com.utils;


import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.coffee.seller.ksa.BaseActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConnectionClass {

    Context context;
    //	public static String BASE_URL		= "http://tamwinati.touran.sa/";
    public static String BASE_URL = "http://dalilalqahwa.com/app/";


    public static String SERVER_URL = BASE_URL + "_seller_2.php";
    public static String WEB_SIGNUP = "signup";
    public static String WEB_AJAX = "ajax";
    public static String WEB_AJAX_UID = "ajaxUsername";
    public static String WEB_RESET = "resetPassword";
    public static String PLACEHOLDER = BASE_URL + "placeholder.png";
    public static String THUMB_URL = BASE_URL + "images/";

    SharePrefsEntry sp;

    public ConnectionClass(Context ctx) {
        this.context = ctx;
        sp = new SharePrefsEntry(context);
    }

    public String sendPostData(String para, File file) {
        String result = "";
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = fmt.format(new Date());
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(SERVER_URL);
            httpPost.addHeader("currentdate", dateString);
            try {
                httpPost.addHeader("language", BaseActivity.getDefLang(context));
            } catch (Exception e) {
            }
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("txt", new StringBody(para));

            if (file != null)
                entity.addPart("file", new FileBody(file));

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);
            HttpEntity httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity);
            Log.e("AndroidRuntime", "connection :: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }


    public String getEncryptedString(String obj) {

        for (int i = 0; i < 10; i++) {
            try {
                byte[] data = obj.getBytes("UTF-8");
                obj = Base64.encodeToString(data, Base64.DEFAULT);
            } catch (Exception e) {
            }

        }
        return obj;
    }

}
