package com.ewarranty.warranty.carddetails;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Card;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ShopAndBillFragment extends Fragment {

    private Spinner periodType;
    private ImageView calenderButton;
    private EditText billNumber,billDate,warrantyPeriod;
    private ImageView forwardFromBill,backToProduct;

    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_bill_shop_details, container, false);

        billNumber = (EditText) rootView.findViewById(R.id.billNumber);
        billDate = (EditText) rootView.findViewById(R.id.billDate);
        warrantyPeriod = (EditText) rootView.findViewById(R.id.warrantyPeriod);

        forwardFromBill = (ImageView) rootView.findViewById(R.id.forwardFromBill);
        backToProduct = (ImageView) rootView.findViewById(R.id.backToProduct);

        periodType = (Spinner) rootView.findViewById(R.id.periodType);

        updateCardDisplayMode(((CardDetailsActivity)getActivity()).cardDetailMode);
        updateData(((CardDetailsActivity)getActivity()).cardDetailMode,((CardDetailsActivity)getActivity()).card);

        billNumber.setOnFocusChangeListener((view, b) -> {
            if(b){
                billNumber.setBackground(getActivity().getDrawable(R.drawable.edit_box_active_bk));
            }else{
                billNumber.setBackground(getActivity().getDrawable(R.drawable.edit_box_inactive_bk));
            }
        });

        billDate.setOnFocusChangeListener((view, b) -> {
            if(b){
                billDate.setBackground(getActivity().getDrawable(R.drawable.edit_box_active_bk));
            }else{
                billDate.setBackground(getActivity().getDrawable(R.drawable.edit_box_inactive_bk));
            }
        });

        warrantyPeriod.setOnFocusChangeListener((view, b) -> {
            if(b){
                warrantyPeriod.setBackground(getActivity().getDrawable(R.drawable.edit_box_active_bk));
            }else{
                warrantyPeriod.setBackground(getActivity().getDrawable(R.drawable.edit_box_inactive_bk));
            }
        });
        calenderButton = (ImageView) rootView.findViewById(R.id.calenderButton);

        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, day);
                billDate.setText(dateFormat.format(newDate.getTime()));

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        billDate.addTextChangedListener(new TextWatcher() {
            int previousCount = 0;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                previousCount = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString() !=null && ((previousCount==1 && editable.toString().length()==2)||(previousCount==4 && editable.toString().length()==5))){
                    billDate.setText(editable.toString()+"-");
                    billDate.setSelection(billDate.getText().toString().length());
                }

                if(editable.toString() !=null && editable.length()>10){
                    billDate.setText(editable.toString().substring(0,10));
                    billDate.setSelection(billDate.getText().toString().length());
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.period_type, R.layout.my_simple_text_view);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodType.setAdapter(adapter);


        forwardFromBill.setOnClickListener(view -> {
            ((CardDetailsActivity)getActivity()).cardDetailView.setCurrentItem(2);
        });

        backToProduct.setOnClickListener(view -> {
            ((CardDetailsActivity)getActivity()).cardDetailView.setCurrentItem(0);
        });

        return rootView;
    }

    private void updateData(CardDetailMode cardDetailMode, Card card) {
        if(cardDetailMode == CardDetailMode.EDIT || cardDetailMode == CardDetailMode.DISPLAY){
            billNumber.setText(card.getBillNumber());
            billDate.setText(card.getBillingDate()==null?"":dateFormat.format(card.getBillingDate()));
            warrantyPeriod.setText(card.getWarrantyPeriod()+"");

        }
    }

    private void updateCardDisplayMode(CardDetailMode cardDetailMode) {
        switch (cardDetailMode){
            case ADD:
            case EDIT:
                forwardFromBill.setVisibility(View.VISIBLE);
                backToProduct.setVisibility(View.VISIBLE);
                billNumber.setEnabled(true);
                billDate.setEnabled(true);
                warrantyPeriod.setEnabled(true);
                break;
            case DISPLAY:
                backToProduct.setVisibility(View.GONE);
                forwardFromBill.setVisibility(View.GONE);
                billNumber.setEnabled(false);
                billDate.setEnabled(false);
                warrantyPeriod.setEnabled(false);
                break;
        }
    }

    public void getUpdatedShopBillInfo(Card card) {
        card.setBillNumber(billNumber.getText().toString());
        if(billDate.getText() != null && billDate.getText().length()==10){
            try {
                card.setBillingDate(dateFormat.parse(billDate.getText().toString()));
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Invalid date, Expected date format (dd-MM-yyyy)", Toast.LENGTH_LONG).show();
            }
        }
        Calendar newCalendar = Calendar.getInstance();

        if(warrantyPeriod.getText() != null && warrantyPeriod.getText().length()>0){
            try {
                int period = Integer.parseInt(warrantyPeriod.getText().toString());
                int typePosition = periodType.getSelectedItemPosition();

                switch (typePosition){
                    case 0:
                        card.setWarrantyPeriod(period); break;
                    case 1:
                        Calendar today = newCalendar.getInstance();
                        today.set(Calendar.MONTH,today.get(Calendar.MONTH)+period);
                        long diff = today.getTime().getTime() - newCalendar.getInstance().getTime().getTime();
                        card.setWarrantyPeriod(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                        break;
                    case 2:
                        Calendar today1 = newCalendar.getInstance();
                        today1.set(Calendar.YEAR,today1.get(Calendar.YEAR)+period);
                        long diff1 = today1.getTime().getTime() - newCalendar.getInstance().getTime().getTime();
                        card.setWarrantyPeriod(TimeUnit.DAYS.convert(diff1, TimeUnit.MILLISECONDS));
                        break;
                    default:
                        card.setWarrantyPeriod(period);
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Warranty Period must be number", Toast.LENGTH_LONG).show();
            }
        }

    }
}