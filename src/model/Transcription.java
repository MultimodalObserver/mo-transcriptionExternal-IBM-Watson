/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Lathy
 */
public class Transcription {
    
    private String pathAudio;
    private String nombreAudio;
    private String idioma = "";
    private float tamanoAudio;
    private float durationAudio;
    private String nameTranscription;
    private String pathTranscription;
    private float tamanoTranscription;

    public Transcription() {
         //To change body of generated methods, choose Tools | Templates.
    }

    public float getTamanoTranscription() {
        return tamanoTranscription;
    }

    public void setTamanoTranscription(float tamanoTranscription) {
        this.tamanoTranscription = tamanoTranscription;
    }

    
    public String getPathTranscription() {
        return pathTranscription;
    }

    public void setPathTranscription(String pathTranscription) {
        this.pathTranscription = pathTranscription;
    }

    
    public String getNameTranscription() {
        return nameTranscription;
    }

    public void setNameTranscription(String nameTranscription) {
        this.nameTranscription = nameTranscription;
    }

    
    
    public String getPathAudio() {
        return pathAudio;
    }

    public void setPathAudio(String pathAudio) {
        this.pathAudio = pathAudio;
    }

    public String getNombreAudio() {
        return nombreAudio;
    }

    public void setNombreAudio(String nombreAudio) {
        this.nombreAudio = nombreAudio;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public float getTamanoAudio() {
        return tamanoAudio;
    }

    public void setTamanoAudio(float tamanoAudio) {
        this.tamanoAudio = tamanoAudio;
    }

    public float getDurationAudio() {
        return durationAudio;
    }

    public void setDurationAudio(float durationAudio) {
        this.durationAudio = durationAudio;
    }

    public Transcription(String pathAudio, String nombreAudio, float tamanoAudio, float durationAudio, String nameTranscription, String pathTranscription, float tamanoTranscription) {
        this.pathAudio = pathAudio;
        this.nombreAudio = nombreAudio;
        this.tamanoAudio = tamanoAudio;
        this.durationAudio = durationAudio;
        this.nameTranscription = nameTranscription;
        this.pathTranscription = pathTranscription;
        this.tamanoTranscription = tamanoTranscription;
    }
    
    

    
    
    
    
    

    
    
    
    
    
}
