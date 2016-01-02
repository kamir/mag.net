package bitocean.etosha.magnet.datamodel;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import bitoceanug.etoshamagnet.R;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource;

/**
 * Created by kamir on 02.01.16.
 */
public class DefaultLocation  extends POI {

    public DefaultLocation(String line, int a) {
        super(line, a);
    }

    public DefaultLocation(Location location) {
        super(location);
    }


    /**
     * Special POIs can overwrite this marker creator ...
     * @return
     */

    public MarkerOptions getMarker() {

        MarkerOptions m;

        BitmapDescriptor bmd = BitmapDescriptorFactory.fromResource(R.drawable.home);

        m = new MarkerOptions()
                .position( new LatLng( location[0], location[1] ) )
                .title( "Home" )
                .icon( bmd );

        Log.i("Default POI:", "use home as marker");

        return m;
    }
}
