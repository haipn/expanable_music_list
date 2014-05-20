package com.android.guide.expandablelistview;

import java.util.Date;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PedometerActivity extends Activity implements
		StepService.ICallback {

	private static final String TAG = "PedometerActivity";
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;
	private Utils mUtils;
	private TextView mTvCurrent;
	private TextView mTvMax;
	private TextView mTvDate;
	private Button mBtnReset;
	private Button mBtnStartStop;
	/**
	 * True, when service is running.
	 */
	private boolean mIsRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mUtils = Utils.getInstance();
		init();

	}

	private void init() {
		mTvCurrent = (TextView) findViewById(R.id.tvCurrent);
		mTvMax = (TextView) findViewById(R.id.tvMax);
		mTvDate = (TextView) findViewById(R.id.tvTime);
		mBtnReset = (Button) findViewById(R.id.btnReset);
		mBtnReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onResetClick(v);

			}
		});
		mBtnStartStop = (Button) findViewById(R.id.btnStartStop);
		mBtnStartStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onPausedClick(v);
			}
		});
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mPedometerSettings = new PedometerSettings(mSettings);

		// Read from preferences if the service was running on the last onPause
		mIsRunning = mPedometerSettings.isServiceRunning();
		if (mIsRunning)
			mBtnStartStop.setText("Stop");
		else 
			mBtnStartStop.setText("Start");
		// Start the service if this is considered to be an application start
		// (last onPause was long ago)
		if (!mIsRunning && mPedometerSettings.isNewStart()) {
			startStepService();
			bindStepService();
		} else if (mIsRunning) {
			bindStepService();
		}

		mPedometerSettings.clearServiceRunning();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		if (mIsRunning) {
			unbindStepService();
		}
		mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
		super.onPause();
	}

	private ViewGroup mMenu;

	public void onPausedClick(View v) {
		if (mIsRunning) {
			((TextView) v).setText("Start");
			unbindStepService();
			stopStepService();
		} else {
			((TextView) v).setText("Stop");
			startStepService();
			bindStepService();
		}
	}

	public void onResetClick(View v) {
		if (mIsRunning) {
			unbindStepService();
			stopStepService();
			mBtnStartStop.setText("Start");

		}
		mIsRunning = false;
		mTvCurrent.setText("0");
		mTvMax.setText("0");
	}

	private StepService mService;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();
			mService.registerCallback(PedometerActivity.this);
			mService.reloadSettings();
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	private void startStepService() {
		if (!mIsRunning) {
			Log.i(TAG, "[SERVICE] Start");
			mIsRunning = true;
			startService(new Intent(PedometerActivity.this, StepService.class));
		}
	}

	private void bindStepService() {
		Log.i(TAG, "[SERVICE] Bind");
		bindService(new Intent(PedometerActivity.this, StepService.class),
				mConnection, Context.BIND_AUTO_CREATE
						+ Context.BIND_DEBUG_UNBIND);
	}

	private void unbindStepService() {
		Log.i(TAG, "[SERVICE] Unbind");
		unbindService(mConnection);
	}

	private void stopStepService() {
		Log.i(TAG, "[SERVICE] Stop");
		if (mService != null) {
			Log.i(TAG, "[SERVICE] stopService");
			stopService(new Intent(PedometerActivity.this, StepService.class));
		}
		mIsRunning = false;
	}

	@Override
	public void sensorChanged(double value) {
		// Log.d("haipn", "sensor changeed:" + value);
		mTvCurrent.setText(value + "");
		String date = DateFormat.format("EEE hh:mm:ss", new Date()).toString();
		mTvDate.setText(date);
	}

	@Override
	public void maxSensorChanged(double value) {
		Log.d("haipn", "max sensor changeed:" + value);
		mTvMax.setText(value + "");
	}

}
