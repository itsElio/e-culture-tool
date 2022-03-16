package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;

public class InserisciOpera extends AppCompatActivity {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private Bitmap captureImage;
    private String foto;
    ImageView fotoInserita;
    Button addFoto;
    String currentPhotoPath;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserisci_opera);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Inizializzazione variabili

        Bundle museo = getIntent().getExtras();
        Serializable serializedMuseo  = museo.getSerializable("museoObject");

        TextInputLayout nomeOpera = (TextInputLayout ) findViewById(R.id.nomeOpera);
        TextInputLayout descrizioneOpera = (TextInputLayout ) findViewById(R.id.infoOpera);

        fotoInserita = findViewById(R.id.fotoOpera2);

        String zonaSelezionata = getIntent().getStringExtra("Zona");
        addFoto = (Button) findViewById(R.id.addFoto);

        // Check dei permessi (requestCode credo sia tipo un id)
        if(ContextCompat.checkSelfPermission(InserisciOpera.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(InserisciOpera.this, new String[] {
                    Manifest.permission.CAMERA
            }, 100);
        }
        if(ContextCompat.checkSelfPermission(InserisciOpera.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(InserisciOpera.this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 101);
        }

        addFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        Button confirmBtn = (Button) findViewById(R.id.buttonConferma);

        // Bottone conferma --> rimanda a riepilogo opere
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Modifica qui!
                Editable nome = nomeOpera.getEditText().getText();
                Editable info = descrizioneOpera.getEditText().getText();

                Intent riepilogoOpere = new Intent(InserisciOpera.this, RiepilogoOpere.class);
                // Deserializza
                Museo nuovoMuseo = (Museo) serializedMuseo;

                if(TextUtils.isEmpty(nome) || TextUtils.isEmpty(info)) {
                    Toast.makeText(InserisciOpera.this, "Compila tutti i campi prima di proseguire", Toast.LENGTH_SHORT).show();
                }  else if (TextUtils.isEmpty(foto)) {
                Toast.makeText(InserisciOpera.this, "Inserisci una foto prima di proseguire", Toast.LENGTH_SHORT).show();
                } else {
                    for (Zona zona : nuovoMuseo.getZone()) {
                        if(zona.getNome().equals(zonaSelezionata)) {
                            zona.addOpera(nome.toString(), info.toString(), foto);
                        }
                    }
                    Bundle museo = new Bundle();
                    museo.putSerializable("museoObject", nuovoMuseo);
                    riepilogoOpere.putExtras(museo);
                    InserisciOpera.this.startActivity(riepilogoOpere);
                }
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
            Button addFoto = (Button) findViewById(R.id.addFoto);
            addFoto.setText(R.string.modificaFotoOpera);
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