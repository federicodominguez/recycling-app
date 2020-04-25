package com.android.app.recycling.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.app.recycling.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Button bActual, bHistorical;
    PieChart pieChart;
    PieDataSet dataSet;
    PieData pieData;
    ArrayList<PieEntry> residues;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bActual = view.findViewById(R.id.bActual);
        bHistorical = view.findViewById(R.id.bHistorical);
        pieChart = view.findViewById(R.id.pieChart);


        // PIE CHART
        residues = new ArrayList<PieEntry>();

        residues.add(new PieEntry(homeViewModel.getTotal_cans().floatValue(),"cans"));
        residues.add(new PieEntry(homeViewModel.getTotal_bottles().floatValue(),"bottles"));
        residues.add(new PieEntry(homeViewModel.getTotal_glass().floatValue(),"glass"));
        residues.add(new PieEntry(homeViewModel.getTotal_paperboard().floatValue(),"paperboard"));
        residues.add(new PieEntry(homeViewModel.getTotal_tetrabriks().floatValue(),"tetrabriks"));


        dataSet =  new PieDataSet(residues,"residues");
        dataSet.setValueTextSize(12f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        pieData = new PieData(dataSet);
        pieChart.setData(pieData);

    }
}
