package bitocean.etosha.magnet.datamodel;

import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import bitocean.etosha.magnet.TakeNote;
import bitocean.etosha.magnet.helper.AppStorage;
import bitocean.etosha.magnet.helper.LatLonConvert;

/**
 * Created by kamir on 03.01.15.
 *
 * The Context-Nodel has all data which represents the current
 * context of the user in the MAG.net-app.
 */
public class ContextModel implements LocationListener {

    static public final int modelVersion = 1;
    static boolean _runReal = true;  // no tests

    static AppStorage appsto = null;

    public void init() {

        appsto = new AppStorage();

        if( usePersonalEntryPoint )
            personalEntryPoint = appsto.loadEntrypointFromSettings();
        else
            personalEntryPoint = getDefaultPageName();

        ContextModel.initWikiListe();

        ContextModel.initTaskTypes();

        ContextModel.initProjects(this.getCurrentUser());

        this.setCurrentContext( personalEntryPoint );
    }



    /**
     * Variables for browsing through the semantic space ...
     */
    public Vector<String> parents = new Vector<String>();
    public Vector<String> children = new Vector<String>();
    public Vector<String> categories = new Vector<String>();

    boolean usePersonalEntryPoint = false;
    public String personalEntryPoint = null;

    /**
     * Default ENTRY POINT
     *
     * The current context is defined by a MediaWiki-Page.
     *
     * As a default context we use a page with the following name:
     *      currentUser + "_" + currentContext
     */
    public static String getCurrentUser() {
        return currentUser;
    }

    public static String currentUser = "Mirko Kämpf";
    private static String currentContext = "none";

    public static String getCurrentProject() {
        return currentProject;
    }
    private static String currentProject = "default";

    /**
     *
     * Address of default MediaWikiServer
     *
     * No PROTOCOLL
     * Full installationpath of MW Software
     *
     */
    public static String wikiserver1 = "semanpix.de/oldtimer/wiki";
    public static String wikiserver2 = "semanpix.de/opendata/wiki";
    public static String wikiserver3 = "semanpix.de/mylife/wiki";

    public static String wikiserver = wikiserver2;

    public static String getCurentPageName(){

        if ( currentNodeIsCatNode ) {
            return getCurrentProject() ;
        }

        return getCurrentUser() + "_" + getCurrentProject() + "_" + getCurrentContext();
    };

    String filenameListPROJECTS = "CML_Projects";

    public static Location location;

    public static void setCurrentUser(String currentUser) {
        ContextModel.currentUser = currentUser;
    }

    public static void  setCurrentProject(String currentProject) {
        ContextModel.currentProject = currentProject;
    }

    public static String getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(String currentContext) {
        ContextModel.currentContext = currentContext;
    }


    /**
     * Prepare the default Context-Page.
     *
     * @return
     */
    public static String getDefaultPageName(){
        return getCurrentUser() + "_" + getCurrentProject() + "_" + getCurrentContext();
    };

    Vector<String> vProjects = null;

    public void setProperty(String key, String s) {

        if ( key.equals( "user" ) ) setCurrentUser(s);
        if ( key.equals( "project" ) ) setCurrentProject(s);
        if ( key.equals( "activity" ) ) setCurrentContext(s);
        if ( key.equals( "wiki" ) ) wikiserver = s;

        TakeNote.refreshContextBar();

    }

    static List<String> listProjects = new ArrayList<String>();
    static List<String> listTaskTypes = new ArrayList<String>();
    static List<String> listWikis = new ArrayList<String>();


    static private void initWikiListe() {

        listWikis = new ArrayList<String>();

        listWikis.add( wikiserver2 );
        listWikis.add( wikiserver1 );
        listWikis.add( wikiserver3 );

    }


    public static void initProjects(String user) {

        listProjects = new ArrayList<String>();

        String[] n = user.split(" ");

        String uri = "http://" + wikiserver + "/index.php?title=Special:Ask/-5B-5BCategory:CATNODE-5D-5D-20-20-5B-5BisOwnedBy::" + n[0] + "-20" + n[1] + "-5D-5D/format%3Dcsv/offset%3D0";


    //    if (!runReal) {
            listProjects.add("Dissertation");
            listProjects.add("Hadoop Training");
            listProjects.add("Etosha");
            listProjects.add("Urlaub");
            listProjects.add("Hobby");
            listProjects.add("Alltag");
            listProjects.add("Lesen");
            listProjects.add("Filme");
            listProjects.add("Publikationen");
            listProjects.add("Konferenzen");
            listProjects.add("Reisen");
    //    }
    //    else {
            listProjects.add("Teile auswählen");
            listProjects.add("Werkstatt organisieren");
            listProjects.add("Werkstatt reinigen");
    //    }

        /**
         *  The Task fills up the list with the projects ...
         */
        DownloadTask dt = new DownloadTask();
        dt.setListe(listProjects);

        dt.doInBackground(uri);

        listProjects = dt.getListe();


    }

