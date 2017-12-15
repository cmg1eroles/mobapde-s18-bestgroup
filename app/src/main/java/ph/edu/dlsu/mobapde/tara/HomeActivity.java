package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    FloatingActionButton fab_addRace;
    Button buttonUser;
    FirebaseAuth mAuth;
    SectionsPagerAdapter adapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            //logged in
            final String currUserID = mAuth.getCurrentUser().getUid();
            final DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currUserID).child("email");

            userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Toast.makeText(getBaseContext(), "YOU ARE IN HOME", Toast.LENGTH_LONG).show();
                    String username = (String)dataSnapshot.getValue();

                    if(username != null)
                        buttonUser.setText(username.charAt(0) + "");

                    Toast.makeText(getBaseContext(), username, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Intent i = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        //check if login and redirects

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        fab_addRace = (FloatingActionButton) findViewById(R.id.fab_addrace);
        fab_addRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    final String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final DatabaseReference currRaceDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currUserID);

                    currRaceDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("currentRace")) {
                                Toast.makeText(getBaseContext(), "Can't create new race because of existing race", Toast.LENGTH_LONG).show();
                            } else {
                                Intent intent = new Intent(HomeActivity.this, CreateRaceActivity.class);
                                startActivityForResult(intent, 1);
                            }

                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        buttonUser = (Button) findViewById(R.id.bt_user);
        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, OwnProfileActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_home);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setTabTextColors(
                ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark),
                ContextCompat.getColor(getBaseContext(), R.color.colorAccent)
        );
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            final String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final DatabaseReference currRaceDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currUserID);

            Toast.makeText(getBaseContext(), "ID: " + currUserID, Toast.LENGTH_LONG).show();

            currRaceDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("currentRace")) {
                        adapter.addFragment(new CurrentFragment(), "CURRENT");
                    } else {
                        adapter.addFragment(new NoCurrentFragment(), "CURRENT");
                    }

                    if(dataSnapshot.hasChild("requests")) {
                        adapter.addFragment(new RequestFragment(), "REQUESTS");
                    } else {
                        adapter.addFragment(new NoRequestFragment(), "REQUESTS");
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        viewPager.setAdapter(adapter);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}