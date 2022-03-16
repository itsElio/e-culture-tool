package com.example.e_culturetoolbakers.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Zona implements Serializable {
    private int id;
    private String nome;
    private String descrizione;
    // Da modificare questa String foto!
    private String foto;

    public List<Opera> opere = new ArrayList<>();

    public void setOpere(List<Opera> opere) {
        this.opere = opere;
    }

    public  ArrayList<String>  getOpereNames() {
        ArrayList<String> nomiZone =new ArrayList<>();
        for (int i=0;i< opere.size();i++){
            nomiZone.add(opere.get(i).getNome());
        }
        return  nomiZone;
    }

    public int getId() {
        return id;
    }


    public Zona(String nomeZona, String descrizioneZona, String fotoZona, int id) {
        this.id=id;
        this.nome = nomeZona;
        this.descrizione = descrizioneZona;
        this.foto = fotoZona;

    }

    public Zona(String nomeZona, String descrizioneZona) {
        this.nome = nomeZona;
        this.descrizione = descrizioneZona;

    }

    public void addOpera(String nomeOpera, String descrizioneOpera, String fotoOpera) {
        Opera nuovaOpera = new Opera(nomeOpera, descrizioneOpera, fotoOpera);
        opere.add(nuovaOpera);
    }

    public String getNome() {
        return nome;
    }
    public List<Opera> getOpere() {
        return opere;
    }

    public Zona(String nomeZona, String descrizioneZona, String fotoZona) {
        this.nome = nomeZona;
        this.descrizione = descrizioneZona;
        this.foto = fotoZona;

    }

    public String getFoto() {
        return this.foto;
    }

    public String getDescrizione() {
        return this.descrizione;
    }

    public Opera findOpera(String nome) {
        for(Opera opera : opere ) {
            if(opera.getNomeOpera().equals(nome)) {
                return opera;
            }
        }
        return null;
    }


}
