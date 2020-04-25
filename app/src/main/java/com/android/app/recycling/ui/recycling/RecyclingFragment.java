package com.android.app.recycling.ui.recycling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.app.recycling.R;

public class RecyclingFragment extends Fragment {

    private RecyclingViewModel recyclingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recyclingViewModel = ViewModelProviders.of(this).get(RecyclingViewModel.class);

        View root = inflater.inflate(R.layout.fragment_recycling, container, false);

        return root;
    }
}
