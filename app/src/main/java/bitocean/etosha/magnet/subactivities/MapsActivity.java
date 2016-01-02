package bitocean.etosha.magnet.subactivities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bitocean.etosha.magnet.datamodel.contextmap.ContextLayer;
import bitocean.etosha.magnet.datamodel.ContextModel;
import bitocean.etosha.magnet.helper.ContextLayerLoader;
import bitocean.etosha.magnet.datamodel.contextmap.POI;
import bitocean.etosha.magnet.TakeNote;
import bitoceanug.etoshamagnet.R;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();


            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
            else {
                Toast.makeText( this, "MAP SERVICE not available!\n",
                        Toast.LENGTH_LONG).show();

            }
        }
    }

    private void setUpMap() {

        // where are we currently ???
        POI here = TakeNote.getContextualLocation();

        Toast.makeText( this, ":: MAP SERVICE ::\n" + here.getLabel(),
                Toast.LENGTH_LONG).show();


        Log.i("###  POI-DOWNLOADER  ###", "setUpMap() :: " + here.getLabel() );

        // we clear the map...
        mMap.clear();

        // The GoogleMaps object will be updated
        mMap.addMarker(here.getMarker());

        // the contextualized layer is now blended into the map
        ContextLayer l1 = ContextLayer.getLayer_POIs_for_Project( ContextModel.getCurrentProject() );

        ContextLayerLoader.loadDataToMap( mMap, l1 );

        Toast.makeText( this, "Selected POIs: \n" +
                l1.itemsNoCoordinates + " without coordinates\n" + l1.itemsWithCoordinates + " POIs.",
                Toast.LENGTH_LONG).show();

    }

    /**
     *  The Task fills up the list with the projects ...
     */
    public void updatePOIs() {

        //POIDownloadTask dt = new POIDownloadTask();

        //dt.setListe(listProjects);

        //dt.doInBackground(uri);

        //listProjects = dt.getListe();

    }

    /**
     * Lade eine CSV Datei herunter und befülle damit eine Liste ...
     *
     * Diese Liste wird dann mit der Methode
     *
     *   getListe()
     *
     * abgerufen.
     */
    private static class POIDownloadTask extends AsyncTask<String, Void, Void> {

        List<String> liste = null;
        public void setListe(List<String> liste) {
            this.liste = liste;
        }
        public List<String> getListe() {
            return liste;
        }

        protected Void doInBackground( String... uri ) {

            DefaultHttpClient httpclient = new DefaultHttpClient();

            liste = new ArrayList<String>();

            try {

                HttpGet httpget = new HttpGet(uri[0]); // server

                Log.i("###  POI-DOWNLOADER  ###", "request-call::" + httpget.getRequestLine());

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

                    String s = "(";

                    String[] listElements = x.toString().split("\n");
                    for( String item : listElements ) {
                        String shorts = item.trim();
                        int i = shorts.length();
                        if ( i > 2 ) {
                            String text = shorts.substring( 1 , i - 1 );
                            liste.add(text);
                            s = s.concat(text + ", ");
                        }
                    }

                    s = s.concat( ")" );

                    Log.i("###*** ContextModel ***###", "response-list-data::" + x.toString() );


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

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }



        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

            CharSequence seq = "A list of new POIs was download ... (" + liste.size() + ") items.";

            Toast.makeText(TakeNote.appContext, seq, Toast.LENGTH_LONG).show();


        }




    }
}
