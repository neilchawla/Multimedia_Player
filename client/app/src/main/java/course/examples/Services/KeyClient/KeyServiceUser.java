package course.examples.Services.KeyClient;

import android.app.Activity ;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import course.examples.Services.KeyCommon.KeyGenerator;

public class KeyServiceUser extends Activity implements AdapterView.OnItemSelectedListener {

	private KeyGenerator connectService;
	private boolean isBound = false;
	private boolean isPlaying = false;
	private int audioNumber;

	private Spinner spinner, spinner2;
	private ArrayAdapter<CharSequence> adapter, adapter2;

	private Button playButton, pauseButton, stopButton;

	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		spinner = findViewById(R.id.audioNumberDropdown);
		adapter = ArrayAdapter.createFromResource(this, R.array.audio_dropdown,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		spinner2 = findViewById(R.id.imageNumberDropdown);
		adapter2 = ArrayAdapter.createFromResource(this, R.array.image_dropdown,
				android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);
		spinner2.setOnItemSelectedListener(this);

		imageView = findViewById(R.id.imageView);

		playButton = findViewById(R.id.playButton);
		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(isBound) {
						connectService.playAudio(audioNumber);
						playButton.setEnabled(false);
						pauseButton.setEnabled(true);
						stopButton.setEnabled(true);
						isPlaying = true;
					}
					else
						Log.i("PlayButton", "Service was not bound");
				} catch (RemoteException e) {
					Log.e("PlayButton", e.toString());
				}
			}
		});

		pauseButton = findViewById(R.id.pauseButton);
		pauseButton.setEnabled(false);
		pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(isBound) {
						connectService.pauseAudio();
						playButton.setEnabled(true);
						pauseButton.setEnabled(false);
						isPlaying = false;
					}
					else
						Log.i("PauseButton", "Service was not bound");
				} catch (RemoteException e) {
					Log.e("PauseButton", e.toString());
				}
			}
		});

		stopButton = findViewById(R.id.stopButton);
		stopButton.setEnabled(false);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(isBound) {
						connectService.stopAudio();
						playButton.setEnabled(true);
						pauseButton.setEnabled(false);
						stopButton.setEnabled(false);
						isPlaying = false;
					}
					else
						Log.i("PauseButton", "Service was not bound");
				} catch (RemoteException e) {
					Log.e("PauseButton", e.toString());
				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		if(!isBound) {
			boolean b = false;
			Intent i = new Intent(KeyGenerator.class.getName());

			ResolveInfo info = getPackageManager().resolveService(i, 0);
			if(info == null) {
				Log.i("OnResume", "ResolveInfo is null");
			}

			i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

			b = bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
			if(b)
				Log.i("OnResume", "bindService() succeeded");
			else
				Log.i("OnResume", "bindService() failed");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			connectService = KeyGenerator.Stub.asInterface(service);
			isBound = true;
			Log.i("ServiceConnection", "Service Connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			connectService = null;
			isBound = false;
		}
	};

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		Log.i("OnItemSelected", "Selected: " + pos);

		if(parent.getId() == R.id.audioNumberDropdown) {
			try {
				audioNumber = pos;
			} catch (Exception e) {
			}
		} else {
			if(isBound) {
				try {
					imageView.setImageBitmap(connectService.sendImage(pos));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {

	}
}

