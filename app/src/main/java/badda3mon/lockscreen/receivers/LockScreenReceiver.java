package badda3mon.lockscreen.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import badda3mon.lockscreen.services.LockScreenService;

public class LockScreenReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		LockScreenService.enqueueWork(context, intent);
	}
}
