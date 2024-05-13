package com.bradley.dart.utils;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This class stores village information
 * @author Trenton
 */
public class Village {

    // ***********************
    // FIELDS
    // ***********************
    private String villageID;
    private String name;
    /*
     * residents stores all patients that live in a given village
     */
    private ArrayList<Resident> residents;

    // ***********************
    // CONSTRUCTORS
    // ***********************

    public Village(String name, ArrayList<Resident> residents) {
        this.villageID = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        this.name = name;
        this.residents = residents;
    }

    public Village(String villageID, String name, ArrayList<Resident> residents) {
        this.villageID = villageID;
        this.name = name;
        this.residents = residents;
    }

    // ***********************
    // METHODS
    // ***********************

    public String getVillageID() {
        return this.villageID;
    }

    public String getName() {
        return this.name;
    }

    public void addResident(Resident resident){
        residents.add(resident);
    }

    public ArrayList<Resident> getResidents() {
        return this.residents;
    }

    @Override
    public String toString() {
        return "Village{" +
                "villageID='" + villageID + '\'' +
                ", name='" + name + '\'' +
                ", residents=" + residents +
                '}';
    }

    /*
     * This method is used to get a copy of the current object
     */
    public Village getEmptyCopy() {
        return new Village(this.villageID, this.name, new ArrayList<>());
    }
}
