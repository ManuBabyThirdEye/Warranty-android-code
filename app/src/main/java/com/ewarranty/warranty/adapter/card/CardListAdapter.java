package com.ewarranty.warranty.adapter.card;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Card;
import com.ewarranty.warranty.models.CardStatus;
import com.ewarranty.warranty.ui.home.OfferAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class CardListAdapter extends PagerAdapter {


    private Activity mContext;
    private final CardListAdapter.OnListFragmentInteractionListener mListener;


    public  CardListAdapter(Activity context, CardListAdapter.OnListFragmentInteractionListener mListener) {
        mContext = context;
        this.mListener = mListener;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View root = inflater.inflate(R.layout.fragment_card_list, collection, false);

        RecyclerView layout = root.findViewById(R.id.cardList);
        LinearLayout noCard = root.findViewById(R.id.noCard);

        List<Card> cardList = new ArrayList<>();

        switch (position) {
            case 0 :
                cardList = items.get(position).stream().map(c->{c.setCardStatus(CardStatus.PENDING); return c;}).collect(Collectors.toList()); break;
            case 1 :
                cardList = items.get(position).stream().map(c->{c.setCardStatus(CardStatus.ACTIVE); return c;}).collect(Collectors.toList()); break;
            case 2 :
                cardList = items.get(position).stream().map(c->{c.setCardStatus(CardStatus.EXPIRED_SOON); return c;}).collect(Collectors.toList()); break;
            case 3 :
                cardList = items.get(position).stream().map(c->{c.setCardStatus(CardStatus.EXPIRED); return c;}).collect(Collectors.toList()); break;
        }

        if(!cardList.isEmpty()){
            layout.setVisibility(View.VISIBLE);
            noCard.setVisibility(View.GONE);
            layout.setHasFixedSize(false);
            layout.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            layout.setAdapter(new CardAdapter(mContext, cardList, card -> {
                if(mListener!=null){
                    mListener.onListFragmentInteraction(card);
                }
            }));
            layout.smoothScrollToPosition(0);
        }else{
            layout.setVisibility(View.GONE);
            noCard.setVisibility(View.VISIBLE);
        }

        collection.addView(root);

        return root;
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

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 : return mContext.getString(R.string.category_card_pending);
            case 1 : return mContext.getString(R.string.category_card_active);
            case 2 : return mContext.getString(R.string.category_card_expire_soon);
            case 3 : return mContext.getString(R.string.category_card_expired);
        }
        return mContext.getString(R.string.category_card_pending);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Card card);
    }
}
