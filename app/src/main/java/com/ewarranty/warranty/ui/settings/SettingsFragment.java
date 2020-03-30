package com.ewarranty.warranty.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bumptech.glide.Glide;
import com.ewarranty.warranty.BuildConfig;
import com.ewarranty.warranty.IncidentActivity;
import com.ewarranty.warranty.LoginActivity;
import com.ewarranty.warranty.NotificationActivity;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.RequestActivity;
import com.ewarranty.warranty.dialog.LanguageSelectDialogFragment;
import com.ewarranty.warranty.dialog.ThemeSelectDialogFragment;
import com.ewarranty.warranty.models.Card;
import com.ewarranty.warranty.models.Company;
import com.ewarranty.warranty.models.Incident;
import com.ewarranty.warranty.models.IncidentCount;
import com.ewarranty.warranty.models.Language;
import com.ewarranty.warranty.models.Theme;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.ewarranty.warranty.util.SettingsAction;
import com.ewarranty.warranty.util.Store;
import com.google.firebase.auth.FirebaseAuth;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Date;

public class SettingsFragment extends Fragment implements IPickResult {

    private SettingsViewModel settingsViewModel;

    private TextView name,phoneNumberOrEmailId,notificationCountTxt,incidentCountCount,currentLanguage,currentTheme;
    private RelativeLayout gotoNotification,gotoShare,gotoContact,gotoAbout,gotoIncident;
    private CardView notificationBlock;
    private RelativeLayout logout,changeLanguage,changeTheme;
    private ImageView profilePic;

    private int unreadNotificationCount = 3;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Store.SERVICE = ServiceGenerator.createService(UserServices.class);

        name = (TextView)root.findViewById(R.id.name);
        phoneNumberOrEmailId = (TextView)root.findViewById(R.id.phoneNumberOrEmailId);
        notificationCountTxt = (TextView)root.findViewById(R.id.notificationCount);
        incidentCountCount = (TextView)root.findViewById(R.id.incidentCountCount);

        currentLanguage = (TextView)root.findViewById(R.id.currentLanguage);
        currentTheme = (TextView)root.findViewById(R.id.currentTheme);

        gotoNotification = (RelativeLayout)root.findViewById(R.id.gotoNotification);
        gotoShare = (RelativeLayout)root.findViewById(R.id.gotoShare);
        gotoContact = (RelativeLayout)root.findViewById(R.id.gotoContact);
        gotoAbout = (RelativeLayout)root.findViewById(R.id.gotoAbout);
        gotoIncident = (RelativeLayout)root.findViewById(R.id.gotoIncident);

        notificationBlock = (CardView)root.findViewById(R.id.notificationBlock);
        profilePic = (ImageView)root.findViewById(R.id.profilePic);

        logout = (RelativeLayout)root.findViewById(R.id.logout);
        changeLanguage = (RelativeLayout)root.findViewById(R.id.changeLanguage);
        changeTheme = (RelativeLayout)root.findViewById(R.id.changeTheme);


