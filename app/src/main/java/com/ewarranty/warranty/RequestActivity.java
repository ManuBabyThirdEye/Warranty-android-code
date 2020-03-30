package com.ewarranty.warranty;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewarranty.warranty.models.IncidentCount;
import com.ewarranty.warranty.models.IncidentStatus;
import com.ewarranty.warranty.util.Store;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;

public class RequestActivity extends AppCompatActivity {

    private CardView noIncidentCard;
    private LinearLayout incidentCountLayout,pendingIncidentLayout,completedIncidentLayout,canceledIncidentLayout;
    private TextView pendingIncidentCount,completedIncidentCount,canceledIncidentCount;
    private ImageView gotoPendingIncident,gotoCompletedIncident,gotoCanceledIncident,back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        Store.SERVICE = ServiceGenerator.createService(UserServices.class);

        incidentCountLayout = (LinearLayout)findViewById(R.id.incidentCountLayout);
        pendingIncidentLayout = (LinearLayout)findViewById(R.id.pendingIncidentLayout);
        completedIncidentLayout = (LinearLayout)findViewById(R.id.completedIncidentLayout);
        canceledIncidentLayout = (LinearLayout)findViewById(R.id.canceledIncidentLayout);

        pendingIncidentCount = (TextView)findViewById(R.id.pendingIncidentCount);
        completedIncidentCount = (TextView)findViewById(R.id.completedIncidentCount);
        canceledIncidentCount = (TextView)findViewById(R.id.canceledIncidentCount);

        gotoPendingIncident = (ImageView)findViewById(R.id.gotoPendingIncident);
        gotoCompletedIncident = (ImageView)findViewById(R.id.gotoCompletedIncident);
        gotoCanceledIncident = (ImageView)findViewById(R.id.gotoCanceledIncident);
        back = (ImageView)findViewById(R.id.back);



    }

    private void updateIncidentCount(IncidentCount incidentCount) {
        if(incidentCount.isEmpty()){
            noIncidentCard.setVisibility(View.VISIBLE);
            incidentCountLayout.setVisibility(View.GONE);
        }else{
            noIncidentCard.setVisibility(View.GONE);
            incidentCountLayout.setVisibility(View.VISIBLE);
            if(incidentCount.getPendingIncidents()>0){
                pendingIncidentLayout.setVisibility(View.VISIBLE);
                pendingIncidentCount.setText(incidentCount.getPendingIncidents()+"");
            }else{
                pendingIncidentLayout.setVisibility(View.GONE);
            }

            if(incidentCount.getCompletedIncidents()>0){
                completedIncidentLayout.setVisibility(View.VISIBLE);
                completedIncidentCount.setText(incidentCount.getCompletedIncidents()+"");
            }else{
                completedIncidentLayout.setVisibility(View.GONE);
            }

            if(incidentCount.getCanceledIncidents()>0){
                canceledIncidentLayout.setVisibility(View.VISIBLE);
                canceledIncidentCount.setText(incidentCount.getCanceledIncidents()+"");
            }else{
                canceledIncidentLayout.setVisibility(View.GONE);
            }
        }

        pendingIncidentLayout.setOnClickListener(view -> {
            Intent incident = new Intent(RequestActivity.this, IncidentListActivity.class);
            incident.putExtra("status", IncidentStatus.PENDING.toString());
            RequestActivity.this.startActivity(incident);
        });

        completedIncidentLayout.setOnClickListener(view -> {
            Intent incident = new Intent(RequestActivity.this, IncidentListActivity.class);
            incident.putExtra("status", IncidentStatus.COMPLETED.toString());
            RequestActivity.this.startActivity(incident);
        });

        canceledIncidentCount.setOnClickListener(view -> {
            Intent incident = new Intent(RequestActivity.this, IncidentListActivity.class);
            incident.putExtra("status", IncidentStatus.CANCELED.toString());
            RequestActivity.this.startActivity(incident);
        });

        back.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        noIncidentCard = (CardView)findViewById(R.id.noIncidentCard);

        Store.SERVICE.getAllRequestCountByStatus(Store.PROFILE.getPhoneNumber()).enqueue(new Callback<IncidentCount>() {
            @Override
            public void onResponse(Call<IncidentCount> call, Response<IncidentCount> response) {
                if(response.body()!=null){
                    updateIncidentCount(response.body());
                    pendingIncidentCount.setText(response.body().getPendingIncidents()+"");
                    completedIncidentCount.setText(response.body().getCompletedIncidents()+"");
                    canceledIncidentCount.setText(response.body().getCanceledIncidents()+"");
                }
            }

            @Override
            public void onFailure(Call<IncidentCount> call, Throwable t) {

            }
        });
    }
}
