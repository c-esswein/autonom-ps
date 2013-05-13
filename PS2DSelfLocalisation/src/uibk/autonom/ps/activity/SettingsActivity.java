package uibk.autonom.ps.activity;

import uibk.autonom.ps.activity.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);

        Button next = (Button) findViewById(R.id.Button02);
        next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsActivity.this.finish();
			}
		});
        
    }
	
}
