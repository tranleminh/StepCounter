package com.functionality.stepcounter;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class graphFragment extends Fragment {

    private TextView tv;

    public graphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        tv = view.findViewById(R.id.demo);
        String message = getArguments().getString("message");
        tv.setText(message);
        return view;
    }

}
