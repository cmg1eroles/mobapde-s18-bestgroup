package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.text.ParseException;
import java.util.ArrayList;

public class AddUsersActivity extends AppCompatActivity {

    RecyclerView rvCurrentParticipants;
    Button buttonAdd;
    ArrayList<User> participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        rvCurrentParticipants = (RecyclerView) findViewById(R.id.rv_participants);
        buttonAdd = (Button) findViewById(R.id.bt_addUsers);
        participants = new ArrayList<User>();

        User user1 = new User("louise_cortez@gmail.com", "louise", "hello1234");
        User user2 = new User("carlo_eroles@gmail.com", "carlo", "hello1234");
        User user3 = new User("sophia_rivera@gmail.com", "sophia", "hello1234");
        User user4 = new User("mscourtney@gmail.com", "mscourtney", "hello1234");

        participants.add(user1);
        participants.add(user2);
        participants.add(user3);
        participants.add(user4);

        UserRaceAdapterSkeleton ua = new UserRaceAdapterSkeleton(participants);
        rvCurrentParticipants.setAdapter(ua);
        rvCurrentParticipants.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        ua.setOnItemClickListener(new UserRaceAdapterSkeleton.OnItemClickListener() {
            @Override
            public void onItemClick(User u) {
                Intent i = new Intent(AddUsersActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ADDING USERS
                AddUserDialog aud = new AddUserDialog();
                aud.show(getFragmentManager(), "");
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

    }
}
