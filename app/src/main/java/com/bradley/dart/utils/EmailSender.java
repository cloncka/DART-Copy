package com.bradley.dart.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * This class executes the functional aspect of sending emails
 *
 * @author Trenton
 */
public class EmailSender {

    final String PASS = "not a password";

    public void sendEmail(final Context ctx, final InputStream inStream, final String uuid) {
        final String username = "fake-emailhere";

        // ensure that the email sending functionality is on a separate thread from main
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String recipient = "placeholderemailhere";

                // Set Gmail SMTP server properties
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com"); // Gmail SMTP server
                props.put("mail.smtp.port", "587"); // Port for Gmail SMTP server

                // Create a session with authentication
                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, PASS);
                            }
                        });

                try {
                    // Create a default MimeMessage object
                    Message message = new MimeMessage(session);

                    // Set From: header field of the header
                    message.setFrom(new InternetAddress(username));

                    // Set To: header field of the header
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(recipient)); // Recipient's email

                    // Set Subject: header field
                    message.setSubject(uuid);

                    // Create the message part
                    BodyPart messageBodyPart = new MimeBodyPart();

                    // Now set the actual message
                    messageBodyPart.setText("");

                    // Create a multipart message
                    Multipart multipart = new MimeMultipart();

                    // Set text message part
                    multipart.addBodyPart(messageBodyPart);

                    // Attachment input stream
                    InputStream inputStream = inStream;

                    // Create the attachment part
                    messageBodyPart = new MimeBodyPart();

                    // Set the attachment using input stream
                    DataSource source = new ByteArrayDataSource(inputStream, "application/octet-stream");
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName("data.pgp"); // Set the attachment file name

                    // Add attachment to multipart
                    multipart.addBodyPart(messageBodyPart);

                    // Set the complete message parts
                    message.setContent(multipart);

                    // Send message
                    Transport.send(message);

                    // Post a toast to the main thread indicating success
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(ctx, "Data uploaded successfully!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                } catch (Exception e) {
                    // Post a toast to the main thread indicating failure

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(ctx, "Upload failed. Check your Internet connection.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }
        });
        thread.start();
    }
}
