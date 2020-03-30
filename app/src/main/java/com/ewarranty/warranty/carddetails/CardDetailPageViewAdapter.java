package com.ewarranty.warranty.carddetails;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Card;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CardDetailPageViewAdapter extends FragmentPagerAdapter {

    Activity activity;
    ProductFragment productFragment;
    ShopAndBillFragment shopAndBillFragment;
    BillImageFragment billImageFragment;

    public CardDetailPageViewAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                productFragment = new ProductFragment();
                return productFragment;
            case 1:
                shopAndBillFragment = new ShopAndBillFragment();
                return shopAndBillFragment;
            case 2:
                billImageFragment = new BillImageFragment();
                return billImageFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 : return activity.getString(R.string.product_details);
            case 1 : return activity.getString(R.string.Bill_shop_details);
            case 2 : return activity.getString(R.string.bill_images);
        }
        return activity.getString(R.string.product_details);
    }

    public Card getUpdatedCard(){
        Card card = new Card();
        productFragment.getUpdatedProductInfo(card);
        shopAndBillFragment.getUpdatedShopBillInfo(card);
        billImageFragment.getUpdatedBillImageInfo(card);
        return card;
    }
}
