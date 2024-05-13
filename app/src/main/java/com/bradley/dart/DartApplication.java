package com.bradley.dart;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.multidex.MultiDexApplication;

import com.bradley.dart.utils.DataEntry;
import com.bradley.dart.utils.FileIO;
import com.bradley.dart.utils.PgpTransformer;
import com.bradley.dart.utils.Resident;
import com.bradley.dart.utils.Village;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;


public class DartApplication extends MultiDexApplication {
/**
 * This class is an application layer used to manage the village data and new data accessed throughout the app
 * @author Trenton
 */
    final private String UUID_FILE_PATH = "uuid.data";
    /*
     * villages is an array of villages that stores all village and patient data included that which has already been exported
     * keep in mind, villages store residents which themselves store data entries
     */
    private ArrayList<Village> villages;
    /*
    * newData is an array of villages that stores all of the newly-added data that has yet to be exported
    * keep in mind, villages store residents which themselves store data entries
     */
    private ArrayList<Village> newData;

    /*
    * userId is a unique identifier for the device generated when the app is first loaded
     */
    private String userId;
    private final Handler handler = new Handler(Looper.getMainLooper());

    /*
    * This is an asyncronous function that retrieves the newData object as soon as it's loaded
     */
    public void getNewData(FileIO.OnVillagesLoadedListener listener) {
        new Thread(() -> {
            while (this.newData == null) {
                continue;
            }
            handler.post(() -> {
                if (listener != null) {
                    listener.onSuccess(this.newData);
                }
            });
        }).start();

    }

    /*
     * This is an asyncronous function that retrieves the villages object as soon as it's loaded from the file system
     */
    public void getVillages(FileIO.OnVillagesLoadedListener listener) {
        new Thread(() -> {
            while (this.villages == null) {
                continue;
            }
            handler.post(() -> {
                if (listener != null) {
                    listener.onSuccess(this.villages);
                }
            });
        }).start();
    }

    public String getUserId() {
        return userId;
    }

    private void setVillages(ArrayList<Village> villages) {
        this.villages = villages;
    }

