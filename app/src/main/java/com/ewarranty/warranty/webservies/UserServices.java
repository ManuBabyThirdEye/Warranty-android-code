package com.ewarranty.warranty.webservies;

import com.ewarranty.warranty.models.Card;
import com.ewarranty.warranty.models.Company;
import com.ewarranty.warranty.models.Incident;
import com.ewarranty.warranty.models.IncidentCount;
import com.ewarranty.warranty.models.Offer;
import com.ewarranty.warranty.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserServices {

    @POST("user")
    public Call<User> loginAsUser(@Body User profile);

    @GET("phoneNumber")
    @Headers("Content-Type: text/plain;charset=UTF-8")
    public Call<String> getPhoneNumberByEmail(@Query("emailId") String email);

    @GET("userEntity")
    public Call<User> getUserByPhoneNUmber(@Query("phoneNumber") String phoneNumber);

    @GET("firstOffers")
    public Call<List<Offer>> mostHappeningOffers();

    @GET("myOffers")
    public Call<List<Offer>> myStoreOffers(@Query("phoneNumber") String phoneNumber,@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

    @GET("otherOffers")
    public Call<List<Offer>> myPaidOffers(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

    @GET("getShopOffers")
    public Call<List<Offer>> getShopOffers(@Query("shopId") long shopId, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize);


    @GET("cards")
    public Call<List<Card>> getCardsByUser(@Query("phoneNumber") String phoneNumber,@Query("productCategory") String category,@Query("cardStatus") String cardStatus);

    @GET("card")
    public Call<Card> getCardById(@Query("cardId") long cardId);

    @Multipart
    @POST("card")
    public Call<Card> createCardForUser(@Query("phoneNumber") String phoneNumber, @Part("cardsDetailsString") RequestBody card, @Part List<MultipartBody.Part> images);

    @Multipart
    @PUT("card")
    public Call<Card> updateCardForUser(@Query("phoneNumber") String phoneNumber, @Part("cardsDetailsString") RequestBody card, @Part List<MultipartBody.Part> images);


    @Multipart
    @POST("addProfilePic")
    @Headers("Content-Type: text/plain;charset=UTF-8")
    public Call<String> addProfilePic(@Query("phoneNumber") String phoneNumber , @Part MultipartBody.Part profileFile);


    @Multipart
    @POST("incident")
    public Call<Incident> createIncidentForUser(@Query("phoneNumber") String phoneNumber, @Part("incidentString") RequestBody incident, @Part List<MultipartBody.Part> images);

    @GET("incidents")
    public Call<List<Incident>> getListOfIncidentByStatus(@Query("phoneNumber") String phoneNumber, @Query("status") String status, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize );

    @GET("incident")
    public Call<Incident> getIncidentById(@Query("incidentId") long id);


    @GET("incidents")
    public Call<List<Incident>> getListOfIncident(@Query("phoneNumber") String phoneNumber);

    @GET("companies")
    public Call<List<Company>> getCompanyList();

    @GET("incidentStatusCount")
    public Call<IncidentCount> getAllRequestCountByStatus(@Query("phoneNumber") String phoneNumber);

    @GET("incidentCount")
    public Call<Integer> getAllRequestCount(@Query("phoneNumber") String phoneNumber);
}
