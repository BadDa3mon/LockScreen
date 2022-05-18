package badda3mon.lockscreen.activities;

import android.Manifest;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import badda3mon.lockscreen.views.RobotoTextView;
import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;

public class LockActivity extends AppCompatActivity {
	private static final String TAG = "LockActivity";

	private static final int REQUEST_PERMISSIONS_CODE = 0;

	private DevicePolicyManager mDevicePolicyManager;
	private ComponentName mComponentName;

	private BannerAdView mBannerAdView;

	private Problem mFirstProblem;
	private Problem mSecondProblem;
	private Problem mThirdProblem;

	private EditText mFirstAnswerEt;
	private EditText mSecondAnswerEt;
	private EditText mThirdAnswerEt;

	private boolean isAnswerCorrect = false;

	public static boolean isDestroyed = true;

	private boolean isNeedDestroy = false;
	private boolean isStartCall = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock);
		try {
			getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
					| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
					| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

			initAdBanner();

			mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			mComponentName = new ComponentName(LockActivity.this, MainAdminReceiver.class);

			String[] perms = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE };
			if (!(ContextCompat.checkSelfPermission(LockActivity.this, perms[0]) == PackageManager.PERMISSION_GRANTED)) {
				ActivityCompat.requestPermissions(this, perms, REQUEST_PERMISSIONS_CODE);
			} else continueAfterRequest();

			Button tel1Button = findViewById(R.id.tel1_button);
			tel1Button.setOnClickListener(v -> {
				String number = PersistenceStorage.getStringProperty("tel1");

				callToNumber(number);
			});

			Button tel2Button = findViewById(R.id.tel2_button);
			tel2Button.setOnClickListener(v -> {
				String number = PersistenceStorage.getStringProperty("tel2");

				callToNumber(number);
			});

			mFirstAnswerEt = findViewById(R.id.first_answer_et);
			mSecondAnswerEt = findViewById(R.id.second_answer_et);
			mThirdAnswerEt = findViewById(R.id.third_answer_et);

			Button btnUnlock = findViewById(R.id.checkAnswerButton);
			btnUnlock.setOnClickListener(v -> {
				isStartCall = false;

				try {
					int firstUserAnswer = Integer.parseInt(mFirstAnswerEt.getText().toString());
					int secondUserAnswer = Integer.parseInt(mSecondAnswerEt.getText().toString());
					int thirdUserAnswer = Integer.parseInt(mThirdAnswerEt.getText().toString());

					Log.d(TAG,mFirstProblem.getRightAnswer() + " / " + firstUserAnswer);
					Log.d(TAG,mSecondProblem.getRightAnswer() + " / " + secondUserAnswer);
					Log.d(TAG,mThirdProblem.getRightAnswer() + " / " + thirdUserAnswer);

					if (mFirstProblem.getRightAnswer() == firstUserAnswer &&
							mSecondProblem.getRightAnswer() == secondUserAnswer &&
							mThirdProblem.getRightAnswer() == thirdUserAnswer){
						isAnswerCorrect = true;
						isNeedDestroy = true;

						finishAndRemoveTask();
					}
				} catch (NumberFormatException e){
					isAnswerCorrect = false;
					isNeedDestroy = false;

					Toast.makeText(this, "Введите ответы и нажмите ОК!", Toast.LENGTH_SHORT).show();
				}
			});

			isDestroyed = false;
		} catch (Exception e){
			Log.e(TAG,"Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void callToNumber(String number){
		isStartCall = true;

		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number));

		startActivity(intent);

		Log.d(TAG,"callToNumber! isStartCall: " + isStartCall);
	}

	private void initAdBanner(){
		mBannerAdView = findViewById(R.id.banner_ad_view);
		mBannerAdView.setAdUnitId("R-M-DEMO-320x50-app_install");
		mBannerAdView.setAdSize(AdSize.BANNER_320x50);
		mBannerAdView.setVisibility(View.VISIBLE);
		mBannerAdView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);

		AdRequest adRequest = new AdRequest.Builder().build();

		mBannerAdView.setBannerAdEventListener(new BannerAdEventListener() {
			@Override
			public void onAdLoaded() {
				Log.d(TAG,"onAdLoaded!");
			}

			@Override
			public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
				Log.e(TAG,"onAdFailedToLoad: " + adRequestError.getCode() + " / " + adRequestError.getDescription());
			}

			@Override
			public void onAdClicked() {
				isNeedDestroy = true;
				Log.d(TAG,"onAdClicked!");
			}

			@Override
			public void onLeftApplication() {
				Log.d(TAG,"onLeftApplication");
			}

			@Override
			public void onReturnedToApplication() {
				Log.d(TAG,"onReturnedToApplication");
			}

			@Override
			public void onImpression(@Nullable ImpressionData impressionData) {
				Log.d(TAG,"onImpression: ...");
			}
		});

		mBannerAdView.loadAd(adRequest);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		Log.d(TAG,"isFinishing: " + isFinishing() + ", hasFocus: " + hasFocus + ", isStartCall: " + isStartCall);

		if (!isFinishing() && !isStartCall)
			if (!hasFocus || isNeedDestroy) {
				finishAndRemoveTask();

				Log.d(TAG,"Finish from onWindowFocusChanged!");
			}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {
			boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);

			if (isAdmin){
				if (!isAnswerCorrect) {
					mDevicePolicyManager.lockNow();
				} else Log.d(TAG,"Answer correct, not locked!");
			}
		} catch (Exception e){
			Log.e(TAG,"Error: " + e.getMessage());
			e.printStackTrace();
		}

		isDestroyed = true;

		Log.e(TAG,"Destroyed");
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

		RelativeLayout layout = findViewById(R.id.lock_layout);

		layout.setSystemUiVisibility(uiOptions);

		Log.e(TAG,"Resumed");
	}

	private void continueAfterRequest(){
		try {
			PersistenceStorage.init(this);

			int selectedLevel = PersistenceStorage.getIntProperty("level");

			mFirstProblem = ProblemGenerator.generate(selectedLevel);
			initProblemByRow(1, mFirstProblem);

			mSecondProblem = ProblemGenerator.generate(selectedLevel);
			initProblemByRow(2, mSecondProblem);

			mThirdProblem = ProblemGenerator.generate(selectedLevel);
			initProblemByRow(3, mThirdProblem);

			ImageView bgImageView = findViewById(R.id.bg_wallpaper_iv);

			if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
				Drawable wallpaperDrawable = WallpaperManager.getInstance(this).getDrawable();

				BitmapDrawable originalDrawable = (BitmapDrawable) wallpaperDrawable;

				Bitmap originalBitmap = originalDrawable.getBitmap();
				Bitmap blurredBitmap = BlurBuilder.getBitmapWithBlur(this, originalBitmap);

				bgImageView.setImageBitmap(blurredBitmap);
				bgImageView.setVisibility(View.VISIBLE);
			} else bgImageView.setVisibility(View.GONE);
		} catch (Exception e){
			Log.e(TAG,"continueAfterRequest: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void initProblemByRow(int row, Problem problem){
		RobotoTextView firstNumTv;
		RobotoTextView markTv;
		RobotoTextView secondNumTv;

		switch (row){
			case 1:
				firstNumTv = findViewById(R.id.first_num_tv);
				markTv = findViewById(R.id.first_mark_num_tv);
				secondNumTv = findViewById(R.id.second_num_tv);
				break;
			case 2:
				firstNumTv = findViewById(R.id.first_num_tv2);
				markTv = findViewById(R.id.second_mark_num_tv);
				secondNumTv = findViewById(R.id.second_num_tv2);
				break;
			default:
				firstNumTv = findViewById(R.id.first_num_tv3);
				markTv = findViewById(R.id.third_mark_num_tv);
				secondNumTv = findViewById(R.id.second_num_tv3);
				break;
		}

		firstNumTv.setText(String.valueOf(problem.getFirstNumber()));
		markTv.setText(problem.getOperationMark());
		secondNumTv.setText(String.valueOf(problem.getSecondNumber()));
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