package com.ewarranty.warranty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ewarranty.warranty.adapter.notification.NotificationAdapter;
import com.ewarranty.warranty.adapter.notification.NotificationTouchHelper;
import com.ewarranty.warranty.models.Notification;
import com.ewarranty.warranty.models.Theme;
import com.ewarranty.warranty.util.SettingsAction;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements NotificationTouchHelper.RecyclerItemTouchHelperListener{

    private List<Notification> notificationList;

    private RelativeLayout noNotificationBlock;
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private ImageButton back;
    private  CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme currentTheme = SettingsAction.getInstance(NotificationActivity.this).getCurrentTheme();
        AppCompatDelegate.setDefaultNightMode(
                currentTheme == Theme.LITE?AppCompatDelegate.MODE_NIGHT_NO:currentTheme == Theme.DARK?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_AUTO);
        setContentView(R.layout.activity_notification);

        noNotificationBlock = (RelativeLayout) findViewById(R.id.noNotificationBlock);
        notificationRecyclerView = (RecyclerView) findViewById(R.id.notificationRecyclerView);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        back = (ImageButton) findViewById(R.id.back);

        notificationList = addDummyNotification();

    }

    @Override
    protected void onStart() {
        super.onStart();

        back.setOnClickListener(view -> {
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            finish();
        });

        if(notificationList.size()>0){
            notificationRecyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
            notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
            notificationAdapter = new NotificationAdapter(NotificationActivity.this,notificationList,item -> {
                notificationAdapter.updateItemRead(item);
            });
            notificationRecyclerView.setAdapter(notificationAdapter);

            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new NotificationTouchHelper(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notificationRecyclerView);


            noNotificationBlock.setVisibility(View.GONE);
            notificationRecyclerView.setVisibility(View.VISIBLE);
        }else{
            noNotificationBlock.setVisibility(View.VISIBLE);
            notificationRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NotificationAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = notificationList.get(viewHolder.getAdapterPosition()).getDisplayMessage();

            // backup of removed item for undo purpose
            final Notification deletedItem = notificationList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            notificationAdapter.removeItem(deletedIndex);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    notificationAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private List<Notification> addDummyNotification() {
        return new ArrayList<>(Arrays.asList(new Notification("Product PC1234212 eWarranty will be expired in 1 Days",true),
                new Notification("Product PC1234212 eWarranty will be expired in 2 Days ",true),
                new Notification("Product PC1234212 eWarranty will be expired in 3 Days ",true),
                new Notification("Product PC1234212 eWarranty will be expired in 4 Days ",false),
                new Notification("Product PC1234212 eWarranty will be expired in 5 Days ",false),
                new Notification("Product PC1234212 eWarranty will be expired in 6 Days ",false)));
    }
}
