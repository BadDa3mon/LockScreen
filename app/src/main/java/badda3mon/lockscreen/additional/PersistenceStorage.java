package badda3mon.lockscreen.additional;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistenceStorage {

	public static final String STORAGE_NAME = "Settings";

	private static SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor mEditor;

	public static void init(Context context) {
		mSharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}

	public static void addStringProperty(String key, String value) {
		mEditor.putString(key, value);
		mEditor.apply();
	}

	public static String getStringProperty(String key) {
		return mSharedPreferences.getString(key, null);
	}

	public static void addIntProperty(String key, int value) {
		mEditor.putInt(key, value);
		mEditor.apply();
	}

	public static int getIntProperty(String key) {
		return mSharedPreferences.getInt(key, -1);
	}

	public static void addBooleanProperty(String key, boolean value) {
		mEditor.putBoolean(key, value);
		mEditor.apply();
	}

	public static boolean getBooleanProperty(String key) {
		return mSharedPreferences.getBoolean(key, false);
	}

	public static void addLongProperty(String key, long value) {
		mEditor.putLong(key, value);
		mEditor.apply();
	}

	public static long getLongProperty(String key) {
		return mSharedPreferences.getLong(key, -1L);
	}
}
