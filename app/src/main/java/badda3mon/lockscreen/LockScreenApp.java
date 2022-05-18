package badda3mon.lockscreen;

import android.app.Application;
import android.util.Log;
import badda3mon.lockscreen.additional.PersistenceStorage;
import com.yandex.mobile.ads.common.MobileAds;

public class LockScreenApp extends Application {
	private static final String TAG = "LockScreenApp";
	@Override
	public void onCreate() {
		super.onCreate();

		PersistenceStorage.init(this);
		MobileAds.initialize(this, () -> Log.d(TAG, "Yandex ADS initialized"));
	}
}
