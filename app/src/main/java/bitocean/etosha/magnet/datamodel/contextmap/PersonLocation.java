package bitocean.etosha.magnet.datamodel.contextmap;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import bitoceanug.etoshamagnet.R;

/**
 * Created by kamir on 02.01.16.
 */
public class PersonLocation extends POI {


    public PersonLocation(String line, int a) {
        super(line, a);
    }

    public String addressText = "???";

    public PersonLocation(Location location) {
        super(location);
    }

    public PersonLocation(String[] fields, int a) {

        super(fields,a);

        // 0	        1	        2	    3	    4	    5
        // Zeitstempel	Anrede	    Name	Vorname	Titel	Strasse und Hausnummer
        //
        // 6	7	8	    9	                    10
        // PLZ	Ort	Land	Telefonnummer (privat)	Telefonnummer (mobil)
        //
        // 11	                    12
        // Emailadresse (privat)	Optional: Adresse in landesüblichem Format
        //
        // 13	            14	                15	16
        // Status im Corps	Funktion im Corps	lat	lon

        markerText = fields[0] + "\n" + fields[13] + " " + fields[2] + " " + fields[3] + "\n"
                   + fields[14] + "\n" +fields[10]
                   + "\n" +fields[11];

    }


    /**
     * Special POIs can overwrite this marker creator ...
     * @return
     */

    public MarkerOptions getMarker() {

        MarkerOptions m;

        BitmapDescriptor bmd = BitmapDescriptorFactory.fromResource(R.drawable.home);

        if ( fields[13].equals("AH") )
            bmd = BitmapDescriptorFactory.fromResource(R.drawable.homeblack);

        if ( fields[13].equals("CB") )
            bmd = BitmapDescriptorFactory.fromResource(R.drawable.homeblue);

        if ( fields[13].equals("iaCB") )
            bmd = BitmapDescriptorFactory.fromResource(R.drawable.homeblue);

        if ( fields[13].equals("F") )
            bmd = BitmapDescriptorFactory.fromResource(R.drawable.homegreen);

        m = new MarkerOptions()
                .position( new LatLng( location[0], location[1] ) )
                .title( markerText )
                .icon( bmd );

        return m;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }
}
