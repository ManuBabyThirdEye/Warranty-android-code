package com.ewarranty.warranty.carddetails;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.ImageModel;
import com.ewarranty.warranty.models.User;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder>{

    private final List<User> mValues;
    private final ShareAdapter.OnListFragmentInteractionListener mListener;
    private final Activity activity;

    public ShareAdapter(Activity activity, List<User> items, ShareAdapter.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    @Override
    public ShareAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_share_person, parent, false);
        return new ShareAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShareAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        CardDetailMode cardDetailMode = ((CardDetailsActivity) activity).cardDetailMode;


        if(holder.mItem.getName().equals("Add New")){
            holder.addShareLayout.setVisibility(View.VISIBLE);
            holder.shareLayout.setVisibility(View.GONE);
        }else{
            holder.addShareLayout.setVisibility(View.GONE);
            holder.shareLayout.setVisibility(View.VISIBLE);
            Glide.with(activity)
                    .load(holder.mItem.getProfileUri()).centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(holder.profilePic);
            if(holder.mItem.getName()!=null && !holder.mItem.getName().equals("")){
                holder.nameOrMobile.setText(holder.mItem.getName());
            }else{
                holder.nameOrMobile.setText(holder.mItem.getPhoneNumber());
            }

        }


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
        public final ImageView profilePic;
        public final TextView nameOrMobile;

        public final LinearLayout shareLayout,addShareLayout;

        public final View mView;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            profilePic = (ImageView) view.findViewById(R.id.profilePic);
            nameOrMobile = (TextView) view.findViewById(R.id.nameOrMobile);
            shareLayout = (LinearLayout) view.findViewById(R.id.shareLayout);
            addShareLayout = (LinearLayout) view.findViewById(R.id.addShareLayout);

        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(User imageModel);
    }

    public void removeItem(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(User item) {
        mValues.add(item);
        notifyDataSetChanged();
    }

    public void updateItem(User userOld,User userNew) {
        int index = mValues.indexOf(userOld);
        if(index !=-1){
            mValues.add(userNew);
            notifyDataSetChanged();
        }
    }
}
