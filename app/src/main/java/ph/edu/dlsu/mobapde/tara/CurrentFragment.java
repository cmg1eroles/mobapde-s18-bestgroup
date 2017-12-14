package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by louis on 11/12/2017.
 */

public class CurrentFragment extends Fragment {

    LinearLayout llYourLoc;
    TextView tvRaceTitle;
    TextView tvRaceLoc;
    TextView tvRaceDate;
    TextView tvRaceTime;
    TextView tvNumUsers;
    Button buttonTara;
    Button buttonLeave;

    DatabaseReference ref;
    FirebaseUser currUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_fragment, container, false);

        llYourLoc = (LinearLayout) view.findViewById(R.id.ll_yourLoc);
        tvRaceTitle = (TextView) view.findViewById(R.id.tv_currtitle);
        tvRaceLoc = (TextView) view.findViewById(R.id.tv_currloc);
        tvRaceDate = (TextView) view.findViewById(R.id.tv_currdate);
        tvRaceTime = (TextView) view.findViewById(R.id.tv_currtime);
        tvNumUsers = (TextView) view.findViewById(R.id.tv_numUsers);
        buttonTara = (Button) view.findViewById(R.id.button_go);
        buttonLeave = (Button) view.findViewById(R.id.button_leave);

        currUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();

        llYourLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), MapsActivity.class);
                startActivity(i);
            }
        });

        tvNumUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), AddUsersActivity.class);
                startActivity(i);
            }
        });

        buttonTara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ref.child("users").child(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String raceID = dataSnapshot.getValue(User.class).getCurrentRace();
                if (raceID != null) {
                    ref.child("races").child(raceID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String title = dataSnapshot.child("title").getValue(String.class);
                            Date date = dataSnapshot.child("date").getValue(Date.class);
                            String year = Integer.toString(date.getYear() + 1900);
                            String month = Integer.toString(date.getMonth());
                            String day = Integer.toString(date.getDate());
                            int m = date.getMinutes();
                            String hrs = Integer.toString(date.getHours());
                            String mins;
                            if (m < 10) {
                                mins = "0" + m;
                            } else {
                                mins = Integer.toString(m);
                            }


                            tvRaceTitle.setText(title);
                            tvRaceLoc.setText(dataSnapshot.child("locName").getValue(String.class));
                            tvRaceDate.setText(month + "/" + day + "/" + year);
                            tvRaceTime.setText(hrs + ":" + mins);
                            tvNumUsers.setText(dataSnapshot.child("participants").getChildrenCount()+" Participants");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return view;
    }
}
