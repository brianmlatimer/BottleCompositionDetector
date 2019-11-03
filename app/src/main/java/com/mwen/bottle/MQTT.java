package com.mwen.bottle;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MQTT extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        boolean isGlass = true;
        String buffer = "" + remoteMessage.getData();
        JSONObject obj = null;
        try {
            obj = new JSONObject(buffer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String key = "type";
        String msg_type = null;
        try {
            msg_type = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert msg_type != null;
        if (msg_type.equals("glass")) {
            Intent i = new Intent(MQTT.this, GlassActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else {
            Intent i = new Intent(MQTT.this, PlasticActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
}
