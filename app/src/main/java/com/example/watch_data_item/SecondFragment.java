package com.example.watch_data_item;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.Navigation;


// This is the second fragment which only exist as an info section
public class SecondFragment extends Fragment {

    // we inflate the layout with view and that view is layout of fragment_second.xml
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        // We use an on click listener to my button called toFragment3 which
        // calls on Navigation that finds the next view from my nav_graph.xml
        // called action_secondFragment_to_thirdFragment
        view.findViewById(R.id.toFragment3).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_secondFragment_to_thirdFragment)
        );

        return view;
    }
}