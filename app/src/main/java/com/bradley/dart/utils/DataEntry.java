package com.bradley.dart.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
* This class stores data for patients' blood glucose readings
* @author Trenton
*/
public class DataEntry {

    // ***********************
    // FIELDS
    // ***********************
    private String entryID;

    /*
    * bloodSugar is a float representing blood glucose in mmol/l
     */
    private float bloodSugar;
    private String additionalComments;

    /*
    * recordedOn should be stored as a DateTime string in the ISO 8601 format
    * example: 2012-03-02T14:57:05.456+0500
     */
    private String recordedOn;


    // ***********************
    // CONSTRUCTORS
    // ***********************

    public DataEntry(float bloodSugar, String additionalComments, String recordedOn) {
        this.entryID = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        this.bloodSugar = bloodSugar;
        this.additionalComments = additionalComments;
        this.recordedOn = recordedOn;
    }

    public DataEntry(String entryID, float bloodSugar, String additionalComments, String recordedOn) {
        this.entryID = entryID;
        this.bloodSugar = bloodSugar;
        this.additionalComments = additionalComments;
        this.recordedOn = recordedOn;
    }

    // ***********************
    // METHODS
    // ***********************

    public String getEntryID() {
        return entryID;
    }

    public float getBloodSugar() {
        return bloodSugar;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }
      
    public String getRecordedOn() { return recordedOn; }

    /*
    * Used to get the recordedOn param as a Date object
     */
    public Date getRecordedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            if(recordedOn == null){
                return null;
            }
            return dateFormat.parse(recordedOn);
        } catch (ParseException e) {
            // Handle parsing exception according to your requirements
            e.printStackTrace();
            return null; // Or throw an exception
        }
    }


    @Override
    public String toString() {
        return "DataEntry{" +
                "entryID='" + entryID + '\'' +
                ", bloodSugar=" + bloodSugar +
                ", additionalComments='" + additionalComments + '\'' +
                ", recordedOn='" + recordedOn + '\'' +
                '}';
    }

    /*
    * This method is used to get a copy of the current object
     */
    public DataEntry getEmptyCopy() {
        return new DataEntry(this.entryID, this.bloodSugar, this.additionalComments, this.recordedOn);
    }
}