package com.bradley.dart;

import static com.bradley.dart.utils.FileIO.getDefaultCharSet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.bradley.dart.utils.EmailSender;
import com.bradley.dart.utils.FileIO;
import com.bradley.dart.utils.Village;
import com.bradley.dart.utils.pgp.PgpEncryptionUtil;
import com.google.gson.Gson;

import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
//this was a test class used to send emails to the server
public class SendEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        Button btnSendEmail = findViewById(R.id.btnSendEmail);

        ImageButton back = findViewById(R.id.back_button_exportback);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnSendEmail.setOnClickListener(view -> {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        /*
                        FOR NOW, JUST GET ALL DATA INTO PGP FORMAT
                        IN THE FUTURE, WE'LL WANT TO ONLY SEND THE NEWLY ADDED DATA
                         */
                        DartApplication app = (DartApplication) getApplication();
                        app.getNewData(new FileIO.OnVillagesLoadedListener() {
                            @Override
                            public void onSuccess(ArrayList<Village> result) {
                                try {
                                    System.out.println("VILLAGES RECEIVED");
                                    ArrayList<Village> villages = result;
                                    Gson gson = new Gson();
                                    final String villagesJson = gson.toJson(villages);

                                    PgpEncryptionUtil pgpEncryptionUtil = new PgpEncryptionUtil(
                                            CompressionAlgorithmTags.BZIP2,
                                            SymmetricKeyAlgorithmTags.AES_128,
                                            true,
                                            true
                                    );

                                    OutputStream outStream = new ByteArrayOutputStream();
                                    ByteArrayInputStream inStream = new ByteArrayInputStream(villagesJson.getBytes(getDefaultCharSet()));
                                    InputStream keyInStream = FileIO.instance.getPublicKey();
                                    pgpEncryptionUtil.encrypt(outStream, inStream, inStream.available(), keyInStream);

                                    /*
                                    SEND OUT THE EMAIL CONTENT
                                     */
                                    EmailSender emailSender = new EmailSender();
                                    emailSender.sendEmail(getApplicationContext(), new ByteArrayInputStream(outStream.toString().getBytes()), app.getUserId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        });
    }

}