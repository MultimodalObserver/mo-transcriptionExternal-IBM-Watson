/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import com.google.common.collect.Lists;
import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.model.SpeechTimestamp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import mo.analysis.AnalysisProvider;
import mo.analysis.TranscriptionProvider;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;
import mo.organization.ProjectOrganization;
import mo.organization.StagePlugin;
import model.Transcription;

/**
 *
 * @author Lathy
 */

@Extension(
    xtends = {
        @Extends(extensionPointId = "mo.analysis.AnalysisProvider")
    }
)
public class TranscriptionPluginExternal  implements AnalysisProvider, TranscriptionProvider<Transcription, SpeechTimestamp>{
    public String name = "Transcription External - IBM-Watson";
    
    private List<Configuration> configurations; 
    ResourceBundle dialogBundle = java.util.ResourceBundle.getBundle("properties/principal");
    int numberConfiguration = 1;
    
    
    public TranscriptionPluginExternal(){
        configurations = new ArrayList<>();
        
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Configuration initNewConfiguration(ProjectOrganization organization) {
        JFrame frame = new JFrame("Transcription External");
        //custom title, warning icon
        JOptionPane.showMessageDialog(frame,
            dialogBundle.getString("SeAccedeAUnPluginTra"),
            "Information",
        JOptionPane.INFORMATION_MESSAGE);
        
        
        TranscriptionExternalConfiguration c = new TranscriptionExternalConfiguration();
        
        c.setId("Configuration TranscriptionExternal "+numberConfiguration);
        configurations.add(c);
        numberConfiguration++;
            
        return c;
    }

    @Override
    public List<Configuration> getConfigurations() {
        return configurations;
    }

    @Override
    public StagePlugin fromFile(File file) {
        if (file.isFile()) {
            try {
                TranscriptionPluginExternal mc = new TranscriptionPluginExternal();
                XElement root = XIO.readUTF(new FileInputStream(file));
                XElement[] pathsX = root.getElements("path");
                for (XElement pathX : pathsX) {
                    String path = pathX.getString();
                    TranscriptionExternalConfiguration c = new TranscriptionExternalConfiguration();
                    Configuration config = c.fromFile(new File(file.getParentFile(), path));
                    if (config != null) {
                        mc.configurations.add(config);
                    }
                }
                return mc;
            } catch (IOException ex) {
                
            }
        }
        return null;
    }

    @Override
    public File toFile(File parent) {
        return null;
    }

    @Override
    public void transcribe(String idioma, String path, int cantidadPalabras, Transcription transcriptionLocal){
        System.out.println("Entro transcribe externo");
        String text = "";
        path = transcriptionLocal.getPathAudio(); //cargo el path de cada audio
        
        File audio = new File(path); //carga el path del archivo de la ventana de carga
         
        
        //File filestring = new File("my/file/dir/file.mp3");
        Media file = new Media(audio.toURI().toString());  

        MediaPlayer mediaPlayer = new MediaPlayer(file);
        //double duration;
        mediaPlayer.setOnReady(new Runnable() {
        //double duration;
            @Override
            public void run() {
                //duration = file.getDuration().toSeconds();
                //labelCambiar.setText(""+file.getDuration().toSeconds());
                transcriptionLocal.setDurationAudio((float)file.getDuration().toSeconds()); //Setea la duracion del audio
                //System.out.println("Duration mediaplayer: "+file.getDuration().toSeconds());

            }
        });
        
        
       
        
        
        IamAuthenticator authenticator = new IamAuthenticator("P4IK4wS-i7fELM1gpdwzbeRybtt9iHtY1HOQMl7whaF-");
        SpeechToText speechToText = new SpeechToText(authenticator);
        speechToText.setServiceUrl("https://api.us-south.speech-to-text.watson.cloud.ibm.com/instances/d63e0973-c01e-4c88-bbf5-d70adaad0427");
        try {
        //build the recognize options.
        RecognizeOptions options = new RecognizeOptions.Builder()
                    
                    .audio(audio)
                    //.contentType(HttpMediaType.AUDIO_MP3) //select your format
                    //.contentType(HttpMediaType.AUDIO_WAV)
                    
                    .contentType(HttpMediaType.APPLICATION_OCTET_STREAM) //permite reconocer el tipo de archivo entrante
                    .model(idioma)
                    .timestamps(true)
                    //.inactivityTimeout(360)

                    
                    .build();
        
        
        //execute the api service
        //Result tiene todas las alternativas del texto
        SpeechRecognitionResults result = speechToText.recognize(options).execute().getResult();
        List<SpeechTimestamp> timestamp = new ArrayList(); //Lista con los timestamps por palabra
        
        
        
        
        for (int i=0; i<result.getResults().size(); i++){
            text = text + result.getResults().get(i).getAlternatives().get(0).getTranscript(); //contiene el texto
            timestamp.addAll(result.getResults().get(i).getAlternatives().get(0).getTimestamps()); //contiene el timestamp de cada palabra
        }
        String PathOfFileCreate ;
        PathOfFileCreate = createFileSrt(path); //Crea el archivo que contiene el .srt
        
        
        List<String> listString = new ArrayList<>();
        List<Object> listObject = new ArrayList<Object>();
        writeFileSrt(PathOfFileCreate,listString, timestamp,listObject, cantidadPalabras);
        transcriptionLocal.setNameTranscription(transcriptionLocal.getNombreAudio()+".srt"); //nombre del archivo de transcripcion del audio
        
        String fileName = PathOfFileCreate;
        transcriptionLocal.setPathTranscription(printPathFile(fileName));//se guarda el path de la transcripcion
        transcriptionLocal.setTamanoTranscription(printFileSize(fileName));//se guarda el tamano de la transcripcion

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TranscriptionPluginExternal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    
    }

    @Override
    public String createFileSrt(String name) {
        String path = null;
        try {
            File file = new File(name+".srt");
            if (file.createNewFile()){
                path = file.getAbsoluteFile().toString();
                //System.out.println("File new create: "+ file.getName());
                
                //System.out.println("\n");
            }else{
                path = file.getAbsoluteFile().toString();
                //System.out.println("File already exists: "+ file.getName());   
            }
            
        } catch (IOException ex) {
            Logger.getLogger(TranscriptionPluginExternal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return path;
    }

    @Override
    public void writeFileSrt(String nameFile, List<String> words, List<SpeechTimestamp> starStamp, List<Object> endStamp, int numberOfPhrase) {
        int row=0;
        
        try {
            FileWriter fileWrite = new FileWriter (nameFile);
            //Divido la lista de timestamp en listas pequeñas de n palabras, guardadas en partition
            for (List<SpeechTimestamp> partition : Lists.partition(starStamp, numberOfPhrase)) {
                
                    fileWrite.write((row+1)+"");
                    fileWrite.write("\n");
                    fileWrite.write(convertSecondsToHours(partition.get(0).getStartTime().floatValue()));
                    fileWrite.write(" --> ");
                    fileWrite.write(convertSecondsToHours(partition.get(partition.size()-1).getEndTime().floatValue()));
                    fileWrite.write("\n");
                    for (int j=0; j<partition.size();j++){
                        fileWrite.write(partition.get(j).getWord());
                        fileWrite.write(" ");

                    }
                    fileWrite.write("\n");
                    fileWrite.write("\n");
                    row++;
                
            }
            fileWrite.write("\n");
            fileWrite.close();//
            
            
        } catch (IOException ex) {
            Logger.getLogger(TranscriptionPluginExternal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public float printFileSize(String fileName) {
        File file = new File(fileName);
        long bytes;
        float kilobytes = 0f;
        if (file.exists()) {

            // size of a file (in bytes)
            bytes = file.length();
            kilobytes = (bytes / 1024f);
            
//            long megabytes = (kilobytes / 1024);
//            long gigabytes = (megabytes / 1024);
//            long terabytes = (gigabytes / 1024);
//            long petabytes = (terabytes / 1024);
//            long exabytes = (petabytes / 1024);
//            long zettabytes = (exabytes / 1024);
//            long yottabytes = (zettabytes / 1024);
              //System.out.println(kilobytes);
//            System.out.println(String.format("%,d bytes", bytes));
              //System.out.println(String.format("%,f kilobytes", kilobytes));
//            System.out.println(String.format("%,d megabytes", megabytes));
//            System.out.println(String.format("%,d gigabytes", gigabytes));
//            System.out.println(String.format("%,d terabytes", terabytes));
//            System.out.println(String.format("%,d petabytes", petabytes));
//            System.out.println(String.format("%,d exabytes", exabytes));
//            System.out.println(String.format("%,d zettabytes", zettabytes));
//            System.out.println(String.format("%,d yottabytes", yottabytes));

        } else {
            //System.out.println("File does not exist!");
        }
        return kilobytes;
    }

    @Override
    public String printPathFile(String fileName) {
        File file = new File(fileName);
        String path=null;
        if (file.exists()) {

            // size of a file (in bytes)
            
            path = file.getAbsolutePath();
            
            
//            long megabytes = (kilobytes / 1024);
//            long gigabytes = (megabytes / 1024);
//            long terabytes = (gigabytes / 1024);
//            long petabytes = (terabytes / 1024);
//            long exabytes = (petabytes / 1024);
//            long zettabytes = (exabytes / 1024);
//            long yottabytes = (zettabytes / 1024);
              //System.out.println(kilobytes);
//            System.out.println(String.format("%,d bytes", bytes));
              //System.out.println(String.format("%,f kilobytes", kilobytes));
//            System.out.println(String.format("%,d megabytes", megabytes));
//            System.out.println(String.format("%,d gigabytes", gigabytes));
//            System.out.println(String.format("%,d terabytes", terabytes));
//            System.out.println(String.format("%,d petabytes", petabytes));
//            System.out.println(String.format("%,d exabytes", exabytes));
//            System.out.println(String.format("%,d zettabytes", zettabytes));
//            System.out.println(String.format("%,d yottabytes", yottabytes));

        } else {
            //System.out.println("File does not exist!");
        }
        return path;
    }
    
    public String convertSecondsToHours (float myseconds){
        float seconds = (float) myseconds;
        float segundos =  (seconds % 60.00f);
        int horas = (int)(seconds / 60);
        int minutos = horas % 60;
        horas = horas / 60;
        String formattedString = String.format("%s:%s:%.03f", horas,minutos,segundos);
        //System.out.println(formattedString);
        //System.out.print( horas + ":" + minutos + ":" + segundos);
        return formattedString;
    
    }
    
}
