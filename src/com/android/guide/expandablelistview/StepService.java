/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.android.guide.expandablelistview;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application. The {@link StepServiceController} and
 * {@link StepServiceBinding} classes show how to interact with the service.
 * 
 * <p>
 * Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service. This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
public class StepService extends Service {
	private static final String TAG = "StepService";
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;
	private SharedPreferences mState;
	private SharedPreferences.Editor mStateEditor;
	private Utils mUtils;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	// private StepBuzzer mStepBuzzer; // used for debugging
	double maxValue = 0.0;
	private PowerManager.WakeLock wakeLock;
	private NotificationManager mNM;
	private long mLastUpdate;
	private SensorEventListener mStepDetector = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {

			Sensor sensor = event.sensor;
			synchronized (this) {
				if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
				} else {
					int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1
							: 0;
					if (j == 1) {
						double vSum = 0;
						float vx = event.values[0];
						float vy = event.values[1];
						float vz = event.values[2];
						vSum = Math.sqrt(Math.abs(vx * vy + vx * vz + vy * vz));

						if (vSum > maxValue) {
							maxValue = vSum;
						}
						if (System.currentTimeMillis() - mLastUpdate > 1000) {
							mLastUpdate = System.currentTimeMillis();
							if (mCallback != null) {
								mCallback.sensorChanged(vSum);
								mCallback.maxSensorChanged(maxValue);
							}
						}
					}

				}

			}

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class StepBinder extends Binder {
		StepService getService() {
			return StepService.this;
		}
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "[SERVICE] onCreate");
		super.onCreate();

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();

		// Load settings
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mPedometerSettings = new PedometerSettings(mSettings);
		mState = getSharedPreferences("state", 0);

		mUtils = Utils.getInstance();
		mUtils.setService(this);

		acquireWakeLock();

		// Start detecting
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		registerDetector();

		// Register our receiver for the ACTION_SCREEN_OFF action. This will
		// make our receiver
		// code be called whenever the phone enters standby mode.
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

		// Used when debugging:
		// mStepBuzzer = new StepBuzzer(this);
		// mStepDetector.addStepListener(mStepBuzzer);

		// Start voice
		reloadSettings();

		// Tell the user we started.
		Toast.makeText(this, "Start service", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "[SERVICE] onStart");
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "[SERVICE] onDestroy");

		// Unregister our receiver.
		unregisterReceiver(mReceiver);
		unregisterDetector();

		mNM.cancel(R.string.app_name);

		wakeLock.release();

		super.onDestroy();

		// Stop detecting
		// Tell the user we stopped.
		Toast.makeText(this, "Stop service", Toast.LENGTH_SHORT).show();
	}

	private void registerDetector() {
		mSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER /*
															 * | Sensor.
															 * TYPE_MAGNETIC_FIELD
															 * | Sensor.
															 * TYPE_ORIENTATION
															 */);
		mSensorManager.registerListener(mStepDetector, mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	private void unregisterDetector() {
		mSensorManager.unregisterListener(mStepDetector);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "[SERVICE] onBind");
		return mBinder;
	}

	/**
	 * Receives messages from activity.
	 */
	private final IBinder mBinder = new StepBinder();

	public interface ICallback {
		public void sensorChanged(double value);

		public void maxSensorChanged(double value);
	}

	private ICallback mCallback;

	public void registerCallback(ICallback cb) {
		Log.i(TAG, "[SERVICE] onRegistercallback");
		mCallback = cb;
		mLastUpdate = System.currentTimeMillis();
		mCallback.maxSensorChanged(maxValue);
		// mStepDisplayer.passValue();
		// mPaceListener.passValue();
	}

	public void reloadSettings() {
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		CharSequence text = getText(R.string.app_name);
		Notification notification = new Notification(R.drawable.ic_launcher,
				null, System.currentTimeMillis());
		notification.flags = Notification.FLAG_NO_CLEAR
				| Notification.FLAG_ONGOING_EVENT;
		Intent pedometerIntent = new Intent();
		pedometerIntent.setComponent(new ComponentName(this,
				PedometerActivity.class));
		pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				pedometerIntent, 0);
		notification.setLatestEventInfo(this, text, "running", contentIntent);

		mNM.notify(R.string.app_name, notification);
	}

	// BroadcastReceiver for handling ACTION_SCREEN_OFF.
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Check action just to be on the safe side.
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				// Unregisters the listener and registers it again.
				StepService.this.unregisterDetector();
				StepService.this.registerDetector();
				if (mPedometerSettings.wakeAggressively()) {
					wakeLock.release();
					acquireWakeLock();
				}
			}
		}
	};

	private void acquireWakeLock() {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		int wakeFlags;
		if (mPedometerSettings.wakeAggressively()) {
			wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP;
		} else if (mPedometerSettings.keepScreenOn()) {
			wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
		} else {
			wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
		}
		wakeLock = pm.newWakeLock(wakeFlags, TAG);
		wakeLock.acquire();
	}

}
