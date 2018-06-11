import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.StringBuffer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import sun.misc.BASE64Encoder;


public class WebRequest {
  public static void main(String[] argv) throws Exception {
    String resource = argv[0];
    String user = "u";
    String pass = "p";
    String creds = user + ":" + pass;

    BASE64Encoder encoder = new BASE64Encoder();
    String encodedCreds = encoder.encode(creds.getBytes());
    int requestTimeout = 5000;
    URL url = new URL(resource);
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

    connection.setRequestMethod("GET");
    connection.setRequestProperty  ("Authorization", "Basic " + encodedCreds);
    connection.setConnectTimeout(requestTimeout);
    connection.setReadTimeout(requestTimeout);

    connection.setUseCaches(false);
    connection.setDoOutput(true);

    SSLSocketFactory sslSocketFactory = getFactorySimple();
    connection.setSSLSocketFactory(sslSocketFactory);

    Certificate[] serverCertificate = connection.getServerCertificates();
    for(Certificate certificate : serverCertificate){
      System.out.println("Certificate Type: " + certificate.getType());
      if (certificate instanceof X509Certificate) {
        X509Certificate x509cert = (X509Certificate) certificate;
        Principal principal = x509cert.getSubjectDN();
        System.out.println("Certificate Subject DN {}" + principal.getName());
        principal = x509cert.getIssuerDN();
        System.out.println("Certificate IssuerDn {}" + principal.getName());
      }
    }

    System.out.println("the connection responded with " + connection.getResponseCode());
    System.out.println("response: " + connection.getContentType());
    System.out.println("length: " + connection.getContentLength());
    System.out.println(connection.getResponseMessage());
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String inputLine;
    StringBuffer content = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    System.out.println(content);

    connection.disconnect();
  }

  private static SSLSocketFactory getFactorySimple() throws Exception {
    SSLContext context = SSLContext.getInstance("TLS");
    context.init(null, null, null);
    return context.getSocketFactory();
  }
}
