package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class OwnProfileActivity extends AppCompatActivity {

    ImageView ivSettings;

    TextView tvInitial;
    TextView tvUsername;
    TextView tvPoints;
    TextView tvNumEarly;
    TextView tvNumOnTime;
    TextView tvNumLate;
    TextView tvNumCancelled;

    RatingBar rbUserRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_profile);
        // Get ListView object from xml

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ivSettings = (ImageView) findViewById(R.id.iv_profsettings);

        tvInitial = (TextView) findViewById(R.id.tv_ownprofileinitial);
        tvUsername = (TextView) findViewById(R.id.tv_profname);
        tvPoints = (TextView) findViewById(R.id.tv_prearlypoints);
        tvNumEarly = (TextView) findViewById(R.id.tv_prnumEarly);
        tvNumOnTime = (TextView) findViewById(R.id.tv_prnumOnTime);
        tvNumLate = (TextView) findViewById(R.id.tv_prnumLate);
        tvNumCancelled = (TextView) findViewById(R.id.tv_prnumCancelled);
        rbUserRating = (RatingBar) findViewById(R.id.ratingBar);

        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OwnProfileActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });
    }

    // ADDED TOOLBAR BACK OPTIONS
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();

        return true;
    }
}
