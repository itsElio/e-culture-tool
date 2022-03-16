package com.example.e_culturetoolbakers.model;

import java.io.Serializable;

public class Opera implements Serializable {

    private String nome;
    private String descrizione;
    private String foto;
    private int id;


    public Opera(String nomeOpera, String descrizioneOpera, String fotoOpera) {
        this.nome = nomeOpera;
        this.descrizione = descrizioneOpera;
        this.foto = fotoOpera;

    }

    public Opera(String nomeOpera, String descrizioneOpera, int id, String fotoOpera) {
        this.nome = nomeOpera;
        this.descrizione = descrizioneOpera;
        this.id = id;
        this.foto = fotoOpera;

    }

    public String getNomeOpera() {
        return nome;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getId() {
        return id;
    }

    public String getFoto() {
        return foto;
    }

    public void modificaOpera(String nomeOpera, String descrizioneOpera, String fotoOpera) {
        this.nome = nomeOpera;
        this.descrizione = descrizioneOpera;
        this.foto = fotoOpera;
    }

    public void modificaOpera(String nomeOpera, String descrizioneOpera) {
        this.nome = nomeOpera;
        this.descrizione = descrizioneOpera;
    }
}