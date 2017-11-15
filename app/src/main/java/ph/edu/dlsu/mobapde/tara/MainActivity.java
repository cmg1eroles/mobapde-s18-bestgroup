package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonLogin;
    Button buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(dsp.contains("name")) {
            Intent i = new Intent(getBaseContext(), HomeActivity.class);
            startActivity(i);
            finish(); // read more about this pa
        }

        buttonLogin = (Button) findViewById(R.id.button_login);
        buttonSignup = (Button) findViewById(R.id.button_signup);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }
}
