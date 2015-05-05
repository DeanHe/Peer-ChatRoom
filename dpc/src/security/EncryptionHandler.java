/**
 * EncryptionHandler.java
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * @date May 13, 2013
 * 
 * The DPC protocol features encryption of peer to peer messages, specifically 
 * the text with in the SendMessage message. This class handles the serialization 
 * of the users public key, as well as the actual hashing of the data.
 */


package security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.Cipher;
import java.security.PublicKey;
import java.security.PrivateKey;
import javax.crypto.SealedObject;

/**
 * This class deals with encryption and decryption of message payload
 * @author dd599
 */
public class EncryptionHandler {
    KeyPairGenerator keyPairGen;
    KeyPair keyPair;
    Cipher cipher;
    
    public EncryptionHandler() {
	try{
	    keyPairGen = KeyPairGenerator.getInstance("RSA"); // Initiate the RSA key generator
	    keyPairGen.initialize(2048); // set the key to 2048 
	    keyPair = keyPairGen.genKeyPair(); // generate a key pair
	    cipher = Cipher.getInstance("RSA"); // get an instance of the Cipher
	    
	    //System.out.println("About to encrypt: TEST ");
	    //SealedObject obj = encrypt("TEST");
	    //System.out.println("Encrypted="+obj.toString());
	    //String sMessage = decrypt(obj);
	    //System.out.println("Decrypted = "+sMessage);
	}
	catch(Exception ee) {
	    ee.printStackTrace();
	}
    }
    
    /**
     * get my private key
     * @return 
     */
    public PrivateKey getPrivateKey(){
	return keyPair.getPrivate();
    }
    
    /**
     * return my public key
     * @return 
     */
    public PublicKey getPublicKey(){
	return keyPair.getPublic();
    }
    
    /**
     * Encrypt the message with my public key
     * @param sToEncrypt
     * @return 
     */
    public SealedObject encrypt (String sToEncrypt) {
	try{
	    cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
	    SealedObject sealedString = new SealedObject(sToEncrypt, cipher);
	    return sealedString;
	}
	catch(Exception ee) {
	    ee.printStackTrace();
	    return null;
	}
    }
    
    /**
     * Decrypt a message with the my own private key
     * @param sealedText
     * @return 
     */
    public String decrypt (SealedObject sealedText) {
	try{
	    cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
	    String message = (String) sealedText.getObject(cipher);
	    return message;
	}
	catch(Exception ee) {
	    ee.printStackTrace();
	    return null;
	}
    }
    
    /**
     * Handle encryption of a message to a peer using the peer's public key that they have provided in the Origin object
     * @param sToEncrypt
     * @param pubKey
     * @return 
     */
    public SealedObject encryptPeerMessage(String sToEncrypt, PublicKey pubKey) {
	try{
	    Cipher c = Cipher.getInstance("RSA");
	    c.init(Cipher.ENCRYPT_MODE, pubKey);
	    SealedObject sealedString = new SealedObject(sToEncrypt, c);
	    return sealedString;
	    
	}
	catch(Exception ee) {
	    ee.printStackTrace();
	    return null;
	}
    }
    
    public String converyKeyToString(PublicKey key) {
	try{
	    byte[] keyBytes = key.getEncoded();
	    sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
	    String pubKeyString = encoder.encode(keyBytes);
	    return pubKeyString;
	}
	catch(Exception ee) {
	    ee.printStackTrace();
	    return null;
	}
    }
    
    public PublicKey reconstituteKeyFromString(String sPubKeyString) {
	try{
	    sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
	    byte[] sigBytes2 = decoder.decodeBuffer(sPubKeyString);
	    java.security.spec.X509EncodedKeySpec x509KeySpec = new java.security.spec.X509EncodedKeySpec(sigBytes2);
	    java.security.KeyFactory keyFact = java.security.KeyFactory.getInstance("RSA");
	    PublicKey pubKey = keyFact.generatePublic(x509KeySpec);
	    return pubKey;
	}
	catch(Exception ee) {
	    ee.printStackTrace();
	    return null;
	}
    }
}
  