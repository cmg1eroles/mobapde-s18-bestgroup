package ph.edu.dlsu.mobapde.tara;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by louis on 11/14/2017.
 */

public class UserRaceAdapterSkeleton extends RecyclerView.Adapter<UserRaceAdapterSkeleton.UserRaceViewHolder> {
    ArrayList<User> usersInRace;

    public void setUsersInRace(ArrayList<User> usersInRace) {
        this.usersInRace = usersInRace;
        notifyDataSetChanged();
    }

    // DUPLICATE ^^^ delete later
    public UserRaceAdapterSkeleton(ArrayList<User> usersInRace){
        this.usersInRace = usersInRace;
    }

    @Override
    public UserRaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserRaceViewHolder(v);
    }


    @Override
    public void onBindViewHolder(UserRaceViewHolder holder, int position) {
        usersInRace.get(position).setListPosition(position);
        User currentUser = usersInRace.get(position);

        holder.tvUsername.setText(currentUser.getEmail());
        holder.tvUserInitial.setText(currentUser.getEmail().charAt(0) + "");
        holder.tvPoints.setText(currentUser.getPoints()+" points");
        holder.itemView.setTag(currentUser);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* TODO call onItemClickListener's onItemClick to trigger the
                  call back method you created in MainActivity */

                // notifyItemChanged(clickedPosition);

                // REDIRECT TO USER PROFILE

                User u = (User) view.getTag();
                onItemClickListener.onItemClick(u);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersInRace.size();
    }

    public class UserRaceViewHolder extends RecyclerView.ViewHolder{

        TextView tvUserInitial;
        TextView tvUsername;
        TextView tvPoints;
        TextView tvRating;

        public UserRaceViewHolder(View itemView) {
            super(itemView);

            tvUserInitial = (TextView) itemView.findViewById(R.id.tv_userinitial);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_raceusername);
            tvPoints = (TextView) itemView.findViewById(R.id.tv_racepoints);
            tvRating = (TextView) itemView.findViewById(R.id.tv_raceurating);
        }

    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        public void onItemClick(User u);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
