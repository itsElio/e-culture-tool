package com.example.e_culturetoolbakers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.e_culturetoolbakers.MainActivity;
import com.example.e_culturetoolbakers.R;
import com.example.e_culturetoolbakers.model.Museo;
import com.example.e_culturetoolbakers.model.Opera;
import com.example.e_culturetoolbakers.model.Zona;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "musei_db.sqlite";
   // public static final String MUSEO_TABLE = "MUSEO";
    public static final String DB_PATH = "/data/data/com.example.e_culturetoolbakers/databases/";
    private Context mContext;
    private SQLiteDatabase mDataBase;

    public DatabaseHelper (Context context) {
        super(context, DB_NAME, null , 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(DB_NAME).getPath();
        if (mDataBase != null && mDataBase.isOpen()) {
            return;
        }
        mDataBase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase(){
        if (mDataBase != null) {
            mDataBase.close();
        }
    }

    public ArrayList<Museo> getListMuseo () {
        Museo museo;
        ArrayList<Museo> listaMusei = new ArrayList<>();
        openDatabase();

        Cursor cursor = mDataBase.rawQuery("SELECT * FROM museo", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            museo = new Museo(cursor.getInt(3), cursor.getString(0), cursor.getString(1), cursor.getString(2));
            listaMusei.add(museo);
            cursor.moveToNext();
        }
        cursor.close();
        //
        closeDatabase();
        return listaMusei;
    }

    public ArrayList<Zona> getListZone(Museo nuovoMuseo) {
        ArrayList<Zona> listaZona= new ArrayList<>();
        openDatabase();
        Zona zona;
        Cursor cursor = mDataBase.rawQuery("SELECT * FROM zona WHERE id_museo ==" + nuovoMuseo.getId_museo(), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            zona = new Zona(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(4));
                                //String nomeZona, String descrizioneZona, String fotoZona
            listaZona.add(zona);
            cursor.moveToNext();
        }
        cursor.close();
        //
        closeDatabase();
        return listaZona;
    }

    public ArrayList<Opera> getListOpere(Zona zona) {
        ArrayList<Opera> listaOpere= new ArrayList<>();
        openDatabase();
        Opera opera;
        Cursor cursor = mDataBase.rawQuery("SELECT * FROM opera WHERE id_zona ==" + zona.getId(), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            opera = new Opera(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            //String nomeZona, String descrizioneZona, String fotoZona
            listaOpere.add(opera);
            cursor.moveToNext();
        }
        cursor.close();
        //
        closeDatabase();
        return listaOpere;
    }

    public ArrayList<String> getListOpere2(String zona) {
        ArrayList<String> listaOpere= new ArrayList<>();
        openDatabase();
        Opera opera;
        Cursor cursor1 = mDataBase.rawQuery("SELECT id FROM zona WHERE nome ==" + "\"" +zona+ "\"", null);
        cursor1.moveToFirst();
        String id = cursor1.getString(0);


        Cursor cursor = mDataBase.rawQuery("SELECT nome FROM opera WHERE id_zona ==" + id, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            listaOpere.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        //
        closeDatabase();
        return listaOpere;
    }

    public ArrayList<String> getListOpereTot() {
        ArrayList<String> listaOpere= new ArrayList<>();
        openDatabase();
        Cursor cursor = mDataBase.rawQuery("SELECT nome FROM opera" , null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            listaOpere.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return listaOpere;
    }

    public ArrayList<String> getListZoneTot() {
        ArrayList<String> listaZone= new ArrayList<>();
        openDatabase();
        Cursor cursor = mDataBase.rawQuery("SELECT nome FROM zona" , null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            listaZone.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return listaZone;
    }


    public ArrayList<String> getInfoOpera(String opera) {
        ArrayList<String> info= new ArrayList<>();
        openDatabase();

        Cursor cursor = mDataBase.rawQuery("SELECT descrizione, foto FROM opera WHERE nome ==" + "\"" +opera+ "\"", null);
        cursor.moveToFirst();
        info.add(cursor.getString(0));
        info.add(cursor.getString(1));

        cursor.close();
        //
        closeDatabase();
        return info;
    }

    public ArrayList<String> getDatiZona(String zona, ArrayList<String> opere) {
        ArrayList<String> dati = new ArrayList<>();
        openDatabase();

        Cursor cursor = mDataBase.rawQuery("SELECT id FROM zona WHERE nome ==" + "\"" +zona+ "\"", null);
        cursor.moveToFirst();
        String id = cursor.getString(0);

        cursor = mDataBase.rawQuery("SELECT Z.descrizione, M.nome, Z.foto, M.id, O.nome " +
                "FROM (Zona Z JOIN Opera O ON Z.id = O.id_zona) " +
                "JOIN Museo M ON Z.id_museo = M.id " +
                "WHERE Z.id = " + "\"" +id+ "\"", null);
        cursor.moveToFirst();
        dati.add(id);
        dati.add(cursor.getString(0));
        dati.add(cursor.getString(1));
        dati.add(cursor.getString(2));
        dati.add(cursor.getString(3));
        while (!cursor.isAfterLast()) {
            opere.add(cursor.getString(4));
            cursor.moveToNext();
        }

        cursor.close();
        closeDatabase();
        return dati;
    }

    public ArrayList<String> getDatiOpera(String opera) {
        ArrayList<String> dati = new ArrayList<>();
        openDatabase();

        Cursor cursor = mDataBase.rawQuery("SELECT id FROM opera WHERE nome ==" + "\"" +opera+ "\"", null);
        cursor.moveToFirst();
        String id = cursor.getString(0);

        cursor = mDataBase.rawQuery("SELECT O.descrizione, M.nome, Z.nome, O.foto " +
                "FROM (Zona Z JOIN Opera O ON Z.id = O.id_zona) " +
                "JOIN Museo M ON Z.id_museo = M.id " +
                "WHERE O.id = " + "\"" +id+ "\"", null);
        cursor.moveToFirst();

        dati.add(id);
        dati.add(cursor.getString(0));
        dati.add(cursor.getString(1));
        dati.add(cursor.getString(2));
        dati.add(cursor.getString(3));

        cursor.close();
        closeDatabase();
        return dati;
    }

    public Museo getMuseoOpera(int id){

        openDatabase();
        Cursor cursor = mDataBase.rawQuery("SELECT M.nome, M.descrizione, M.tipo_museo " +
                "FROM (Zona Z JOIN Opera O ON Z.id = O.id_zona) " +
                "JOIN Museo M ON Z.id_museo = M.id " +
                "WHERE O.id = " + "\"" +id+ "\"", null);
        cursor.moveToFirst();

        Museo museo = new Museo(cursor.getString(0),cursor.getString(1),cursor.getString(2));

        cursor.close();
        closeDatabase();
        return museo;
    }

    public Museo getMuseoZona(int id){

        openDatabase();
        Cursor cursor = mDataBase.rawQuery("SELECT M.nome, M.descrizione, M.tipo_museo " +
                "FROM Zona Z JOIN Museo M ON Z.id_museo = M.id " +
                "WHERE Z.id = " + "\"" +id+ "\"", null);
        cursor.moveToFirst();

        Museo museo = new Museo(cursor.getString(0),cursor.getString(1),cursor.getString(2));

        cursor.close();
        closeDatabase();
        return museo;
    }

    public Zona getZonaOpera(int id){

        openDatabase();
        Cursor cursor = mDataBase.rawQuery("SELECT Z.nome, Z.descrizione " +
                "FROM Zona Z JOIN Opera O ON Z.id = O.id_zona " +
                "WHERE O.id = " + "\"" +id+ "\"", null);
        cursor.moveToFirst();

        Zona zona = new Zona(cursor.getString(0),cursor.getString(1));

        cursor.close();
        closeDatabase();
        return zona;
    }

    public void inserisciTest(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", "Nuovo museo");
        values.put("tipo_museo", "ss");
        values.put("descrizione", "x");

        db.insert("museo", null, values);
    }

    public int getIdMuseo(String nome) {
        openDatabase();
        Cursor cursor = mDataBase.rawQuery("SELECT id FROM museo M WHERE M.nome ==" + "\"" +nome+ "\"", null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);

        cursor.close();
        closeDatabase();
        return id;
    }

    public int getIdZona(String nome) {
        openDatabase();
        Cursor cursor = mDataBase.rawQuery("SELECT id FROM zona WHERE nome ==" + "\"" +nome+ "\"", null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);

        cursor.close();
        closeDatabase();
        return id;
    }

    public int operaPresente(String opera) {
        int id = -1;
        openDatabase();

        Cursor cursor = mDataBase.rawQuery("SELECT id FROM opera WHERE nome ==" + "\"" +opera+ "\"", null);

        if(cursor.getCount()!=0){
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }

        cursor.close();
        closeDatabase();
        return id;
    }

    public void inserisciMuseo(String nome, String descrizione, String tipo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("tipo_museo", tipo);
        values.put("descrizione", descrizione);

        db.insert("museo", null, values);
    }

    public void inserisciZona(String nome, String descrizione, int id, String foto){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("descrizione", descrizione);
        values.put("id_museo", id);
        values.put("foto", foto);

        db.insert("zona", null, values);
    }

    public void inserisciOpera(String nome, String descrizione, int id, String foto){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("descrizione", descrizione);
        values.put("foto", foto);
        values.put("id_zona", id);

        db.insert("opera", null, values);
    }

    public void updateOpera(int id, String nome, String descrizione, String foto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues(); // nuovo valore per la colonna
        values.put("nome", nome);
        values.put("descrizione", descrizione);
        values.put("foto", foto);

        // Which row to update, based on the ID
        String selection = "id" + " = ?";
        int selectionArgs = id;
        int count = db.update(
                "opera",
                values,
                selection,
                new String[]{String.valueOf(selectionArgs)});
    }

    public void updateZona(int id, String nome, String descrizione, String foto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues(); // nuovo valore per la colonna
        values.put("nome", nome);
        values.put("descrizione", descrizione);
        values.put("foto", foto);

        // Which row to update, based on the ID
        String selection = "id" + " = ?";
        int selectionArgs = id;
        int count = db.update(
                "zona",
                values,
                selection,
                new String[]{String.valueOf(selectionArgs)});
    }

    public void updateMuseo(String nome, String descrizione, String tipo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues(); // nuovo valore per la colonna
        values.put("descrizione", descrizione);
        values.put("tipo_museo", tipo);


        String selection = "nome" + " = ?";
        String selectionArgs = nome;
        int count = db.update(
                "museo",
                values,
                selection,
                new String[]{selectionArgs});
    }

    public void aggiorna() {

        openDatabase();
        if (mDataBase != null) {
            mDataBase.delete("Museo", null, null);
            mDataBase.delete("Opera", null, null);
            mDataBase.delete("Zona", null, null);
        }

        closeDatabase();
    }

}
