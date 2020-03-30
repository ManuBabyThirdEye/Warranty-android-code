package com.ewarranty.warranty.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    private long companyId;
    private String companyName;
    private String logo;
    private String supportEmailId;
    private String supportWhatsappNumber;
    private String supportPhoneNumber;
}
