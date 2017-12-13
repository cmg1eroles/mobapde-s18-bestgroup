package ph.edu.dlsu.mobapde.tara;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by courtneyngo on 20/11/2017.
 */

public class RequestViewHolder extends RecyclerView.ViewHolder {

    TextView tvRequestTitle;
    TextView tvInvitedBy;
    TextView tvLocation;
    TextView tvDate;

    public RequestViewHolder(View itemView) {
        super(itemView);

        tvRequestTitle = (TextView) itemView.findViewById(R.id.tv_requesttitle);
        tvInvitedBy = (TextView) itemView.findViewById(R.id.tv_invitedby);
        tvLocation = (TextView) itemView.findViewById(R.id.tv_requestloc);
        tvDate = (TextView) itemView.findViewById(R.id.tv_requestdt);
    }
}

