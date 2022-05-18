package badda3mon.lockscreen.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import badda3mon.lockscreen.services.LockScreenForegroundService;

public class BootCompletedReceiver extends BroadcastReceiver {
	private static final String TAG = "BootCompletedReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
			Intent foregroundServiceIntent = new Intent(context, LockScreenForegroundService.class);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				context.startForegroundService(foregroundServiceIntent);
			} else context.startService(foregroundServiceIntent);

			Log.d(TAG,"Boot completed receive!");
		}
	}
}
