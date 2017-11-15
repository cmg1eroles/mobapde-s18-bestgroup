package ph.edu.dlsu.mobapde.tara;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by louis on 11/13/2017.
 */

public class RequestAdapterSkeleton extends RecyclerView.Adapter<RequestAdapterSkeleton.RequestViewHolder> {
    ArrayList<Request> requests;

    public void setRequest(ArrayList<Request> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    public RequestAdapterSkeleton(ArrayList<Request> requests){
        this.requests = requests;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO inflate view
        // TODO return a WeatherViewHolder

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        return new RequestViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        requests.get(position).setListPosition(position);
        Request currentRequest = requests.get(position);

        holder.tvReqTitle.setText(currentRequest.getRace().getTitle());
        holder.tvReqRequester.setText(currentRequest.getRequester().getUsername());
        holder.itemView.setTag(currentRequest);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* TODO call onItemClickListener's onItemClick to trigger the
                  call back method you created in MainActivity */

                // notifyItemChanged(clickedPosition);

                //Request r = (Request) view.getTag();
                //onItemClickListener.onItemClick(r);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView tvReqTitle;
        TextView tvReqLoc;
        TextView tvReqDt;
        TextView tvReqRequester;
        Button btnAccept;
        Button btnDecline;

        public RequestViewHolder(View itemView) {
            super(itemView);

            tvReqTitle = (TextView) itemView.findViewById(R.id.tv_requesttitle);
            tvReqLoc = (TextView) itemView.findViewById(R.id.tv_requestloc);
            tvReqDt = (TextView) itemView.findViewById(R.id.tv_requestdt);
            tvReqRequester = (TextView) itemView.findViewById(R.id.tv_invitedby);
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
