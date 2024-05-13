package com.bradley.dart.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.bradley.dart.utils.pgp.PgpDecryptionUtil;
import com.bradley.dart.utils.pgp.PgpEncryptionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class is a singleton that handles the File IO for the application
 * @author Trenton
 */
public class FileIO {

    @SuppressLint("StaticFieldLeak")
    public static FileIO instance;

    // Use this every single time when accessing FileIO, never instantiate a new instance in code
    public static FileIO getInstance(Context context, int privateKeyResource, int publicKeyResource) {
        if (instance == null) {
            instance = new FileIO(context, privateKeyResource, publicKeyResource);
        }
        return instance;
    }

    // ***********************
    // FIELDS
    // ***********************
    private final Context ctx;
    private final Gson gson;
    private final PgpEncryptionUtil pgpEncryptionUtil;
    private PgpDecryptionUtil pgpDecryptionUtil;
    private final int publicKeyResource;

    private final String VILLAGE_DATA_FILE = "villages.json";
    private final String NEW_DATA_FILE = "new_data.json";
    private final Handler handler;

    // ***********************
    // CONSTRUCTORS
    // ***********************

    private FileIO(Context context, int privateKeyResource, int publicKeyResource) {
        this.ctx = context;
        this.gson = new Gson();
        this.publicKeyResource = publicKeyResource;
        this.handler = new Handler(Looper.getMainLooper());

        this.pgpEncryptionUtil = new PgpEncryptionUtil(
                CompressionAlgorithmTags.BZIP2,
                SymmetricKeyAlgorithmTags.AES_128,
                true,
                true
        );

        // get the private key file details
        InputStream privateKeyStream = getPrivateKey(privateKeyResource);
        Scanner scanner = new Scanner(privateKeyStream).useDelimiter("\\A");
        String pk = scanner.hasNext() ? scanner.next() : "";

        try {
            this.pgpDecryptionUtil = new PgpDecryptionUtil(pk,"dummy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * Use this to load village data directly from the Android file system
     */
    public void loadVillagesAsync(OnVillagesLoadedListener listener) {
        loadLocalFileAsync(VILLAGE_DATA_FILE, true, new OnFileLoadedListener() {
            @Override
            public void onSuccess(String content) {
                ArrayList<Village> villages = gson.fromJson(content, new TypeToken<ArrayList<Village>>(){}.getType());

                // Handle the loaded villages
                if (listener != null) {
                    listener.onSuccess(villages);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the file loading failure
                e.printStackTrace();
                // For example, show an error message
                if (listener != null) {
                    listener.onFailure(e);
                }
            }
        });
    }

    /*
     * Use this to load new data directly from the Android file system
     */
    public void loadNewDataAsync(OnVillagesLoadedListener listener) {
        loadLocalFileAsync(NEW_DATA_FILE, true, new OnFileLoadedListener() {
            @Override
            public void onSuccess(String content) {
                ArrayList<Village> villages = gson.fromJson(content, new TypeToken<ArrayList<Village>>(){}.getType());

                // Handle the loaded villages
                if (listener != null) {
                    listener.onSuccess(villages);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the file loading failure
                e.printStackTrace();
                // For example, show an error message
                if (listener != null) {
                    listener.onFailure(e);
                }
            }
        });
    }

    /*
     * Use this to load a file directly from the res folder
     * @param resource Specifies the resource id
     * @param encrypted Specifies if the file is encrypted
     */
    public void loadRawFileAsync(int resource, boolean encrypted, OnFileLoadedListener listener) {
        new Thread(() -> {
            try {
                InputStream inStream = ctx.getResources().openRawResource(resource);
                if (encrypted) {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    pgpDecryptionUtil.decrypt(inStream, outStream);
                    inStream = new ByteArrayInputStream(outStream.toByteArray());
                    outStream.close();
                }

                String content;
                Scanner scanner = new Scanner(inStream).useDelimiter("\\A");
                content = scanner.hasNext() ? scanner.next() : "";
                scanner.close();
                inStream.close();

                // Use Handler to post the result to the UI thread
                handler.post(() -> {
                    if (listener != null) {
                        listener.onSuccess(content);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                // Use Handler to post the exception to the UI thread
                handler.post(() -> {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
            }
        }).start();
    }


    /*
     * Use this to load a file directly from the Android file system
     * @param filename Specifies the path to the file
     * @param encrypted Specifies if the file is encrypted
     */
    public void loadLocalFileAsync(String filename, boolean encrypted, OnFileLoadedListener listener) {
        new Thread(() -> {
            try {
                FileInputStream inStream = ctx.openFileInput(filename);

                String content;

                if (encrypted) {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    pgpDecryptionUtil.decrypt(inStream, outStream);
                    content = outStream.toString();
                    outStream.close();
                } else {

                    Scanner s = new Scanner(inStream).useDelimiter("\\A");
                    content = s.hasNext() ? s.next() : "";

//                    content = inStream.toString();
                }

                inStream.close();

                // Use Handler to post the result to the UI thread
                handler.post(() -> {
                    if (listener != null) {
                        listener.onSuccess(content);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Use Handler to post the exception to the UI thread
                handler.post(() -> {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
            }
        }).start();
    }

    /*
    * Use to save village data to the local file system
     */
    public void saveVillagesAsync(ArrayList<Village> villages, final OnSaveVillagesListener listener) {
        if (villages == null) {
            if (listener != null) {
                listener.onFailure(new NullPointerException("Null village data entered"));
            }
            return;
        }

        final String villagesJson = gson.toJson(villages);

        saveLocalFileAsync(VILLAGE_DATA_FILE, villagesJson, true, new OnFileSavedListener() {
            @Override
            public void onSuccess(Void res) {
                // Notify the listener that villages are successfully saved
                if (listener != null) {
                    listener.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Notify the listener that the save operation failed
                if (listener != null) {
                    listener.onFailure(e);
                }
            }
        });
    }

    /*
     * Use to save new data to the local file system
     */
    public void saveNewDataAsync(ArrayList<Village> newData, final OnSaveVillagesListener listener) {
        if (newData == null) {
            if (listener != null) {
                listener.onFailure(new NullPointerException("Null village data entered"));
            }
            return;
        }

        final String villagesJson = gson.toJson(newData);

        saveLocalFileAsync(NEW_DATA_FILE, villagesJson, true, new OnFileSavedListener() {
            @Override
            public void onSuccess(Void res) {
                // Notify the listener that villages are successfully saved
                if (listener != null) {
                    listener.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Notify the listener that the save operation failed
                if (listener != null) {
                    listener.onFailure(e);
                }
            }
        });
    }

    /*
     * Use to save data to a file in the Android file system
     * @param filename Specifies the path of the file
     * @param encrypted Specifies whether the file should be encrypted with PGP
     */
    public void saveLocalFileAsync(final String filename, final String content, final boolean encrypted, final OnFileSavedListener listener) {
        new Thread(() -> {
            try {
                FileOutputStream outStream = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
                if (encrypted) {
                    ByteArrayInputStream inStream = new ByteArrayInputStream(content.getBytes(getDefaultCharSet()));
                    InputStream keyInStream = getPublicKey();
                    pgpEncryptionUtil.encrypt(outStream, inStream, inStream.available(), keyInStream);
                } else {
                    outStream.write(content.getBytes(getDefaultCharSet()));
                }

                // Notify the listener that the file is successfully saved
                if (listener != null) {
                    listener.onSuccess(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Notify the listener that the save operation failed
                if (listener != null) {
                    listener.onFailure(e);
                }
            }
        }).start();
    }

    public InputStream getPrivateKey(int privateKeyResource) {
        return ctx.getResources().openRawResource(privateKeyResource);
    }

    public InputStream getPublicKey() {
        return ctx.getResources().openRawResource(publicKeyResource);
    }

    public static String getDefaultCharSet() {
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
        String enc = writer.getEncoding();
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enc;
    }

    public interface FileOperationListener<T> {
        default void onSuccess(T result) {
            // Default implementation for success
        }

        default void onFailure(Exception e) {
            // Default implementation for failure
            e.printStackTrace();
        }
    }

    public interface OnFileLoadedListener extends FileOperationListener<String> {
    }

    public interface OnVillagesLoadedListener extends FileOperationListener<ArrayList<Village>> {
    }


    public interface OnSaveVillagesListener extends FileOperationListener<Void> {
    }

    public interface OnFileSavedListener extends FileOperationListener<Void> {
    }
}
