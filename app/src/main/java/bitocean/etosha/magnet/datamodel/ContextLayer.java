package bitocean.etosha.magnet.datamodel;

import android.util.Log;

import java.util.Vector;

import bitocean.etosha.magnet.helper.SmwAskUrlFactory;

/**
 * Created by kamir on 16.01.15.
 */
public class ContextLayer {

    static ContextModel cm = null;

    public static void init( ContextModel _cm ) {
        cm = _cm;
    }

    public static String testUrl = "http://semanpix.de/oldtimer/wiki/index.php?title=Special:Ask/-5B-5BCategory:-20GeoImage-5D-5D-20-5B-5BNotes::-20animals-7C-7Ccars-7C-7Csunlight-5D-5D/-3FDate/-3FLatitude/-3FLongitude/-3FAltitude%3DAltitude(m)/-3FNotes/format%3D-20csv/searchlabel%3D-20Dowload-20CSV-2DFile/offset%3D0";

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
        Vector<String> lines = _loadCSVFromURL( url );

        // convert all lines
        for( String line : lines ) {
            pois.add( new POI( line ) );
        }

        return pois;
    }

    /**
     *
     * @param url
     * @return
     */
    private Vector<String> _loadCSVFromURL(String url) {

        Log.i("###  Context-Layer  ###", "request-call:: " + url );

        String line2 = "Peter001,15 January 2015 11:48:00,52° 17' 43.90 N,12° 51' 33.45 E,102,nature;animals";
        String line3 = "Peter002,27 August 2014,52° 17' 43.92 N,12° 51' 33.97 E,101.5,nature;animals";
        String line4 = "Peter006,27 August 2014,52° 17' 41.30 N,12° 51' 33.48 E,103,nature;cars";
        String line5 = "Peter012,18 September 2014,52° 12' 59.32 N,13° 28' 54.51 E,157.5,nature;forest,sunlight";
        String line6 = "Peter014,18 September 2014,52° 12' 58.67 N,13° 29' 05.99 E,130.5,nature;forest,sunlight";
        String line7 = "Peter020,18 September 2014,52° 12' 56.93 N,13° 29' 12.42 E,169.5,nature;forest,sunlight";
        String line8 = "Peter021,18 September 2014,52° 12' 52.74 N,13° 29' 15.65 E,141.5,nature;forest,sunlight,buildings";
        String line9 = "Peter022,18 September 2014,52° 12' 52.65 N,13° 29' 15.93 E,140.5,nature;forest;sunlight";

        Log.i("###  Context-Layer  ###", "!!! YOU ARE IN DEMO MODE !!! " );

        Vector<String> lines = new Vector<String>();
        lines.add( line2 );
        lines.add( line3 );
        lines.add( line4 );
        lines.add( line5 );
        lines.add( line6 );
        lines.add( line7 );
        lines.add( line8 );
        lines.add( line9 );

        return lines;

    }


    /**
     * Factory Method for Context Layer creation.
     *
     * Here we use the default URL for simple tests.
     *
     * @return
     */
    public static ContextLayer getHighLevelLayerFilterByProject( String project ) {

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
    public static ContextLayer getTestLayer1() {
        ContextLayer cl = new ContextLayer();
        cl.url = testUrl;
        return cl;
    }
}
