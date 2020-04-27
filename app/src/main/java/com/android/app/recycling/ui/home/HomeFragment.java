package com.android.app.recycling.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.android.app.recycling.R;
import com.android.app.recycling.SharedViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private SharedViewModel sharedViewModel;
    Button bActual, bHistorical;
    PieChart pieChart;
    PieDataSet dataSet;
    PieData pieData;
    ArrayList<PieEntry> residues = new ArrayList<PieEntry>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        bActual = root.findViewById(R.id.bActual);
        bHistorical = root.findViewById(R.id.bHistorical);
        pieChart = root.findViewById(R.id.pieChart);

        sharedViewModel.getResidues().observe(getViewLifecycleOwner(), new Observer<HashMap<String, Integer>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, Integer> stringIntegerHashMap) {

                Boolean set =  false;

                if(!residues.isEmpty()) {
                    for (HashMap.Entry<String, Integer> hm : stringIntegerHashMap.entrySet()) {
                        for (PieEntry p : residues) {
                            if (p != null && hm.getKey() == p.getLabel()) {
                                residues.remove(p);
                                p.setY(p.getValue() + hm.getValue());
                                residues.add(p);
                                set = true;
                            }
                        }
                        if (!set && hm.getValue() != 0){
                            residues.add(new PieEntry(hm.getValue().floatValue(),hm.getKey()));
                        }
                    }
                }
                else {
                    for (HashMap.Entry<String, Integer> hm : stringIntegerHashMap.entrySet()){
                        if(hm.getValue() != 0 ) {
                            residues.add(new PieEntry(hm.getValue().floatValue(),hm.getKey()));
                        }
                    }
                }

                dataSet.setValues(residues);

                pieData.notifyDataChanged();
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataSet =  new PieDataSet(residues,"residues");
        dataSet.setValueTextSize(12f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        pieData = new PieData(dataSet);
        pieChart.setData(pieData);

    }
}
