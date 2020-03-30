package com.ewarranty.warranty.ui.home;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Notification;
import com.ewarranty.warranty.models.Offer;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class OfferAdapter  extends RecyclerView.Adapter<OfferAdapter.ViewHolder>{

    private final List<Offer> mValues;
    private final OfferAdapter.OnListFragmentInteractionListener mListener;
    private final Activity activity;
    private final boolean isVertical;

    public OfferAdapter(Activity activity,List<Offer> items,boolean isVertical, OfferAdapter.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
        this.isVertical = isVertical;
    }

    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(isVertical){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_offer_vertical, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_offer, parent, false);
        }

        return new OfferAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OfferAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        Glide.with(activity)
                .load(holder.mItem.getImageUri()).centerCrop()
                .placeholder(R.drawable.no_data_found)
                .into(holder.offerImage);

        holder.offerDetails.setText(holder.mItem.getOfferDetails()+"");
        holder.offerPrice.setText(holder.mItem.getOfferRate()+" Rs");
        holder.actualPrice.setText(holder.mItem.getActualRate()+" Rs");
        holder.actualPrice.setPaintFlags(holder.actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        double diff = holder.mItem.getActualRate() - holder.mItem.getOfferRate();
        double per = diff*100/holder.mItem.getActualRate();
        holder.discount.setText(Math.round(per)+"%");

        holder.mView.setOnClickListener(view -> {
            if(mListener!=null){
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView offerImage;
        public final View mView;
        public final TextView offerDetails,offerPrice,actualPrice,discount;
        public Offer mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            offerImage = (ImageView) view.findViewById(R.id.offerImage);
            offerDetails = (TextView) view.findViewById(R.id.offerDetails);
            offerPrice = (TextView) view.findViewById(R.id.offerPrice);
            actualPrice = (TextView) view.findViewById(R.id.actualPrice);
            discount = (TextView) view.findViewById(R.id.discount);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Offer offer);
    }

    public void addListOfOffers(List<Offer> offerList){
        mValues.addAll(offerList);
        notifyDataSetChanged();
    }


}
