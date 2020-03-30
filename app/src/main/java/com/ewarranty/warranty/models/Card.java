package com.ewarranty.warranty.models;

import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Expose
    private long cardId;
    @Expose
    private String productId;
    @Expose
    private String productName;
    @Expose
    private CategoryType productCategory;
    @Expose
    private String productOtherInformation;
    @Expose
    private String billNumber;
    @Expose
    private Date billingDate;
    @Expose
    private long warrantyPeriod;
    @Expose
    private CardStatus cardStatus;

    @Expose(serialize = false)
    private Shop shop;
    @Expose
    private long shopId;
    @Expose
    private List<String> imageUris;
    @Expose
    private List<User> sharedUser;

    @Expose(serialize = false)
    private Company company;
    @Expose
    private long companyId;
}
