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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.e_culturetoolbakers.database.DatabaseHelper;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ModificaOperaRicerca extends AppCompatActivity {

    ImageView fotoInserita;
    private TextView nomeOpera;
    private TextInputLayout descrizioneOpera;
    private String mCurrentPhotoPath;
    private Bitmap captureImage;
    String currentPhotoPath;
    private String foto;
    Button addFoto;

    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DataSnapshot newSnapshot;
    private StorageReference opereRef;

    private DatabaseHelper db_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_opera_ricerca);


        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        db_helper = new DatabaseHelper(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance("https://e-culture-tool-c381c-default-rtdb.europe-west1.firebasedatabase.app/");


        Intent intent = getIntent();
        Opera operaDaModificare = (Opera) intent.getSerializableExtra("opera");
        Museo museo = (Museo) intent.getSerializableExtra("museo");
        Zona zona = (Zona) intent.getSerializableExtra("zona");

        // Ref del database!!
        DatabaseReference ref = database.getReference("Musei");

        // Check dei permessi
        if(ContextCompat.checkSelfPermission(ModificaOperaRicerca.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ModificaOperaRicerca.this, new String[] {
                    Manifest.permission.CAMERA
            }, 100);
        }
        if(ContextCompat.checkSelfPermission(ModificaOperaRicerca.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ModificaOperaRicerca.this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 101);
        }


        fotoInserita = findViewById(R.id.fotoOpera2);

        String url = "gs://e-culture-tool-c381c.appspot.com/images/";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref1 = storage.getReferenceFromUrl(url);

        ref1.child(operaDaModificare.getFoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(fotoInserita);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        nomeOpera = findViewById(R.id.nomeOpera);
        nomeOpera.setText(operaDaModificare.getNomeOpera());

        descrizioneOpera = findViewById(R.id.infoOpera);
        descrizioneOpera.getEditText().setText(operaDaModificare.getDescrizione());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot);
                System.out.println(snapshot);
                // Snapshot all'opera !!
                newSnapshot = snapshot.child(museo.getNome()).child("zone").child(zona.getNome()).child("opere").child(operaDaModificare.getNomeOpera());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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

                String nome = nomeOpera.getText().toString();
                Editable info = descrizioneOpera.getEditText().getText();

                DatabaseReference ref = database.getReference("Musei");
                DatabaseReference newRef = ref.child(museo.getNome()).child("zone").child(zona.getNome()).child("opere").child(operaDaModificare.getNomeOpera());

                if(foto!= null){

                    // Modifica valori su firebase
                    newRef.child("nome").setValue(nome.toString());
                    newRef.child("descrizione").setValue(info.toString());

                    Uri file = Uri.fromFile(new File(foto));
                    System.out.println("file: "+file);
                    String newOperaString = foto.substring(foto.lastIndexOf("/") + 1);
                    newRef.child("foto").setValue(newOperaString);

                    db_helper.updateOpera(operaDaModificare.getId(), nome.toString(), info.toString(), newOperaString);

                    opereRef = mStorageRef.child("images/" + newOperaString);

                    opereRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    // ...
                                    System.out.println("Fail");
                                }
                            });
                } else {
                    // Modifica valori su firebase
                    newRef.child("nome").setValue(nome.toString());
                    newRef.child("descrizione").setValue(info.toString());

                    db_helper.updateOpera(operaDaModificare.getId(), nome, info.toString(), operaDaModificare.getFoto());
                }

                Intent intent = new Intent(ModificaOperaRicerca.this, SchermataOpera.class);
                intent.putExtra("nome",nome);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Modifica qui, aggiungi il request code per le foto da gallery.
        if (requestCode == 2) {
            try {
                // Non so perchÃ¨ data == null quando la foto viene presa da Camera
                // Suppongo sia perchÃ¨ viene passato Extra all'intent sottoforma di
                // Array Parcelable piuttosto di intent
                // Gestito if null -> camera; else -> gallery;
                if(data == null || data.getFlags() == 0 ) {
                    captureImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                    // Assegna alla variabile foto (opera.foto) il path al file creato
                    foto = currentPhotoPath;
                } else {
                    Uri photoUri = data.getData();
                    // Assegna alla variabile foto (opera.foto) il path al file giÃ  presente
                    foto = getRealPathFromURI(photoUri);
                    captureImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoUri.toString()));
                }
                fotoInserita.setImageBitmap(captureImage);
                System.out.println("La mia foto invece: " + foto);
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
            // Errore
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
        TextView nomeOpera = findViewById(R.id.nomeOpera);
        TextInputLayout infoOpera = (TextInputLayout ) findViewById(R.id.infoOpera);

        CharSequence nomeText = nomeOpera.getText();
        outState.putCharSequence("nome", nomeText);

        CharSequence infoText = infoOpera.getEditText().getText();
        outState.putCharSequence("info", infoText);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        TextView nomeOpera = findViewById(R.id.nomeOpera);
        TextInputLayout infoOpera = (TextInputLayout ) findViewById(R.id.infoOpera);

        CharSequence nomeSalvato = savedInstanceState.getCharSequence("nome");
        CharSequence infoSalvato = savedInstanceState.getCharSequence("info");

        nomeOpera.setText(nomeSalvato);
        infoOpera.getEditText().setText(infoSalvato);

    }

}