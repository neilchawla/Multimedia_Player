package course.examples.Services.KeyService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import course.examples.Services.KeyCommon.KeyGenerator;

public class KeyGeneratorImpl extends Service {

	private MediaPlayer mediaPlayer;
	private boolean isPlaying = false;
	private boolean isPaused = false;
	private int pausePos;
	private int startID;

	private Notification notification;

	final static ArrayList<Integer> audioClips = new ArrayList<>(
			Arrays.asList(R.raw.a1, R.raw.a2, R.raw.a3, R.raw.a4, R.raw.a5)
	);

	final static ArrayList<Integer> images = new ArrayList<>(
			Arrays.asList(R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4,
					R.drawable.img5)
	);


	@Override
	public void onCreate() {
		super.onCreate();
	}


	private final KeyGenerator.Stub myBinder = new KeyGenerator.Stub() {
		@Override
		public synchronized void playAudio(int number) {
			try {
				createNotificationChannel();

				notification = new NotificationCompat.Builder(getApplicationContext(), "musicPlayer")
						.setSmallIcon(android.R.drawable.ic_media_play)
						.setOngoing(true).setContentTitle("Music Playing")
						.setTicker("Music is playing")
						.build();

				startForeground(1, notification);

				if(isPaused) {
					mediaPlayer.seekTo(pausePos);
					mediaPlayer.start();
					isPaused = false;
				} else {
					mediaPlayer = MediaPlayer.create(getApplicationContext(), audioClips.get(number));
					mediaPlayer.setLooping(false);
					mediaPlayer.start();
					isPlaying = true;
					Log.i("playAudio", "Playing Audio: " + number);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public synchronized void pauseAudio() {
			if(mediaPlayer.isPlaying())
				mediaPlayer.pause();
			pausePos = mediaPlayer.getCurrentPosition();
			isPaused = true;
		}

		@Override
		public synchronized void stopAudio() {
			mediaPlayer.stop();
			mediaPlayer.reset();
			isPaused = false;
			isPlaying = false;
			Log.i("stopAudio", "Stopping audio");
		}

		@Override
		public synchronized Bitmap sendImage(int number) {
			Bitmap resource = BitmapFactory.decodeResource(getApplicationContext().getResources(), images.get(number));
			return resource;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("onUnbind", "Unbinding service");
		stopService(new Intent(this, this.getClass()));
		stopSelf();
		mediaPlayer.stop();
		return false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(null != mediaPlayer) {
			startID = startId;
			if(mediaPlayer.isPlaying())
				mediaPlayer.seekTo(0);
			else
				mediaPlayer.start();
		}

		return START_STICKY;
	}

	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = "Music player notification";
			String description = "The channel for music player notifications";
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel("musicPlayer", name, importance);
			channel.setDescription(description);
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

}


