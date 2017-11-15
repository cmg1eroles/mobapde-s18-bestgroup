package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    TextView tvInitial;
    TextView tvUsername;
    TextView tvPoints;
    TextView tvNumEarly;
    TextView tvNumOnTime;
    TextView tvNumLate;
    TextView tvNumCancelled;

    RatingBar rbUserRating;
    LinearLayout llClickable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvInitial = (TextView) findViewById(R.id.tv_profileinitial);
        tvUsername = (TextView) findViewById(R.id.tv_profnameother);
        tvPoints = (TextView) findViewById(R.id.tv_earlypoints);
        tvNumEarly = (TextView) findViewById(R.id.tv_numEarly);
        tvNumOnTime = (TextView) findViewById(R.id.tv_numOnTime);
        tvNumLate = (TextView) findViewById(R.id.tv_numLate);
        tvNumCancelled = (TextView) findViewById(R.id.tv_numCancelled);
        rbUserRating = (RatingBar) findViewById(R.id.ratingBarother);
        llClickable = (LinearLayout) findViewById(R.id.ll_clickablerate);

        llClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, RatingActivity.class);
                startActivity(i);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();

        return true;
    }
}