    public static List<String> getListProjects() {
        return listProjects;
    }

    public static void initTaskTypes() {
        listTaskTypes = new ArrayList<String>();
        listTaskTypes.add("TODO");
        listTaskTypes.add("Problem");
        listTaskTypes.add("Lösung");
        listTaskTypes.add("Frage");
        listTaskTypes.add("Antwort");
        listTaskTypes.add("Vorbereitung");
        listTaskTypes.add("Follow-Up");
        listTaskTypes.add("Erledigt");
    }

    public static List<String> getListTaskTypes() {
        return listTaskTypes;
    }

    /**
     *
    private List<String> loadList(String filenameListPROJECTS) {
        Vector<String> l = new Vector<String>();
        return l;
    }
     *
     */

    static double[] d = new double[2];

    /**
     * Here we create the wiki code / MarkDown code for a POI.
     *
     * @return
     */
    public static POI getGeoContextAnnotation() {

        POI h = null;

        if ( location == null ) {

            String here = "DEFAULT LOCATION 1,01 JANUARY 2016,52° 11' 00.00 N,11° 12' 11.11 E, 340.5,_getGeoContextAnnotation\", 1";
            h = new POI( here, 1 );

        }
        else {

            h = new POI( location );

        }

        // the location is translated into the SMW representation ...
        String hereSTRING = LatLonConvert.getSMWRepresentationOfLocationAsText(location);

        h.setLabel( hereSTRING );

        Log.i("LOCATOR.SMW (hereSTRING) => ", hereSTRING );

        return h;
    }




    public POI getGeoContext() {

        POI here = null;

        if ( location == null ) {

            here = new POI("DEFAULT LOCATION ***,01 JANUARY 2016,52° 11' 00.00 N,11° 12' 11.11 E, 340.5,DEFAULT;LOCATION", 1);

        }
        else {

            here = new POI( location );

        }


        Log.i("### WE ARE HERE : ###", here.getLabel() );

        return here;

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        d[0] = location.getLatitude();
        d[1] = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void resetContext() {
        currentNodeIsCatNode = false;
        currentCategory = "unknown";
    };

    /**
     *
     * With this flag we track if the current node is a Category-Node.
     *
     */
    static boolean currentNodeIsCatNode = false;
    static public void setCatNode(boolean b) {
        currentNodeIsCatNode = b;
    }
    static public boolean getCATNODE() {
        return currentNodeIsCatNode;
    }

    private String currentCategory = "unknown";
    public String getCurrentCategory() {
        return currentCategory;
    }
    public void setCurrentCategory(String currentCategory) {
        this.currentCategory = currentCategory;
    }

    public static List<String> getListWikiserver() {
        if ( listWikis.size() < 2 ) {
            initWikiListe();
        }
        System.out.println(" WIKIS: " + listWikis.size() );
        return listWikis;
    }

    static List<String> listProjectsTMP = new ArrayList<String>();

    public void _addNewProject(String s) {

        setCurrentProject( s );

        listProjects.add(0, s );

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
    private static class DownloadTask extends AsyncTask<String, Void, Void> {

        List<String> liste = null;
        public void setListe(List<String> liste) {
            this.liste = liste;
        }
        public List<String> getListe() {
            return liste;
        }

        protected Void doInBackground( String... uri ) {


            DefaultHttpClient httpclient = new DefaultHttpClient();




            if ( liste == null )
               liste = new ArrayList<String>();

            try {

                HttpGet httpget = new HttpGet(uri[0]); // server
                httpget.setHeader("Authorization", AppStorage.getAuthorizationString());

                Log.i("###***ContextModel***###", "request-call ::" + httpget.getRequestLine());
                Log.i("###***ContextModel***###", "request-auth ::" + AppStorage.getAuthorizationString());

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
                    Log.i("###*** ContextModel ***###", "response-list-data:: (" + x.toString() +")" );

                    String s = "(";

                    String[] listElements = x.toString().split("\n");
                    for( String item : listElements ) {
                        String shorts = item.trim();
                        int i = shorts.length();
                        if ( i > 2 ) {
                            String text = shorts;
                            if( shorts.startsWith("\"") && shorts.endsWith("\"") )
                                text = shorts.substring( 1 , i - 1 );

                            liste.add(text);
                            s = s.concat(text + ", ");
                        }
                    }

                    s = s.concat( ")" );

                    Log.i("###*** ContextModel ***###", "response-list-data:: (" + s.toString() +")" );

                    liste.add("DUMMY");


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

            CharSequence seq = "A list download was done ... (" + liste.size() + ") items.";

            Toast.makeText( TakeNote.appContext, seq, Toast.LENGTH_LONG).show();

        }




    }

}
