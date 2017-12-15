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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    DatabaseReference ref;
    String currUserID;

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

        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OwnProfileActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(currUserID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                tvInitial.setText(u.getEmail().charAt(0) + "");
                tvUsername.setText(u.getEmail());
                tvPoints.setText(u.getPoints());
                tvNumEarly.setText(u.getNumEarly());
                tvNumOnTime.setText(u.getNumOnTime());
                tvNumLate.setText(u.getNumLate());
                tvNumCancelled.setText(u.getNumCancelled());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
