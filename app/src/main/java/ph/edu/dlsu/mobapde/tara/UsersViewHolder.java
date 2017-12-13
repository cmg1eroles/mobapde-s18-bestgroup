package ph.edu.dlsu.mobapde.tara;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by courtneyngo on 20/11/2017.
 */

public class UsersViewHolder extends RecyclerView.ViewHolder {

    TextView tvInitial;
    TextView tvUsername;
    TextView tvPoints;
    TextView tvRatings;

    public UsersViewHolder(View itemView) {
        super(itemView);

        tvInitial = (TextView) itemView.findViewById(R.id.tv_invitedby);
        tvUsername = (TextView) itemView.findViewById(R.id.tv_raceusername);
        tvPoints = (TextView) itemView.findViewById(R.id.tv_racepoints);
        tvRatings = (TextView) itemView.findViewById(R.id.tv_raceurating);
    }
}

