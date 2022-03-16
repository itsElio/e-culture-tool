package com.example.e_culturetoolbakers;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.e_culturetoolbakers.database.DatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Locale locale;
    private Configuration config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button buttonLanguage = getView().findViewById(R.id.buttonLanguage);
        Button buttonDownload = getView().findViewById(R.id.buttonDownload);
        Button buttonUpload = getView().findViewById(R.id.buttonUpload);
        buttonLanguage.setOnClickListener(this);
        buttonDownload.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLanguage:
                changeLanguage();
                break;
            case R.id.buttonDownload:
                downloadData();
                break;
            case R.id.buttonUpload:
                uploadData();
                break;
        }
    }

    public void changeLanguage() {
        SharedPreferences preferences = getActivity().getPreferences(Activity.MODE_PRIVATE);
        String currentLang = preferences.getString("My_Lang", "");
        if(!currentLang.equals("en") && !currentLang.equals("en_US") && !currentLang.equals("en_us")) {
            locale = new Locale("en");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("My_Lang", "en");
            editor.apply();
        } else {
            locale = new Locale("it_IT");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("My_Lang", "it_IT");
            editor.apply();
        }
        Locale.setDefault(locale);
        config = getActivity().getResources().getConfiguration();
        config.locale = locale;
        getActivity().getResources().updateConfiguration(config,
                getActivity().getResources().getDisplayMetrics());

        // Go to homepage
        Intent myIntent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(myIntent);
    }

    public void downloadData() {
        File file = new File(getActivity().getExternalFilesDir(null) + File.separator + "musei_db.sqlite");
        if(file.exists()){
            Toast.makeText(getActivity(), "Dati già scaricati!", Toast.LENGTH_LONG).show();
        }else {
            String url = "//";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("");
            request.setTitle("Download E-Culture Tool data");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(getActivity(), null, "musei_db.sqlite");

            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
            Toast.makeText(getActivity(), "Dati scaricati da fonte esterna!", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadData() {
        DatabaseHelper db_helper = new DatabaseHelper(getActivity());

        db_helper.getReadableDatabase();
        //Copia DB
        if (copiaDatabase(getActivity())){
            Toast.makeText(getActivity(), "Caricamento effettuato!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Caricamento fallito", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private boolean copiaDatabase (Context context) {
        try {
            File file = new File(getActivity().getExternalFilesDir(null) + File.separator + "musei_db.sqlite");
            if(!file.exists()){
                return false;
            }
            FileInputStream inputStream = new FileInputStream(file);
            file.delete();
            String outFileName = DatabaseHelper.DB_PATH + DatabaseHelper.DB_NAME;
            File f = new File(outFileName);
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[]buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) >0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            return false;
        }
    }
}