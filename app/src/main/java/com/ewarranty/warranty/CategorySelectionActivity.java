package com.ewarranty.warranty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ewarranty.warranty.models.Category;
import com.ewarranty.warranty.models.CategoryType;
import com.ewarranty.warranty.ui.Category.CategoryAdapter;
import com.ewarranty.warranty.ui.Category.CategoryFragment;
import com.ewarranty.warranty.ui.Category.SpacesItemDecoration;
import com.ewarranty.warranty.util.Store;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class CategorySelectionActivity extends AppCompatActivity {

    private RecyclerView categoryList;
    private CategoryAdapter mAdapter;
    private Button submitCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        ArrayList<Category> categorySelectionList = new ArrayList<>(Arrays.asList(
                new Category(0, CategoryType.ALL_CATEGORY, R.string.category_all, R.drawable.all_category,false),
                new Category(1, CategoryType.ELECTRONICS, R.string.category_electronics, R.drawable.electronics,false),
                new Category(2, CategoryType.HOME_FURNITURE, R.string.category_home_furniture, R.drawable.furniture,false),
                new Category(3, CategoryType.TV_APPLIANCES, R.string.category_tv_appliances, R.drawable.appliances,false),
                new Category(4, CategoryType.SPORTS, R.string.category_sports, R.drawable.sports,false),
                new Category(5, CategoryType.KID, R.string.category_kids, R.drawable.kids,false),
                new Category(6, CategoryType.JEWELS, R.string.category_jewels, R.drawable.jewels,false)
        ));

        if(!CollectionUtils.isEmpty(Store.SELECTED_SHOP.getCategoryList())){
            boolean allSelected = true;
            for (Category category:
                categorySelectionList) {
                if(Store.SELECTED_SHOP.getCategoryList().contains(category.getCategoryType())){
                    category.setSelected(true);
                }else if(category.getCategoryType() != CategoryType.ALL_CATEGORY && category.getCategoryType() != CategoryType.UPDATE){
                    allSelected = false;
                }
            }
            for (Category category:
            categorySelectionList) {
                if (category.getCategoryType() == CategoryType.ALL_CATEGORY){
                    category.setSelected(allSelected);
                }
            }
        }

        categoryList = (RecyclerView) findViewById(R.id.categoryList);
        submitCategory = (Button) findViewById(R.id.submitCategory);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this,3);
        categoryList.setLayoutManager(mLayoutManager);
        categoryList.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new CategoryAdapter(this, categorySelectionList, position -> {
            mAdapter.changeSelectionCategory(position);
        });
        categoryList.setAdapter(mAdapter);
        categoryList.addItemDecoration(new SpacesItemDecoration(30));

        submitCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : API to submit category
                Store.SELECTED_SHOP.setCategoryList(mAdapter.getSelectedCategoryType());
                if(CategoryFragment.mAdapter!=null){
                    CategoryFragment.mAdapter.updateCategoryList(mAdapter.getSelectedCategory());
                }
                finish();
            }
        });
    }
}
