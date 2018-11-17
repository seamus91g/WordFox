package capsicum.game.wordfox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerChoiceListener} interface
 * to handle interaction events.
 */
public class ExistingPlayerFragment extends Fragment {
    private RecyclerView rv;
    private ConstraintLayout myCl;
    private LinearLayoutManager lm;
    private RecyclerView.Adapter rvAdapter;
    private PlayerChoiceListener.FragmentView mListener;
    private int screenWidth;

    public ExistingPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View li = inflater.inflate(R.layout.fragment_existing_player, container, false);
        return li;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Activity activity = getActivity();
        rv = activity.findViewById(R.id.listExistingPlayers);
        calculateScreenDimensions();
        myCl = activity.findViewById(R.id.existing_player_fragment_rootCL);
        rv.setHasFixedSize(true);
        lm = new LinearLayoutManager(activity);
        rv.setLayoutManager(lm);
        rvAdapter = mListener.getPlayersAdapter();
        rvAdapter.getItemCount();
        rv.setAdapter(rvAdapter);
        showNoExistingPlayersMessage();
    }

    private void calculateScreenDimensions() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    private void showNoExistingPlayersMessage() {
        int numExistingPlayers = rvAdapter.getItemCount();
        Timber.d("showNoExistingPlayersMessage: numExistingPlayers: " + numExistingPlayers);


        if(numExistingPlayers == 0){
            TextView noExistingPlayersTV = (TextView) myCl.findViewById(R.id.existing_player_fragment_noExistingPlayersTV);
            noExistingPlayersTV.setWidth(screenWidth/2);
            noExistingPlayersTV.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayerChoiceListener.FragmentView) {
            mListener = (PlayerChoiceListener.FragmentView) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    ////////////////////////////////////////

}
