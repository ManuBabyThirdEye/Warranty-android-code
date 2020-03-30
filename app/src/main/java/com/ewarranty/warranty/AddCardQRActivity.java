package com.ewarranty.warranty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.ewarranty.warranty.carddetails.CardDetailMode;
import com.ewarranty.warranty.carddetails.CardDetailsActivity;
import com.google.zxing.Result;

public class AddCardQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    private Button addWarranty;

    private static final int REQUEST_CAMERA = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_qr);

        mScannerView = (ZXingScannerView) findViewById(R.id.mScannerView);
        addWarranty = (Button) findViewById(R.id.addWarranty);

        addWarranty.setOnClickListener(view -> {
           // mScannerView.stopCamera();
            Intent cardDetail = new Intent(AddCardQRActivity.this, CardDetailsActivity.class);
            cardDetail.putExtra("cardDetailMode", CardDetailMode.ADD.toString());
            AddCardQRActivity.this.startActivity(cardDetail);
            finish();
        });

        if (ActivityCompat.checkSelfPermission(AddCardQRActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddCardQRActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        String scannedResult = result.getText();

        if(scannedResult!=null){
            if(validateScannedResult(scannedResult)){
                Toast.makeText(AddCardQRActivity.this,"QR Code scanning not supported", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(AddCardQRActivity.this,"Invalid QR Code", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateScannedResult(String scannedResult) {
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    mScannerView.startCamera();

                } else {
                    finish();
                }
                break;
            }


        }
    }
}
