package com.ewarranty.warranty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lombok.NonNull;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ewarranty.warranty.carddetails.ImageAdapter;
import com.ewarranty.warranty.models.ImageModel;
import com.ewarranty.warranty.models.Incident;
import com.ewarranty.warranty.models.IncidentStatus;
import com.ewarranty.warranty.models.LoaderState;
import com.ewarranty.warranty.util.SettingsAction;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.ewarranty.warranty.ui.Category.SpacesItemDecoration;
import com.ewarranty.warranty.util.Store;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RaiseIncidentActivity extends AppCompatActivity implements IPickResult {

    private long cardId;
    private EditText description;
    private TextView issueStatusPrimary, issueStatusSec;
    private ImageButton back;
    private Button raiseRequest;
    private RelativeLayout loadingLayout;
    private LottieAnimationView successLoader,failedLoader,waitingLoader;
    private RecyclerView imageGridView;

    private List<ImageModel> selectedImageList = new ArrayList<>();
    public ImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_incident);

        if (getIntent()!=null) {
            cardId = getIntent().getLongExtra("cardId",0L);
        }

        Store.SERVICE = ServiceGenerator.createService(UserServices.class);

        imageGridView = (RecyclerView)findViewById(R.id.imageGridView);

        description = (EditText)findViewById(R.id.description);
        issueStatusPrimary = (TextView)findViewById(R.id.issueStatusPrimary);
        issueStatusSec = (TextView)findViewById(R.id.issueStatusSec);

        back = (ImageButton)findViewById(R.id.back);
        raiseRequest = (Button)findViewById(R.id.raiseRequest);

        loadingLayout = (RelativeLayout)findViewById(R.id.loadingLayout);

        successLoader = (LottieAnimationView)findViewById(R.id.successLoader);
        failedLoader = (LottieAnimationView)findViewById(R.id.failedLoader);
        waitingLoader = (LottieAnimationView)findViewById(R.id.waitingLoader);

        back.setOnClickListener(view -> {
            finish();
        });

        raiseRequest.setOnClickListener(view -> {
            SettingsAction.hideSoftKeyboard(RaiseIncidentActivity.this);
            Incident incident = new Incident();
            incident.setIncidentDescription(description.getText().toString());
            incident.setWarrantyCardId(cardId);
            incident.setIncidentStatus(IncidentStatus.PENDING);
            incident.setRating(-1);
            if(validateIncident(incident)) {

                List<MultipartBody.Part> incidentImages = new ArrayList<>();
                int index = 0;
                for (ImageModel image : selectedImageList) {
                    if (!image.isAddNew() && image.getImage() != null) {
                        incidentImages.add(prepareFilePart("images", "Image" + index, image.getImage()));
                        index++;
                    }
                }

                showLoader(LoaderState.WAIT);

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .setDateFormat("dd-MM-yyyy").create();
                String incidentString = gson.toJson(incident);
                Log.d("Test", "onFailure: " + incidentString);
                RequestBody incidentRequestBody = createPartFromString(incidentString);


                Store.SERVICE.createIncidentForUser(Store.PROFILE.getPhoneNumber(), incidentRequestBody, incidentImages).enqueue(new Callback<Incident>() {
                    @Override
                    public void onResponse(Call<Incident> call, Response<Incident> response) {
                        if (response.body() == null) {
                            showLoader(LoaderState.FAILED);
                        } else {
                            showLoader(LoaderState.SUCCESS,response.body());
                            Log.d("Test", "onResponse: " + response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Incident> call, Throwable t) {
                        showLoader(LoaderState.FAILED);
                    }
                });
            }
        });

        description.setOnFocusChangeListener((view, b) -> {
            if(b){
                description.setBackground(getDrawable(R.drawable.textarea_active_bk));
            }else{
                description.setBackground(getDrawable(R.drawable.textarea_inactive_bk));
            }
        });

        PickSetup pickSetup =new PickSetup()
                .setTitle(getResources().getString(R.string.photo_selection_text))
                .setCancelText(getResources().getString(R.string.photo_cancel_text))
                .setCameraButtonText(getResources().getString(R.string.camara_selection_text))
                .setGalleryButtonText(getResources().getString(R.string.gallery_selection_text))
                .setTitleColor(getResources().getColor(R.color.primaryTextColor,null))
                .setBackgroundColor(getResources().getColor(R.color.mainBackground,null))
                .setCancelTextColor(getResources().getColor(R.color.primaryTextColor,null))
                .setButtonTextColor(getResources().getColor(R.color.primaryTextColor,null))
                .setIconGravity(Gravity.TOP)
                .setButtonOrientation(LinearLayout.HORIZONTAL)
                .setGalleryIcon(R.drawable.gallery)
                .setCameraIcon(R.drawable.camera);

        selectedImageList.add(new ImageModel(true,null,null));

        mAdapter = new ImageAdapter(RaiseIncidentActivity.this, selectedImageList, imageModel -> {
            if(imageModel.isAddNew()){
                PickImageDialog.build(pickSetup).show(this);
            }
        });

        GridLayoutManager mLayoutManager = new GridLayoutManager(RaiseIncidentActivity.this,4);
        imageGridView.setLayoutManager(mLayoutManager);
        imageGridView.setItemAnimator(new DefaultItemAnimator());

        imageGridView.setAdapter(mAdapter);
        imageGridView.addItemDecoration(new SpacesItemDecoration(30));
        imageGridView.setNestedScrollingEnabled(false);

    }

    private boolean validateIncident(Incident incident) {
        if(incident.getIncidentDescription() == null || incident.getIncidentDescription().equals("")){
            Toast.makeText(RaiseIncidentActivity.this, "Please enter description", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName,String fileName, Bitmap fileBitmap) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        fileBitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, bos.toByteArray());



        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, fileName, requestBody);
    }
    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            mAdapter.addItem(new ImageModel(false,r.getBitmap(),r.getUri().toString()));
        } else {
            Toast.makeText(RaiseIncidentActivity.this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void showLoader(LoaderState loaderState){
        showLoader(loaderState,null);
    }

    private void showLoader(LoaderState loaderState,Incident incident){
        loadingLayout.setVisibility(View.VISIBLE);
        switch (loaderState){
            case WAIT:
                waitingLoader.setVisibility(View.VISIBLE);
                failedLoader.setVisibility(View.GONE);
                successLoader.setVisibility(View.GONE);
                issueStatusPrimary.setVisibility(View.VISIBLE);
                issueStatusSec.setVisibility(View.VISIBLE);
                issueStatusPrimary.setText(getResources().getString(R.string.raise_incident_loading_1));
                issueStatusSec.setText(getResources().getString(R.string.raise_incident_loading_2));
                break;
            case SUCCESS:
                waitingLoader.setVisibility(View.GONE);
                failedLoader.setVisibility(View.GONE);
                successLoader.setVisibility(View.VISIBLE);
                issueStatusPrimary.setVisibility(View.VISIBLE);
                issueStatusSec.setVisibility(View.VISIBLE);
                issueStatusPrimary.setText(getResources().getString(R.string.raise_incident_complete));
                issueStatusSec.setText(incident.getIncidentId()+"");
                successLoader.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        Store.SELECTED_INCIDENT = incident;
                        Intent incident = new Intent(RaiseIncidentActivity.this, IncidentActivity.class);
                        RaiseIncidentActivity.this.startActivity(incident);
                        RaiseIncidentActivity.this.finish();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                break;
            case FAILED:
                waitingLoader.setVisibility(View.GONE);
                failedLoader.setVisibility(View.VISIBLE);
                successLoader.setVisibility(View.GONE);
                issueStatusPrimary.setVisibility(View.VISIBLE);
                issueStatusSec.setVisibility(View.GONE);
                issueStatusPrimary.setText(getResources().getString(R.string.raise_incident_failed));
                failedLoader.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        loadingLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                break;
        }
    }
}
