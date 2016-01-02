package bitocean.etosha.magnet.datamodel;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import bitocean.etosha.magnet.helper.LatLonConvert;

/**
 * Created by kamir on 16.01.15.
 */
public class POI {

    LatLonConvert lat = null;
    LatLonConvert lon = null;

    public POI( String line , int a ) {

        init( line );

    }


    public POI(Location location) {

        String s = LatLonConvert.getPOIString( location, "NAME", "DATE" );

        Log.i("LOCATOR.SMW", s );

        init( s );
    }


    public void init( String line ) {

        String[] fields = line.split(",");

        label = fields[0] + "," + fields[1];

        Log.i("INIT with LINE : ", "("+line+")" );

        int i = 0;
        for( String f : fields) {
            Log.i("INIT => FIELDS ***(" + i + ")*** : ", f );
            i++;
        }

        String[] sLat = fields[2].split(" ");
        String[] sLon = fields[3].split(" ");

        int j = 0;
        for( String f : sLat) {
            Log.i("INIT -> FIELDS sLat [" + j + "] : ", f );
            j++;
        }

        int k = 0;
        for( String f : sLon) {
            Log.i("INIT -> FIELDS sLon [" + k + "] : ", f );
            k++;
        }


        double fa1 = Double.parseDouble( sLat[0].substring(0,2) );
        double fa2 = Double.parseDouble( sLat[1].substring(0,2) );
        double fa3 = Double.parseDouble( sLat[2].substring(0,5) );

        double fb1 = Double.parseDouble( sLon[0].substring(0,2) );
        double fb2 = Double.parseDouble( sLon[1].substring(0,2) );
        double fb3 = Double.parseDouble( sLon[2].substring(0,5) );


        LatLonConvert lat = new LatLonConvert( fa1, fa2, fa3 );
        LatLonConvert lon = new LatLonConvert( fb1, fb2, fb3 );

        Log.i("POI.lat", "We are here: " + fa1 + " " + fa2 + " " + fa3 );
        Log.i("POI.lon", "We are here: " + fb1 + " " + fb2 + " " + fb3 );

        labelSMW = lat.getSMWLabel() + " " + lon.getSMWLabel();

        location[0] = lat.getDecimal();
        location[1] = lon.getDecimal();

    }

    public String getLabel() {
        return label;
    }

    /**
     *  A measured location is a pair of double values.
     *  The LatLonConvert is used to transform it into a
     *  readable format.
     */
    public double location[] = new double[2];

    private String label = null;
    private String labelSMW = null;

    public void setLabel(String label) {
        this.label = label;
    }


    /**
     * Special POIs can overwrite this marker creator ...
     * @return
     */

    public MarkerOptions getMarker() {
        MarkerOptions m = new MarkerOptions().position(
                new LatLng( location[0], location[1])
        ).title(label);
        return m;
    }

}