        Language language = SettingsAction.getInstance(getActivity()).getCurrentLanguage();
        if(language == Language.MALAYALAM){
            currentLanguage.setText("മലയാളം");
        }else{
            currentLanguage.setText(language.toString());
        }
        if(Store.PROFILE != null && Store.PROFILE.getProfileUri()!=null){
            Glide.with(getActivity())
                    .load(Store.PROFILE.getProfileUri()).centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(profilePic);
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

        profilePic.setOnClickListener(view -> {
            PickImageDialog.build(pickSetup,SettingsFragment.this).show(getActivity());
        });

        Theme theme = SettingsAction.getInstance(getActivity()).getCurrentTheme();
        if(theme == Theme.LITE){
            currentTheme.setText(R.string.settings_theme_lite);
        }else if(theme == Theme.DARK){
            currentTheme.setText(R.string.settings_theme_dark);
        }else{
            currentTheme.setText(R.string.settings_theme_auto);
        }

        Store.SERVICE.getAllRequestCount(Store.PROFILE.getPhoneNumber()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body()!=null){
                    incidentCountCount.setText(response.body()+"");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(unreadNotificationCount>99){
            notificationBlock.setVisibility(View.VISIBLE);
            notificationCountTxt.setText("99+");
        }else if(unreadNotificationCount>0){
            notificationBlock.setVisibility(View.VISIBLE);
            notificationCountTxt.setText(unreadNotificationCount+"");
        }else{
            notificationBlock.setVisibility(View.GONE);
        }

        if(Store.PROFILE !=null && Store.PROFILE.getName()!=null){
            name.setText(Store.PROFILE.getName().toUpperCase());
        }
        if(Store.PROFILE !=null && Store.PROFILE.getPhoneNumber()!=null && !Store.PROFILE.getPhoneNumber().equals("")){
            phoneNumberOrEmailId.setText(Store.PROFILE.getPhoneNumber());
        }else if(Store.PROFILE !=null && Store.PROFILE.getEmailId()!=null && !Store.PROFILE.getEmailId().equals("")){
            phoneNumberOrEmailId.setText(Store.PROFILE.getEmailId());
        }

        logout.setOnClickListener(view -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Logout")
                    .setMessage("Do you really want to logout?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            FirebaseAuth.getInstance().signOut();
                            Intent login = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(login);
                            getActivity().finish();
                            Toast.makeText(getActivity(), "Yaay", Toast.LENGTH_SHORT).show();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        });

        changeLanguage.setOnClickListener(view -> {
            new LanguageSelectDialogFragment().setOnItemSelectListener(new LanguageSelectDialogFragment.OnItemSelectListener() {
                @Override
                public void onItemSelectListener(Language language,String languageShot) {
                    SettingsAction.getInstance(getContext()).updateCurrentLanguage(language,languageShot);
                    Intent intent = getActivity().getIntent();
                    intent.putExtra("page",2);
                    getActivity().finish();
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            }).show(getFragmentManager(),"dialog");
        });

        changeTheme.setOnClickListener(view -> {
            new ThemeSelectDialogFragment().setOnItemSelectListener(new ThemeSelectDialogFragment.OnItemSelectListener() {
                @Override
                public void onItemSelectListener(Theme theme) {
                    SettingsAction.getInstance(getContext()).updateCurrentTheme(theme);
                    Intent intent = getActivity().getIntent();
                    intent.putExtra("page",2);
                    getActivity().finish();
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }).show(getFragmentManager(),"dialog");
        });

        gotoNotification.setOnClickListener(view -> {
            Intent notification = new Intent(getActivity(), NotificationActivity.class);
            getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            getActivity().startActivity(notification);
        });

        gotoIncident.setOnClickListener(view -> {

            /*Store.SELECTED_INCIDENT = new Incident();
            Store.SELECTED_INCIDENT.setIncidentDescription("This is a dummy incident description");
            Store.SELECTED_INCIDENT.setIncidentId(43124532645345L);
            Store.SELECTED_INCIDENT.setRelatedWarrantyCard(new Card());
            Store.SELECTED_INCIDENT.getRelatedWarrantyCard().setBillNumber("43124235231");
            Store.SELECTED_INCIDENT.getRelatedWarrantyCard().setProductId("783643452452");
            Store.SELECTED_INCIDENT.getRelatedWarrantyCard().setProductName("Fridge");
            Store.SELECTED_INCIDENT.getRelatedWarrantyCard().setBillingDate(new Date());
            Store.SELECTED_INCIDENT.getRelatedWarrantyCard().setWarrantyPeriod(245);
            Store.SELECTED_INCIDENT.getRelatedWarrantyCard().setImageUris(Arrays.asList("http://image1","http://image2","http://image3"));
            Store.SELECTED_INCIDENT.getRelatedWarrantyCard().setCompany(new Company(1,"Polycab","","manub.varkala@gmail.com","+919539417692","+919539417692"));
            Intent incident = new Intent(getActivity(), IncidentActivity.class);
            getActivity().startActivity(incident);*/

            Intent request = new Intent(getActivity(), RequestActivity.class);
            getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            getActivity().startActivity(request);
        });

        gotoShare.setOnClickListener(view -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "eWarranty");
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
            }
        });

    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            Bitmap imageBitmap = r.getBitmap();
            Glide.with(getActivity())
                    .load(imageBitmap).centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(profilePic);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            RequestBody requestBody = RequestBody.create(MultipartBody.FORM, bos.toByteArray());
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("profileFile", "Profile_pic.jpg", requestBody);
            Store.SERVICE.addProfilePic(Store.PROFILE.getPhoneNumber(),imagePart).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("Test", "onFailure: "+response);
                    Toast.makeText(getActivity(),response.body(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("Test", "onFailure: "+t.toString());
                    Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }



}