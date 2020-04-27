package com.android.app.recycling;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<HashMap<String,Integer>> residues;

    public SharedViewModel() {
        residues = new MutableLiveData<HashMap<String, Integer>>();
    }

    public LiveData<HashMap<String, Integer>> getResidues() {
        return residues;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setResidues(HashMap<String, Integer> residues) {

        if(this.residues.getValue() != null){
            for (HashMap.Entry<String, Integer> r : residues.entrySet()) {
                this.residues.getValue().merge(r.getKey(),r.getValue(),Integer::sum);
            }
        } else {
            this.residues.setValue(residues);
        }
    }
}
