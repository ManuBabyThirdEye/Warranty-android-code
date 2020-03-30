package com.ewarranty.warranty.models;

import com.google.gson.annotations.Expose;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shop {
    private long shopId;
    private String shopName;
    private String gstNumber;
    private ServiceType serviceType;
    private List<CategoryType> categoryList;
}
