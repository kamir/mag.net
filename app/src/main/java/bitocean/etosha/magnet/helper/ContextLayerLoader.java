package bitocean.etosha.magnet.helper;

import com.google.android.gms.maps.GoogleMap;

import java.util.Vector;

import bitocean.etosha.magnet.datamodel.ContextLayer;
import bitocean.etosha.magnet.datamodel.POI;

/**
 * Created by kamir on 16.01.15.
 */
public class ContextLayerLoader {

    public static void loadDataToMap( GoogleMap mMap, ContextLayer layer ) {

        Vector<POI> pois = layer.getPOIs();

        for( POI p : pois ) {

            mMap.addMarker( p.getMarker() );

        }

    }
}
