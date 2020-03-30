package com.ewarranty.warranty.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Offer {

    private String imageUri;
    private double offerRate;
    private double actualRate;
    private String offerDetails;
    private String offerDescription;
    private Shop shop;
}
