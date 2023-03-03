package src;
import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import java.security.KeyStore;
import java.security.cert.*;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */

public class client {
  public static void main(String[] args) throws Exception {
    String host = null;
    int port = -1;
    for (int i = 0; i < args.length; i++) {
      System.out.println("args[" + i + "] = " + args[i]); //PORTS
    }
    if (args.length < 2) {
      System.out.println("USAGE: java client host port");
      System.exit(-1);
    }
    try { /* get input parameters */
      host = args[0];
      port = Integer.parseInt(args[1]);
    } catch (IllegalArgumentException e) {
      System.out.println("USAGE: java client host port");
      System.exit(-1);
    }



    try {
      SSLSocketFactory factory = null;

      boolean notLoggedIn = true;
      String password, username = null;
      FileInputStream certificate = null;
      char[] passwordChars = null;
      
      while (notLoggedIn) {
        try {
          
          Scanner scan = new Scanner(System.in);
          System.out.print("Name: ");
          username = scan.nextLine();
          System.out.print("Password: ");
          password = scan.nextLine();
          certificate = new FileInputStream("src/certificates/" + username + "keystore");
          
          notLoggedIn = false;
          passwordChars = password.toCharArray();
          scan.close();
  
        } catch (FileNotFoundException e) {
          System.out.println("Faulty username or password.");
        }
      }
      
      try {
        char[] pwTrustStore = "password".toCharArray();
       // char[] password
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        // keystore password (storepass)

        ks.load(certificate, passwordChars);  
        // truststore password (storepass);
        ts.load(new FileInputStream("src/certificates/clienttruststore"), pwTrustStore); 
        kmf.init(ks, passwordChars); // user password (keypass)
        tmf.init(ts); // keystore can be used as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        factory = ctx.getSocketFactory();
      } catch (Exception e) {
        System.out.println(" GG Faulty username or password.");
        throw new IOException(e.getMessage());
      }
      SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
      System.out.println("\nsocket before handshake:\n" + socket + "\n");

      /*
       * send http request
       *
       * See SSLSocketClient.java for more information about why
       * there is a forced handshake here when using PrintWriters.
       */

      socket.startHandshake();
      SSLSession session = socket.getSession();
      Certificate[] cert = session.getPeerCertificates();
      String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
      System.out.println("certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");

      System.out.println("socket after handshake:\n" + socket + "\n");
      System.out.println("secure connection established\n\n");

      BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String msg;
      while (true) {
          System.out.print(">");


          msg = read.readLine();
          if (msg.equalsIgnoreCase("quit")) {
              break;
          }
          System.out.print("sending '" + msg + "' to server...");
          // Send the message to the server
          out.println(msg);
          System.out.println("done");
          // Read the response from the server
          String response = in.readLine();
          System.out.println("received '" + response + "' from server\n");

          
          //System.out.println("READ NOT READY");
      }
      in.close();
      out.close();
      read.close();
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
