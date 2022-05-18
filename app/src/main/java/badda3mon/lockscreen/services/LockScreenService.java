package badda3mon.lockscreen.services;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import badda3mon.lockscreen.activities.LockActivity;
import badda3mon.lockscreen.additional.PersistenceStorage;

public class LockScreenService extends JobIntentService {
	private static final String TAG = "LockScreenService";
	public static final int JOB_ID = 0;
	public static void enqueueWork(Context context, Intent intent) {
		PersistenceStorage.init(context);

		enqueueWork(context, LockScreenService.class, JOB_ID, intent);
	}
	@Override
	protected void onHandleWork(@NonNull Intent intent) {
		try {
			String action = intent.getAction();

			if (action.equals(Intent.ACTION_SCREEN_ON)){
				Log.d(TAG,"Screen ON!");
			} else if (action.equals(Intent.ACTION_SCREEN_OFF)){
				if (isCallActive(this)){
					Log.e(TAG,"Call active!");
				} else {
					int savedLevel = PersistenceStorage.getIntProperty("level");

					if (savedLevel != -1){
						Intent startIntent = new Intent(this, LockActivity.class);
						startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

						if (Looper.myLooper() == null) Looper.prepare();
						startActivity(startIntent);
					} else Log.e(TAG,"Level not selected!");
				}
			} else {
				Log.d(TAG,"Screen UNLOCKED!");
			}
		} catch (Exception e){
			Log.e(TAG,"Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static boolean isCallActive(Context context){
		TelecomManager manager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

		boolean isCall = manager.isInCall();

		Log.e(TAG,"isCallActive: " + isCall);

		return isCall;
	}
}
