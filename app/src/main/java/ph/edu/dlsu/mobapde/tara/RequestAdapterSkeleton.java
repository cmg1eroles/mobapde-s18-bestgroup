package ph.edu.dlsu.mobapde.tara;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    public void setUserRequests(ArrayList<Request> requests) {
        this.requests = requests;
        notifyDataSetChanged();
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

        DatabaseReference userRef = ref.child("races").child(currentRequest.getRace_id());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                title = dataSnapshot.child("title").getValue(String.class);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.tvTitle.setText(title + "");
        holder.tvInvitedBy.setText(currentRequest.getSender() + "");
        holder.tvLocation.setText(loc + "");
        holder.tvDate.setText(month + "/" + day + "/" + year + " | " + hrs + ":" + mins);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* TODO call onItemClickListener's onItemClick to trigger the
                  call back method you created in MainActivity */

                // notifyItemChanged(clickedPosition);

                // REDIRECT TO USER PROFILE

//                Request r = (Request) view.getTag();
//                onItemClickListener.onItemClick(r);
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

        public RequestViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_requesttitle);
            tvInvitedBy = (TextView) itemView.findViewById(R.id.tv_invitedby);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_requestloc);
            tvDate = (TextView) itemView.findViewById(R.id.tv_requestdt);

            btnAccept = (Button) itemView.findViewById(R.id.bt_accept);
            btnDecline = (Button) itemView.findViewById(R.id.bt_decline);
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