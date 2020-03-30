package com.ewarranty.warranty.adapter.notification;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Notification;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {


    private final List<Notification> mValues;
    private final NotificationAdapter.OnListFragmentInteractionListener mListener;
    private final Activity activity;

    public NotificationAdapter(Activity activity,List<Notification> items, NotificationAdapter.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_notification, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.message.setText(holder.mItem.getDisplayMessage());
        if(holder.mItem.isUnread()) {
            holder.viewForeground.setBackground(activity.getResources().getDrawable(R.drawable.border_with_corner_radius_unread,null));
        }else{
            holder.viewForeground.setBackground(activity.getResources().getDrawable(R.drawable.border_with_corner_radius,null));
        }

        holder.mView.setOnClickListener(view -> {
            if(mListener!=null){
                mListener.onListFragmentInteraction(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView message;
        public RelativeLayout viewBackground, viewForeground;
        public Notification mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            message = (TextView) view.findViewById(R.id.message);
            viewBackground = view.findViewById(R.id.viewBackground);
            viewForeground = view.findViewById(R.id.viewForeground);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(int position);
    }

    public void removeItem(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Notification item, int position) {
        mValues.add(position, item);
        notifyItemInserted(position);
    }

    public void updateItemRead(int position) {
        mValues.get(position).setUnread(false);
        notifyItemChanged(position);
    }
}
