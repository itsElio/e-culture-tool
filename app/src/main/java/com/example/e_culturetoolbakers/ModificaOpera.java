package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;

public class ModificaOpera extends AppCompatActivity {
    ImageView fotoInserita;
    private TextInputLayout nomeOpera;
    private TextInputLayout descrizioneOpera;
    private String mCurrentPhotoPath;
    private Bitmap captureImage;
    String currentPhotoPath;
    private String foto;
    Button addFoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_opera);


        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        Bundle museo = getIntent().getExtras();

        Serializable serializedMuseo  = museo.getSerializable("museoObject");
        Serializable serializedOpera = museo.getSerializable("opera");
        Serializable serializedZona = museo.getSerializable("zona");

        Opera operaDaModificare = (Opera) serializedOpera;

        Museo nuovoMuseo = (Museo) serializedMuseo;

        Zona zonaSelezionata = (Zona) serializedZona;

        fotoInserita = findViewById(R.id.fotoOpera2);
        // Setta image
        //fotoInserita.setImageBitmap(captureImage);
        try {
            fotoInserita.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("file:" + operaDaModificare.getFoto())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        nomeOpera = findViewById(R.id.nomeOpera);
        nomeOpera.getEditText().setText(operaDaModificare.getNomeOpera());

        descrizioneOpera = findViewById(R.id.infoOpera);
        descrizioneOpera.getEditText().setText(operaDaModificare.getDescrizione());

        addFoto = (Button) findViewById(R.id.addFoto);

        addFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        Button confirmBtn = findViewById(R.id.buttonConferma);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent riepilogoRegistrazione = new Intent(ModificaOpera.this, RiepilogoRegistrazione.class);

                Editable nome = nomeOpera.getEditText().getText();
                Editable info = descrizioneOpera.getEditText().getText();

                if(foto == null){
                    nuovoMuseo.findZona(zonaSelezionata.getNome()).findOpera(operaDaModificare.getNomeOpera()).modificaOpera(nome.toString(), info.toString());
                } else {
                    nuovoMuseo.findZona(zonaSelezionata.getNome()).findOpera(operaDaModificare.getNomeOpera()).modificaOpera(nome.toString(), info.toString(), foto);
                }

                Bundle museo = new Bundle();
                museo.putSerializable("museoObject", nuovoMuseo);

                riepilogoRegistrazione.putExtras(museo);

                ModificaOpera.this.startActivity(riepilogoRegistrazione);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Modifica qui, aggiungi il request code per le foto da gallery.
        if (requestCode == 2) {
            try {
                // Non so perchè data == null quando la foto viene presa da Camera
                // Suppongo sia perchè viene passato Extra all'intent sottoforma di
                // Array Parcelable piuttosto di intent
                // Gestito if null -> camera; else -> gallery;
                if(data == null || data.getFlags() == 0 ) {
                    captureImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                    // Assegna alla variabile foto (opera.foto) il path al file creato
                    foto = currentPhotoPath;
                } else {
                    Uri photoUri = data.getData();
                    // Assegna alla variabile foto (opera.foto) il path al file già presente
                    foto = getRealPathFromURI(photoUri);
                    captureImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoUri.toString()));
                }
                fotoInserita.setImageBitmap(captureImage);
            } catch (IOException e){
                // TODO
            }
        }
    }


    // Prendi in input nome opera !!
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {

        Intent addFotoFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent addFotoFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Errore, fai qualcosa non lo so
            System.out.println("Should never reach here");
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider", photoFile);
            // Qui non fa vera e propria differenza tra gli Intent chiamati, viene differenziato solo in base agli output
            addFotoFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            Intent[] intentArray = { addFotoFromCamera };

            Intent chooser = Intent.createChooser(addFotoFromGallery, "Seleziona l'app per aggiungere una foto");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooser, 2);
        }

    }

    // Restituisce il path assoluto di un file di cui si conosce solo l'Uri;
    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        TextInputLayout nomeOpera = (TextInputLayout ) findViewById(R.id.nomeOpera);
        TextInputLayout infoOpera = (TextInputLayout ) findViewById(R.id.infoOpera);

        CharSequence nomeText = nomeOpera.getEditText().getText();
        outState.putCharSequence("nome", nomeText);

        CharSequence infoText = infoOpera.getEditText().getText();
        outState.putCharSequence("info", infoText);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        TextInputLayout nomeOpera = (TextInputLayout ) findViewById(R.id.nomeOpera);
        TextInputLayout infoOpera = (TextInputLayout ) findViewById(R.id.infoOpera);

        CharSequence nomeSalvato = savedInstanceState.getCharSequence("nome");
        CharSequence infoSalvato = savedInstanceState.getCharSequence("info");

        nomeOpera.getEditText().setText(nomeSalvato);
        infoOpera.getEditText().setText(infoSalvato);

    }
}