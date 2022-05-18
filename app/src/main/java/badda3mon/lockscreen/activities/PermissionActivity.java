package badda3mon.lockscreen.activities;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import badda3mon.lockscreen.R;
import badda3mon.lockscreen.receivers.MainAdminReceiver;

public class PermissionActivity extends AppCompatActivity {
	private static final String TAG = "PermissionActivity";

	private int mCountPerms = 0;

	private final int mMaxPermsCount = 6;

	private DevicePolicyManager mDevicePolicyManager;
	private ComponentName mComponentName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_permission);

		mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mComponentName = new ComponentName(this, MainAdminReceiver.class);

		Button okBtn = findViewById(R.id.permission_ok_btn);
		okBtn.setOnClickListener(v -> {
			mCountPerms = (hasNeedlePermissions(this) ? mMaxPermsCount : 0);

			requestAllPermissions();
		});
	}

	private void requestAllPermissions(){
		String[] perms = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE };

		ActivityCompat.requestPermissions(this, perms,123);

		if (!mDevicePolicyManager.isAdminActive(mComponentName)){
			String desc = getResources().getString(R.string.admin_permission_description);

			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, desc);

			startActivityForResult(intent,124);
		}

		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (!pm.isIgnoringBatteryOptimizations(getPackageName())){
				Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				intent.setData(Uri.parse("package:" + getPackageName()));

				startActivityForResult(intent,125);
			}

			if (!Settings.canDrawOverlays(this)){
				Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
						Uri.parse("package:" + getPackageName()));

				startActivityForResult(intent,126);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 124) mCountPerms++;
		else if (requestCode == 125) mCountPerms++;
		else if (requestCode == 126) mCountPerms++;

		if (mCountPerms == mMaxPermsCount) startNextActivity();

		Log.d(TAG,"mCountPerms: " + mCountPerms);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == 123 && mCountPerms != mMaxPermsCount){
			for (int i : grantResults){
				if (i != PackageManager.PERMISSION_GRANTED){
					Toast.makeText(this, R.string.denied_permission, Toast.LENGTH_SHORT).show();
				}
			}

			mCountPerms += 3;
		}

		if (mCountPerms == mMaxPermsCount) startNextActivity();

		Log.d(TAG,"mCountPerms: " + mCountPerms);
	}

	private void startNextActivity(){
		Intent intent = new Intent(this, SettingsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		startActivity(intent);
	}

	public static boolean hasNeedlePermissions(Context context){
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName cn = new ComponentName(context, MainAdminReceiver.class);

		PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);

		boolean hasStoragePermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
		boolean hasPhonePermissions = (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
				&& (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);

		boolean hasAdminPermission = dpm.isAdminActive(cn);

		boolean hasBatteryPermission = true;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			hasBatteryPermission = pm.isIgnoringBatteryOptimizations(context.getPackageName());
		}

		boolean hasDrawOverlays = true;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			hasDrawOverlays = Settings.canDrawOverlays(context);
		}

		boolean hasAllPerms = hasStoragePermission && hasPhonePermissions && hasAdminPermission && hasBatteryPermission && hasDrawOverlays;

		Log.d(TAG,"hasAllPerms: " + hasAllPerms);

		return hasAllPerms;
	}
}