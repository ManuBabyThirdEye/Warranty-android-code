package com.ewarranty.warranty.ui.Category;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Category;
import com.ewarranty.warranty.models.CategoryType;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private final List<Category> mValues;
    private final CategoryAdapter.OnListFragmentInteractionListener mListener;
    private final Activity activity;

    public CategoryAdapter(Activity activity,List<Category> items, CategoryAdapter.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_category, parent, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.categoryImage.setImageResource(holder.mItem.getCategoryResourceId());
        holder.categoryHeading.setText(holder.mItem.getCategoryNameResourceId());

        holder.selectionOverlay.setVisibility(holder.mItem.isSelected()?View.VISIBLE:View.GONE);
        holder.categoryHeading.setTextColor(holder.mItem.isSelected()?activity.getResources().getColor(R.color.innerBackground,null):activity.getResources().getColor(R.color.primaryTextColor,null));

        holder.mView.setOnClickListener(view -> {
            if(mListener!=null){
                mListener.onListFragmentInteraction(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void changeSelectionCategory(int position) {
        if(mValues.get(position).getCategoryType()== CategoryType.ALL_CATEGORY){
            boolean updateStatus = !mValues.get(position).isSelected();
            mValues.stream().forEach(c->{
                c.setSelected(updateStatus);
            });
        }else{
            mValues.get(position).setSelected(!mValues.get(position).isSelected());
        }
        notifyDataSetChanged();
    }

    public List<Category> getSelectedCategory() {
        return mValues.stream().filter(c->c.isSelected() && c.getCategoryType() != CategoryType.ALL_CATEGORY).collect(Collectors.toList());
    }

    public List<CategoryType> getSelectedCategoryType() {
        return mValues.stream().filter(c->c.isSelected() && c.getCategoryType() != CategoryType.ALL_CATEGORY).map(Category::getCategoryType).collect(Collectors.toList());
    }

    public void updateCategoryList(List<Category> categoryList) {

        List<CategoryType> categoryTypeList = categoryList.stream().map(Category::getCategoryType).collect(Collectors.toList());

        Log.d("Test", "categoryTypeList: "+categoryTypeList);

        mValues.removeIf(c->!categoryTypeList.contains(c.getCategoryType()));

        Log.d("Test", "mValues: "+mValues);


        List<CategoryType> mValuesCategoryTypeList = mValues.stream().map(Category::getCategoryType).collect(Collectors.toList());

        for (Category category:
            categoryList) {
            if(!mValuesCategoryTypeList.contains(category.getCategoryType())){
                category.setSelected(false);
                mValues.add(category);
            }
        }

        if(!CollectionUtils.isEmpty(mValues) && !mValuesCategoryTypeList.contains(CategoryType.ALL_CATEGORY)){
            Collections.reverse(mValues);
            mValues.add(new Category(0, CategoryType.ALL_CATEGORY,R.string.category_all,R.drawable.all_category,false));
            Collections.reverse(mValues);
        }

        mValues.add(new Category(111,CategoryType.UPDATE,R.string.category_update,R.drawable.update_category,false));
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView categoryImage;
        public final View mView;
        public final TextView categoryHeading;
        public final RelativeLayout selectionOverlay;
        public Category mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            categoryImage = (ImageView) view.findViewById(R.id.categoryImage);
            categoryHeading = (TextView) view.findViewById(R.id.categoryHeading);
            selectionOverlay = (RelativeLayout) view.findViewById(R.id.selectionOverlay);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(int position);
    }
}
