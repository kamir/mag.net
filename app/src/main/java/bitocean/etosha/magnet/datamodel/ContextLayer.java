package bitocean.etosha.magnet.datamodel;

import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import bitocean.etosha.magnet.helper.AppStorage;
import bitocean.etosha.magnet.helper.SmwAskUrlFactory;

/**
 * Created by kamir on 16.01.15.
 */
public class ContextLayer {

    static ContextModel cm = null;

    public static void init( ContextModel _cm ) {
        cm = _cm;
    }


    public static String testUrl1 = "http://semanpix.de/oldtimer/wiki/index.php?title=Special:Ask/-5B-5BCategory:-20GeoImage-5D-5D-20-5B-5BNotes::-20animals-7C-7Ccars-7C-7Csunlight-5D-5D/-3FDate/-3FLatitude/-3FLongitude/-3FAltitude%3DAltitude(m)/-3FNotes/format%3D-20csv/searchlabel%3D-20Dowload-20CSV-2DFile/offset%3D0";
    public static String testUrl2 = "http://semanpix.de/opendata/wiki/index.php?title=Special:Ask/-5B-5BCategory:POI-5D-5D-20-5B-5BbelongsToProject::MP1-5D-5D/-3FLatitude/-3FLongitude/-3FAltitude/-3FNotes/format%3Dcsv/offset%3D0";

    public static String getQueryUrlFor_POIs_in_Project( String project ) {
        String url = "http://semanpix.de/opendata/wiki/index.php?title=Special:Ask/-5B-5BCategory:POI-5D-5D-20-5B-5BbelongsToProject::"+ project +"-5D-5D/-3FLatitude/-3FLongitude/-3FAltitude/-3FNotes/format%3Dcsv/offset%3D0";
        return url;
    }

    String url = null;
    Vector<POI> pois = new Vector<POI>();

    /**
     * We create POIs from text lines and return a Vector<POI>.
     *
     * @return
     */
    public Vector<POI> getPOIs() {

        if( pois == null)
        pois = new Vector<POI>();

        // call URI for CSV file

        Vector<String> lines = loadCSVFromURL(url);

        // Vector<String> lines = getDummyCSVFromURL(url);
        int j = 0;
        int i = 0;

        // convert all lines
        for( String line : lines ) {
            try {
                pois.add(new POI(line, 1));  // version 1 of POI query
                j++;
            }
            catch(Exception ex) {
                ex.printStackTrace();
                i++;
            }



        }

        Log.i("###  Context-Layer-Loader  ###", "items with    coordinates :: " + j );
        Log.i("###  Context-Layer-Loader  ###", "items without coordinates :: " + i);

        itemsWithCoordinates = j;
        itemsNoCoordinates = i;

        return pois;
    }

    public int itemsWithCoordinates = 0;
    public int itemsNoCoordinates = 0;

    private Vector<String> loadCSVFromURL(String url) {

        Vector<String> lines = new Vector<String>();

        Log.i("###  Context-Layer  ###", "request-call:: " + url );

        DefaultHttpClient httpclient = new DefaultHttpClient();

        try {

            HttpGet httpget = new HttpGet( url ); // query
            httpget.setHeader("Authorization", AppStorage.getAuthorizationString());

            HttpResponse response = null;
            try {
                response = httpclient.execute(httpget);

                InputStream stream = response.getEntity().getContent();

                byte[] bytes = new byte[1000];

                StringBuilder x = new StringBuilder();

                int numRead = 0;
                while ((numRead = stream.read(bytes)) >= 0) {
                    x.append(new String(bytes, 0, numRead));
                }
                Log.i("###*** Context-Layer ***###", "response-csv-data:: \n\b" + x.toString() );


                String[] rows = x.toString().split("\n");
                for( String item : rows ) {
                    Log.i("###*** Context-Layer ***###", "LINE :: " + item );

                    item = removeLeadingQuotes(item);

                    Log.i("###*** Context-Layer ***###", "CLEANED :: " + item);

                    lines.add( item );
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (response != null)
                    Log.i("***ContextModel***", "response " + response.getStatusLine().toString());
                Log.i("***ContextModel***", "response " + response.toString() );

            } finally {

            }
        } finally {

        }















        return lines;

    }

    /**
     * TODO: Does not do anything currently.
     *
     * @param item
     * @return
     */
    private String removeLeadingQuotes(String item) {
        String[] f = item.split("~");

        return item;

    }

    /**
     *
     * @param url
     * @return
     */
    private Vector<String> getDummyCSVFromURL(String url) {

        Log.i("###  Context-Layer  ###", "DUMMY-request-call:: " + url );

        String line1 = "Peter001,15 January 2015 11:48:00,52° 17' 43.90 N,12° 51' 33.45 E,102,nature;animals";
        String line2 = "Peter002,27 August 2014,52° 17' 43.92 N,12° 51' 33.97 E,101.5,nature;animals";
        String line3 = "Peter006,27 August 2014,52° 17' 41.30 N,12° 51' 33.48 E,103,nature;cars";
//        Log.i("###  Context-Layer  ###", "!!! YOU ARE IN DEMO MODE !!! " );

        Vector<String> lines = new Vector<String>();

        lines.add( line1 );
        lines.add( line2 );
        lines.add( line3 );

        return lines;

    }


    /**
     * Factory Method for Context Layer creation.
     *
     * Here we use the default URL for simple tests.
     *
     * @return
     */
    public static ContextLayer ___getHighLevelLayerFilterByProject( String project ) {

        ContextLayer cl = new ContextLayer();

        // Calculate the right URL for the request ...
        String url = SmwAskUrlFactory.getQuery(cm, "PROJECT", project);

        Log.i("###  Context-Layer  ###", "Show map for PROJECT::" + project );

        cl.url = url;

        Log.i("###  Context-Layer  ###", "Load from URL: \n" + url );

        return cl;
    }

    /**
     * Factory Method for Context Layer creation.
     *
     * Here we use the default URL for simple tests.
     *
     * @return
     */
    public static ContextLayer getLayer_POIs_for_Project( String pro ) {
        ContextLayer cl = new ContextLayer();
        cl.url = ContextLayer.getQueryUrlFor_POIs_in_Project( pro );
        return cl;
    }
}
