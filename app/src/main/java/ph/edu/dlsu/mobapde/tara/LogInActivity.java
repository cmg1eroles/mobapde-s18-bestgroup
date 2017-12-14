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

import ph.edu.dlsu.mobapde.tara.R;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvsignup;

    Button btlogin;
    EditText etemail;
    EditText etpassword;
    TextView tvForgotPw;

    ProgressBar progressbar;

    private FirebaseAuth mAuth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvForgotPw = (TextView) findViewById(R.id.tv_forgotpw);
        etemail = (EditText) findViewById(R.id.et_email);

        tvsignup = (TextView) findViewById(R.id.tv_signup);
        tvsignup.setOnClickListener(this);

        btlogin = (Button) findViewById(R.id.bt_login);
        btlogin.setOnClickListener(this);

        etemail = (EditText) findViewById(R.id.et_email);
        etpassword = (EditText) findViewById(R.id.et_password);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        tvForgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etemail.getText().toString().length() > 0) {
                    resetPassword(etemail.getText().toString());
                } else {
                    Toast t = Toast.makeText(getBaseContext(), "No email specified", Toast.LENGTH_LONG);
                    t.show();
                }
            }
        });
    }


    private void loginUser(){

        String email = etemail.getText().toString().trim();
        String password = etpassword.getText().toString();

        if(email.isEmpty()){
            etemail.setError("Email is required");
            etemail.requestFocus();
            return;

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.setError("Email is invalid");
            etemail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            etpassword.setError("Password is required");
            etpassword.requestFocus();
            return;

        }


        if (password.length() < 6){
            etpassword.setError("Password should be equal or more than 6 characters");
            etpassword.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressbar.setVisibility(View.GONE);

                    FirebaseUser fu = mAuth.getCurrentUser();

                    db.child("users").child(fu.getUid()).child("status").setValue("Active");

                    Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public boolean resetPassword(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast t = Toast.makeText(getBaseContext(), "Password reset email sent", Toast.LENGTH_LONG);
                            t.show();
                        } else {
                            Toast t = Toast.makeText(getBaseContext(), "Error authenticating email", Toast.LENGTH_LONG);
                            t.show();
                        }
                    }
                });

        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.tv_signup:
                startActivity(new Intent(this, SignUpActivity.class));
                break;

            case R.id.bt_login:
                loginUser();
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