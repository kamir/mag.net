package bitocean.etosha.magnet.datamodel.contextmap;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import bitocean.etosha.magnet.TakeNote;
import bitocean.etosha.magnet.helper.LatLonConvert;

/**
 * Created by kamir on 16.01.15.
 */
public class POI {

    public String[] fields;

    LatLonConvert lat = null;
    LatLonConvert lon = null;

    String markerText;

    // a is not used right now ...
    //
    //   a=1 facts from wiki
    //   a=2 Personen
    //
    //   the wiki has coordinates in a different format. (3 fields vs. 1 double value)
    //
    public POI( String line , int a ) {

        init( line );

    }

    public POI( String[] line , int a ) {

        init2(line);

    }


    private void init2(String[] fields) {

        int i = 0;
        for( String f : fields) {
            Log.i("INIT => FIELDS ***(" + i + ")*** : ", f );
            i++;
        }

        /**
         *
         * Here we do not have lat lon available!
         *
         * Lets use the address to get a Location.
         *
         * 5 : Str. Hsnr
         * 6 : PLZ
         * 7 : Ort
         * 8 : Land
         */

        String locationText = fields[5] + ", " + fields[6] + ", " + fields[7] + ", " + fields[8];

        Log.i("INIT with LOCTATION STRING : ", "{"+locationText+"}" );


        Geocoder geocoder = new Geocoder( TakeNote.appContext, Locale.getDefault() );

        try {

            List<Address> loc = geocoder.getFromLocationName(locationText, 1);

            if ( loc.size() > 0 ) {
                Address a = loc.get(0);
                Log.i("Found: ", "{" + a.getLatitude() + ", " + a.getLongitude() +"}");

                location[0] = a.getLatitude();
                location[1] = a.getLongitude();

                labelSMW = "{" + a.getLatitude() + ", " + a.getLongitude() +"}";

            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }




    }

    public POI(Location location) {

        String s = LatLonConvert.getPOIString( location, "NAME", "DATE" );

        Log.i("LOCATOR.SMW", s);

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

    public String getMarkerText() {
        return markerText;
    }

    public void setMarkerText(String markerText) {
        this.markerText = markerText;
    }
}
