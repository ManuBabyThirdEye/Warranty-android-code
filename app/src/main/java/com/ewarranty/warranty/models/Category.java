package com.ewarranty.warranty.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private int categoryId;
    private CategoryType categoryType;
    private int categoryNameResourceId;
    private int categoryResourceId;
    private boolean selected;
}
