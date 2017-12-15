package ph.edu.dlsu.mobapde.tara;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by louis on 11/14/2017.
 */

public class RequestAdapterSkeleton extends RecyclerView.Adapter<RequestAdapterSkeleton.RequestViewHolder> {
    ArrayList<Request> requests;

    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    String title = "";
    Date date;
    String year = "";
    String month = "";
    String day = "";
    int m = 0;
    String hrs = "";
    String mins = "";
    String loc = "";

    View view_btnaccept;
    View view_btndecline;

    HashMap<String, Integer> rIndices = new HashMap<String, Integer>();

    RequestViewHolder rvh;

    String currRequestID = "";
    String currSender = "";

    public void setUserRequests(ArrayList<Request> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    public void addRequestHashMap(String key, Integer index) {
        rIndices.put(key, index);
    }

    // DUPLICATE ^^^ delete later
    public RequestAdapterSkeleton(ArrayList<Request> requests){
        this.requests = requests;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        requests.get(position).setListPosition(position);
        Request currentRequest = requests.get(position);

        currRequestID = currentRequest.getId();
        currSender = currentRequest.getSender();
        rvh = holder;

        DatabaseReference userRef = ref.child("races").child(currentRequest.getRace_id());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                title = dataSnapshot.child("title").getValue(String.class);
                System.out.println("onDataChange() 1" + title);
                date = dataSnapshot.child("date").getValue(Date.class);
                year = Integer.toString(date.getYear() + 1900);
                month = Integer.toString(date.getMonth());
                day = Integer.toString(date.getDate());
                m = date.getMinutes();
                hrs = Integer.toString(date.getHours());

                if(dataSnapshot.hasChild("locName")) {
                    loc = dataSnapshot.child("locName").getValue(String.class);
                } else {
                    loc = "WALANG LOC NAME";
                }

                if (m < 10) {
                    mins = "0" + m;
                } else {
                    mins = Integer.toString(m);
                }

                System.out.println("onDataChange() 2" + title);

                rvh.tvTitle.setText(title + "");
                rvh.tvInvitedBy.setText(currSender + "");
                rvh.tvLocation.setText(loc + "");
                rvh.tvDate.setText(month + "/" + day + "/" + year + " | " + hrs + ":" + mins);

                rvh.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("users").child(current.getUid());
                        view_btnaccept = view;

                        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("currentRace")) {
                                    Toast.makeText(view_btnaccept.getContext(), "Can't accept new race because of existing race", Toast.LENGTH_LONG).show();
                                } else {
                                    // add race to user's current race
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        TextView tvInvitedBy;
        TextView tvLocation;
        TextView tvDate;

        Button btnAccept;
        Button btnDecline;

        public RequestViewHolder(final View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_requesttitle);
            tvInvitedBy = (TextView) itemView.findViewById(R.id.tv_invitedby);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_requestloc);
            tvDate = (TextView) itemView.findViewById(R.id.tv_requestdt);

            btnAccept = (Button) itemView.findViewById(R.id.bt_accept);
            btnDecline = (Button) itemView.findViewById(R.id.bt_decline);

            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("In button decline");
                    System.out.println("CURRENT REQUEST ID: " + currRequestID);
                    FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("users").child(current.getUid()).child("requests").child(currRequestID);

                    System.out.println(dbref.getKey());
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int ind = rIndices.get(dataSnapshot.getKey());
                            dataSnapshot.getRef().removeValue();
                            requests.remove(ind);


                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //dbref.removeValue();
                }
            });
        }

    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        public void onItemClick(Request r);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}