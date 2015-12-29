
package org.etosha;

/*
 * 
 */
 
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * A simple example that uses HttpClient to execute an HTTP request against
 * a target site that requires user authentication.
 */
public class ClientAuthentication {

    public static void main(String[] args) throws Exception {
        
        
        String urlS = "http://semanpix.de/opendata/wiki";

        String realm = "To request a user account get in touch with Mirko. You should know how to do this.";
        
        
        HttpHost target = new HttpHost("semanpix.de", 80, "http");
        
 
        
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        
        credsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials("borussia", "1836"));
        
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        
        try {
            
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local
            // auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);
            
            HttpGet httpget = new HttpGet( urlS );

            System.out.println("*** Executing request " + httpget.getRequestLine());
            
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                
                for( Header s : response.getAllHeaders() ) {
                    System.out.println( s.toString()  );
                        
                }
                
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println("----------------------------------------");
                

                BufferedReader rd = new BufferedReader(new InputStreamReader( response.getEntity().getContent()  ));
            
            String result = "";
            String line;
            
            while ((line = rd.readLine()) != null) {
                result = result.concat(line + "\n");
            }
            rd.close();
            
            System.out.println(result);

                        
                        
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}