    private void setNewData(ArrayList<Village> newData) {
        this.newData = newData;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    /*
    * Use when adding a new village to the data
     */
    public void addVillage(Village village) {
        this.villages.add(village);
        this.newData.add(village.getEmptyCopy());
        this.saveVillages(this.villages);
        this.saveNewData(this.newData);
    }

    /**
     * Use when adding a new patient to the data
     * @param parentVillage Specifies the village that the patient resides in
     */
    public void addResident(Village parentVillage, Resident resident) {
        // add patient to existing village in village data
        for (int i = 0; i < this.villages.size(); i++) {
            if (parentVillage.getVillageID() == this.villages.get(i).getVillageID()) {
                this.villages.get(i).addResident(resident);
                break;
            }
        }
        this.saveVillages(this.villages);

        // add patient to existing village in new data if present, otherwise create new village copy and add to that
        boolean foundVillage = false;
        for (int i = 0; i < this.newData.size(); i++) {
            if (parentVillage.getVillageID() == this.newData.get(i).getVillageID()) {
                this.newData.get(i).addResident(resident.getEmptyCopy());
                foundVillage = true;
                break;
            }
        }
        if (!foundVillage) {
            this.newData.add(parentVillage.getEmptyCopy());
            this.newData.get(this.newData.size()-1).addResident(resident.getEmptyCopy());
        }
        this.saveNewData(this.newData);
    }

    /**
     * Use when adding a new entry for a given patient in a given village
     * @param parentVillage Specifies the village that the patient resides in
     * @param parentResident Specifies the resident that the entry is being added to
     */
    public void addEntry(Village parentVillage, Resident parentResident, DataEntry entry) {
        // add entry to existing village and resident in village data
        for (int i = 0; i < this.villages.size(); i++) {
            if (parentVillage.getVillageID() == this.villages.get(i).getVillageID()) {
                Village currentVillage = this.villages.get(i);
                ArrayList<Resident> villageResidents = currentVillage.getResidents();
                for (int j = 0; j < villageResidents.size(); j++) {
                    if (parentResident.getPatientID() == villageResidents.get(j).getPatientID()) {
                        villageResidents.get(j).addDataEntry(entry);
                    }
                }
            }
        }
        this.saveVillages(this.villages);

        // add entry to existing village and resident in new data, otherwise create copies and add to that
        boolean foundVillage = false;
        boolean foundPatient = false;
        for (int i = 0; i < this.newData.size(); i++) {
            if (parentVillage.getVillageID() == this.newData.get(i).getVillageID()) {
                Village currentVillage = this.newData.get(i);
                ArrayList<Resident> villageResidents = currentVillage.getResidents();
                for (int j = 0; j < villageResidents.size(); j++) {
                    if (parentResident.getPatientID() == villageResidents.get(j).getPatientID()) {
                        foundPatient = true;
                        villageResidents.get(j).addDataEntry(entry.getEmptyCopy());
                    }
                }
                if (!foundPatient) {
                    DataEntry copyEntry = entry.getEmptyCopy();
                    Resident copyResident = parentResident.getEmptyCopy();
                    copyResident.addDataEntry(copyEntry);
                    currentVillage.addResident(copyResident);
                }
                foundVillage = true;
            }
        }
        if (!foundVillage) {
            DataEntry copyEntry = entry.getEmptyCopy();
            Resident copyResident = parentResident.getEmptyCopy();
            Village copyVillage = parentVillage.getEmptyCopy();
            copyResident.addDataEntry(copyEntry);
            copyVillage.addResident(copyResident);
            this.newData.add(copyVillage);
        }
        this.saveNewData(this.newData);
    }

    /*
     * Use when saving new data to local file system
     */
    public void saveNewData(ArrayList<Village> newData) {
        setNewData(newData);

        FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).saveNewDataAsync(newData, new FileIO.OnSaveVillagesListener() {
            @Override
            public void onSuccess(Void result) {
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /*
     * Use when saving village data to local file system
     */
    public void saveVillages(ArrayList<Village> villages) {
        setVillages(villages);
        FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).saveVillagesAsync(villages, new FileIO.OnSaveVillagesListener() {
            @Override
            public void onSuccess(Void result) {
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /*
    * Generates a unique ID for the user when the app is loaded for the first time
     */
    private void generateUuid() {
        String newId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).saveLocalFileAsync(UUID_FILE_PATH, newId, false, new FileIO.OnFileSavedListener() {
            @Override
            public void onSuccess(Void result) {
                setUserId(newId);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                setUserId("USER_ID_FAILED_TO_ASSIGN");
//                int x = 1/0;
            }
        });
    }

    /*
    * onCreate is an inhereted method from Application that runs on application startup
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Load or generated the user's UUID
        FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).loadLocalFileAsync(UUID_FILE_PATH, false, new FileIO.OnFileLoadedListener() {
            @Override
            public void onSuccess(String uuid) {
                setUserId(uuid);
            }

            @Override
            public void onFailure(Exception e) {
                generateUuid();
            }
        });

        // Load the villages data from the file system if present, otherwise load default (empty)
        FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).loadVillagesAsync(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> villages) {

                System.out.println("VILLAGES SIZE: " + villages.size());

                if (villages == null) {

                    loadDefaultVillages();
                    } else {
                        setVillages(villages);
                    }
                //setVillages(new ArrayList<>());
            }

            @Override
            public void onFailure(Exception e) {
                loadDefaultVillages();
                e.printStackTrace();
            }
        });

        // Load the new data from the file system if present, otherwise set empty
        FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).loadNewDataAsync(new FileIO.OnVillagesLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Village> newData) {
                if (newData == null) {
                    saveNewData(new ArrayList<Village>());
                } else {
                    saveNewData(newData);
                }
            }

            @Override
            public void onFailure(Exception e) {
                setNewData(new ArrayList<Village>());
                e.printStackTrace();
            }
        });
    }

    /*
    * loads the default villages found in the fallback_villages.json file, you can upload sample JSON data for debugging
     */
    private void loadDefaultVillages() {
        FileIO.getInstance(getApplicationContext(), R.raw.privatekey, R.raw.publickey).loadRawFileAsync(R.raw.fallback_villages, false, new FileIO.OnFileLoadedListener() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ArrayList<Village> villages = gson.fromJson(result, new TypeToken<ArrayList<Village>>(){}.getType());
                saveVillages(villages);
            }

            @Override
            public void onFailure(Exception e) {
                saveVillages(new ArrayList<Village>());
//                e.printStackTrace();
            }
        });
    }

}
