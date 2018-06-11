import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import java.io.File;
import java.security.UnrecoverableKeyException;
import java.util.Collections;
import java.util.Scanner;
import sun.misc.BASE64Encoder;


public class KeystoreInspect {
  public static void main(String[] argv) throws Exception {
    //inputs
    String targetKeystorePath = argv[0];
    String targetKeystorePassword = argv[1];
    String alias = argv[2];

    //vars
    BASE64Encoder encoder = new BASE64Encoder();
    File targetKeystoreFile = new File(targetKeystorePath);
    FileInputStream is = new FileInputStream(targetKeystorePath);
    Key k = null;
    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

    System.out.println("opening " + targetKeystoreFile.getName() + "...");
    keystore.load(is, targetKeystorePassword.toCharArray());
    System.out.println("this keystore contains " + keystore.size() + (keystore.size() > 1 ? " entries, they are listed below:" : " entry:"));
    for (String key : Collections.list(keystore.aliases())){
      System.out.println(key);
    }
    System.out.println("");

    /*
    KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(argv[1].toCharArray());
    KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)keystore.getEntry(argv[2], protParam);
    PrivateKey myPrivateKey = pkEntry.getPrivateKey();
    System.out.println(myPrivateKey);
    */
    System.out.println("attempting to recover key for \"" + alias + "\"");
    if( keystore.containsAlias(alias) ){
      try {
        System.out.println("using keystore password...");
        k = keystore.getKey(alias, targetKeystorePassword.toCharArray());
      } catch(UnrecoverableKeyException uke) {
        System.out.println("keystore password did not work, input another password to try:");
        Scanner sc = new Scanner(System.in);
        k = keystore.getKey(alias, sc.nextLine().toCharArray());
      }

      System.out.println("key format is: " + k.getFormat());
      /*
      Certificate cert = keystore.getCertificate(alias);
      PublicKey publicKey = cert.getPublicKey();
      KeyPair kp = new KeyPair(publicKey, (PrivateKey) k);
      */
      System.out.println("---BEGIN PRIVATE KEY---");
      System.out.println( encoder.encode(k.getEncoded()) );
      System.out.println("---END PRIVATE KEY---\n");
    }

  }
}
