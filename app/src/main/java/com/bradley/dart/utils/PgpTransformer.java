package com.bradley.dart.utils;

import com.bradley.dart.R;
import com.bradley.dart.utils.pgp.PgpDecryptionUtil;
import com.bradley.dart.utils.pgp.PgpEncryptionUtil;

import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class PgpTransformer {

    private final PgpEncryptionUtil pgpEncryptionUtil;
    private PgpDecryptionUtil pgpDecryptionUtil;
    public PgpTransformer() {
        this.pgpEncryptionUtil = new PgpEncryptionUtil(
                CompressionAlgorithmTags.BZIP2,
                SymmetricKeyAlgorithmTags.AES_128,
                true,
                true
        );

        InputStream privateKeyStream = FileIO.instance.getPrivateKey(R.raw.privatekey);
        Scanner scanner = new Scanner(privateKeyStream).useDelimiter("\\A");
        String pk = scanner.hasNext() ? scanner.next() : "";

        try {
            this.pgpDecryptionUtil = new PgpDecryptionUtil(pk,"dummy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CURRENTLY USED FOR DEBUGGING ONLY - Trenton
    public String encryptStr2Str(final String content) throws IOException, PGPException {
        InputStream inStream = new ByteArrayInputStream(content.getBytes());
        OutputStream outStream = new ByteArrayOutputStream();
        this.pgpEncryptionUtil.encrypt(outStream, inStream, inStream.available(), FileIO.instance.getPublicKey());
        return outStream.toString();
    }
}
