package com.ewarranty.warranty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ewarranty.warranty.adapter.incident.IncidentAdapter;
import com.ewarranty.warranty.models.Incident;
import com.ewarranty.warranty.models.IncidentStatus;
import com.ewarranty.warranty.models.IncidentWorkFlow;
import com.ewarranty.warranty.models.IncidentWorkFlowStatus;
import com.ewarranty.warranty.models.Serviceman;
import com.ewarranty.warranty.models.Theme;
import com.ewarranty.warranty.util.SettingsAction;
import com.ewarranty.warranty.util.Store;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class IncidentListActivity extends AppCompatActivity {

    private IncidentStatus incidentStatus;
    public static List<Incident> incidentList;
    private RecyclerView incidentRecyclerView;
    public static IncidentAdapter incidentAdapter;

    private TextView incidentHeader;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme currentTheme = SettingsAction.getInstance(IncidentListActivity.this).getCurrentTheme();
        AppCompatDelegate.setDefaultNightMode(
                currentTheme == Theme.LITE?AppCompatDelegate.MODE_NIGHT_NO:currentTheme == Theme.DARK?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_AUTO);

        setContentView(R.layout.activity_incident_list);
        Store.SERVICE = ServiceGenerator.createService(UserServices.class);
        if (getIntent()!=null) {
            incidentStatus = IncidentStatus.valueOf(getIntent().getStringExtra("status"));
        }

        incidentRecyclerView = (RecyclerView) findViewById(R.id.incidentRecyclerView);
        incidentHeader = (TextView) findViewById(R.id.incidentHeader);
        back = (ImageView) findViewById(R.id.back);

        Log.d("Test", "onCreate: "+Store.PROFILE);

        if(Store.PROFILE!=null){
            Store.SERVICE.getListOfIncidentByStatus(Store.PROFILE.getPhoneNumber(),incidentStatus.toString(),0,20).enqueue(new Callback<List<Incident>>() {
                @Override
                public void onResponse(Call<List<Incident>> call, Response<List<Incident>> response) {
                    if(response.body()!=null){
                        incidentList = response.body();
                        Log.d("Test", "incidentList: "+incidentList);
                        if(incidentList.size()>0){
                            incidentRecyclerView.setLayoutManager(new LinearLayoutManager(IncidentListActivity.this));
                            incidentRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            incidentAdapter = new IncidentAdapter(IncidentListActivity.this,incidentList, item -> {
                                Store.SELECTED_INCIDENT = item;
                                Intent incidentActivity = new Intent(IncidentListActivity.this, IncidentActivity.class);
                                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                                startActivity(incidentActivity);
                            });
                            incidentRecyclerView.setAdapter(incidentAdapter);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Incident>> call, Throwable t) {
                    Log.d("Test", "incidentList: "+t.getMessage());
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        switch (incidentStatus){
            case PENDING: incidentHeader.setText(R.string.home_incident_pending_heading); break;
            case COMPLETED: incidentHeader.setText(R.string.home_incident_completed_heading); break;
            case CANCELED: incidentHeader.setText(R.string.home_incident_canceled_heading); break;
        }

        back.setOnClickListener(view -> finish());
    }

}
