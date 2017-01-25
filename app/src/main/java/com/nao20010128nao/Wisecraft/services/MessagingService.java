package com.nao20010128nao.Wisecraft.services;

import android.util.*;
import com.google.firebase.messaging.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import com.nao20010128nao.Wisecraft.*;
import java.util.*;
import android.widget.*;

public class MessagingService extends FirebaseMessagingService {

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		Log.d("MessagingService", "From: " + remoteMessage.getFrom());
		if (remoteMessage.getData().size() > 0) {
			Log.d("MessagingService", "Message data payload: " + remoteMessage.getData());
		}
		if (remoteMessage.getNotification() != null) {
			Log.d("MessagingService", "Message Notification Body: " + remoteMessage.getNotification().getBody());
		}
		String title=remoteMessage.getNotification().getTitle();
		String body=remoteMessage.getNotification().getBody();
		String tag=remoteMessage.getNotification().getTag();
		List<String> notificationTags=Arrays.asList(BuildConfig.NOTIFICATION_TAGS);
		if(!notificationTags.contains(tag)){
			return;
		}
		if(getPackageName().endsWith(".alpha")){
			Toast.makeText(this,title+"\n"+body,Toast.LENGTH_LONG).show();
		}
	}
}
