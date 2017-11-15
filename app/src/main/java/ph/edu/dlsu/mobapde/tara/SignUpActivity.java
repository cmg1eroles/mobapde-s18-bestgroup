package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{


    EditText etsemail;
    EditText etspassword;
    EditText etsvpassword;

    TextView tvlogin;

    Button btsignup;

    ProgressBar progressbar;

    private FirebaseAuth mAuth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        etsemail = (EditText) findViewById(R.id.et_semail);
        etspassword = (EditText) findViewById(R.id.et_spassword);
        etsvpassword = (EditText) findViewById(R.id.et_svpassword);

        btsignup = (Button) findViewById(R.id.bt_signup);
        btsignup.setOnClickListener(this);

        tvlogin = (TextView) findViewById(R.id.tv_login);
        tvlogin.setOnClickListener(this);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
    }

    private void registerUser(){
        String email = etsemail.getText().toString().trim();
        String password = etspassword.getText().toString();
        String vpassword = etsvpassword.getText().toString();

        if(email.isEmpty()){
            etsemail.setError("Email is required");
            etsemail.requestFocus();
            return;

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etsemail.setError("Email is invalid");
            etsemail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            etspassword.setError("Password is required");
            etspassword.requestFocus();
            return;

        }

        if(vpassword.isEmpty()){
            etsvpassword.setError("Password verification is required");
            etsvpassword.requestFocus();
            return;

        }

        if (password.length() < 6){
            etspassword.setError("Password should be equal or more than 6 characters");
            etspassword.requestFocus();
            return;
        }

        if(!password.equals(vpassword)){
            etsvpassword.setError("Passwords do not match");
            etsvpassword.requestFocus();
            return;

        }

        progressbar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressbar.setVisibility(View.GONE);

                            FirebaseUser fu = mAuth.getCurrentUser();
                            User u = new User();
                            u.setEmail(fu.getEmail());
                            u.setStatus("Active");
                            u.setNumCancelled(0);
                            u.setNumEarly(0);
                            u.setNumLate(0);
                            u.setNumOnTime(0);

                            db.child("users").child(fu.getUid()).setValue(u);

                            Toast.makeText(getBaseContext(), "User registered!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            finish();
                        }else{

                            if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(getApplicationContext(), "You are already registered!",
                                        Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "Error!",
                                        Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_signup:
                registerUser();
                break;

            case R.id.tv_login:
                startActivity(new Intent(this, LogInActivity.class));
                break;
        }
    }

    // ADDED TOOLBAR BACK OPTIONS
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();

        return true;
    }
}