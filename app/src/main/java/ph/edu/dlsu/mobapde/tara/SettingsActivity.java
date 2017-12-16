package ph.edu.dlsu.mobapde.tara;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    TextView tvChangePW;
    TextView tvLogout;

    FirebaseAuth mAuth;
    DatabaseReference db;
    FirebaseUser fbu = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvChangePW = (TextView) findViewById(R.id.tv_cpassword);
        tvLogout = (TextView) findViewById(R.id.tv_logout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        tvChangePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog cpd = new ChangePasswordDialog();
                cpd.show(getFragmentManager(), "");
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to main activity
                db.child("users").child(mAuth.getCurrentUser().getUid()).child("status").setValue("Inactive");

                FirebaseDatabase.getInstance().getReference("lastOnline").child(mAuth.getCurrentUser().getUid()).removeValue();

                mAuth.signOut();

                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);



                finish();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(getApplicationContext(), OwnProfileActivity.class);
        startActivity(i);

        return true;
    }

    public void changePassword(String oldPassword, final String newPassword, String confirmPassword) {
        if(newPassword.equals(confirmPassword)) {
            AuthCredential ac = EmailAuthProvider.getCredential(fbu.getEmail(), oldPassword);

            fbu.reauthenticate(ac)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                fbu.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast t = Toast.makeText(getBaseContext(), "Successfully updated password", Toast.LENGTH_LONG);
                                            t.show();
                                        } else {
                                            Toast t = Toast.makeText(getBaseContext(), "Error updating password", Toast.LENGTH_LONG);
                                            t.show();
                                        }
                                    }
                                });
                            } else {
                                Toast t = Toast.makeText(getBaseContext(), "Authentication failed", Toast.LENGTH_LONG);
                                t.show();
                            }
                        }
                    });
        } else {
            Toast t = Toast.makeText(getBaseContext(), "Passwords do not match", Toast.LENGTH_LONG);
            t.show();
        }
    }
}
