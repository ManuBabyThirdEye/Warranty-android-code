package com.ewarranty.warranty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.ewarranty.warranty.models.Offer;
import com.ewarranty.warranty.models.Shop;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.ewarranty.warranty.ui.home.OfferAdapter;
import com.ewarranty.warranty.util.Store;
import com.google.gson.Gson;

import java.util.List;

public class OfferListActivity extends AppCompatActivity {

    ImageButton back;
    TextView shopName;
    RecyclerView offerList;
    OfferAdapter offerAdapter;
    LinearLayout noOffer;
    LottieAnimationView empty;
    Shop shop;

    int currentPage = 0;
    int pageSize = 15;
    boolean gettingOffers = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_list);

        Gson g = new Gson();
        Store.SERVICE = ServiceGenerator.createService(UserServices.class);

        if (getIntent()!=null) {
            shop = g.fromJson(getIntent().getStringExtra("shop"),Shop.class);
        }

        if(shop == null){
            finish();
        }

        back = (ImageButton) findViewById(R.id.back);
        shopName = (TextView) findViewById(R.id.shopName);
        offerList = (RecyclerView) findViewById(R.id.offerList);
        noOffer = (LinearLayout) findViewById(R.id.noOffer);
        empty = (LottieAnimationView) findViewById(R.id.empty);

        shopName.setText(shop.getShopName());

        back.setOnClickListener(view -> {
            finish();
        });

        Store.SERVICE.getShopOffers(shop.getShopId(),currentPage,pageSize).enqueue(new Callback<List<Offer>>() {
            @Override
            public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                if(response.body()!=null && !response.body().isEmpty()){
                    offerList.setVisibility(View.VISIBLE);
                    noOffer.setVisibility(View.GONE);
                    offerList.setHasFixedSize(false);
                    offerList.setLayoutManager(new LinearLayoutManager(OfferListActivity.this, LinearLayoutManager.VERTICAL, false));
                    offerAdapter = new OfferAdapter(OfferListActivity.this, response.body(),true, offer -> {
                        Intent offerList = new Intent(OfferListActivity.this, OfferDetailsActivity.class);
                        offerList.putExtra("offer",g.toJson(offer));
                        OfferListActivity.this.startActivity(offerList);
                    });
                    offerList.setAdapter(offerAdapter);
                    offerList.smoothScrollToPosition(0);
                }else{
                    offerList.setVisibility(View.GONE);
                    noOffer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Offer>> call, Throwable t) {
                offerList.setVisibility(View.GONE);
                noOffer.setVisibility(View.VISIBLE);
            }
        });

        offerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 2 >= totalItemCount;
                if (endHasBeenReached && totalItemCount==(currentPage+1)*pageSize && !gettingOffers) {
                    currentPage = currentPage+1;
                    gettingOffers = true;
                    Store.SERVICE.getShopOffers(shop.getShopId(),currentPage,pageSize).enqueue(new Callback<List<Offer>>() {
                        @Override
                        public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                            if(response.body()!=null && !response.body().isEmpty() && offerAdapter !=null){
                               offerAdapter.addListOfOffers(response.body());
                            }
                            gettingOffers = false;
                        }

                        @Override
                        public void onFailure(Call<List<Offer>> call, Throwable t) {
                            gettingOffers = false;
                        }
                    });
                }
            }
        });

    }

}
