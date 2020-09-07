package com.oz.playground;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class SignatureUtils {

    /** Algorithm to be used for signature validation */
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

    public SignatureUtils() {}

    /**
     * Performs actual signature validation with prepared input
     * */
    private boolean isDataSignatureValid(@NonNull byte[] data, @NonNull byte[] signature,
                                         @NonNull PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }

    /**
     * Extracts and wraps public key from pem format string
     * */
    private PublicKey extractPublicKeyFromPem(@NonNull String pem)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyOpeningText = "-----BEGIN PUBLIC KEY-----\n";
        String publicKeyClosingText = "-----END PUBLIC KEY-----";
        String base64PublicKey = pem.substring(
                pem.indexOf(publicKeyOpeningText) + publicKeyOpeningText.length(),
                pem.indexOf(publicKeyClosingText)
        );
        byte[] publicKey = Base64.decode(base64PublicKey, Base64.NO_WRAP);
        return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(publicKey));
    }

    /**
     * Validates signature of provided data.
     *
     * @param dataString utf-8 string to be validated
     * @param signatureString utf-8 signature representation used for validation
     * @param pemString pem format certificate string used for validation
     * @return true if dataString passes validation, false otherwise
     */
    public boolean isDataSignatureValid(String dataString, String signatureString, String pemString)
            throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        byte[] hexData = dataString.trim().getBytes(StandardCharsets.UTF_8);
        byte[] hexSignature = Base64.decode(signatureString, Base64.DEFAULT);
        PublicKey publicKey = extractPublicKeyFromPem(pemString);

        return isDataSignatureValid(hexData, hexSignature, publicKey);
    }

}
