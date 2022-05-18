package badda3mon.lockscreen.activities;

import android.content.Intent;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import badda3mon.lockscreen.R;
import badda3mon.lockscreen.additional.PersistenceStorage;

public class MasterKeyActivity extends AppCompatActivity {
	private static final String TAG = "MasterKeyActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master_key);

		EditText masterKeyEt = findViewById(R.id.master_key_et);

		Button okButton = findViewById(R.id.master_key_ok_btn);
		okButton.setOnClickListener(v -> {
			String savedMasterKey = PersistenceStorage.getStringProperty("master_key");

			Editable masterKeyEditable = masterKeyEt.getText();

			if (savedMasterKey != null){

				if (masterKeyEditable != null && masterKeyEditable.length() > 0){
					String inputtedMasterKey = masterKeyEditable.toString();

					if (savedMasterKey.equals(inputtedMasterKey)){
						Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();

						startNextActivity();
					} else {
						Toast.makeText(this, "Неверный ключ!", Toast.LENGTH_SHORT).show();
					}
				} else Toast.makeText(this, "Введите ключ!", Toast.LENGTH_SHORT).show();
			} else {
				if (masterKeyEditable != null && masterKeyEditable.length() > 0){
					String inputtedMasterKey = masterKeyEditable.toString();

					PersistenceStorage.addStringProperty("master_key", inputtedMasterKey);

					Toast.makeText(this, "Ключ сохранён!", Toast.LENGTH_SHORT).show();

					startNextActivity();
				} else Toast.makeText(this, "Введите ключ!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void startNextActivity(){
		boolean hasPermissions = PermissionActivity.hasNeedlePermissions(this);

		Intent intent = new Intent(this, ((hasPermissions) ? SettingsActivity.class : PermissionActivity.class));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		startActivity(intent);
	}
}