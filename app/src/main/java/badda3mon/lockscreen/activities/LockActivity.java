package badda3mon.lockscreen.activities;

import android.Manifest;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import badda3mon.lockscreen.R;
import badda3mon.lockscreen.additional.BlurBuilder;
import badda3mon.lockscreen.additional.PersistenceStorage;
import badda3mon.lockscreen.additional.ProblemGenerator;
import badda3mon.lockscreen.models.Problem;
import badda3mon.lockscreen.receivers.MainAdminReceiver;

public class LockActivity extends AppCompatActivity {
	private static final String TAG = "LockActivity";

	private static final int REQUEST_PERMISSIONS_CODE = 0;

	private DevicePolicyManager mDevicePolicyManager;
	private ComponentName mComponentName;

	private Problem mFirstProblem;
	private Problem mSecondProblem;
	private Problem mThirdProblem;

	private boolean isAnswerCorrect = false;

	public static boolean isDestroyed = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock);

		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mComponentName = new ComponentName(LockActivity.this, MainAdminReceiver.class);

		String[] perms = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE };
		if (!(ContextCompat.checkSelfPermission(LockActivity.this, perms[0]) == PackageManager.PERMISSION_GRANTED)) {
			ActivityCompat.requestPermissions(this, perms, REQUEST_PERMISSIONS_CODE);
		} else continueAfterRequest();

		Button btnUnlock = findViewById(R.id.checkAnswerButton);
		btnUnlock.setOnClickListener(v -> {
			int firstUserAnswer, secondUserAnswer, thirdUserAnswer;

			EditText firstAnswer = findViewById(R.id.firstAnswer);
			firstUserAnswer = Integer.parseInt(firstAnswer.getText().toString());

			EditText secondAnswer = findViewById(R.id.secondAnswer);
			secondUserAnswer = Integer.parseInt(secondAnswer.getText().toString());

			EditText thirdAnswer = findViewById(R.id.thirdAnswer);
			thirdUserAnswer = Integer.parseInt(thirdAnswer.getText().toString());

			Log.d(TAG,mFirstProblem.getRightAnswer() + " / " + firstUserAnswer);
			Log.d(TAG,mSecondProblem.getRightAnswer() + " / " + secondUserAnswer);
			Log.d(TAG,mThirdProblem.getRightAnswer() + " / " + thirdUserAnswer);

			if (mFirstProblem.getRightAnswer() == firstUserAnswer &&
					mSecondProblem.getRightAnswer() == secondUserAnswer &&
					mThirdProblem.getRightAnswer() == thirdUserAnswer){

				isAnswerCorrect = true;

				finish();
			}
		});

		isDestroyed = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);

		if (isAdmin){
			if (!isAnswerCorrect) {
				mDevicePolicyManager.lockNow();
			} else Log.d(TAG,"Answer correct, not locked!");
		} else Toast.makeText(this, "Вы не выдали права администратора, приложение может работать некорректно!", Toast.LENGTH_SHORT).show();

		isDestroyed = true;

		Log.d(TAG,"onDestroy");
	}

	@Override
	protected void onResume() {
		super.onResume();

		final int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
				| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
				| View.SYSTEM_UI_FLAG_IMMERSIVE;

		RelativeLayout layout = findViewById(R.id.mainXml);

		layout.setSystemUiVisibility(uiOptions);
	}

	private void continueAfterRequest(){
		int selectedLevel = PersistenceStorage.getIntProperty("level");

		TextView firstProblemTextView = findViewById(R.id.firstCase);
		TextView secondProblemTextView = findViewById(R.id.secondCase);
		TextView thirdProblemTextView = findViewById(R.id.thirdCase);

		mFirstProblem = ProblemGenerator.generate(selectedLevel);
		firstProblemTextView.setText(mFirstProblem.getProblemStringWithEqualsMark());

		mSecondProblem = ProblemGenerator.generate(selectedLevel);
		secondProblemTextView.setText(mSecondProblem.getProblemStringWithEqualsMark());

		mThirdProblem = ProblemGenerator.generate(selectedLevel);
		thirdProblemTextView.setText(mThirdProblem.getProblemStringWithEqualsMark());

		Drawable wallpaperDrawable = WallpaperManager.getInstance(this).getDrawable();

		ImageView imageView = findViewById(R.id.wallpaper);
		imageView.setImageDrawable(wallpaperDrawable);

		BitmapDrawable originalDrawable = (BitmapDrawable) imageView.getDrawable();

		Bitmap originalBitmap = originalDrawable.getBitmap();
		Bitmap blurredBitmap = BlurBuilder.getBitmapWithBlur(LockActivity.this, originalBitmap);

		imageView.setImageBitmap(blurredBitmap);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == REQUEST_PERMISSIONS_CODE){
			for(int result : grantResults){
				Log.d(TAG,"Grant result: " + result);
			}

			continueAfterRequest();
		}
	}
}