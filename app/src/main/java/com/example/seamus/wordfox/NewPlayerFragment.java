package com.example.seamus.wordfox;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.UUID;

import static com.example.seamus.wordfox.WordfoxConstants.MAX_PLAYER_NAME_LENGTH;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerChoiceListener} interface
 * to handle interaction events.
 */
public class NewPlayerFragment extends Fragment {
    private PlayerChoiceListener.FragmentView mListener;
    private EditText newPlayerEditText;

    public NewPlayerFragment() {
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
        return inflater.inflate(R.layout.fragment_new_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton save = getActivity().findViewById(R.id.newPlayerSave);
        save.setOnClickListener(saveListener);
        newPlayerEditText = getActivity().findViewById(R.id.newPlayerET);
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String name = newPlayerEditText.getText().toString();
            if (name.length() == 0) {
                return;
            }
            if (name.length() > MAX_PLAYER_NAME_LENGTH) {
                name = name.substring(0, MAX_PLAYER_NAME_LENGTH);
            }
            if (mListener != null) {
                PlayerIdentity newPlayer = new PlayerIdentity(UUID.randomUUID(), name);
                mListener.setChoice(newPlayer);
            }
        }
    };

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
}
