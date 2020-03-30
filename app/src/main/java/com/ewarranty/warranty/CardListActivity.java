package com.ewarranty.warranty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ewarranty.warranty.adapter.card.CardListAdapter;
import com.ewarranty.warranty.carddetails.CardDetailMode;
import com.ewarranty.warranty.carddetails.CardDetailsActivity;
import com.ewarranty.warranty.models.Card;
import com.ewarranty.warranty.models.CardStatus;
import com.ewarranty.warranty.models.Category;
import com.ewarranty.warranty.models.CategoryType;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.ewarranty.warranty.util.Store;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CardListActivity extends AppCompatActivity {

    ViewPager cardPageView;
    TextView whichCategory;
    ImageButton back;
    Category selectedCategory;
    ImageButton addCardFab;
    int selectedCategoryIndex;

    boolean fetchingCardList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        if (getIntent()!=null) {
            selectedCategoryIndex = getIntent().getIntExtra("category",0);
            if(Store.CATEGORY_LIST != null){
                selectedCategory = Store.CATEGORY_LIST.stream().filter(c1 -> c1.getCategoryId() == selectedCategoryIndex).findFirst().orElse(Store.CATEGORY_LIST.get(0));
            }
        }

        if(selectedCategory == null && Store.CATEGORY_LIST != null && !Store.CATEGORY_LIST.isEmpty()){
            selectedCategory = Store.CATEGORY_LIST.get(0);
        }else if(selectedCategory == null){
            selectedCategory = new Category(0, CategoryType.ALL_CATEGORY,R.string.category_all,R.drawable.all_category,false);
        }
        addCardFab = (ImageButton) findViewById(R.id.addCardFab);


        cardPageView = (ViewPager) findViewById(R.id.cardPageView);
        whichCategory = (TextView) findViewById(R.id.whichCategory);
        back = (ImageButton) findViewById(R.id.back);

        Store.SERVICE = ServiceGenerator.createService(UserServices.class);
        Store.CURRENT_CARD_LIST = null;
        updateCardList();

    }

    @Override
    protected void onStart() {
        super.onStart();
        back.setOnClickListener(view -> {
            finish();
        });

        addCardFab.setOnClickListener(view -> {
            Intent cardDetails = new Intent(CardListActivity.this,CardDetailsActivity.class);
            cardDetails.putExtra("category", selectedCategoryIndex);
            cardDetails.putExtra("cardDetailMode", CardDetailMode.ADD.toString());
            CardListActivity.this.startActivity(cardDetails);
        });

        if(!fetchingCardList){
            updateCardList();
        }

        whichCategory.setText(selectedCategory.getCategoryNameResourceId());
    }

    private void updateCardList() {
        if(Store.PROFILE !=null && Store.CURRENT_CARD_LIST == null){
            fetchingCardList = true;
            Call<List<Card>> getCardList = Store.SERVICE.getCardsByUser(Store.PROFILE.getPhoneNumber(), selectedCategory.getCategoryType().toString());
            Log.d("updateCardList", "onResponse: "+getCardList.request());

            getCardList.enqueue(new Callback<List<Card>>() {
                @Override
                public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                    List<Card> cardList = response.body();
                    Log.d("updateCardList", "onResponse: "+cardList);
                    Log.d("updateCardList", "onResponse: "+call.request());



                    if(cardList == null){
                        cardList = new ArrayList<>();
                    }
                    Store.CURRENT_CARD_LIST = cardList;

                    PagerAdapter adapter = new CardListAdapter(CardListActivity.this,card -> {
                        Intent cardDetail = new Intent(CardListActivity.this, CardDetailsActivity.class);
                        cardDetail.putExtra("cardDetailMode", CardDetailMode.DISPLAY.toString());
                        cardDetail.putExtra("cardId", card.getCardId());
                        cardDetail.putExtra("category", selectedCategoryIndex);
                        CardListActivity.this.startActivity(cardDetail);
                    });
                    cardPageView.setAdapter(adapter);
                    fetchingCardList = false;
                }

                @Override
                public void onFailure(Call<List<Card>> call, Throwable t) {
                    Log.d("updateCardList", "onResponse: "+t.getMessage());
                    fetchingCardList = false;
                }
            });
        }else if(Store.PROFILE !=null){


            PagerAdapter adapter = new CardListAdapter(CardListActivity.this,card -> {
                Intent cardDetail = new Intent(CardListActivity.this, CardDetailsActivity.class);
                cardDetail.putExtra("cardDetailMode", CardDetailMode.DISPLAY.toString());
                cardDetail.putExtra("cardId", card.getCardId());
                CardListActivity.this.startActivity(cardDetail);
            });
            cardPageView.setAdapter(adapter);
        }
    }

    private List<Card> getCardByStatus(List<Card> cardList, CardStatus status) {
        List<Card> filterActiveCard = cardList.stream().filter(c->c.getCardStatus() == CardStatus.ACTIVE).collect(Collectors.toList());
        switch (status){
            case PENDING:
                return cardList.stream().filter(c->c.getCardStatus() == null || c.getCardStatus() == CardStatus.PENDING).collect(Collectors.toList());
            case ACTIVE:
            case EXPIRED_SOON:
            case EXPIRED:
                return filterActiveCard.stream().filter(c->checkExpireStatus(c,status)).collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    private boolean checkExpireStatus(Card card, CardStatus status) {
        Date expire = findExpireDate(card.getBillingDate(),card.getWarrantyPeriod());
        Calendar expireCalendar = Calendar.getInstance();
        expireCalendar.setTime(expire);

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        switch (status){
            case ACTIVE:
                expireCalendar.add(Calendar.DATE,-60);
                return expireCalendar.after(today);
            case EXPIRED_SOON:
                expireCalendar.add(Calendar.DATE,-60);
                return expireCalendar.before(today);
            case EXPIRED:
                return expireCalendar.before(today);
        }
        return true;
    }

    private Date findExpireDate(Date billDate, long warrantyPeriod) {
        if(billDate == null){
            return new Date();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(billDate);
        c.add(Calendar.DATE,Integer.parseInt(warrantyPeriod+""));
        return c.getTime();
    }
}
