/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mendez.transportdysplay.networking;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.mendez.transportdysplay.activity.Inicio;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class FIRMessagingService extends FirebaseMessagingService {

    Handler mHandler;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
		Log.e("Msg", remoteMessage.getData().size()+"");
        Log.e("Msg", remoteMessage.getFrom()+"");
        Log.e("Msg", remoteMessage.getData()+"");
        Log.e("Msg", remoteMessage.getNotification().getBody()+"");

        //sendNotification(remoteMessage.getData());
        try {
            sendNotification(remoteMessage.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mHandler = new Handler();
    }

    private void sendNotification(Map<String, String> data) {
        Bundle msg = new Bundle();
        for (String key : data.keySet()) {
            Log.e(key, data.get(key));
            msg.putString(key, data.get(key));
        }


        Intent in=new Intent(getBaseContext(),Inicio.class);
        in.putExtra("bun",msg);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);




    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        this.stopForeground(true);
    }

}
