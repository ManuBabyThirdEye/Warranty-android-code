package com.ewarranty.warranty.models;

import android.graphics.Bitmap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageModel {
    private boolean addNew;
    private Bitmap image;
    private String uri;
}
