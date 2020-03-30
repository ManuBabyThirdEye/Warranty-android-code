package com.ewarranty.warranty.carddetails;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ewarranty.warranty.IncidentActivity;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.RaiseIncidentActivity;
import com.ewarranty.warranty.models.Card;
import com.ewarranty.warranty.models.ImageModel;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter  extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    private final List<ImageModel> mValues;
    private final ImageAdapter.OnListFragmentInteractionListener mListener;
    private final Activity activity;


    public ImageAdapter(Activity activity, List<ImageModel> items, ImageAdapter.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }



    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_single_image, parent, false);
        return new ImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        CardDetailMode cardDetailMode = CardDetailMode.ADD;
        if(activity instanceof CardDetailsActivity){
            cardDetailMode = ((CardDetailsActivity) activity).cardDetailMode;
        }else if(activity instanceof IncidentActivity){
            cardDetailMode = CardDetailMode.DISPLAY;
        }else if(activity instanceof RaiseIncidentActivity){
            cardDetailMode = CardDetailMode.ADD;
        }
        Log.d("ImageAdapter", "onBindViewHolder: "+cardDetailMode);

        if(cardDetailMode == CardDetailMode.EDIT || cardDetailMode == CardDetailMode.ADD){
            holder.delete.setVisibility(View.VISIBLE);
        }else{
            holder.delete.setVisibility(View.GONE);
        }

        if(holder.mItem.isAddNew()){
            holder.addNewLayout.setVisibility(View.VISIBLE);
            holder.imageLayout.setVisibility(View.GONE);
        }else{
            holder.addNewLayout.setVisibility(View.GONE);
            holder.imageLayout.setVisibility(View.VISIBLE);
            if(holder.mItem.getUri() != null){
                Glide.with(activity)
                        .load(holder.mItem.getUri()).centerCrop()
                        .placeholder(R.drawable.no_data_found)
                        .into(holder.image);
            }else if(holder.mItem.getImage() != null){
                Glide.with(activity)
                        .load(holder.mItem.getImage()).centerCrop()
                        .placeholder(R.drawable.no_data_found)
                        .into(holder.image);
            }

        }

        holder.delete.setOnClickListener(view -> {
            removeItem(position);
        });

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
        public final ImageView image,delete;

        public final RelativeLayout addNewLayout,imageLayout;

        public final View mView;
        public ImageModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (ImageView) view.findViewById(R.id.image);
            delete = (ImageView) view.findViewById(R.id.delete);
            addNewLayout = (RelativeLayout) view.findViewById(R.id.addNewLayout);
            imageLayout = (RelativeLayout) view.findViewById(R.id.imageLayout);

        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ImageModel imageModel);
    }

    public void removeItem(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(ImageModel item) {
        mValues.add(item);
        notifyDataSetChanged();
    }
}
