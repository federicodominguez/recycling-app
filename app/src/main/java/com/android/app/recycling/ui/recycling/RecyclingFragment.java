package com.android.app.recycling.ui.recycling;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.app.recycling.R;
import com.android.app.recycling.SharedViewModel;

import java.util.HashMap;

public class RecyclingFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    Button bAdd;
    EditText etCans,etBottles,etGlass,etTetrabriks,etPaperboard;
    private HashMap<String,Integer> residues;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        residues = new HashMap<String,Integer>();

        View root = inflater.inflate(R.layout.fragment_recycling, container, false);

        return root;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bAdd = view.findViewById(R.id.bAdd);

        etBottles = view.findViewById(R.id.etBottles);
        etCans = view.findViewById(R.id.etCans);
        etPaperboard = view.findViewById(R.id.etPaperboard);
        etGlass = view.findViewById(R.id.etGlass);
        etTetrabriks = view.findViewById(R.id.etTetrabriks);

        bAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                residues.put("cans",Integer.parseInt(!etCans.getText().toString().isEmpty() ? etCans.getText().toString() : "0"));
                residues.put("glass",Integer.parseInt(!etGlass.getText().toString().isEmpty() ? etGlass.getText().toString() : "0"));
                residues.put("paperboard",Integer.parseInt(!etPaperboard.getText().toString().isEmpty() ? etPaperboard.getText().toString() : "0"));
                residues.put("tetrabriks",Integer.parseInt(!etTetrabriks.getText().toString().isEmpty() ? etTetrabriks.getText().toString() : "0"));
                residues.put("bottles",Integer.parseInt(!etBottles.getText().toString().isEmpty() ? etBottles.getText().toString() : "0"));
                sharedViewModel.setResidues(residues);

                Toast.makeText(requireContext(), "Reciclaje agregado", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
