/**
 * PgpDecryptionUtil provides methods to decrypt OpenPGP encrypted data using Bouncy Castle library.
 * This utility class utilizes Bouncy Castle's APIs for handling OpenPGP encryption and decryption.
 */
package com.bradley.dart.utils.pgp;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.security.Security;
import java.util.Iterator;
import java.util.Objects;

public class PgpDecryptionUtil {

    /**
     * Static block to add Bouncy Castle provider to the JVM security providers.
     */
    static {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.addProvider(new BouncyCastleProvider());
    }

    private final char[] passCode;
    private final PGPSecretKeyRingCollection pgpSecretKeyRingCollection;

    /**
     * Constructs a PgpDecryptionUtil object with the provided private key input stream and passphrase.
     *
     * @param privateKeyIn An InputStream containing the private key data.
     * @param passCode     The passphrase to decrypt the private key.
     * @throws IOException  If an I/O error occurs.
     * @throws PGPException If an error occurs in PGP processing.
     */
    public PgpDecryptionUtil(InputStream privateKeyIn, String passCode) throws IOException, PGPException {
        this.passCode = passCode.toCharArray();
        this.pgpSecretKeyRingCollection = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(privateKeyIn),
                new JcaKeyFingerprintCalculator());
    }

    /**
     * Constructs a PgpDecryptionUtil object with the provided private key string and passphrase.
     *
     * @param privateKeyStr A String containing the private key data.
     * @param passCode      The passphrase to decrypt the private key.
     * @throws IOException  If an I/O error occurs.
     * @throws PGPException If an error occurs in PGP processing.
     */
    public PgpDecryptionUtil(String privateKeyStr, String passCode) throws IOException, PGPException {
        this(IOUtils.toInputStream(privateKeyStr, Charset.defaultCharset()), passCode);
    }

    /**
     * Finds the secret key corresponding to the given key ID and decrypts it.
     *
     * @param keyID The ID of the secret key to find.
     * @return The decrypted PGPPrivateKey.
     * @throws PGPException If an error occurs in PGP processing.
     */
    private PGPPrivateKey findSecretKey(long keyID) throws PGPException {
        PGPSecretKey pgpSecretKey = pgpSecretKeyRingCollection.getSecretKey(keyID);
        return pgpSecretKey == null ? null : pgpSecretKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME).build(passCode));
    }

    /**
     * Decrypts the input stream containing OpenPGP encrypted data and writes the decrypted data to the output stream.
     *
     * @param encryptedIn An InputStream containing the encrypted data.
     * @param clearOut    An OutputStream to write the decrypted data.
     * @throws PGPException If an error occurs in PGP processing.
     * @throws IOException  If an I/O error occurs.
     */
    public void decrypt(InputStream encryptedIn, OutputStream clearOut) throws PGPException, IOException {
        // Removing armour and returning the underlying binary encrypted stream
        encryptedIn = PGPUtil.getDecoderStream(encryptedIn);
        JcaPGPObjectFactory pgpObjectFactory = new JcaPGPObjectFactory(encryptedIn);

        Object obj = pgpObjectFactory.nextObject();
        //The first object might be a marker packet
        PGPEncryptedDataList pgpEncryptedDataList = (obj instanceof PGPEncryptedDataList)
                ? (PGPEncryptedDataList) obj : (PGPEncryptedDataList) pgpObjectFactory.nextObject();

        PGPPrivateKey pgpPrivateKey = null;
        PGPPublicKeyEncryptedData publicKeyEncryptedData = null;

        Iterator<PGPEncryptedData> encryptedDataItr = pgpEncryptedDataList.getEncryptedDataObjects();
        while (pgpPrivateKey == null && encryptedDataItr.hasNext()) {
            publicKeyEncryptedData = (PGPPublicKeyEncryptedData) encryptedDataItr.next();
            pgpPrivateKey = findSecretKey(publicKeyEncryptedData.getKeyID());
        }

        if (Objects.isNull(publicKeyEncryptedData)) {
            throw new PGPException("Could not generate PGPPublicKeyEncryptedData object");
        }

        if (pgpPrivateKey == null) {
            throw new PGPException("Could Not Extract private key");
        }
        CommonUtils.decrypt(clearOut, pgpPrivateKey, publicKeyEncryptedData);
    }

    /**
     * Decrypts the byte array containing OpenPGP encrypted data and returns the decrypted data as a byte array.
     *
     * @param encryptedBytes The byte array containing the encrypted data.
     * @return The decrypted data as a byte array.
     * @throws PGPException If an error occurs in PGP processing.
     * @throws IOException  If an I/O error occurs.
     */
    public byte[] decrypt(byte[] encryptedBytes) throws PGPException, IOException {
        ByteArrayInputStream encryptedIn = new ByteArrayInputStream(encryptedBytes);
        ByteArrayOutputStream clearOut = new ByteArrayOutputStream();
        decrypt(encryptedIn, clearOut);
        return clearOut.toByteArray();
    }
}
