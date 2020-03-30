package com.ewarranty.warranty.util;

import com.ewarranty.warranty.models.Card;
import com.ewarranty.warranty.models.Category;
import com.ewarranty.warranty.models.Incident;
import com.ewarranty.warranty.models.Offer;
import com.ewarranty.warranty.models.Shop;
import com.ewarranty.warranty.models.User;
import com.ewarranty.warranty.models.Settings;
import com.ewarranty.warranty.webservies.UserServices;

import java.util.List;

public class Store {
    public static User PROFILE;
    public static Settings SETTINGS;
    public static UserServices SERVICE;
    public static List<Category> CATEGORY_LIST;
    public static List<Offer> MOST_HAPPENING_OFFER_LIST;
    public static List<Offer> MY_SHOP_OFFER_LIST;
    public static List<Offer> PAID_OFFER_LIST;
    public static List<Card> CURRENT_CARD_LIST;
    public static Card SELECTED_CARD;
    public static Incident SELECTED_INCIDENT;

    public static Shop SELECTED_SHOP;



}
