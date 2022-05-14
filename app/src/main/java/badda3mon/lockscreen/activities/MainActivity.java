package badda3mon.lockscreen.activities;

import android.app.admin.DevicePolicyManager;
import android.content.*;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import badda3mon.lockscreen.additional.PersistenceStorage;
import badda3mon.lockscreen.receivers.MainAdminReceiver;
import badda3mon.lockscreen.services.LockScreenForegroundService;
import badda3mon.lockscreen.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";

	private static final int REQUEST_ADMIN_CODE = 1;

	private DevicePolicyManager mDevicePolicyManager;
	private ComponentName mComponentName;

	private TextView mLevelTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PersistenceStorage.init(MainActivity.this);

		mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mComponentName = new ComponentName(MainActivity.this, MainAdminReceiver.class);

		mLevelTextView = findViewById(R.id.current_level_text);

		int level = PersistenceStorage.getIntProperty("level");
		if (level != -1) getButtonByLevel(level).setTextColor(Color.RED);

		String levelText = getResources().getString(R.string.current_level, level).replace("-1","не выбран");

		mLevelTextView.setText(levelText);

		if (!mDevicePolicyManager.isAdminActive(mComponentName)){
			Toast.makeText(this, "Для работы приложения необходимо выдать права администратора!", Toast.LENGTH_SHORT).show();

			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "For lock and unlock device!");

			startActivityForResult(intent, REQUEST_ADMIN_CODE);
		} else Log.d(TAG,"App already have admin access!");

		Intent foregroundServiceIntent = new Intent(MainActivity.this, LockScreenForegroundService.class);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			startForegroundService(foregroundServiceIntent);
		} else startService(foregroundServiceIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		checkIfUserHasAccess();
	}

	private void checkIfUserHasAccess(){
		String URLString = "https://badda3mon.ru/checkAccessToLockScreen.php";

		Thread thread = new Thread(() -> {
			try {
				URL url = new URL(URLString);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				InputStream inputStream = connection.getInputStream();
				InputStreamReader streamReader = new InputStreamReader(inputStream);

				BufferedReader bufferedReader = new BufferedReader(streamReader);

				String answer = bufferedReader.readLine();

				Log.e(TAG,"Access granted? -> " + answer);

				if (!answer.equals("true")) runOnUiThread(() -> {
					finishAffinity();

					Toast.makeText(this, "Доступ к приложение закрыт! Обратитесь в телеграм: t.me/badda3mon", Toast.LENGTH_LONG).show();
				});

				connection.disconnect();
			} catch (IOException e){
				Log.e(TAG,"Error: " + e.getMessage());
				e.printStackTrace();

				runOnUiThread(() -> {
					Toast.makeText(this, "Доступ к приложение закрыт! Обратитесь в телеграм: t.me/badda3mon", Toast.LENGTH_LONG).show();

					finishAffinity();
				});
			}
		});
		thread.start();
	}

	public void onChangeLevelButtonsClick(View view){
		int level;

		Button firstLevel = getButtonByLevel(1);
		Button secondLevel = getButtonByLevel(2);
		Button thirdLevel = getButtonByLevel(3);
		Button fourthLevel = getButtonByLevel(4);
		Button fifthLevel = getButtonByLevel(5);

		firstLevel.setEnabled(true); firstLevel.setTextColor(Color.WHITE);
		secondLevel.setEnabled(true); secondLevel.setTextColor(Color.WHITE);
		thirdLevel.setEnabled(true); thirdLevel.setTextColor(Color.WHITE);
		fourthLevel.setEnabled(true); fourthLevel.setTextColor(Color.WHITE);
		fifthLevel.setEnabled(true); fifthLevel.setTextColor(Color.WHITE);

		view.setEnabled(false); ((Button) view).setTextColor(Color.RED);

		switch (view.getId()){
			case R.id.first_level_btn: level = 1; break;
			case R.id.second_level_btn: level = 2; break;
			case R.id.third_level_btn: level = 3; break;
			case R.id.fourth_level_btn: level = 4; break;
			default: level = 5; break;
		}

		String levelText = getResources().getString(R.string.current_level, level);
		mLevelTextView.setText(levelText);

		PersistenceStorage.addIntProperty("level", level);
	}

	private Button getButtonByLevel(int level){
		int buttonId;

		switch (level){
			case 1: buttonId = R.id.first_level_btn; break;
			case 2: buttonId = R.id.second_level_btn; break;
			case 3: buttonId = R.id.third_level_btn; break;
			case 4: buttonId = R.id.fourth_level_btn; break;
			default: buttonId = R.id.fifth_level_btn; break;
		}

		return findViewById(buttonId);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_ADMIN_CODE){
			if (resultCode == RESULT_OK){
				Toast.makeText(this, "Права администратора получены!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Ошибка при получении прав администратора!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}