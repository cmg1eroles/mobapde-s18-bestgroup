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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    String userID;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ref = FirebaseDatabase.getInstance().getReference();

        Intent i = getIntent();
        userID = i.getStringExtra("user_id");

        tvInitial = (TextView) findViewById(R.id.tv_profileinitial);
        tvUsername = (TextView) findViewById(R.id.tv_profnameother);
        tvPoints = (TextView) findViewById(R.id.tv_points);
        tvNumEarly = (TextView) findViewById(R.id.tv_numEarly);
        tvNumOnTime = (TextView) findViewById(R.id.tv_numOnTime);
        tvNumLate = (TextView) findViewById(R.id.tv_numLate);
        tvNumCancelled = (TextView) findViewById(R.id.tv_numCancelled);
        llClickable = (LinearLayout) findViewById(R.id.ll_clickablerate);

        if (userID != null){
            ref.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    tvInitial.setText((u.getEmail().charAt(0)+"").toUpperCase());
                    tvUsername.setText(u.getEmail() + "");
                    tvPoints.setText("Points: " + u.getPoints());
                    tvNumEarly.setText(u.getNumEarly()+"");
                    tvNumOnTime.setText(u.getNumOnTime()+"");
                    tvNumLate.setText(u.getNumLate()+"");
                    tvNumCancelled.setText(u.getNumCancelled()+"");
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();

        return true;
    }
}