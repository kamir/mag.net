package org.etosha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;

/**
 *
 * @author kamir
 */
public class SWCApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            //        String urlS = "http://www.semanpix.de/mylife/wiki";
            String urlS = "http://semanpix.de/opendata/wiki";
            
//        HttpGet getRequest = new HttpGet(urlS);
//        getRequest.addHeader("Authorization", "Basic " + getBasicAuthenticationEncoding());
//        
//        System.out.println(getRequest.getRequestLine());
            
            java.net.CookieManager cm = new java.net.CookieManager();
            cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            java.net.CookieHandler.setDefault(cm);
            
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });
            
            URL url = new URL(urlS);
            URLConnection connection = url.openConnection();
            
            connection.setRequestProperty("Authorization", "Basic " + getBasicAuthenticationEncoding());
            
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String result = "";
            String line;
            
            while ((line = rd.readLine()) != null) {
                result = result.concat(line);
            }
            rd.close();
            
            System.out.println(result);
        } 
        catch (Exception ex) {
            Logger.getLogger(SWCApp.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }

    static String username = "borussia";
    static String password = "1836";

    private static String getBasicAuthenticationEncoding() {

        String userPassword = username + ":" + password;
        String enc = new String(Base64.encodeBase64(userPassword.getBytes()));

        System.out.println(enc);

        return enc;
    }

}
