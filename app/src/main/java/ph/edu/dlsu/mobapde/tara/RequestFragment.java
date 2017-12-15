package ph.edu.dlsu.mobapde.tara;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    DatabaseReference ref;

    HashMap<String, Integer> rIndices;

    ArrayList<Request> requests;
    RequestAdapterSkeleton ra;
    FirebaseAuth mAuth;
    FirebaseUser currUser;
    //FirebaseRecyclerAdapter<Request, RequestViewHolder> requestFirebaseRecyclerAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference requestDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currUserID).child("requests");

        rvRequests = (RecyclerView) view.findViewById(R.id.rv_requests);
        requests = new ArrayList<Request>();
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();

        User user1 = new User("louise_cortez@gmail.com", "louiseeee", "hello1234");
        User user2 = new User("what@gmail.com", "username2", "hello1234");
        Race race = new Race("MOBAPDE MCO1 Meeting");
        Race race2 = new Race("SOFENGG Brain Storming");
        Race race3 = new Race("OJT Interview");

        requests.add(new Request(user1, user2, race, false));
        requests.add(new Request(user1, user2, race2, false));
        requests.add(new Request(user1, user2, race3, false));

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
                                int requestnum = 0;
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Toast.makeText(getContext(), "request #" + requestnum, Toast.LENGTH_LONG).show();
//                                    Request request = snapshot.getValue(Request.class);
//
//                                    rIndices.put(dataSnapshot.getKey(), requests.size());
//                                    requests.add(request);
//                                    ra.setUserRequests(requests);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.requests_fragment, container, false);

        return view;
    }

    public void addRequest(String userKey) {

    }
}