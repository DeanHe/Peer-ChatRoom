package DPCCore.messages;

/**
 * ChangekeyResponse.java
 * @date June 8, 2013
 * @team_members Andrew Mulroney, Dimitar Dimitrov, Georgi Simeonov, Tengda He
 * A response to the ChangeKeyRequest which contains a newly generated symmetric key encrypted by the sent public key.
 * Display the Client feature.
 */
import java.security.*;

public class ChangeKeyResponse extends DPCGenericObject {
    private PublicKey publicKey;
    public ChangeKeyResponse(PublicKey pubKey) {
	publicKey = pubKey;
    }
    
    public Key getPublicKey() {
	return publicKey;
    }
    
    public void setPublicKey(PublicKey key) {
	publicKey = key;
    }
}
