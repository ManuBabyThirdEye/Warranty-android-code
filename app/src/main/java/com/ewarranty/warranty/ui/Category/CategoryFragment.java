package com.ewarranty.warranty.ui.Category;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ewarranty.warranty.CardListActivity;
import com.ewarranty.warranty.CategorySelectionActivity;
import com.ewarranty.warranty.IncidentListActivity;
import com.ewarranty.warranty.NotificationActivity;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Category;
import com.ewarranty.warranty.models.CategoryType;
import com.ewarranty.warranty.models.IncidentCount;
import com.ewarranty.warranty.models.IncidentStatus;
import com.ewarranty.warranty.models.Role;
import com.ewarranty.warranty.util.Store;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryFragment extends Fragment {

    private RecyclerView categoryList;
    public static CategoryAdapter mAdapter;
    List<Category> newCategoryList;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_category, container, false);

        categoryList = (RecyclerView) root.findViewById(R.id.categoryList);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),3);
        categoryList.setLayoutManager(mLayoutManager);
        categoryList.setItemAnimator(new DefaultItemAnimator());
        if(Store.PROFILE.getRole()== Role.SHOP){
            newCategoryList = Store.CATEGORY_LIST.stream().filter(c -> !CollectionUtils.isEmpty(Store.SELECTED_SHOP.getCategoryList()) && Store.SELECTED_SHOP.getCategoryList().contains(c.getCategoryType())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(newCategoryList)){
                Collections.reverse(newCategoryList);
                newCategoryList.add(new Category(0, CategoryType.ALL_CATEGORY,R.string.category_all,R.drawable.all_category,false));
                Collections.reverse(newCategoryList);
            }
            newCategoryList.add(new Category(111,CategoryType.UPDATE,R.string.category_update,R.drawable.update_category,false));

        }else{
            newCategoryList = Store.CATEGORY_LIST;
        }
        mAdapter = new CategoryAdapter(getActivity(), newCategoryList,position -> {
            if(newCategoryList.get(position).getCategoryType() != CategoryType.UPDATE){
                Intent card = new Intent(getActivity(), CardListActivity.class);
                card.putExtra("category",newCategoryList.get(position).getCategoryId());
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                getActivity().startActivity(card);
            }else{
                Intent card = new Intent(getActivity(), CategorySelectionActivity.class);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                getActivity().startActivity(card);
            }
        });
        categoryList.setAdapter(mAdapter);
        categoryList.addItemDecoration(new SpacesItemDecoration(30));

        return root;
    }

}