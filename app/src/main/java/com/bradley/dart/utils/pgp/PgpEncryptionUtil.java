/**
 * PgpEncryptionUtil provides methods for encrypting data using OpenPGP standard with Bouncy Castle library.
 * This utility class allows encryption of data streams or byte arrays using public key encryption.
 */
package com.bradley.dart.utils.pgp;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.*;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Objects;

public class PgpEncryptionUtil {

    /**
     * Static block to add Bouncy Castle provider to the JVM security providers if not already added.
     */
    static {
        if (Objects.isNull(Security.getProvider(BouncyCastleProvider.PROVIDER_NAME))) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // Default values for encryption parameters
    private int compressionAlgorithm = CompressionAlgorithmTags.ZIP;
    private int symmetricKeyAlgorithm = SymmetricKeyAlgorithmTags.AES_128;
    private boolean armor = true;
    private boolean withIntegrityCheck = true;
    private int bufferSize = 1 << 16;

    /**
     * Constructs a PgpEncryptionUtil object with custom encryption parameters.
     *
     * @param compressionAlgorithm    The compression algorithm to use.
     * @param symmetricKeyAlgorithm   The symmetric key algorithm to use.
     * @param armor                   Whether to use armor encoding.
     * @param withIntegrityCheck      Whether to include integrity check.
     */
    public PgpEncryptionUtil(int compressionAlgorithm, int symmetricKeyAlgorithm, boolean armor, boolean withIntegrityCheck) {
        this.compressionAlgorithm = compressionAlgorithm;
        this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
        this.armor = armor;
        this.withIntegrityCheck = withIntegrityCheck;
    }

    /**
     * Encrypts the input stream and writes the encrypted data to the output stream.
     *
     * @param encryptOut   The output stream to write the encrypted data.
     * @param clearIn      The input stream containing the clear data.
     * @param length       The length of the clear data.
     * @param publicKeyIn  The input stream containing the public key for encryption.
     * @throws IOException  If an I/O error occurs.
     * @throws PGPException If an error occurs in PGP processing.
     */
    public void encrypt(OutputStream encryptOut, InputStream clearIn, long length, InputStream publicKeyIn)
            throws IOException, PGPException {
        PGPCompressedDataGenerator compressedDataGenerator =
                new PGPCompressedDataGenerator(compressionAlgorithm);
        PGPEncryptedDataGenerator pgpEncryptedDataGenerator = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(symmetricKeyAlgorithm)
                        .setWithIntegrityPacket(withIntegrityCheck)
                        .setSecureRandom(new SecureRandom())
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
        );
        pgpEncryptedDataGenerator.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(
                CommonUtils.getPublicKey(publicKeyIn)));
        if (armor) {
            encryptOut = new ArmoredOutputStream(encryptOut);
        }
        OutputStream cipherOutStream = pgpEncryptedDataGenerator.open(encryptOut, new byte[bufferSize]);
        CommonUtils.copyAsLiteralData(compressedDataGenerator.open(cipherOutStream), clearIn, length, bufferSize);
        compressedDataGenerator.close();
        cipherOutStream.close();
        encryptOut.close();
    }

    /**
     * Encrypts the byte array and returns the encrypted data as a byte array.
     *
     * @param clearData    The byte array containing the clear data.
     * @param publicKeyIn  The input stream containing the public key for encryption.
     * @return The encrypted data as a byte array.
     * @throws PGPException If an error occurs in PGP processing.
     * @throws IOException  If an I/O error occurs.
     */
    public byte[] encrypt(byte[] clearData, InputStream publicKeyIn) throws PGPException, IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(clearData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encrypt(outputStream, inputStream, clearData.length, publicKeyIn);
        return outputStream.toByteArray();
    }

    /**
     * Encrypts the input stream and returns the encrypted data as an input stream.
     *
     * @param clearIn      The input stream containing the clear data.
     * @param length       The length of the clear data.
     * @param publicKeyIn  The input stream containing the public key for encryption.
     * @return The encrypted data as an input stream.
     * @throws IOException  If an I/O error occurs.
     * @throws PGPException If an error occurs in PGP processing.
     */
    public InputStream encrypt(InputStream clearIn, long length, InputStream publicKeyIn)
            throws IOException, PGPException {
        File tempFile = File.createTempFile("pgp-", "-encrypted");
        FileOutputStream fos = new FileOutputStream(tempFile.getPath());
        encrypt(fos, clearIn, length, publicKeyIn);
        FileInputStream fis = new FileInputStream(tempFile.getPath());
        return fis;
    }

    /**
     * Encrypts the byte array using the provided public key string and returns the encrypted data as a byte array.
     *
     * @param clearData     The byte array containing the clear data.
     * @param publicKeyStr  The public key string for encryption.
     * @return The encrypted data as a byte array.
     * @throws PGPException If an error occurs in PGP processing.
     * @throws IOException  If an I/O error occurs.
     */
    public byte[] encrypt(byte[] clearData, String publicKeyStr) throws PGPException, IOException {
        return encrypt(clearData, IOUtils.toInputStream(publicKeyStr, getDefaultCharSet()));
    }

    /**
     * Encrypts the input stream using the provided public key string and returns the encrypted data as an input stream.
     *
     * @param clearIn       The input stream containing the clear data.
     * @param length        The length of the clear data.
     * @param publicKeyStr  The public key string for encryption.
     * @return The encrypted data as an input stream.
     * @throws IOException  If an I/O error occurs.
     * @throws PGPException If an error occurs in PGP processing.
     */
    public InputStream encrypt(InputStream clearIn, long length, String publicKeyStr) throws IOException, PGPException {
        return encrypt(clearIn, length, IOUtils.toInputStream(publicKeyStr, getDefaultCharSet()));
    }

    /**
     * Retrieves the default character set encoding.
     *
     * @return The default character set encoding.
     */
    private static String getDefaultCharSet() {
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
        return writer.getEncoding();
    }
}
