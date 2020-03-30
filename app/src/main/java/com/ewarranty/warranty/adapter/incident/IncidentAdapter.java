package com.ewarranty.warranty.adapter.incident;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ewarranty.warranty.IncidentListActivity;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Incident;
import com.ewarranty.warranty.models.IncidentStatus;
import com.ewarranty.warranty.models.IncidentWorkFlow;
import com.ewarranty.warranty.models.IncidentWorkFlowStatus;
import com.ewarranty.warranty.util.Store;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.ViewHolder> {

    private final List<Incident> mValues;
    private final IncidentAdapter.OnListFragmentInteractionListener mListener;
    private final Activity activity;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    public IncidentAdapter(Activity activity,List<Incident> items, IncidentAdapter.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    @Override
    public IncidentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_incident, parent, false);
        return new IncidentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IncidentAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if(holder.mItem.getIncidentStatus()!=null){
            switch (holder.mItem.getIncidentStatus()){
                case PENDING: holder.statusIndicator.setBackgroundResource(R.color.pending); break;
                case COMPLETED: holder.statusIndicator.setBackgroundResource(R.color.completed); break;
                case CANCELED: holder.statusIndicator.setBackgroundResource(R.color.red); break;
            }
        }

        holder.incidentId.setText("INCIDENT ID : "+holder.mItem.getIncidentId());
        holder.incidentDescription.setText(holder.mItem.getIncidentDescription());

        /*if(holder.mItem.isOpen()){
            holder.gotoOpen.setVisibility(View.GONE);
            holder.gotoProduct.setVisibility(View.VISIBLE);
            holder.productWorkFlow.setVisibility(View.VISIBLE);
            holder.action.setVisibility(View.VISIBLE);
        }else{
            holder.gotoOpen.setVisibility(View.VISIBLE);
            holder.gotoProduct.setVisibility(View.GONE);
            holder.productWorkFlow.setVisibility(View.GONE);
            holder.action.setVisibility(View.GONE);
        }*/

        if(holder.mItem.getIncidentStatus()== IncidentStatus.COMPLETED){
            if(holder.mItem.getRating() > 0){
                holder.ratingLayout.removeAllViews();
                for(int i=0;i<holder.mItem.getRating();i++){
                    ImageView star = new ImageView(activity);
                    star.setImageDrawable(activity.getDrawable(R.drawable.ic_full_star));
                    star.setMaxHeight(15);
                    star.setMaxWidth(15);
                    holder.ratingLayout.addView(star);
                }
            }
        }

        //IncidentWorkFlowStatus currentStatus = addWorkFlowInToLayout(holder.productWorkFlow,holder.mItem.getIncidentWorkFlowList());

        /*holder.gotoOpen.setOnClickListener(view -> {
            if(mListener!=null){
                mListener.onListFragmentInteraction(position);
            }
        });*/

        holder.mView.setOnClickListener(view -> {
            if(mListener!=null){
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });

    }

    private IncidentWorkFlowStatus addWorkFlowInToLayout(LinearLayout productWorkFlow, List<IncidentWorkFlow> incidentWorkFlowList) {
        incidentWorkFlowList.sort((i1,i2)->i1.getDate().compareTo(i2.getDate()));
        productWorkFlow.removeAllViews();
        for (int i = 0 ;i<incidentWorkFlowList.size();i++) {
            View layout = createIncidentWorkFlowItem(productWorkFlow,incidentWorkFlowList,i,false);
            productWorkFlow.addView(layout);
        }
        if(incidentWorkFlowList.get(incidentWorkFlowList.size()-1).getNextWorkFlow()!=null){
            View layout = createIncidentWorkFlowItem(productWorkFlow,incidentWorkFlowList,incidentWorkFlowList.size()-1,true);
            productWorkFlow.addView(layout);
        }

        return incidentWorkFlowList.get(incidentWorkFlowList.size()-1).getWorkFlowStatus();
    }

    private View createIncidentWorkFlowItem(LinearLayout productWorkFlow, List<IncidentWorkFlow> incidentWorkFlowList, int i, boolean isLastElement) {
        View layout = LayoutInflater.from(activity).inflate(R.layout.item_workflow, productWorkFlow, false);
        TextView dateAndTime,workflowDetails;
        RelativeLayout flowLine;
        CardView statusColor;

        dateAndTime = (TextView) layout.findViewById(R.id.dateAndTime);
        workflowDetails = (TextView) layout.findViewById(R.id.workflowDetails);

        flowLine = (RelativeLayout) layout.findViewById(R.id.flowLine);
        statusColor = (CardView) layout.findViewById(R.id.statusColor);

        if(isLastElement){
            statusColor.setCardBackgroundColor(activity.getResources().getColor(R.color.pending,null));
            flowLine.setBackground(activity.getResources().getDrawable(R.drawable.dotted_line,null));
            workflowDetails.setText(incidentWorkFlowList.get(i).getNextWorkFlow());
            dateAndTime.setText("");
        }else{
            statusColor.setCardBackgroundColor(activity.getResources().getColor(R.color.completed,null));
            workflowDetails.setText(incidentWorkFlowList.get(i).getDescription());
            dateAndTime.setText(convertToDisplayDate(incidentWorkFlowList.get(i).getDate(),
                    i==0 || !(DATE_FORMAT.format(incidentWorkFlowList.get(i-1).getDate()).equals(DATE_FORMAT.format(incidentWorkFlowList.get(i).getDate())))));
        }

        if(i==0){
            flowLine.setVisibility(View.GONE);
            LinearLayout.LayoutParams statusColorParam = new LinearLayout.LayoutParams(25, 25);
            statusColorParam.setMargins(0,70,0,0);
            statusColor.setLayoutParams(statusColorParam);
        }
        return layout;
    }

    private String convertToDisplayDate(Date date, boolean showDate) {
        return showDate?DATE_FORMAT.format(date)+" "+TIME_FORMAT.format(date):TIME_FORMAT.format(date);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView incidentId,incidentDescription;
        public LinearLayout ratingLayout,statusIndicator;
        public Incident mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            incidentId = (TextView) view.findViewById(R.id.incidentId);
            incidentDescription = (TextView) view.findViewById(R.id.incidentDescription);

            //statusDescription = (TextView) view.findViewById(R.id.statusDescription);
            //gotoProduct = (TextView) view.findViewById(R.id.gotoProduct);

            //productWorkFlow = (LinearLayout) view.findViewById(R.id.productWorkFlow);
            ratingLayout = (LinearLayout) view.findViewById(R.id.ratingLayout);
            statusIndicator = (LinearLayout) view.findViewById(R.id.statusIndicator);

            //gotoOpen = (ImageView) view.findViewById(R.id.gotoOpen);
            //action = (Button) view.findViewById(R.id.action);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Incident incident);
    }

    public void removeItem(Incident item) {
        int pos = -1;
        for (int i=0;i<mValues.size();i++) {
            if(mValues.get(i).getIncidentId() == item.getIncidentId()){
                pos = i;
                break;
            }
        }
        if(pos!=-1){
            removeItemByPosition(pos);
        }
    }

    public void removeItemByPosition(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Incident item, int position) {
        mValues.add(position, item);
        notifyItemInserted(position);
    }

    public void openOrCloseIncident(int position) {

        boolean currentState = mValues.get(position).isOpen();
        mValues.forEach(v->{
            v.setOpen(false);
        });
        mValues.get(position).setOpen(!currentState);
        notifyDataSetChanged();
    }

}
