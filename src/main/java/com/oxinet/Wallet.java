package com.oxinet;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    
    public Wallet() {
        generateKeyPair();
        System.out.println("Public key: "+ getPublicKey() + "\nPrivate Key: " + getPrivateKey());
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            keyGen.initialize(ecSpec);
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
    private PrivateKey getPrivateKey() {
        return privateKey;
    }

    public byte[] sign(byte[] data) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(privateKey);
            return signature.sign();
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}