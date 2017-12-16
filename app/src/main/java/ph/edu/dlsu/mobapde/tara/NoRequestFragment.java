package ph.edu.dlsu.mobapde.tara;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by louis on 12/5/2017.
 */

public class NoRequestFragment extends Fragment {
    TextView tvNoRequest;

    DatabaseReference ref;
    FirebaseAuth mAuth;
    FirebaseUser currUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.norequest_fragment, container, false);

        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();

        System.out.println("on create view no request");

        ref.child("users").child(currUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("pumasok sa child added");
                if(dataSnapshot.getKey().equals("requests")) {
                    System.out.println("USER HAS REQUEST");
                    ref.child("users").child(currUser.getUid()).child("requests").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();

                            transaction.replace(R.id.container_request, new RequestFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();

                            System.out.println("added new request");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tvNoRequest = (TextView) view.findViewById(R.id.tv_norequest);

        return view;
    }

    public void refreshRequest() {
            //((HomeActivity) getActivity()).refreshHome();

    }
}
