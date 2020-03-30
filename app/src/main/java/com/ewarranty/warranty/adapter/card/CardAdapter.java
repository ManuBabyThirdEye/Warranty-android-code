package com.ewarranty.warranty.adapter.card;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Card;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {


    private final List<Card> mValues;
    private final CardAdapter.OnListFragmentInteractionListener mListener;
    private final Activity activity;
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    public CardAdapter(Activity activity,List<Card> items, CardAdapter.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_warranty_card, parent, false);
        return new CardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.productName.setText(holder.mItem.getProductName());
        holder.shopName.setText(holder.mItem.getShop()!=null?holder.mItem.getShop().getShopName():"");
        holder.cardNumber.setText(convertToCardNumberFormat(holder.mItem.getCardId()));
        holder.validThru.setText(calculateWarrantyEndDate(holder.mItem.getBillingDate(),holder.mItem.getWarrantyPeriod()));
        holder.productId.setText(holder.mItem.getProductId()+"");
        switch (holder.mItem.getCardStatus()){
            case PENDING:
            case ACTIVE:
                holder.mView.setBackground(activity.getResources().getDrawable(R.drawable.active_card,null)); break;
            case EXPIRED_SOON:
            case EXPIRED:
                holder.mView.setBackground(activity.getResources().getDrawable(R.drawable.expired_card,null)); break;

        }

        holder.mView.setOnClickListener(view -> {
            if(mListener!=null){
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });

    }

    private String convertToCardNumberFormat(long cardId) {
        String cardString =  cardId+"";
        StringBuilder newCardNumber = new StringBuilder();
        int i=0;
        for (char c: cardString.toCharArray()) {
            if(i%4==0){
                newCardNumber.append("  ");
            }
            newCardNumber.append(c);
            i++;
        }
        return newCardNumber.toString();
    }

    private String calculateWarrantyEndDate(Date billDate, long warrantyPeriod) {
        if(billDate == null){
            return "No Billing Date";
        }
        Calendar c = Calendar.getInstance();
        c.setTime(billDate);
        c.add(Calendar.DATE,Integer.parseInt(warrantyPeriod+""));
        return dateFormat.format(c.getTime());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView productName,shopName,cardNumber,validThru,productId;
        public Card mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            productName = (TextView) view.findViewById(R.id.productName);
            shopName = (TextView) view.findViewById(R.id.shopName);
            cardNumber = (TextView) view.findViewById(R.id.cardNumber);
            validThru = (TextView) view.findViewById(R.id.validThru);
            productId = (TextView) view.findViewById(R.id.productId);

        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Card card);
    }

    public void removeItem(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(List<Card> item, int position) {
        mValues.addAll(item);
        notifyDataSetChanged();
    }

}
