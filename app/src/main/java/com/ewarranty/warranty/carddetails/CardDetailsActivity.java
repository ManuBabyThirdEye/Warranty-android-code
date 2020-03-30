package com.ewarranty.warranty.carddetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import lombok.NonNull;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.RaiseIncidentActivity;
import com.ewarranty.warranty.models.Card;
import com.ewarranty.warranty.models.Company;
import com.ewarranty.warranty.models.ImageModel;
import com.ewarranty.warranty.models.LoaderState;
import com.ewarranty.warranty.models.User;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.ewarranty.warranty.util.Store;
import com.ewarranty.warranty.util.ZoomOutPageTransformer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CardDetailsActivity extends AppCompatActivity implements IPickResult {


    public CardDetailMode cardDetailMode;
    long cardId;
    ImageButton edit,close,back;
    public ViewPager cardDetailView;
    public Card card;
    Button raiseRequest;
    public ImageAdapter mAdapter;
    public ShareAdapter shareAdapter;

    private List<ImageModel> selectedImageList = new ArrayList<>();
    private List<User> selectedSharedUserList = new ArrayList<>();
    private LottieAnimationView successLoader,failedLoader,waitingLoader;
    private RelativeLayout loadingLayout;
    private TextView statusPrimary,statusSec;

    public static final int RQS_PICK_CONTACT = 1, REQUEST_CONTACT = 2;
    CardDetailPageViewAdapter cardDetailPageViewAdapter;
    int currentPage = 0;
    int selectedCategoryIndex = 0;
    User dummyUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        if (getIntent()!=null) {
            selectedCategoryIndex = getIntent().getIntExtra("category",0);
        }

        dummyUser = new User();
        dummyUser.setName("Add New");

        Store.SERVICE = ServiceGenerator.createService(UserServices.class);

        if (getIntent()!=null) {
            if(getIntent().getStringExtra("cardDetailMode")!=null){
                cardDetailMode = CardDetailMode.valueOf(getIntent().getStringExtra("cardDetailMode"));
            }else{
                cardDetailMode = CardDetailMode.DISPLAY;
            }
            if(cardDetailMode != CardDetailMode.ADD){
                cardId = getIntent().getLongExtra("cardId",0);
            }
        }
        back = (ImageButton) findViewById(R.id.back);
        edit = (ImageButton) findViewById(R.id.edit);
        close = (ImageButton) findViewById(R.id.close);
        raiseRequest = (Button) findViewById(R.id.raiseRequest);
        successLoader = (LottieAnimationView) findViewById(R.id.successLoader);
        failedLoader = (LottieAnimationView) findViewById(R.id.failedLoader);
        waitingLoader = (LottieAnimationView) findViewById(R.id.waitingLoader);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);

        statusPrimary = (TextView) findViewById(R.id.statusPrimary);
        statusSec = (TextView) findViewById(R.id.statusSec);

        cardDetailView = findViewById(R.id.cardDetailView);
        loadingLayout.setVisibility(View.GONE);

        edit.setVisibility(View.GONE);
        close.setVisibility(View.GONE);

        back.setOnClickListener(view -> {
            finish();
        });

        Store.SERVICE.getCardById(cardId).enqueue(new Callback<Card>() {
            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                card = response.body();
                if(cardDetailMode != CardDetailMode.ADD && card == null){
                    finish();
                }
                updateCardDetails();
            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {

            }
        });

        cardDetailView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.setVisibility(View.GONE);
                close.setVisibility(View.VISIBLE);
                raiseRequest.setVisibility(View.GONE);
                cardDetailMode = CardDetailMode.EDIT;

                Collections.reverse(selectedImageList);
                selectedImageList.add(new ImageModel(true,null,null));
                Collections.reverse(selectedImageList);

                Collections.reverse(selectedSharedUserList);
                selectedSharedUserList.add(dummyUser);
                Collections.reverse(selectedSharedUserList);

                cardDetailView.setAdapter(cardDetailPageViewAdapter);
                cardDetailView.setCurrentItem(currentPage);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                raiseRequest.setVisibility(View.VISIBLE);
                cardDetailMode = CardDetailMode.DISPLAY;
                selectedImageList.clear();
                if(card.getImageUris() != null && !card.getImageUris().isEmpty()){
                    for (String image:
                            card.getImageUris()) {
                        selectedImageList.add(new ImageModel(false,null,image));
                    }
                }
                selectedSharedUserList.clear();
                if(card.getSharedUser() != null && !card.getSharedUser().isEmpty()){
                    for (User user:
                            card.getSharedUser()) {
                        selectedSharedUserList.add(user);
                    }
                }
                cardDetailView.setAdapter(cardDetailPageViewAdapter);
                cardDetailView.setCurrentItem(currentPage);
            }
        });


        raiseRequest.setOnClickListener(view -> {
            Intent raiseIncidentActivity = new Intent(CardDetailsActivity.this, RaiseIncidentActivity.class);
            raiseIncidentActivity.putExtra("cardId",card.getCardId());
            Store.SELECTED_CARD = card;
            CardDetailsActivity.this.startActivity(raiseIncidentActivity);
        });
    }

    private void updateCardDetails() {

        selectedImageList.clear();
        if(cardDetailMode == CardDetailMode.DISPLAY){
            close.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
            raiseRequest.setVisibility(View.VISIBLE);
            if(card.getImageUris() != null && !card.getImageUris().isEmpty()){
                for (String image:
                        card.getImageUris()) {
                    selectedImageList.add(new ImageModel(false,null,image));
                }
            }
        }else if(cardDetailMode == CardDetailMode.EDIT){
            edit.setVisibility(View.GONE);
            close.setVisibility(View.VISIBLE);
            raiseRequest.setVisibility(View.GONE);
            selectedImageList.add(new ImageModel(true,null,null));
            if(card.getImageUris() != null && !card.getImageUris().isEmpty()){
                for (String image:
                        card.getImageUris()) {
                    selectedImageList.add(new ImageModel(false,null,image));
                }
            }
            selectedSharedUserList.add(dummyUser);
            if(card.getSharedUser() != null && !card.getSharedUser().isEmpty()){
                for (User user:
                        card.getSharedUser()) {
                    selectedSharedUserList.add(user);
                }
            }

        }else if(cardDetailMode == CardDetailMode.ADD){
            edit.setVisibility(View.GONE);
            close.setVisibility(View.GONE);
            raiseRequest.setVisibility(View.GONE);
            selectedImageList.add(new ImageModel(true,null,null));
            selectedSharedUserList.add(dummyUser);
        }

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

        mAdapter = new ImageAdapter(CardDetailsActivity.this, selectedImageList, imageModel -> {
            if(imageModel.isAddNew()){
                PickImageDialog.build(pickSetup).show(this);
            }
        });

        shareAdapter = new ShareAdapter(CardDetailsActivity.this, selectedSharedUserList, user -> {
            if(user.getName().equals("Add New")){
                if (ActivityCompat.checkSelfPermission(CardDetailsActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CardDetailsActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, RQS_PICK_CONTACT);
            }
        });

        cardDetailPageViewAdapter = new CardDetailPageViewAdapter(CardDetailsActivity.this.getSupportFragmentManager(),CardDetailsActivity.this);
        cardDetailView.setAdapter(cardDetailPageViewAdapter);
        cardDetailView.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            mAdapter.addItem(new ImageModel(false,r.getBitmap(),null));
        } else {
            Toast.makeText(CardDetailsActivity.this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RQS_PICK_CONTACT){
            if(resultCode == RESULT_OK){
                Uri contactData = data.getData();
                Cursor c =  managedQuery(contactData, null, null, null, null);

                if (c.moveToFirst()) {
                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    User user = new User();
                    try {
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex("data1"));
                            Store.SERVICE.getUserByPhoneNUmber(cNumber).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if(response.body()!=null){
                                        shareAdapter.updateItem(user,response.body());
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {

                                }
                            });
                            user.setPhoneNumber(cNumber);

                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        user.setName(name);
                        shareAdapter.addItem(user);
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
        }else if(requestCode == ProductFragment.SELECT_COMPANY){
            if (resultCode == RESULT_OK) {
                Gson g = new Gson();
                Company company = g.fromJson(data.getStringExtra(ProductFragment.COMPANY_DETAILS), Company.class);
                ProductFragment.company.setText(company.getCompanyName());
                ProductFragment.SELECT_COMPANY_OBJECT = company;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CONTACT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, RQS_PICK_CONTACT);
                } else {
                    finish();
                }
                break;
            }


        }
    }

    public void createOrUpdateCard(){
        Card card = cardDetailPageViewAdapter.getUpdatedCard();
        card.setSharedUser(selectedSharedUserList.stream().filter(u->u.getName() == null || !u.getName().equals("Add New")).collect(Collectors.toList()));
        Log.d("Test", "onFailure: "+selectedSharedUserList);

        if(validateCard(card)){

            List<MultipartBody.Part> billImages = new ArrayList<>();
            int index = 0;
            for(ImageModel image : selectedImageList){
                if(!image.isAddNew() && image.getImage()!=null){
                    billImages.add(prepareFilePart("images","Image"+index,image.getImage()));
                    index++;
                }
            }
            if(cardDetailMode == CardDetailMode.EDIT){
                card.setCardId(cardId);
                if(card.getCompany()!=null){
                    card.setCompanyId(card.getCompany().getCompanyId());
                }

                card.setImageUris(this.card.getImageUris());
                card.setSharedUser(this.card.getSharedUser());
            }

            showLoader(LoaderState.WAIT);

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setDateFormat("dd-MM-yyyy").create();
            String cardString = gson.toJson(card);
            Log.d("Test", "onFailure: "+cardString);
            RequestBody cardRequestBody = createPartFromString(cardString);

            Call<Card> createOrUpdateService;
            createOrUpdateService = Store.SERVICE.createCardForUser(Store.PROFILE.getPhoneNumber(), cardRequestBody, billImages);
            createOrUpdateService.enqueue(new Callback<Card>() {
                @Override
                public void onResponse(Call<Card> call, Response<Card> response) {
                    if(response.body() == null){
                        showLoader(LoaderState.FAILED);
                        Log.d("Test", "onResponse: "+response.body());
                    }else{
                        showLoader(LoaderState.SUCCESS,response.body());
                        Store.CURRENT_CARD_LIST.removeIf(c->c.getCardId()==response.body().getCardId());
                        Collections.reverse(Store.CURRENT_CARD_LIST);
                        Store.CURRENT_CARD_LIST.add(response.body());
                        Collections.reverse(Store.CURRENT_CARD_LIST);
                        Log.d("Test", "onResponse: "+response.body());
                    }
                }

                @Override
                public void onFailure(Call<Card> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("Test", "onResponse: "+t.getMessage());
                    showLoader(LoaderState.FAILED);
                }
            });

        }
    }

    private boolean validateCard(Card card) {
        if(card.getProductName() == null || card.getProductName().equals("")){
            Toast.makeText(CardDetailsActivity.this, "Please enter product name", Toast.LENGTH_LONG).show();
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

    private void showLoader(LoaderState loaderState) {
        showLoader(loaderState,null);
    }

    private void showLoader(LoaderState loaderState,Card card){
        loadingLayout.setVisibility(View.VISIBLE);
        switch (loaderState){
            case WAIT:
                waitingLoader.setVisibility(View.VISIBLE);
                failedLoader.setVisibility(View.GONE);
                successLoader.setVisibility(View.GONE);
                break;
            case SUCCESS:
                waitingLoader.setVisibility(View.GONE);
                failedLoader.setVisibility(View.GONE);
                successLoader.setVisibility(View.VISIBLE);
                statusPrimary.setText(getResources().getString(R.string.raise_incident_complete));
                if(card!=null){
                    statusSec.setText(card.getCardId()+"");
                }
                successLoader.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        CardDetailsActivity.this.finish();
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
