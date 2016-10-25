package com.nao20010128nao.Wisecraft.misc.collector;

import android.*;
import android.content.*;
import android.location.*;
import android.support.v4.content.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.*;

import android.Manifest;

/**
 * Class which managing whether we are in the night or not.
 * https://github.com/android/platform_frameworks_support/blob/master/v7/appcompat/src/android/support/v7/app/TwilightManager.java
 */
public class GeolocationLoader {
	public static final GeolocationLoader INSTANCE_WITH_APPLICATION=new GeolocationLoader(TheApplication.instance);
	
	private final Context mContext;
	private final LocationManager mLocationManager;

	public GeolocationLoader(Context context) {
		mContext = context;
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	public Location getLastKnownLocation() {
		Location coarseLoc = null;
		Location fineLoc = null;

		int permission = PermissionChecker.checkSelfPermission(mContext,
															   Manifest.permission.ACCESS_COARSE_LOCATION);
		if (permission == PermissionChecker.PERMISSION_GRANTED) {
			coarseLoc = getLastKnownLocationForProvider(LocationManager.NETWORK_PROVIDER);
		}

		permission = PermissionChecker.checkSelfPermission(mContext,
														   Manifest.permission.ACCESS_FINE_LOCATION);
		if (permission == PermissionChecker.PERMISSION_GRANTED) {
			fineLoc = getLastKnownLocationForProvider(LocationManager.GPS_PROVIDER);
		}

		if (fineLoc != null && coarseLoc != null) {
			// If we have both a fine and coarse location, use the latest
			return fineLoc.getTime() > coarseLoc.getTime() ? fineLoc : coarseLoc;
		} else {
			// Else, return the non-null one (if there is one)
			return fineLoc != null ? fineLoc : coarseLoc;
		}
	}
	
	public QuickLocation getLastKnownLocationForSerialize(){
		Location loc=getLastKnownLocation();
		if(loc==null)return null;
		QuickLocation qloc=new QuickLocation();
		qloc.longitude=loc.getLongitude();
		qloc.latitude=loc.getLatitude();
		qloc.altitude=loc.getAltitude();
		qloc.accuracy=loc.getAccuracy();
		qloc.bearing=loc.getBearing();
		qloc.speed=loc.getSpeed();
		return qloc;
	}

	private Location getLastKnownLocationForProvider(String provider) {
		if (mLocationManager != null) {
			try {
				if (mLocationManager.isProviderEnabled(provider)) {
					return mLocationManager.getLastKnownLocation(provider);
				}
			} catch (Exception e) {
				Log.d("GeolocationLoader", "Failed to get last known location", e);
			}
		}
		return null;
	}
	
	public static class QuickLocation{
		public double longitude,latitude,altitude;
		public float accuracy,bearing,speed;
	}
}
