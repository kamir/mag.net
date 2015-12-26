package bitocean.etosha.magnet.datamodel;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import bitocean.etosha.magnet.helper.LatLonConvert;

/**
 * Created by kamir on 16.01.15.
 */
public class POI {

    public POI() {    }

    public POI( String line ) {

        String[] fields = line.split(",");

        label = fields[0] + "," + fields[1];

        String[] sLat = fields[2].split(" ");
        String[] sLon = fields[3].split(" ");

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


        location[0] = lat.getDecimal();
        location[1] = lon.getDecimal();

    }

    public double location[] = new double[2];
    public String label = null;

    public MarkerOptions getMarker() {
        MarkerOptions m = new MarkerOptions().position(
                new LatLng( location[0], location[1])
        ).title(label);
        return m;
    }
}
