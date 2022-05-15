package badda3mon.lockscreen.services;

import android.app.*;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import badda3mon.lockscreen.R;
import badda3mon.lockscreen.activities.MainActivity;
import badda3mon.lockscreen.receivers.LockScreenReceiver;

public class LockScreenForegroundService extends Service {
	private static final String TAG = "LockScreenForeground";

	private boolean isReceiverRegistered = false;
	private LockScreenReceiver mLockScreenReceiver;

	private String mChannelID;
	private String mChannelName;

	private Handler mHandler = new Handler(Looper.getMainLooper());
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mChannelID = getResources().getString(R.string.notification_channel_id);
		mChannelName = getResources().getString(R.string.notification_channel_name);

		mLockScreenReceiver = new LockScreenReceiver();

		createNotificationChannel();
		startForegroundWithNotification();
		checkAndUpdateReceiver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startForegroundWithNotification();

		return super.onStartCommand(intent, flags, startId);
	}

	private void startForegroundWithNotification(){
		String msg = "LockScreen worked!";

		Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(this, mChannelID)
				.setOngoing(true)
				.setSmallIcon(R.drawable.ic_launcher_foreground)
				.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText(msg)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentIntent(pendingIntent).build();

		startForeground(1, notification);
	}

	private void checkAndUpdateReceiver(){
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_ANSWER);
		filter.addAction(Intent.ACTION_USER_PRESENT);

		try {
			registerReceiver(mLockScreenReceiver, filter);

			mHandler.postDelayed(() -> {
				checkAndUpdateReceiver();
			},3000);
		} catch (Exception e){
			Log.e(TAG,"Error: " + e.getMessage());
			e.printStackTrace();

			Toast.makeText(this, "[LockFGService]Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		Log.d(TAG,"checkAndUpdateReceiver()");
	}

	private void createNotificationChannel(){
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(mChannelID, mChannelName, NotificationManager.IMPORTANCE_HIGH);

			notificationManager.createNotificationChannel(channel);
		}
	}
}
