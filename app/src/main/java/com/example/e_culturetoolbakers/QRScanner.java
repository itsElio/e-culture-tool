package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.e_culturetoolbakers.database.DatabaseHelper;
import com.google.zxing.Result;

import java.util.logging.Logger;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle state) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        super.onCreate(state);
        // Setta la view per lo scanner
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Devi accettare il permesso per scannerizzare il QR", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(this, MainActivity.class);
                startActivity(myIntent);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Attiva la fotocamera e lo scanner
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Processa il risultato per aprire i dettagli dell'opera

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        if( databaseHelper.operaPresente(rawResult.getText()) != -1){
            Intent myIntent = new Intent(this, SchermataOpera.class);
            //Toast.makeText(getApplicationContext(), " "+rawResult.getText(), Toast.LENGTH_LONG).show();
            myIntent.putExtra("nome", rawResult.getText());
            startActivity(myIntent);
        } else{
            Intent myIntent = new Intent(this, MainActivity.class);
            Toast.makeText(getApplicationContext(), "Codice non valido", Toast.LENGTH_LONG).show();
            startActivity(myIntent);
        }


        finish();
    }
}