package src;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;


public class server implements Runnable {
  private ServerSocket serverSocket = null;
  private static int numConnectedClients = 0;
  private cmdHandler cmdHandler;
  
  public server(ServerSocket ss) throws IOException {
    serverSocket = ss;
    newListener();
  }

  public void run() {
    try {
      SSLSocket socket=(SSLSocket)serverSocket.accept();
      newListener();
      SSLSession session = socket.getSession();
      Certificate[] cert = session.getPeerCertificates();
      String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
      // String issuer = cert.getSubjectX500Principal().getI
      // BigInteger serial = ((X509Certificate) cert[0]).getSubjectX500Principal().getSerialNumber();
      String issuer = ((X509Certificate)cert[0]).getIssuerX500Principal().getName();
      String serial = ((X509Certificate)cert[0]).getSerialNumber().toString();
      String alias = "";

      numConnectedClients++;
      System.out.println("client connected");
      System.out.println("client name (cert subject DN field): " + subject);
      System.out.println("Issuer: " + issuer);
      System.out.println("Serial number: " + serial);

      System.out.println(numConnectedClients + " concurrent connection(s)\n");

     

      PrintWriter out = null;
      BufferedReader in = null;
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      
      Setup.run();
      List<User> users = Setup.users;
      //VERIFY USER PASSWORD AROUND HERE. DONT FORGET TO MAKE WRITING TO FILE POSSIBLE LATER, WHEN CREATING AND ALTERING RECORD.
      RecordSystem recordSystem = Setup.recordSystem;
      User user = users.get(0);
      System.out.println(user);
      cmdHandler cmdHandler = new cmdHandler(user, recordSystem, users); //FIND USER THAT LOGGED IN FROM LIST OF USERS AND CREATE THIS CMDHANDLER
      // TODO
      // Read USER from commandline smthn. "readInput()" and get CN="USER" from the client certificate
      // Compare USERS to each other in order to verify access. 
      // TODO
      // Read password from the user, compare it to the passwordfile. 
      String clientMsg = null;
      while ((clientMsg = in.readLine().trim()) != "quit") {
        out.println(cmdHandler.handle(clientMsg));
        out.flush();
        //String rev = new StringBuilder(clientMsg).reverse().toString(); ////////////////////
        //System.out.println("received '" + clientMsg + "' from client");
        //System.out.print("sending '" + rev + "' to client..."); //////////////////
        //out.println(rev);
        //System.out.println("done\n");
      }
      System.out.println("Session is over. Goodbye!");
      in.close();
      out.close();
      socket.close();
      numConnectedClients--;
      System.out.println("client disconnected");
      System.out.println(numConnectedClients + " concurrent connection(s)\n");
    } catch (IOException e) {
      System.out.println("Client died: " + e.getMessage());
      e.printStackTrace();
      return;
    }
  }
  
  private void newListener() { (new Thread(this)).start(); } // calls run()


  public static void main(String args[]) {
    System.out.println("\nServer Started\n");
    int port = -1;
    System.out.println(port);
    if (args.length >= 1) {
      port = Integer.parseInt(args[0]);
    }
    String type = "TLSv1.2";
    try {
      ServerSocketFactory ssf = getServerSocketFactory(type);
      ServerSocket ss = ssf.createServerSocket(port);
      ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
      new server(ss);
    } catch (IOException e) {
      System.out.println("Unable to start Server: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static ServerSocketFactory getServerSocketFactory(String type) {
    if (type.equals("TLSv1.2")) {
      SSLServerSocketFactory ssf = null;
      try { // set up key manager to perform server authentication
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");
        char[] password = "password".toCharArray();
        // keystore password (storepass)
        ks.load(new FileInputStream("src/certificates/serverkeystore"), password);  
        // truststore password (storepass) src/certificates/clienttruststore
        ts.load(new FileInputStream("src/certificates/servertruststore"), password); 
        kmf.init(ks, password); // certificate password (keypass)
        tmf.init(ts);  // possible to use keystore as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        ssf = ctx.getServerSocketFactory();
        return ssf;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      return ServerSocketFactory.getDefault();
    }
    return null;
  }


// given request certificate, that has been signed by the CA, the server will authenticate the client
// ie it was found in the truststore and the certificate is valid
// this function takes that as parameter and returns alias
private String getAlias(X509Certificate cert) {
    try {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream("src/certificates/servertruststore"), "password".toCharArray());
        Enumeration<String> aliases = trustStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (trustStore.isCertificateEntry(alias)) {
                Certificate trustedCert = trustStore.getCertificate(alias);
                if (cert.equals(trustedCert)) {
                    return alias;
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
  }
}

