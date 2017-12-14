package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddUsersActivity extends AppCompatActivity {

    RecyclerView rvCurrentParticipants;
    Button buttonAdd;
    ArrayList<User> participants;
    HashMap<String, Integer> pIndices;

    UserRaceAdapterSkeleton ua;

    String raceID, receiverID;

    DatabaseReference ref;
    FirebaseAuth mAuth;
    FirebaseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();


        rvCurrentParticipants = (RecyclerView) findViewById(R.id.rv_participants);
        buttonAdd = (Button) findViewById(R.id.bt_addUsers);
        participants = new ArrayList<User>();
        pIndices = new HashMap<String, Integer>();

        ua = new UserRaceAdapterSkeleton(participants);
        rvCurrentParticipants.setAdapter(ua);
        rvCurrentParticipants.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ADDING USERS
                AddUserDialog aud = new AddUserDialog();
                aud.show(getFragmentManager(), "");
            }
        });

        ref.child("users").child(currUser.getUid()).child("currentRace").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                raceID = dataSnapshot.getValue(String.class)+"";

                ref.child("races").child(raceID).child("participants").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        ref.child("users").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User u = dataSnapshot.getValue(User.class);
                                Log.i("Added User", u.toString());
                                pIndices.put(dataSnapshot.getKey(), participants.size());
                                Log.i(dataSnapshot.getKey(), Integer.toString(participants.size()));
                                participants.add(u);
                                ua.setUsersInRace(participants);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        ref.child("users").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int ind = pIndices.get(dataSnapshot.getKey());
                                User u = participants.remove(ind);

                                ua.setUsersInRace(participants);
                                Log.i("Removed User", u.toString());
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        ua.setOnItemClickListener(new UserRaceAdapterSkeleton.OnItemClickListener() {
            @Override
            public void onItemClick(User u) {
                Intent i = new Intent(AddUsersActivity.this, ProfileActivity.class);
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

    public void addUserToRace(String username) {
        Query query = ref.child("users").orderByChild("email").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren().iterator().hasNext()) {
                    receiverID = dataSnapshot.getChildren().iterator().next().getKey() + "";

                    HashMap<String, String> request = new HashMap<>();
                    request.put("race_id", raceID);
                    request.put("sender", currUser.getEmail());

                    String key = ref.child("users").child(receiverID).child("requests").push().getKey();

                    ref.child("users").child(receiverID).child("requests").child(key).setValue(request);
                    ref.child("races").child(raceID).child("participants").child(receiverID).setValue(false);

                    Toast.makeText(getBaseContext(), "User successfully added to the race!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "User with this email does not exist!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}