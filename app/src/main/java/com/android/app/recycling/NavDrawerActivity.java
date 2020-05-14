package com.android.app.recycling;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.android.app.recycling.ui.recycling.RecyclingFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NavDrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    TextView tvHNDaddress,tvHNDusername;
    SharedPreferences pref_session, pref_actual_recycling;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_nav_drawer);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            tvHNDusername =  headerView.findViewById(R.id.tvHND_username);
            tvHNDaddress =  headerView.findViewById(R.id.tvHND_address);
            pref_session = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            pref_actual_recycling = getSharedPreferences("ActualRecycling",Context.MODE_PRIVATE);
            //Comprobando si ya hay una sesion abierta por un usuario
            if (pref_session.getBoolean("UserInSession",false)) {
                tvHNDusername.setText(pref_session.getString("user",""));
                tvHNDaddress.setText(pref_session.getString("address",""));
                if(pref_session.getBoolean("hasRecycling",false)){
                    sharedViewModel.setResidues((HashMap<String, Integer>) pref_actual_recycling.getAll());
                }
            }
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                      R.id.nav_home,R.id.nav_gallery,R.id.nav_account, R.id.nav_listRecycling)
                    .setDrawerLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPause()
    {
        super.onPause();
        SharedPreferences.Editor editor=pref_actual_recycling.edit();
        SharedPreferences.Editor editor2=pref_session.edit();
        if((!sharedViewModel.getResidues().isEmpty()) && (pref_session.getBoolean("UserInSession",false))){
            editor2.putBoolean("hasRecycling",true);
            for (Map.Entry<String, Integer> entry : sharedViewModel.getResidues().entrySet()) {
                String k = entry.getKey();
                Integer v = entry.getValue();
                editor.putInt(k, v);
            }
        }
        else{
            editor2.putBoolean("hasRecycling",false);
        }
        editor.commit();
        editor2.commit();
    }
}
