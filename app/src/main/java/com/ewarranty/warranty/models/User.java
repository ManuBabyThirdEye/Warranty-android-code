package com.ewarranty.warranty.models;

import com.google.gson.annotations.Expose;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Expose
    private String uid;
    @Expose
    private String deviceId;
    @Expose
    private String name;
    @Expose
    private String emailId;
    @Expose
    private String phoneNumber;
    @Expose
    private String profileUri;
    @Expose
    private Role role;
    @Expose
    private List<Shop> shopVOList;
}
