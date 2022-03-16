package com.example.e_culturetoolbakers.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Museo implements Serializable {
    private int id_museo;
    private String nome;
    private String informazioni;
    private String tipo;
    private ArrayList<Zona> zone = new ArrayList<>();


    public Museo(int id_museo,String nomeMuseo, String tipoMuseo, String informazioniMuseo) {
        this.id_museo=id_museo;
        this.nome = nomeMuseo;
        this.informazioni = informazioniMuseo;
        this.tipo = tipoMuseo;

    }

    public  ArrayList<String>  getZoneNames() {
        ArrayList<String> nomiZone =new ArrayList<>();
        for (int i=0;i< zone.size();i++){
            nomiZone.add(zone.get(i).getNome());
        }
        return  nomiZone;
    }

    public int getId_museo() {
        return id_museo;
    }

    public  ArrayList<Zona>  getZoneByNames(ArrayList<String> nomiZone) {
        ArrayList<Zona> zoneSelezionate = new ArrayList<>();
        boolean salta=false;
        for (int i=0;i< zone.size();i++){
            int j=0;
            salta=false;
            while(j< nomiZone.size()&& !salta){
                if(zone.get(i).getNome().equals(nomiZone.get(j))) {
                    zoneSelezionate.add(zone.get(i));
                    salta = true;
                }
                j++;
            }

        }
        return  zoneSelezionate;
    }

    public void setZone(ArrayList<Zona> zone) {
        this.zone = zone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getInformazioni() {
        return informazioni;
    }

    public void setInformazioni(String informazioni) {
        this.informazioni = informazioni;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void addZona(int id,String nomeZona, String descrizioneZona, String fotoZona) {
        Zona nuovaZona = new Zona( nomeZona, descrizioneZona, fotoZona, id);
        zone.add(nuovaZona);
    }

    public void addZona(String nomeZona, String descrizioneZona, String fotoZona) {
        Zona nuovaZona = new Zona(nomeZona, descrizioneZona, fotoZona);
        zone.add(nuovaZona);
    }

    public Museo(String nomeMuseo, String informazioniMuseo, String tipoMuseo) {
        this.nome = nomeMuseo;
        this.informazioni = informazioniMuseo;
        this.tipo = tipoMuseo;

    }

    public Zona findZona(String nomeZona) {
        for(Zona zona : this.zone) {
            if(zona.getNome().equals(nomeZona)) {
                return zona;
            }
        }
        return null;
    }

    public List<Zona> getZone() {
        if(zone.isEmpty()) {
            return null;
        }
        return zone;
    }


}
