package uibk.autonom.ps.activity;

import org.opencv.core.Scalar;

import uibk.autonom.ps.activity.R;
import uibk.autonom.ps.colordetector.ColorConverter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_view);

		init();
		Button next = (Button) findViewById(R.id.Button02);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsActivity.this.finish();
			}
		});

		Button save = (Button) findViewById(R.id.ButtonSave);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonSave_click();
			}
		});
	}

	private void init() {
		int val1, val2, val3, val4;
		Boolean useMonochrome;
		
		
		SharedPreferences settings = getSharedPreferences("settings", 0);
		val1=settings.getInt("val1", 0);
		val2=settings.getInt("val2", 0);
		val3=settings.getInt("val3", 0);
		val4=settings.getInt("val4", 0);
		useMonochrome=settings.getBoolean("useMonochrome", false);
		
		EditText edit = (EditText) findViewById(R.id.editColorVector1);
		edit.setText(""+val1);
		edit = (EditText) findViewById(R.id.editColorVector2);
		edit.setText(""+val2);
		edit = (EditText) findViewById(R.id.editColorVector3);
		edit.setText(""+val3);
		edit = (EditText) findViewById(R.id.editColorVector4);
		edit.setText(""+val4);
		
		CheckBox checkbox = (CheckBox) findViewById(R.id.checkBoxUseMonochrome);
		checkbox.setChecked(useMonochrome);
	}

	private void buttonSave_click() {
		int val1, val2, val3, val4;
		Boolean useMonochrome;

		EditText edit = (EditText) findViewById(R.id.editColorVector1);
		val1 = Integer.valueOf(edit.getText().toString());
		edit = (EditText) findViewById(R.id.editColorVector2);
		val2 = Integer.valueOf(edit.getText().toString());
		edit = (EditText) findViewById(R.id.editColorVector3);
		val3 = Integer.valueOf(edit.getText().toString());
		edit = (EditText) findViewById(R.id.editColorVector4);
		val4 = Integer.valueOf(edit.getText().toString());
		
		CheckBox checkbox = (CheckBox) findViewById(R.id.checkBoxUseMonochrome);
		useMonochrome = checkbox.isChecked();

		// Store settings
		SharedPreferences settings = getSharedPreferences("settings", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("val1", val1);
		editor.putInt("val2", val2);
		editor.putInt("val3", val3);
		editor.putInt("val4", val4);
		editor.putBoolean("useMonochrome", useMonochrome);
		editor.commit();

		ColorConverter.mColorRadius = new Scalar(val1, val2, val3, val4);
		

		MainActivity.showMessage("Success!");
	}

}
