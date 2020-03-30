package com.ewarranty.warranty.carddetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ewarranty.warranty.CompanyListActivity;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.RaiseIncidentActivity;
import com.ewarranty.warranty.models.Card;
import com.ewarranty.warranty.models.Category;
import com.ewarranty.warranty.models.CategoryType;
import com.ewarranty.warranty.models.Company;
import com.ewarranty.warranty.util.Store;

import java.util.stream.Collectors;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProductFragment extends Fragment {

    private EditText productName,productId,productOther;
    public static TextView company;
    private RelativeLayout companyLayout;
    private ImageView forwardFromProduct;
    private Spinner productCategory;
    ArrayAdapter<CharSequence> productCategoryAdapter;
    int selectedCategoryIndex = 7;
    public static final int SELECT_COMPANY = 3;
    public static final String COMPANY_DETAILS = "company";
    public static Company SELECT_COMPANY_OBJECT = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_product_details, container, false);

        if (getActivity().getIntent()!=null) {
            selectedCategoryIndex = getActivity().getIntent().getIntExtra("category",0);
            selectedCategoryIndex =  selectedCategoryIndex==0?7:selectedCategoryIndex;
        }

        productCategory = (Spinner) rootView.findViewById(R.id.productCategory);
        productName = (EditText) rootView.findViewById(R.id.productName);
        productId = (EditText) rootView.findViewById(R.id.productId);
        forwardFromProduct = (ImageView) rootView.findViewById(R.id.forwardFromProduct);
        company = (TextView) rootView.findViewById(R.id.company);
        companyLayout = (RelativeLayout) rootView.findViewById(R.id.companyLayout);

        productOther = (EditText) rootView.findViewById(R.id.productOther);

        /*productCategoryAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.product_category, R.layout.my_simple_text_view);*/
        productCategoryAdapter = new ArrayAdapter(getActivity(),R.layout.my_simple_text_view,
                Store.CATEGORY_LIST.stream().filter(c->c.getCategoryType()!= CategoryType.ALL_CATEGORY).map(c->getActivity().getString(c.getCategoryNameResourceId())).collect(Collectors.toList()));
        productCategoryAdapter.setDropDownViewResource(R.layout.my_simple_spinner_dropdown_item);
        productCategory.setAdapter(productCategoryAdapter);

        updateCardDisplayMode(((CardDetailsActivity)getActivity()).cardDetailMode);
        updateData(((CardDetailsActivity)getActivity()).cardDetailMode,((CardDetailsActivity)getActivity()).card);

        productName.setOnFocusChangeListener((view, b) -> {
            if(b){
                productName.setBackground(getActivity().getDrawable(R.drawable.edit_box_active_bk));
            }else{
                productName.setBackground(getActivity().getDrawable(R.drawable.edit_box_inactive_bk));

            }
        });

        productId.setOnFocusChangeListener((view, b) -> {
            if(b){
                productId.setBackground(getActivity().getDrawable(R.drawable.edit_box_active_bk));
            }else{
                productId.setBackground(getActivity().getDrawable(R.drawable.edit_box_inactive_bk));

            }
        });

        productOther.setOnFocusChangeListener((view, b) -> {
            if(b){
                productOther.setBackground(getActivity().getDrawable(R.drawable.edit_box_active_bk));
            }else{
                productOther.setBackground(getActivity().getDrawable(R.drawable.edit_box_inactive_bk));

            }
        });

        forwardFromProduct.setOnClickListener(view -> {
            ((CardDetailsActivity)getActivity()).cardDetailView.setCurrentItem(1);
        });

        companyLayout.setOnClickListener(view -> {
            if(((CardDetailsActivity)getActivity()).cardDetailMode == CardDetailMode.EDIT || ((CardDetailsActivity)getActivity()).cardDetailMode == CardDetailMode.ADD){
                Intent companyListActivity = new Intent(getActivity(), CompanyListActivity.class);
                getActivity().startActivityForResult(companyListActivity,SELECT_COMPANY);
            }
        });

        return rootView;
    }

    private void updateData(CardDetailMode cardDetailMode, Card card) {
        if(cardDetailMode == CardDetailMode.EDIT || cardDetailMode == CardDetailMode.DISPLAY){
            productName.setText(card.getProductName());
            productId.setText(card.getProductId());
            productOther.setText(card.getProductOtherInformation());
            Category category = Store.CATEGORY_LIST.stream().filter(c -> c.getCategoryType() == card.getProductCategory()).findFirst().orElse(Store.CATEGORY_LIST.get(selectedCategoryIndex));
            int pos = productCategoryAdapter.getPosition(getResources().getString(category.getCategoryNameResourceId()));
            productCategory.setSelection(pos);
            if(card.getCompany()!=null){
                company.setText(card.getCompany().getCompanyName());
            }
        }else{
            int pos = productCategoryAdapter.getPosition(getResources().getString(Store.CATEGORY_LIST.get(selectedCategoryIndex).getCategoryNameResourceId()));
            productCategory.setSelection(pos);
        }
    }

    private void updateCardDisplayMode(CardDetailMode cardDetailMode) {
        switch (cardDetailMode){
            case ADD:
            case EDIT:
                forwardFromProduct.setVisibility(View.VISIBLE);
                productName.setEnabled(true);
                productCategory.setEnabled(true);
                productId.setEnabled(true);
                productOther.setEnabled(true);
                break;
            case DISPLAY:
                forwardFromProduct.setVisibility(View.GONE);
                productName.setEnabled(false);
                productCategory.setEnabled(false);
                productId.setEnabled(false);
                productOther.setEnabled(false);
                break;
        }
    }

    public void getUpdatedProductInfo(Card card){
        card.setProductName(productName.getText().toString());
        card.setProductCategory(Store.CATEGORY_LIST.get(productCategory.getSelectedItemPosition()+1).getCategoryType());
        card.setProductId(productId.getText().toString());
        card.setProductOtherInformation(productOther.getText().toString());
        if(SELECT_COMPANY_OBJECT!=null){
            card.setCompanyId(SELECT_COMPANY_OBJECT.getCompanyId());
        }
    }
}
