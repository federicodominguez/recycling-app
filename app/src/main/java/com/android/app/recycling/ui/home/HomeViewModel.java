package com.android.app.recycling.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private Integer total_cans, total_paperboard, total_bottles, total_glass, total_tetrabriks;

    public HomeViewModel() {
        total_bottles = 10;
        total_cans = 10;
        total_glass = 10;
        total_paperboard = 10;
        total_tetrabriks = 10;
    }

    public Integer getTotal_cans() {
        return total_cans;
    }

    public void setTotal_cans(Integer total_cans) {
        this.total_cans = total_cans;
    }

    public Integer getTotal_paperboard() {
        return total_paperboard;
    }

    public void setTotal_paperboard(Integer total_paperboard) {
        this.total_paperboard = total_paperboard;
    }

    public Integer getTotal_bottles() {
        return total_bottles;
    }

    public void setTotal_bottles(Integer total_bottles) {
        this.total_bottles = total_bottles;
    }

    public Integer getTotal_glass() {
        return total_glass;
    }

    public void setTotal_glass(Integer total_glass) {
        this.total_glass = total_glass;
    }

    public Integer getTotal_tetrabriks() {
        return total_tetrabriks;
    }

    public void setTotal_tetrabriks(Integer total_tetrabriks) {
        this.total_tetrabriks = total_tetrabriks;
    }

}