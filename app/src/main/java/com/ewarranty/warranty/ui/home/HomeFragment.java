package com.ewarranty.warranty.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.ewarranty.warranty.OfferDetailsActivity;
import com.ewarranty.warranty.OfferListActivity;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.CategoryType;
import com.ewarranty.warranty.models.Offer;
import com.ewarranty.warranty.models.Role;
import com.ewarranty.warranty.models.Shop;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.ewarranty.warranty.util.Store;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ViewPager pager;
    private TabLayout paidOfferTabLayout;
    private LinearLayout offerForYouLayout,custView,shopView;
    private ViewSkeletonScreen skeletonScreen;
    private TextView offerForYouText;
    private Spinner shopListSpinner;
    ArrayAdapter<CharSequence> shopListSpinnerAdapter;

    private Map<Shop, List<Offer>> yourOfferMap = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        offerForYouText = (TextView) root.findViewById(R.id.offerForYouText);

        pager = (ViewPager) root.findViewById(R.id.paidOfferViewPagerNew);
        paidOfferTabLayout = (TabLayout) root.findViewById(R.id.paidOfferTabLayout);

        paidOfferTabLayout.setupWithViewPager(pager);

        offerForYouLayout = (LinearLayout) root.findViewById(R.id.offerForYouLayout);

        custView = (LinearLayout) root.findViewById(R.id.custView);
        shopView = (LinearLayout) root.findViewById(R.id.shopView);

        shopListSpinner = (Spinner) root.findViewById(R.id.shopListSpinner);
        shopListSpinner.setEnabled(true);

        skeletonScreen = Skeleton.bind(offerForYouLayout)
                .load(R.layout.card_offer_skeleton)
                .show();

        Store.SERVICE = ServiceGenerator.createService(UserServices.class);

        if(Store.PROFILE!=null && Store.PROFILE.getRole() == Role.CUSTOMER){
            custView.setVisibility(View.VISIBLE);
            shopView.setVisibility(View.GONE);
            shopListSpinner.setVisibility(View.GONE);
            Store.SERVICE.mostHappeningOffers().enqueue(new Callback<List<Offer>>() {
                @Override
                public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                    if(response.body() !=null){
                        Log.d("Test", "Offer: "+response.body());

                        Store.MOST_HAPPENING_OFFER_LIST = response.body();
                        PagerAdapter adapter = new PaidOfferAdapter(getContext(), Store.MOST_HAPPENING_OFFER_LIST);
                        pager.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<List<Offer>> call, Throwable t) {
                    Store.MOST_HAPPENING_OFFER_LIST = new ArrayList<>();
                }
            });

            Store.SERVICE.myStoreOffers(Store.PROFILE.getPhoneNumber(),0,30).enqueue(new Callback<List<Offer>>() {
                @Override
                public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                    if(response.body() !=null){
                        Log.d("Test", "Offer: "+response.body());
                        Store.MY_SHOP_OFFER_LIST = response.body();
                        createYourOfferMap();
                        createOfferLayout(offerForYouLayout,yourOfferMap);
                    }
                }

                @Override
                public void onFailure(Call<List<Offer>> call, Throwable t) {
                    Store.MY_SHOP_OFFER_LIST = new ArrayList<>();
                    //createYourOfferMap();
                    //createOfferLayout(offerForYouLayout,yourOfferMap);
                }
            });
            Store.SERVICE.myPaidOffers(0,30).enqueue(new Callback<List<Offer>>() {
                @Override
                public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                    if(response.body() !=null){
                        Store.PAID_OFFER_LIST = response.body();
                        createYourOfferMap();
                        createOfferLayout(offerForYouLayout,yourOfferMap);
                    }
                }

                @Override
                public void onFailure(Call<List<Offer>> call, Throwable t) {
                    Store.PAID_OFFER_LIST = new ArrayList<>();
                    //createYourOfferMap();
                    //createOfferLayout(offerForYouLayout,yourOfferMap);
                }
            });
        }else if(Store.PROFILE !=null && Store.PROFILE.getRole() == Role.SHOP){
            custView.setVisibility(View.GONE);
            shopView.setVisibility(View.VISIBLE);
            shopListSpinner.setVisibility(View.VISIBLE);
            shopListSpinnerAdapter = new ArrayAdapter(getActivity(),R.layout.my_simple_text_view, Store.PROFILE.getShopVOList().stream().map(Shop::getShopName).collect(Collectors.toList()));
            shopListSpinnerAdapter.setDropDownViewResource(R.layout.my_simple_spinner_dropdown_item);
            shopListSpinner.setAdapter(shopListSpinnerAdapter);
            shopListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Store.SELECTED_SHOP = Store.PROFILE.getShopVOList().get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void createYourOfferMap() {
        yourOfferMap = new HashMap<>();
        if(Store.PAID_OFFER_LIST!=null && !Store.PAID_OFFER_LIST.isEmpty()){
            for (Offer offer: Store.PAID_OFFER_LIST) {
                if(yourOfferMap.get(offer.getShop())==null){
                    yourOfferMap.put(offer.getShop(),new ArrayList<>());
                }
                yourOfferMap.get(offer.getShop()).add(offer);
            }
        }
        if(Store.MY_SHOP_OFFER_LIST!=null && !Store.MY_SHOP_OFFER_LIST.isEmpty()){
            for (Offer offer: Store.MY_SHOP_OFFER_LIST) {
                if(yourOfferMap.get(offer.getShop())==null){
                    yourOfferMap.put(offer.getShop(),new ArrayList<>());
                }
                yourOfferMap.get(offer.getShop()).add(offer);
            }
        }
    }

    private void createOfferLayout(LinearLayout offerForYouLayout, Map<Shop, List<Offer>> yourOfferMap) {
        offerForYouLayout.removeAllViews();
        skeletonScreen.hide();
        if(yourOfferMap != null && !yourOfferMap.isEmpty()){
            offerForYouText.setVisibility(View.VISIBLE);
        }
        yourOfferMap.forEach((shop, offers) -> {
            if(shop!=null){
                offerForYouLayout.addView(createOfferForShop(offerForYouLayout,shop,offers));
            }
        });


    }

    private View createOfferForShop(LinearLayout offerForYouLayout, Shop shop, List<Offer> offers) {

        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.card_your_offer_list, offerForYouLayout, false);

        TextView shopName,viewAll;
        RecyclerView shopOfferList;

        shopName = (TextView) layout.findViewById(R.id.shopName);
        viewAll = (TextView) layout.findViewById(R.id.viewAll);

        Gson g = new Gson();

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent offerList = new Intent(getActivity(), OfferListActivity.class);
                offerList.putExtra("shop",g.toJson(shop));
                getActivity().startActivity(offerList);
            }
        });

        shopOfferList = (RecyclerView) layout.findViewById(R.id.shopOfferList);

        shopName.setText(shop.getShopName());

        shopOfferList.setHasFixedSize(false);
        shopOfferList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        shopOfferList.setAdapter(new OfferAdapter(getActivity(), offers,false,offer -> {
            Intent offerList = new Intent(getActivity(), OfferDetailsActivity.class);
            offerList.putExtra("offer",g.toJson(offer));
            getActivity().startActivity(offerList);
        }));
        shopOfferList.smoothScrollToPosition(0);

        return layout;
    }
}