package com.ewarranty.warranty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ewarranty.warranty.adapter.company.CompanyListAdapter;
import com.ewarranty.warranty.adapter.incident.IncidentAdapter;
import com.ewarranty.warranty.carddetails.ProductFragment;
import com.ewarranty.warranty.models.Company;
import com.ewarranty.warranty.util.Store;
import com.ewarranty.warranty.webservies.ServiceGenerator;
import com.ewarranty.warranty.webservies.UserServices;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompanyListActivity extends AppCompatActivity {

    private ImageButton back;
    private EditText searchCompany;
    private RecyclerView companyRecyclerView;
    CompanyListAdapter companyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);

        Store.SERVICE = ServiceGenerator.createService(UserServices.class);

        back = (ImageButton) findViewById(R.id.back);
        searchCompany = (EditText) findViewById(R.id.searchCompany);
        companyRecyclerView = (RecyclerView) findViewById(R.id.companyRecyclerView);

        back.setOnClickListener(view -> {
            finish();
        });

        searchCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Store.SERVICE.getCompanyList().enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
                if(response.body()!=null){
                    companyRecyclerView.setLayoutManager(new LinearLayoutManager(CompanyListActivity.this));
                    companyRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    Gson g = new Gson();
                    companyAdapter = new CompanyListAdapter(CompanyListActivity.this,response.body(), company -> {
                        Intent output = new Intent();
                        output.putExtra(ProductFragment.COMPANY_DETAILS, g.toJson(company));
                        setResult(RESULT_OK, output);
                        finish();
                    });
                    companyRecyclerView.setAdapter(companyAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {

            }
        });

    }
}
