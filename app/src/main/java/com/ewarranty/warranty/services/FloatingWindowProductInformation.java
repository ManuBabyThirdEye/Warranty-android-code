package com.ewarranty.warranty.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.util.Store;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import androidx.annotation.Nullable;

public class FloatingWindowProductInformation extends Service {
    WindowManager windowManager;
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    CountDownTimer counter;
    private static final int SWIPE_THRESHOLD = 200;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);


        final View view = LayoutInflater.from(this)
                .inflate(R.layout.floating_information, null, false);

        TextView productId = view.findViewById(R.id.productId);
        TextView productName = view.findViewById(R.id.productName);
        TextView billNumber = view.findViewById(R.id.billNumber);
        TextView billDate = view.findViewById(R.id.billDate);

        LinearLayout cardInfo = view.findViewById(R.id.cardInfo);
        TextView noCardInfo = view.findViewById(R.id.noCardInfo);
        cardInfo.setVisibility(View.GONE);
        noCardInfo.setVisibility(View.VISIBLE);
        if(Store.SELECTED_INCIDENT!=null && Store.SELECTED_INCIDENT.getWarrantyCard()!=null){
            cardInfo.setVisibility(View.VISIBLE);
            noCardInfo.setVisibility(View.GONE);
            productId.setText(Store.SELECTED_INCIDENT.getWarrantyCard().getProductId());
            productName.setText(Store.SELECTED_INCIDENT.getWarrantyCard().getProductName());
            billNumber.setText(Store.SELECTED_INCIDENT.getWarrantyCard().getBillNumber());
            billDate.setText(dateFormat.format(Store.SELECTED_INCIDENT.getWarrantyCard().getBillingDate()));
        }else if(Store.SELECTED_INCIDENT.getWarrantyCard()!=null){
            noCardInfo.setText(getResources().getString(R.string.raise_incident_no_company));
        }else{
            noCardInfo.setText(getResources().getString(R.string.raise_incident_no_card));
        }

    }

    private void closeFloatingPlayer(View view) {
        windowManager.removeView(view);
        stopSelf();
    }
}
