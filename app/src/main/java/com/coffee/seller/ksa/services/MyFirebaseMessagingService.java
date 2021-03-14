package com.coffee.seller.ksa.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import org.json.JSONObject;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.coffee.seller.ksa.Orders;
import com.coffee.seller.ksa.SelectDeliveryGuy;
import com.utils.Functions;
import com.utils.SharePrefsEntry;

import static com.coffee.seller.ksa.BaseActivity.globalLog;

/**
 * Created by mac on 1/08/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



        if (remoteMessage == null)
            return;

        if (remoteMessage.getNotification() != null)
        {
            handleNotification(remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().toString().length() > 0)
        {
            try
            {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                globalLog("notification :: "+json);
                handleDataMessage(json);
            } catch (Exception e) {
                globalLog("notification :: err "+e.getMessage());
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message)
    {

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
        {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(MyFirebaseInstanceIDService.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
        else
        {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject jobj) {
        Functions function              = new Functions(getApplicationContext());
        SharePrefsEntry sp              = new SharePrefsEntry(getApplicationContext());
        try
        {
            JSONObject json = new JSONObject(jobj.getString("data"));
            String title    = json.getString("title");
            String msg      = json.getString("message");
            String ts       = json.getString("timestamp");
            JSONObject jo   = new JSONObject(json.getString("payload"));
            Intent intent   = null;
            try
            {
                String type  = jo.getString("type");
                int status   = jo.getInt("status");
                if(type.equals("del_status"))
                {
                    if(status == 0)
                    {
                        intent      = new Intent(getApplicationContext(), SelectDeliveryGuy.class);
                        intent.putExtra("orderid",jo.getString("orderid"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }
                    else
                    {
                        intent      = new Intent();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try
            {
                String type  = jo.getString("type");
                String stData   = jo.toString();
                if (type.equals("new_order")){
                    intent          = new Intent(getApplicationContext(), Orders.class);
                    intent.putExtra("data",stData);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try
            {
                jo              = new JSONObject(jo.getString("json_data"));
                String stData   = jo.toString();

                intent          = new Intent(getApplicationContext(), Orders.class);
                intent.putExtra("data",stData);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            }catch (Exception e){
                e.printStackTrace();
            }



            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
            notificationUtils.showNotificationMessage(title,msg,ts,intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
//    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
//    }
}