package ph.edu.dlsu.mobapde.tara;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by louis on 12/5/2017.
 */

public class NoCurrentFragment extends Fragment {
    TextView tvNoRace;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currentnorace_fragment, container, false);

        tvNoRace = (TextView) view.findViewById(R.id.tv_norace);

        return view;
    }
}
