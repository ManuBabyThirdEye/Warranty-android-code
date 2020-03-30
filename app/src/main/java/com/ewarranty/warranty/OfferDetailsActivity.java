package com.ewarranty.warranty;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewarranty.warranty.models.Offer;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.ewarranty.warranty.util.Store;
import com.google.gson.Gson;

public class OfferDetailsActivity extends AppCompatActivity {

    private ImageView offerImage;
    private View mView;
    private TextView offerDetails,offerPrice,actualPrice,discount,offerDescription;

    Offer offer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        Gson g = new Gson();
        Store.SERVICE = ServiceGenerator.createService(UserServices.class);

        if (getIntent()!=null) {
            offer = g.fromJson(getIntent().getStringExtra("offer"), Offer.class);
        }

        offerImage = (ImageView) findViewById(R.id.offerImage);
        offerDetails = (TextView) findViewById(R.id.offerDetails);
        offerPrice = (TextView) findViewById(R.id.offerPrice);
        actualPrice = (TextView) findViewById(R.id.actualPrice);
        discount = (TextView) findViewById(R.id.discount);
        offerDescription = (TextView) findViewById(R.id.offerDescription);


        Glide.with(OfferDetailsActivity.this)
                .load(offer.getImageUri())
                .placeholder(R.drawable.no_data_found)
                .into(offerImage);

        offerDetails.setText(offer.getOfferDetails()+"");
        offerPrice.setText(offer.getOfferRate()+" Rs");
        actualPrice.setText(offer.getActualRate()+" Rs");
        actualPrice.setPaintFlags(actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        double diff = offer.getActualRate() - offer.getOfferRate();
        double per = diff*100/offer.getActualRate();
        discount.setText(Math.round(per)+"%");
        offerDescription.setText(offer.getOfferDescription());
    }
}
