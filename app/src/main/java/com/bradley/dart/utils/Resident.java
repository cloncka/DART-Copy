package com.bradley.dart.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * This class stores patients' personal information
 * @author Trenton
 */
public class Resident {

    // ***********************
    // FIELDS
    // ***********************
    private String patientID;
    private String name;
    private String address;
    /*
     * birthday should be stored as a Date string in the ISO 8601 format
     * example: 2012-03-02T00:00:00.000Z
     */
    private String birthday;
    /*
     * entries stores all a given patient's glucose readings
     */
    private ArrayList<DataEntry> entries;

    // ***********************
    // CONSTRUCTORS
    // ***********************

    public Resident(String name, String address, String birthday, ArrayList<DataEntry> entries) {
        this.patientID = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        this.name = name;
        this.entries = entries;
        this.address = address;
        this.birthday = birthday;
    }

    public Resident(String patientID, String name, String address, String birthday, ArrayList<DataEntry> entries) {
        this.patientID = patientID;
        this.name = name;
        this.entries = entries;
        this.address = address;
        this.birthday = birthday;
    }

    // ***********************
    // METHODS
    // ***********************

    public String getPatientID() {
        return patientID;
    }

    public String getName() {
        return name;
    }

    public ArrayList<DataEntry> getEntries() {
        return entries;
    }

    public void addDataEntry(DataEntry dataEntry){
        entries.add(dataEntry);
    }
  
    public String getAddress() {
        return address;
    }

    public String getBirthday() {
        return birthday;
    }

    /*
     * Used to get the recordedOn param as a Date object
     */
    public Date getBirthdayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return dateFormat.parse(birthday);
        } catch (ParseException e) {
            // Handle parsing exception according to your requirements
            e.printStackTrace();
            return null; // Or throw an exception
        }
    }

    @Override
    public String toString() {
        return "Resident{" +
                "patientID='" + patientID + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", birthday='" + birthday + '\'' +
                ", entries=" + entries.size() +
                '}';
    }

    /*
     * This method is used to get a copy of the current object
     */
    public Resident getEmptyCopy() {
        return new Resident(this.patientID, this.name, this.address, this.birthday, new ArrayList<>());
    }
}
