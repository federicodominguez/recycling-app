package com.android.app.recycling;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class SharedViewModel extends ViewModel {

    private HashMap<String,Integer> residues;

    public SharedViewModel() {
        this.residues = new HashMap<String, Integer>();
    }

    public HashMap<String, Integer> getResidues() { return residues; }

    public void setResidues(HashMap<String, Integer> residues) {

        this.residues = residues;

    }
}
