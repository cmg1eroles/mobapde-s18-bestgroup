package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by louis on 11/13/2017.
 */

public class RequestFragment extends Fragment {

    RecyclerView rvRequests;

    ArrayList<Request> requests;
    RequestAdapterSkeleton ra;
    FirebaseAuth mAuth;
    FirebaseUser currUser;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference requestDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currUserID).child("requests");

        rvRequests = (RecyclerView) view.findViewById(R.id.rv_requests);
        requests = new ArrayList<Request>();
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();

        ra = new RequestAdapterSkeleton(requests);
        rvRequests.setAdapter(ra);
        rvRequests.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL, false));

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            final DatabaseReference currRequestDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currUserID);

            currRequestDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("requests")) {
                        currRequestDatabaseRef.child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Request request = snapshot.getValue(Request.class);
                                    request.setId(snapshot.getKey());

                                    ra.addRequestHashMap(snapshot.getKey(), requests.size());

                                    requests.add(request);
                                    ra.setUserRequests(requests, RequestFragment.this);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    } else {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void refreshRequest() {
        if(requests.size() == 0) {
            //((HomeActivity) getActivity()).refreshHome();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.main_content, new NoRequestFragment());
            transaction.addToBackStack(null);
            transaction.commit();

            System.out.println("In refreshRequest");
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.requests_fragment, container, false);

        return view;
    }

    public void addRequest(String userKey) {

    }
}