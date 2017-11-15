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

/**
 * Created by louis on 11/12/2017.
 */

public class CurrentFragment extends Fragment {

    LinearLayout llYourLoc;
    TextView tvRaceLoc;
    TextView tvRaceDate;
    TextView tvRaceTime;
    TextView tvNumUsers;
    Button buttonTara;
    Button buttonLeave;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_fragment, container, false);

        llYourLoc = (LinearLayout) view.findViewById(R.id.ll_yourLoc);
        tvRaceLoc = (TextView) view.findViewById(R.id.tv_currloc);
        tvRaceDate = (TextView) view.findViewById(R.id.tv_currdate);
        tvRaceTime = (TextView) view.findViewById(R.id.tv_currtime);
        tvNumUsers = (TextView) view.findViewById(R.id.tv_numUsers);
        buttonTara = (Button) view.findViewById(R.id.button_go);
        buttonLeave = (Button) view.findViewById(R.id.button_leave);

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

        return view;
    }
}
