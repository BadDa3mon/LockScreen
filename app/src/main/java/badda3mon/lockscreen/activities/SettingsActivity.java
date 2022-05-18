package badda3mon.lockscreen.activities;

import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialog;
import badda3mon.lockscreen.additional.PersistenceStorage;
import badda3mon.lockscreen.services.LockScreenForegroundService;
import badda3mon.lockscreen.R;
import com.google.android.material.textfield.TextInputEditText;
import com.yandex.mobile.ads.common.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";

	private boolean isInitialized;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		isInitialized = false;

		int level = PersistenceStorage.getIntProperty("level");
		if (level == -1) level = 1;

		RadioGroup radioGroup = findViewById(R.id.level_rg);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				int level;
				switch (i){
					case R.id.level2_rb: level = 2; break;
					case R.id.level3_rb: level = 3; break;
					case R.id.level4_rb: level = 4; break;
					case R.id.level5_rb: level = 5; break;
					default: level = 1;
				}

				PersistenceStorage.addIntProperty("level", level);
			}
		});

		try {
			RadioButton currentRb = getRadioButtonByLevel(level);
			currentRb.setChecked(true);
		} catch (Exception e) {
			Log.e(TAG,"Error: " + e.getMessage());
			e.printStackTrace();
		}

		EditText firstPhone = findViewById(R.id.first_phone_et);
		EditText secondPhone = findViewById(R.id.second_phone_et);

		String savedFirstNum = PersistenceStorage.getStringProperty("tel1");
		String savedSecondNum = PersistenceStorage.getStringProperty("tel2");

		if (savedFirstNum != null) firstPhone.setText(savedFirstNum);
		if (savedSecondNum != null) secondPhone.setText(savedSecondNum);

		Button saveBtn = findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(v -> {
			String first = "";
			try {
				first = firstPhone.getText().toString();
			} catch (Exception e){
				Log.e(TAG,"Error: " + e.getMessage());
				e.printStackTrace();

				first = null;
			}

			PersistenceStorage.addStringProperty("tel1", first);

			String second = "";
			try {
				second = secondPhone.getText().toString();
			} catch (Exception e){
				Log.e(TAG,"Error: " + e.getMessage());
				e.printStackTrace();

				second = null;
			}

			PersistenceStorage.addStringProperty("tel2", second);

			Toast.makeText(this, "Изменения сохранены!", Toast.LENGTH_SHORT).show();
		});

		Intent foregroundServiceIntent = new Intent(SettingsActivity.this, LockScreenForegroundService.class);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			startForegroundService(foregroundServiceIntent);
		} else startService(foregroundServiceIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		checkIfUserHasAccess();

		if (isInitialized) {
			Intent intent = new Intent(this, MasterKeyActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

			startActivity(intent);
		}

		isInitialized = true;
	}

	private RadioButton getRadioButtonByLevel(int level) throws Exception {
		RadioButton radioButton;

		switch (level){
			case 1: radioButton = findViewById(R.id.level1_rb); break;
			case 2: radioButton = findViewById(R.id.level2_rb); break;
			case 3: radioButton = findViewById(R.id.level3_rb); break;
			case 4: radioButton = findViewById(R.id.level4_rb); break;
			case 5: radioButton = findViewById(R.id.level5_rb); break;
			default: throw new Exception("Incorrect level: " + level);
		}

		return radioButton;
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
}