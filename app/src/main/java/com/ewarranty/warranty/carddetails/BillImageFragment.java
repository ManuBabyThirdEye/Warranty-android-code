package com.ewarranty.warranty.carddetails;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Card;
import com.ewarranty.warranty.ui.Category.SpacesItemDecoration;
import com.ewarranty.warranty.util.SettingsAction;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BillImageFragment extends Fragment{

    private Button createCard;
    private ImageButton backToBill;
    private RecyclerView addCardImageGridView;
    private RecyclerView addShareCardGridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_bill_image, container, false);

        createCard = (Button) rootView.findViewById(R.id.createCard);
        backToBill = (ImageButton) rootView.findViewById(R.id.backToBill);

        CardDetailMode cardDetailMode = ((CardDetailsActivity)getActivity()).cardDetailMode;

        addCardImageGridView = (RecyclerView) rootView.findViewById(R.id.addCardImageGridView);
        addShareCardGridView = (RecyclerView) rootView.findViewById(R.id.addShareCardGridView);

        backToBill.setOnClickListener(view -> {
            ((CardDetailsActivity)getActivity()).cardDetailView.setCurrentItem(1);
        });

        createCard.setOnClickListener(view -> {
            SettingsAction.hideSoftKeyboard(getActivity());
            ((CardDetailsActivity)getActivity()).createOrUpdateCard();
        });

        updateCardDisplayMode(((CardDetailsActivity)getActivity()).cardDetailMode);
        updateBillImageData(((CardDetailsActivity)getActivity()).cardDetailMode,((CardDetailsActivity)getActivity()).mAdapter);
        updateShareUserData(((CardDetailsActivity)getActivity()).cardDetailMode,((CardDetailsActivity)getActivity()).shareAdapter);

        return rootView;
    }

    private void updateBillImageData(CardDetailMode cardDetailMode, ImageAdapter mAdapter) {
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),4);
        addCardImageGridView.setLayoutManager(mLayoutManager);
        addCardImageGridView.setItemAnimator(new DefaultItemAnimator());

        addCardImageGridView.setAdapter(mAdapter);
        addCardImageGridView.addItemDecoration(new SpacesItemDecoration(30));
        addCardImageGridView.setNestedScrollingEnabled(false);
    }

    private void updateShareUserData(CardDetailMode cardDetailMode, ShareAdapter shareAdapter) {
        GridLayoutManager mLayoutManager1 = new GridLayoutManager(getActivity(),4);
        addShareCardGridView.setLayoutManager(mLayoutManager1);
        addShareCardGridView.setItemAnimator(new DefaultItemAnimator());

        addShareCardGridView.setAdapter(shareAdapter);
        addShareCardGridView.addItemDecoration(new SpacesItemDecoration(30));
        addShareCardGridView.setNestedScrollingEnabled(false);
    }

    private void updateCardDisplayMode(CardDetailMode cardDetailMode) {
        switch (cardDetailMode){
            case ADD:
            case EDIT:
                createCard.setVisibility(View.VISIBLE);
                backToBill.setVisibility(View.VISIBLE);

                break;
            case DISPLAY:
                createCard.setVisibility(View.GONE);
                backToBill.setVisibility(View.GONE);

                break;
        }
    }

    public void getUpdatedBillImageInfo(Card card) {

    }
}