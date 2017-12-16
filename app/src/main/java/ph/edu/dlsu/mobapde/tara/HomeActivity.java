package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
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

    CurrentFragment cf;
    NoCurrentFragment ncf;
    RequestFragment rf;
    NoRequestFragment nrf;
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
                    String username = (String)dataSnapshot.getValue();

                    if(username != null)
                        buttonUser.setText(username.charAt(0) + "");
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

    private void setupViewPager(final ViewPager viewPager) {
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            final String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final DatabaseReference currRaceDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currUserID);

            /*cf = new CurrentFragment();
            ncf = new NoCurrentFragment();
            rf = new RequestFragment();
            nrf = new NoRequestFragment();*/
            /*adapter.addFragment(new NoCurrentFragment(), "CURRENT");
            adapter.addFragment(new NoRequestFragment(), "REQUESTS");*/

            viewPager.setAdapter(adapter);

            currRaceDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    adapter.mFragmentList.clear();
                    adapter.mFragmentTitleList.clear();
                    adapter.notifyDataSetChanged();

                    if(dataSnapshot.hasChild("currentRace")) {
                        Log.i("currentRace", "yes");
                        adapter.addFragment(new CurrentFragment(), "CURRENT");
                    } else {
                        Log.i("currentRace", "no");
                        adapter.addFragment(new NoCurrentFragment(), "CURRENT");
                    }

                    if(dataSnapshot.hasChild("requests")) {
                        Log.i("requests", "yes");
                        adapter.addFragment(new RequestFragment(), "REQUESTS");
                    } else {
                        Log.i("requests", "no");
                        adapter.addFragment(new NoRequestFragment(), "REQUESTS");
                    }

                    adapter.notifyDataSetChanged();
                    viewPager.setAdapter(adapter);
                    //refreshHome();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            /*currRaceDatabaseRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if (dataSnapshot.getKey().equalsIgnoreCase("currentRace")) {
                        Log.i(dataSnapshot.getKey(), "added");
                        Fragment f = adapter.getItem(1);
                        adapter.mFragmentList.clear();
                        adapter.mFragmentTitleList.clear();

                        adapter.addFragment(new CurrentFragment(), "CURRENT");
                        adapter.addFragment(f, "REQUESTS");

                        adapter.notifyDataSetChanged();
                        refreshHome();

                    } else if (dataSnapshot.getKey().equalsIgnoreCase("requests")) {
                        Fragment f = adapter.getItem(0);
                        adapter.mFragmentList.clear();
                        adapter.mFragmentTitleList.clear();

                        adapter.addFragment(f, "CURRENT");
                        adapter.addFragment(new RequestFragment(), "REQUESTS");

                        adapter.notifyDataSetChanged();
                        refreshHome();
                    }
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.i(dataSnapshot.getKey(), "removed");
                    if (dataSnapshot.getKey().equalsIgnoreCase("currentRace")) {
                        Fragment f = adapter.getItem(1);
                        adapter.mFragmentList.clear();
                        adapter.mFragmentTitleList.clear();

                        adapter.addFragment(new NoCurrentFragment(), "CURRENT");
                        adapter.addFragment(f, "REQUESTS");

                        adapter.notifyDataSetChanged();
                        refreshHome();

                    } else if (dataSnapshot.getKey().equalsIgnoreCase("requests")) {
                        Fragment f = adapter.getItem(0);
                        adapter.mFragmentList.clear();
                        adapter.mFragmentTitleList.clear();

                        adapter.addFragment(f, "CURRENT");
                        adapter.addFragment(new NoRequestFragment(), "REQUESTS");

                        adapter.notifyDataSetChanged();
                        refreshHome();
                    }
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });*/
        }
    }

    public void refreshHome() {
        Intent refresh = new Intent(this, HomeActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(refresh);
        finish();
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

        /*public void setItem(int position, Fragment f) {
            mFragmentList.set(position, f);
        }*/

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