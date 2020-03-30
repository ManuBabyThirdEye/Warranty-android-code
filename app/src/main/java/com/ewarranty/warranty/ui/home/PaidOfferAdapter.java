package com.ewarranty.warranty.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Offer;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class PaidOfferAdapter extends PagerAdapter {

    private Context mContext;
    private List<Offer> items;
    private ImageView offerImage;
    private TextView shopName;


    public PaidOfferAdapter(Context context, List<Offer> items) {
        mContext = context;
        this.items = items;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.card_paid_offer, collection, false);

        offerImage = (ImageView)layout.findViewById(R.id.offerImage);
        shopName = (TextView)layout.findViewById(R.id.shopName);

        Glide.with(mContext)
                .load(items.get(position).getImageUri()).centerCrop()
                .placeholder(R.drawable.no_data_found)
                .into(offerImage);

        shopName.setText(items.get(position).getShop() != null?items.get(position).getShop().getShopName():"");

        collection.addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
