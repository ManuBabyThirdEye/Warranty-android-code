package com.ewarranty.warranty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ewarranty.warranty.carddetails.CardDetailsActivity;
import com.ewarranty.warranty.carddetails.ImageAdapter;
import com.ewarranty.warranty.models.Company;
import com.ewarranty.warranty.models.ImageModel;
import com.ewarranty.warranty.models.Incident;
import com.ewarranty.warranty.models.IncidentStatus;
import com.ewarranty.warranty.services.FloatingWindowProductInformation;
import com.ewarranty.warranty.ui.Category.SpacesItemDecoration;
import com.ewarranty.warranty.util.SettingsAction;
import com.ewarranty.warranty.util.Store;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class IncidentActivity extends AppCompatActivity {

    ImageButton back,edit,close;
    Button markComplete;
    TextView incidentId,description,companyInformation,noImages;
    RecyclerView imageGridView;
    LinearLayout whatsappButton,emailButton,phoneButton,companyLayout,rateLayout;
    List<ImageModel> damagedImageModelList = new ArrayList<>();

    ImageView star1,star2,star3,star4,star5;

    public final static int REQUEST_CODE = 2;

    private static final int REQUEST_PHONE_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident);
        if(Store.SELECTED_INCIDENT == null){
            finish();
        }

        Store.SERVICE = ServiceGenerator.createService(UserServices.class);

        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);


        back = (ImageButton) findViewById(R.id.back);
        edit = (ImageButton) findViewById(R.id.edit);
        close = (ImageButton) findViewById(R.id.close);
        markComplete = (Button) findViewById(R.id.markComplete);

        incidentId = (TextView) findViewById(R.id.incidentId);
        description = (TextView) findViewById(R.id.description);
        companyInformation = (TextView) findViewById(R.id.companyInformation);
        noImages = (TextView) findViewById(R.id.noImages);

        imageGridView = (RecyclerView) findViewById(R.id.imageGridView);

        whatsappButton = (LinearLayout) findViewById(R.id.whatsappButton);
        emailButton = (LinearLayout) findViewById(R.id.emailButton);
        phoneButton = (LinearLayout) findViewById(R.id.phoneButton);
        companyLayout = (LinearLayout) findViewById(R.id.companyLayout);
        rateLayout = (LinearLayout) findViewById(R.id.rateLayout);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        markComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Store.SELECTED_INCIDENT.getRating()==-1){
                    markComplete.setText(getResources().getString(R.string.incident_rate_incident));
                    companyLayout.setVisibility(View.GONE);
                    rateLayout.setVisibility(View.VISIBLE);
                }else{
                    markComplete.setVisibility(View.GONE);
                    Store.SELECTED_INCIDENT.setIncidentStatus(IncidentStatus.COMPLETED);
                    Store.SELECTED_INCIDENT.setWarrantyCardId(Store.SELECTED_INCIDENT.getWarrantyCard().getCardId());
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .setDateFormat("dd-MM-yyyy").create();
                    String incidentString = gson.toJson(Store.SELECTED_INCIDENT);
                    RequestBody incidentRequestBody = SettingsAction.createPartFromString(incidentString);
                    Store.SERVICE.createIncidentForUser(Store.PROFILE.getPhoneNumber(),incidentRequestBody,null).enqueue(new Callback<Incident>() {
                        @Override
                        public void onResponse(Call<Incident> call, Response<Incident> response) {
                            Toast.makeText(IncidentActivity.this,"Rating submitted !!!",Toast.LENGTH_SHORT).show();
                            IncidentListActivity.incidentAdapter.removeItem(Store.SELECTED_INCIDENT);
                        }

                        @Override
                        public void onFailure(Call<Incident> call, Throwable t) {

                        }
                    });
                }
            }
        });

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Store.SELECTED_INCIDENT.getIncidentStatus()==IncidentStatus.PENDING){
                    star1.setImageResource(R.drawable.ic_full_star);
                    star2.setImageResource(R.drawable.ic_empty_star);
                    star3.setImageResource(R.drawable.ic_empty_star);
                    star4.setImageResource(R.drawable.ic_empty_star);
                    star5.setImageResource(R.drawable.ic_empty_star);
                    Store.SELECTED_INCIDENT.setRating(1);
                }
            }
        });

        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Store.SELECTED_INCIDENT.getIncidentStatus()==IncidentStatus.PENDING){
                    star1.setImageResource(R.drawable.ic_full_star);
                    star2.setImageResource(R.drawable.ic_full_star);
                    star3.setImageResource(R.drawable.ic_empty_star);
                    star4.setImageResource(R.drawable.ic_empty_star);
                    star5.setImageResource(R.drawable.ic_empty_star);
                    Store.SELECTED_INCIDENT.setRating(2);
                }
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Store.SELECTED_INCIDENT.getIncidentStatus()==IncidentStatus.PENDING){
                    star1.setImageResource(R.drawable.ic_full_star);
                    star2.setImageResource(R.drawable.ic_full_star);
                    star3.setImageResource(R.drawable.ic_full_star);
                    star4.setImageResource(R.drawable.ic_empty_star);
                    star5.setImageResource(R.drawable.ic_empty_star);
                    Store.SELECTED_INCIDENT.setRating(3);
                }
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Store.SELECTED_INCIDENT.getIncidentStatus()==IncidentStatus.PENDING){
                    star1.setImageResource(R.drawable.ic_full_star);
                    star2.setImageResource(R.drawable.ic_full_star);
                    star3.setImageResource(R.drawable.ic_full_star);
                    star4.setImageResource(R.drawable.ic_full_star);
                    star5.setImageResource(R.drawable.ic_empty_star);
                    Store.SELECTED_INCIDENT.setRating(4);
                }
            }
        });

        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Store.SELECTED_INCIDENT.getIncidentStatus()==IncidentStatus.PENDING){
                    star1.setImageResource(R.drawable.ic_full_star);
                    star2.setImageResource(R.drawable.ic_full_star);
                    star3.setImageResource(R.drawable.ic_full_star);
                    star4.setImageResource(R.drawable.ic_full_star);
                    star5.setImageResource(R.drawable.ic_full_star);
                    Store.SELECTED_INCIDENT.setRating(5);
                }
            }
        });

        Store.SERVICE.getIncidentById(Store.SELECTED_INCIDENT.getIncidentId()).enqueue(new Callback<Incident>() {
            @Override
            public void onResponse(Call<Incident> call, Response<Incident> response) {
                if(response.body()!=null){
                    Log.d("Test", "getIncidentById: "+response.body());
                    Store.SELECTED_INCIDENT = response.body();
                    updateIncident();
                }
            }

            @Override
            public void onFailure(Call<Incident> call, Throwable t) {
                Log.d("Test", "getIncidentById: "+t.getMessage());
            }
        });

        whatsappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toNumber = Store.SELECTED_INCIDENT.getWarrantyCard().getCompany().getSupportWhatsappNumber();
                toNumber = toNumber.replace("+", "").replace(" ", "");
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                sendIntent.putExtra(Intent.EXTRA_TEXT, createMessageForWhatsApp(Store.SELECTED_INCIDENT));
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFloatingInformationActivity();
                String[] TO = {Store.SELECTED_INCIDENT.getWarrantyCard().getCompany().getSupportEmailId()};
                String[] CC = {"manub.varkala@gmail.com"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Quvee] "+Store.SELECTED_INCIDENT.getWarrantyCard().getProductId()+" damage request");
                emailIntent.putExtra(Intent.EXTRA_TEXT, createMessageForEmail(Store.SELECTED_INCIDENT));

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                }
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(IncidentActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(IncidentActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Store.SELECTED_INCIDENT.getWarrantyCard().getCompany().getSupportPhoneNumber()));
                startActivity(intent);
                createFloatingInformationActivity();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void updateIncident() {
        incidentId.setText(Store.SELECTED_INCIDENT.getIncidentId()+"");

        if(!CollectionUtils.isEmpty(Store.SELECTED_INCIDENT.getIncidentImages())){
            imageGridView.setVisibility(View.VISIBLE);
            noImages.setVisibility(View.GONE);

            for (Incident.ImageObject url: Store.SELECTED_INCIDENT.getIncidentImages()) {
                damagedImageModelList.add(new ImageModel(false,null,url.getImageUris()));
            }

            GridLayoutManager mLayoutManager = new GridLayoutManager(IncidentActivity.this,4);
            imageGridView.setLayoutManager(mLayoutManager);
            imageGridView.setItemAnimator(new DefaultItemAnimator());
            ImageAdapter mAdapter = new ImageAdapter(IncidentActivity.this, damagedImageModelList, imageModel -> {

            });
            imageGridView.setAdapter(mAdapter);
            imageGridView.addItemDecoration(new SpacesItemDecoration(30));
            imageGridView.setNestedScrollingEnabled(false);
        }else{
            imageGridView.setVisibility(View.GONE);
            noImages.setVisibility(View.VISIBLE);
        }

        if(Store.SELECTED_INCIDENT.getIncidentDescription()!=null){
            description.setText(Store.SELECTED_INCIDENT.getIncidentDescription());
        }

        if(Store.SELECTED_INCIDENT.getIncidentStatus()== IncidentStatus.PENDING){
            companyLayout.setVisibility(View.VISIBLE);
            rateLayout.setVisibility(View.GONE);
            if(Store.SELECTED_INCIDENT.getWarrantyCard()!=null && Store.SELECTED_INCIDENT.getWarrantyCard().getCompany()!=null){
                boolean anySupport = false;

                Company thisCompany =Store.SELECTED_INCIDENT.getWarrantyCard().getCompany();
                if(thisCompany.getSupportWhatsappNumber()!=null){
                    whatsappButton.setVisibility(View.VISIBLE);
                    anySupport = true;
                }
                if(thisCompany.getSupportEmailId()!=null){
                    emailButton.setVisibility(View.VISIBLE);
                    anySupport = true;
                }
                if(thisCompany.getSupportPhoneNumber()!=null){
                    phoneButton.setVisibility(View.VISIBLE);
                    anySupport = true;
                }

                if(anySupport){
                    companyInformation.setText(getResources().getString(R.string.raise_incident_has_contact_1)+" "+thisCompany.getCompanyName()+" "+getResources().getString(R.string.raise_incident_has_contact_2));
                }else{
                    companyInformation.setText(thisCompany.getCompanyName()+" "+getResources().getString(R.string.raise_incident_no_contact));
                }
            }else if(Store.SELECTED_INCIDENT.getWarrantyCard()!=null){
                companyInformation.setText(getResources().getString(R.string.raise_incident_no_company));
            }else{
                companyInformation.setText(getResources().getString(R.string.raise_incident_no_card));
            }
            markComplete.setText(getResources().getString(R.string.incident_mark_complete));
        }else if(Store.SELECTED_INCIDENT.getIncidentStatus()== IncidentStatus.COMPLETED){
            if(Store.SELECTED_INCIDENT.getRating()<0){
                markComplete.setText(getResources().getString(R.string.incident_rate_incident));
            }else{
                if(Store.SELECTED_INCIDENT.getRating()>=1){
                    star1.setImageResource(R.drawable.ic_full_star);
                }
                if(Store.SELECTED_INCIDENT.getRating()>=2){
                    star2.setImageResource(R.drawable.ic_full_star);
                }
                if(Store.SELECTED_INCIDENT.getRating()>=3){
                    star3.setImageResource(R.drawable.ic_full_star);
                }
                if(Store.SELECTED_INCIDENT.getRating()>=4){
                    star4.setImageResource(R.drawable.ic_full_star);
                }
                if(Store.SELECTED_INCIDENT.getRating()>=5){
                    star5.setImageResource(R.drawable.ic_full_star);
                }
                markComplete.setVisibility(View.GONE);
            }
            companyLayout.setVisibility(View.GONE);
            rateLayout.setVisibility(View.VISIBLE);
        }


    }

    private String createMessageForEmail(Incident selectedIncident) {

        String data = "Hi Team," +
                "\n\nThe product "+selectedIncident.getWarrantyCard().getProductName()+" is damaged. Please " +
                "find more information regarding the product.\n" +
                "\nProduct Name : "+selectedIncident.getWarrantyCard().getProductName() +
                "\nProduct ID : "+ selectedIncident.getWarrantyCard().getProductId() +
                "\nMore Product Info : "+selectedIncident.getWarrantyCard().getProductOtherInformation() +
                "\nBill Number : "+ selectedIncident.getWarrantyCard().getBillNumber() +
                "\nBilled Date : "+selectedIncident.getWarrantyCard().getBillingDate() +
                "\nWarranty End Date : "+findExpireDate(selectedIncident.getWarrantyCard().getBillingDate(),selectedIncident.getWarrantyCard().getWarrantyPeriod());


        if(!CollectionUtils.isEmpty(selectedIncident.getIncidentImages())){
            data = data +
                    "\n\nClick below link to view the damaged product image" +
                    "";
        }

        data = data +"\n\nPlease let me know, if you need any more information" +
                "\n\nThanks in Advance!!!" +
                "\n"+Store.PROFILE.getName();
        return data;
    }

    private String createMessageForWhatsApp(Incident selectedIncident) {

        return "Hi Team," +
                "\n\nThe product "+selectedIncident.getWarrantyCard().getProductName()+" is damaged. Please " +
                "find more information regarding the product.\n" +
                "\nProduct Name : "+selectedIncident.getWarrantyCard().getProductName() +
                "\nProduct ID : "+ selectedIncident.getWarrantyCard().getProductId() +
                "\nMore Product Info : "+selectedIncident.getWarrantyCard().getProductOtherInformation() +
                "\nBill Number : "+ selectedIncident.getWarrantyCard().getBillNumber() +
                "\nBilled Date : "+selectedIncident.getWarrantyCard().getBillingDate() +
                "\nWarranty End Date : "+findExpireDate(selectedIncident.getWarrantyCard().getBillingDate(),selectedIncident.getWarrantyCard().getWarrantyPeriod()) +
                "\n\nPlease let me know, if you need any more information" +
                "\n\nThanks in Advance!!!" +
                "\n"+Store.PROFILE.getName();
    }

    private Date findExpireDate(Date billDate, long warrantyPeriod) {
        if(billDate == null){
            return new Date();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(billDate);
        c.add(Calendar.DATE,Integer.parseInt(warrantyPeriod+""));
        return c.getTime();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + Store.SELECTED_INCIDENT.getWarrantyCard().getCompany().getSupportPhoneNumber()));
                    startActivity(intent);
                } else {
                    finish();
                }
                break;
            }


        }
    }

    public void createFloatingInformationActivity(){
        if (SettingsAction.canDrawOverlays(this)) {
            Log.d("Test", "createFloatingInformationActivity: PERMISSION_GRANTED");
            startService(new Intent(IncidentActivity.this, FloatingWindowProductInformation.class));
        }else{
            Log.d("Test", "createFloatingInformationActivity: NO PERMISSION_GRANTED");
            checkDrawOverlayPermission();
        }
    }

    public void checkDrawOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (SettingsAction.canDrawOverlays(this)) {
                Log.d("Test", "createFloatingInformationActivity: PERMISSION_GRANTED");
                startService(new Intent(IncidentActivity.this, FloatingWindowProductInformation.class));
            }
        }
    }
}
