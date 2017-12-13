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

import com.google.firebase.auth.FirebaseAuth;

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
                Intent intent = new Intent(HomeActivity.this, CreateRaceActivity.class);
                startActivityForResult(intent, 1);
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
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // CHECK IF USER HAS CURRENT RACE
        // IF NOT
        // adapter.addFragment(new NoCurrentFragment(), "CURRENT");
        // NOT SURE CHECK PA
        adapter.addFragment(new CurrentFragment(), "CURRENT");
        adapter.addFragment(new RequestFragment(), "REQUESTS");

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
