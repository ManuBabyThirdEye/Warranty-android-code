package com.ewarranty.warranty.adapter.company;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Company;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CompanyListAdapter  extends RecyclerView.Adapter<CompanyListAdapter.ViewHolder> {


    private final List<Company> mValues;
    private final CompanyListAdapter.OnListFragmentInteractionListener mListener;
    private final Activity activity;

    public CompanyListAdapter(Activity activity,List<Company> items, CompanyListAdapter.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    @Override
    public CompanyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_company_selection, parent, false);
        return new CompanyListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CompanyListAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.companyName.setText(holder.mItem.getCompanyName());
        if(holder.mItem.getLogo()!=null){
            Glide.with(activity)
                    .load(holder.mItem.getLogo()).centerCrop()
                    .placeholder(R.drawable.no_data_found)
                    .into(holder.companyLogo);
        }

        if(holder.mItem.getSupportEmailId()!=null){
            holder.email.setVisibility(View.VISIBLE);
        }else{
            holder.email.setVisibility(View.GONE);
        }

        if(holder.mItem.getSupportWhatsappNumber()!=null){
            holder.whatsapp.setVisibility(View.VISIBLE);
        }else{
            holder.whatsapp.setVisibility(View.GONE);
        }

        if(holder.mItem.getSupportPhoneNumber()!=null){
            holder.phone.setVisibility(View.VISIBLE);
        }else{
            holder.phone.setVisibility(View.GONE);
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
        public final View mView;
        public final TextView companyName;
        public final ImageView companyLogo,whatsapp,email,phone;
        public Company mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            companyName = (TextView) view.findViewById(R.id.companyName);
            companyLogo = (ImageView) view.findViewById(R.id.companyLogo);
            whatsapp = (ImageView) view.findViewById(R.id.whatsapp);
            email = (ImageView) view.findViewById(R.id.email);
            phone = (ImageView) view.findViewById(R.id.phone);

        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Company card);
    }

    public void removeItem(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(List<Company> item, int position) {
        mValues.addAll(item);
        notifyDataSetChanged();
    }

    public void updateList(List<Company> item) {
        mValues.clear();
        mValues.addAll(item);
        notifyDataSetChanged();
    }



}
