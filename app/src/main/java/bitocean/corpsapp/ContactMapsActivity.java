package bitocean.corpsapp;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Vector;

import bitocean.etosha.magnet.datamodel.contextmap.POI;
import bitocean.etosha.magnet.datamodel.contextmap.PersonLocation;
import bitocean.etosha.magnet.helper.AppStorage;
import bitoceanug.etoshamagnet.R;

import static android.provider.ContactsContract.*;


/**
 *
 * This module loads the list data from Google-Docs or Github in CSV format and updates local contacts.
 *
 * It loads the address data and stores it with an additional label:
 *
 *    contact-type "Label=Mitglied Corps-Borussia-Halle"
 *    corps-status
 *
 *    anrede   =>  AZ1
 *    titel    =>  AZ2 = titel + vorname + name
 *    strasse
 *    PLZ, Ort
 *
 */
public class ContactMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    /**
     * The groupID is selected during startup ...
     */
    String groupID = "-1";


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // this.defaultGroupName = this.testGroupName;

        mMap = googleMap;



        /**
         * On Github is the latest adress list as CSV file.
         */
//        synchListWithContacts(
//                "https://github.com/kamir/corps-borussia-templates/raw/master/WORKSPACE/Adressbuch/latest.csv",
//                OP_MODE_privateServer
//        );

//        https://developers.google.com/drive/android/auth
//        synchListWithContacts(
//                "https://docs.google.com/spreadsheets/u/1/d/1ZAxmrprFBk40H2C90o5rcPq8hlK5mn4p4lnmc2SJzmo/export?format=csv&id=1ZAxmrprFBk40H2C90o5rcPq8hlK5mn4p4lnmc2SJzmo&gid=1569145611",
//                OP_MODE_GoogleDocs
//        );


        boolean groupExists = isGroupAvailable();

        String groupCreateMessage = "Gruppe: \n   " + defaultGroupName + "\nist vorhanden.";

        if (!groupExists) {

            groupCreateMessage = "Neue Gruppe: \n   " + defaultGroupName + "\nwurde erstellt.";

            createGroup();
        } else {

        }

        initContactList();

        //
        // This BLOG-POST was really cool and helpfull !!!
        //
        // https://laaptu.wordpress.com/2012/07/18/android-fetching-group-and-its-contact/
        //
        ArrayList<Item> listGroups = fetchGroups();

        for (Item i : listGroups) {
            Log.i("Group-FETCHER ::", i.toString());
        }

        // LinkedHashMap<Item,ArrayList<Item>>
        for (Item i : groupList.keySet()) {

            Log.i("CONTACT-FETCHER ::", "GROUP:  " + i.toString() + " => " + i.name );

            if( i.name.equals( defaultGroupName ) ) {

                for (Item j : groupList.get(i)) {
                    Log.i("CONTACT-FETCHER ::", "     :  " + j.toString());
                }

            }
        }

        synchListWithContacts(
                "http://corps-borussia-halle.semanpix.de/karte/latest.tsv",
                OP_MODE_GoogleDocs
        );

        /**
         * Currently we can not store all details in the contact db.
         * Therefore we use the MEMO field to store the full record
         * in a TSV-format, the same way like it is loaded from the
         * changelist.
         */

        // ASK for updates and take only newer ...
        //
        // storeChangesInContacts();
        // store date of update ...


        // http://stackoverflow.com/questions/4075694/inserting-contacts-in-android-2-2
        storeChangesInContactsSilently();

        plotNewPOIsToMap(mMap);

        // Add a marker in Halle, and show the Corpshaus.
        LatLng corpshaus = new LatLng(51.4925533, 11.954786);
        mMap.addMarker(new MarkerOptions().position(corpshaus).title("(HOME) Corps Borussia Halle (GID:" + groupID + ")"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(corpshaus));

        Toast.makeText(ContactMapsActivity.this, groupCreateMessage,
                Toast.LENGTH_LONG).show();


    }

    private void storeChangesInContactsSilently() {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        Log.i("SILENT-CONTACT-Synch::", " GO " );


        for (POI loc : poiListe) {

            String name = loc.fields[13] + " " + loc.fields[2] + " " + loc.fields[3];

//            Intent intent = new Intent(Intent.ACTION_INSERT);

//            intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
//            intent.putExtra(ContactsContract.Intents.Insert.PHONE, loc.fields[10]);
//            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, loc.fields[11]); // MOBIL
//            intent.putExtra(Intents.Insert.TERTIARY_PHONE, loc.fields[9]);  // FESTNETZ
//            intent.putExtra(ContactsContract.Intents.Insert.NOTES,
//                    "Imported from OFFICIAL FORM !!! " + loc.getMarkerText());
//            intent.putExtra(ContactsContract.Intents.Insert.POSTAL, loc.fields[5] + " " + loc.fields[6] + "\n" + loc.fields[7]
//                    + " " + loc.fields[8]);






            int rawContactInsertIndex = ops.size();


            ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                    .withValue(RawContacts.ACCOUNT_NAME, null)
                    .build());

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(CommonDataKinds.Phone.NUMBER, "9X-XXXXXXXXX")
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, "Mike Sullivan")
                    .build());

            Log.i("SILENT-CONTACT-Synch::", ops.size() + " " );


        }

        try {

            ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }


        Log.i("SILENT-CONTACT-Synch::", "LINK TO GROUP!");


        for (POI loc : poiListe) {

            String name = loc.fields[13] + " " + loc.fields[2] + " " + loc.fields[3];

            Vector<String> ids = fetchContactIDforname( name );
            // which is the contact id of the new one?
            for( String id: ids ) {

                Log.i( "<<< CREATOR >>> :",  id );

                try
                {

                    // Add selected contact to selected group
                    ContentValues values = new ContentValues();
                    values.put(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID,Integer.parseInt(id)); // 245 is a contact id, replace with selected contact id

                    values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,groupID);// 3 is a group id, replace with selected group id

                    values.put(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);

                    ContextWrapper context = this;
                    context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                    // End add contact to group code
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    Log.d("add group error :", ""+ e.getMessage().toString());
                }

            }

            Log.i("CONTACT-Synch::", loc.getMarkerText());

        }



    }


    /**
     * This app uses a default group to place new adresses, loaded from our change list.
     */
    public String defaultGroupName = "Corpsbrüder (Borussia Halle)";
    public String testGroupName = "Meine Kontakte";

    /**
     * Default mode is:       OP_MODE_privateServer = 0;
     * <p/>
     * Is it enough to use simple Authentication for this server? YES
     * <p/>
     * To provide this list we have to deploy the TSV file, which is loaded from Google-Docs
     * or later on from any other place, e.g., the Borussia Portal.
     */
    int mode = 0;

    /**
     * This is the initial mode, for fast results...
     */
    public static final int OP_MODE_privateServer = 0;       // Is it enough to use simple Authentication for Github?


    /**
     * Additional modes for later ...
     */
    public static final int OP_MODE_Github = 1;       // Is it enough to use simple Authentication for Github?
    public static final int OP_MODE_GoogleDocs = 2;


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private ArrayList<Item> fetchGroups() {
        ArrayList<Item> groupList = new ArrayList<Item>();
        String[] projection = new String[]{ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};
        Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
                projection, null, null, null);
        ArrayList<String> groupTitle = new ArrayList<String>();
        while (cursor.moveToNext()) {
            Item item = new Item();
            item.id = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));
            String groupName = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));

            if (groupName.contains("Group:"))
                groupName = groupName.substring(groupName.indexOf("Group:") + "Group:".length()).trim();

            if (groupName.contains("Favorite_"))
                groupName = "Favorite";

            if (groupName.contains("Starred in Android") || groupName.contains("My Contacts"))
                continue;

            if (groupTitle.contains(groupName)) {
                for (Item group : groupList) {
                    if (group.name.equals(groupName)) {
                        group.id += "," + item.id;
                        break;
                    }
                }
            } else {
                groupTitle.add(groupName);
                item.name = groupName;
                groupList.add(item);
            }

        }

        cursor.close();

        Collections.sort(groupList, new Comparator<Item>() {
            public int compare(Item item1, Item item2) {
                return item2.name.compareTo(item1.name) < 0
                        ? 0 : -1;
            }
        });

        return groupList;
    }


    //    public ArrayList<String> getAllNumbersFromGroupId(String navn)
    public String getAllNumbersFromGroupId(String groupTitle) {
        String selection = ContactsContract.Groups.DELETED + "=? and " + ContactsContract.Groups.GROUP_VISIBLE + "=?";

        String[] selectionArgs = {"0", "1"};

        Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI, null, selection, selectionArgs, null);

        cursor.moveToFirst();
        int len = cursor.getCount();

        StringBuffer l = new StringBuffer();

        ArrayList<String> numbers = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            String title = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));

            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));

            Log.i("<<< LOCAL-CONTACT-LOADER >>>", title + " " + id + " ??? " + groupTitle);

            if (title.equals(groupTitle)) {

                Log.i("<<< LOCAL-CONTACT-LOADER >>>", "WORK ON : " + title + " " + id);

                String[] cProjection = {Contacts.DISPLAY_NAME, CommonDataKinds.GroupMembership.CONTACT_ID};

                Cursor groupCursor = getContentResolver().query(

                        Data.CONTENT_URI,
                        cProjection,
                        CommonDataKinds.GroupMembership.GROUP_ROW_ID + " =? AND "
                                + ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "='"
                                + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'",
                        new String[]{String.valueOf(id)}, null);

                Log.i("<<< LOCAL-CONTACT-LOADER >>>", "CURSOR: " + groupCursor);


                if (groupCursor != null && groupCursor.moveToFirst()) {

                    int z = 0;

                    do {

                        Log.i("<<< LOCAL-CONTACT-LOADER >>>", z + " ");

                        z++;

                        int nameCoumnIndex = groupCursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME);

                        String name = groupCursor.getString(nameCoumnIndex);

                        long contactId = groupCursor.getLong(groupCursor.getColumnIndex(CommonDataKinds.GroupMembership.CONTACT_ID));

                        Cursor numberCursor = getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{CommonDataKinds.Phone.NUMBER}, CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);

                        if (numberCursor.moveToFirst()) {
                            int numberColumnIndex = numberCursor.getColumnIndex(CommonDataKinds.Phone.NUMBER);
                            do {

                                String phoneNumber = numberCursor.getString(numberColumnIndex);

                                numbers.add(phoneNumber);
                                l.append(phoneNumber + "\n");

                            } while (numberCursor.moveToNext());
                            numberCursor.close();
                        }
                    } while (groupCursor.moveToNext());
                    groupCursor.close();
                }
                break;
            }

            cursor.moveToNext();
        }
        cursor.close();

//        return numbers;
        return l.toString();
    }


    private void storeChangesInContacts() {


        for (POI loc : poiListe) {

            Intent intent = new Intent(Intent.ACTION_INSERT);

            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

            // Sets the special extended data for navigation
            intent.putExtra("finishActivityOnSaveCompleted", true);

            String name = loc.fields[13] + " " + loc.fields[2] + " " + loc.fields[3];

            intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, loc.fields[10]);
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, loc.fields[11]); // MOBIL
            intent.putExtra(Intents.Insert.TERTIARY_PHONE, loc.fields[9]);  // FESTNETZ

            /*
            intent.putExtra(ContactsContract.Groups.TITLE, this.defaultGroupName );
            intent.putExtra(ContactsContract.Groups._ID, this.groupID );
            intent.putExtra(ContactsContract.Groups.GROUP_VISIBLE, true );
            */

            intent.putExtra(ContactsContract.Intents.Insert.NOTES,
                    "Imported from OFFICIAL FORM !!! " + loc.getMarkerText());

            intent.putExtra(ContactsContract.Intents.Insert.POSTAL, loc.fields[5] + " " + loc.fields[6] + "\n" + loc.fields[7]
                    + " " + loc.fields[8]);

            this.startActivity(intent);

            Log.i("CONTACT-Synch::", loc.getMarkerText());

        }

        for (POI loc : poiListe) {

            String name = loc.fields[13] + " " + loc.fields[2] + " " + loc.fields[3];

            Vector<String> ids = fetchContactIDforname( name );
            // which is the contact id of the new one?
            for( String id: ids ) {

                Log.i( "<<< CREATOR >>> :",  id );

                try
                {

                    // Add selected contact to selected group
                    ContentValues values = new ContentValues();
                    values.put(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID,Integer.parseInt(id)); // 245 is a contact id, replace with selected contact id

                    values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,groupID);// 3 is a group id, replace with selected group id

                    values.put(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);

                    ContextWrapper context = this;
                    context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                    // End add contact to group code
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    Log.d("add group error :", ""+ e.getMessage().toString());
                }




            }




            Log.i("CONTACT-Synch::", loc.getMarkerText());

        }



    }


    ContentValues groupValues;

    void createGroup() {

        ContentResolver cr = this.getContentResolver();
        groupValues = new ContentValues();
        groupValues.put(ContactsContract.Groups.TITLE, defaultGroupName);
        cr.insert(ContactsContract.Groups.CONTENT_URI, groupValues);

        isGroupAvailable();

    }


    /**
     * Contacts with label="Mitglied Corps Borussia Halle" are loaded and plotted in a Map
     * <p/>
     * Input: contact-list
     * Output: Google-Maps-View
     */
    private void plotNewPOIsToMap(GoogleMap m) {


        m.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });


        for (POI loc : poiListe) {

            m.addMarker(loc.getMarker());

        }

        Toast.makeText(ContactMapsActivity.this, "Loaded contact list from device to map.",
                Toast.LENGTH_LONG).show();


    }

    /**
     * Load a CSV file with addresses.
     * <p/>
     * Input: URL-String
     * Output: none
     * <p/>
     * Effect: changes the contacts
     *
     * @param url
     */

    //
    // liste muss 4 spalten haben.
    //
    ArrayList<String> liste = null;
    ArrayList<POI> poiListe = null;

    public void synchListWithContacts(String url, int mode) {
        this.mode = mode;

        DefaultHttpClient httpclient = new DefaultHttpClient();

        liste = new ArrayList<String>();
        poiListe = new ArrayList<POI>();

        try {

            HttpGet httpget = new HttpGet(url); // server
            httpget.setHeader("Authorization", AppStorage.getAuthorizationString());

            Log.i("### Contact List Loader (CLL) ###", "request-call ::" + httpget.getRequestLine());

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

                Log.i("### CLL ###", "response-data:: {" + x.toString() + "}");

                int lc = 0;
                String[] lines = x.toString().split("\n");
                for (String item : lines) {

                    String[] fields = item.split("\t");


                    String text = "";

                    int index = 0;
                    for (String f : fields) {

                        String shorts = f.trim();

                        int i = shorts.length();

                        String contr = shorts;

                        if (i > 2) {
                            if (shorts.startsWith("\"") && shorts.endsWith("\""))
                                contr = shorts.substring(1, i);
                        }

                        fields[index] = contr;
                        index++;

                        text = text.concat("; " + contr);
                    }

                    if (lc != 0) {

                        liste.add(text);
                        Log.i("### CLL ###", "(" + lc + ") " + text);

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


                        // String initText = fields[13] + " " + fields[2] + " " + fields[3] + "," + fields[14] + "," + fields[15] + "," +fields[16];
                        // PersonLocation loc = new PersonLocation( initText , 2 );  works only if lat-lon is available

                        PersonLocation loc = new PersonLocation(fields, 2);
                        loc.setFields(fields);

                        // just in case, maybe we do not have any marker content ready.
                        if (loc.getMarkerText() == null)
                            loc.setMarkerText(text);

                        poiListe.add(loc);

                    }

                    lc++;


                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (response != null)
                    Log.i("### CLL ###", "response " + response.getStatusLine().toString());
                Log.i("### CLL ###", "response " + response.toString());

            } finally {

            }
        } finally {

        }

        Toast.makeText(ContactMapsActivity.this, "Loaded contact list to device. (" + liste.size() + ")",
                Toast.LENGTH_LONG).show();

    }

    public boolean isGroupAvailable() {

        final String[] GROUP_PROJECTION = new String[]{
                ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};

        Cursor group_coursor = getContentResolver().query(
                ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, null, null, null);
        group_coursor.moveToFirst();

        ArrayList<String> liste = new ArrayList<String>();

        while (!group_coursor.isAfterLast()) {

            int idcolumn = group_coursor.getColumnIndex(ContactsContract.Groups.TITLE);
            String id = group_coursor.getString(idcolumn);

            int idcolumn2 = group_coursor.getColumnIndex(Groups._ID);
            String gid = group_coursor.getString(idcolumn2);


            if (id.equals(defaultGroupName)) {
                groupID = gid;

                Log.i("GROUP-LIST", "### " + defaultGroupName + " ###" + id + "(" + gid + ")");

                return true;
            }

            liste.add(id);

            Log.i("GROUP-LIST", id + "(" + gid + ")");

            group_coursor.moveToNext();
        }

        LinkedHashSet<String> s = new LinkedHashSet<String>();
        s.addAll(liste);

        return false;

    }

    LinkedHashMap<Item, ArrayList<Item>> groupList = null;

    private void initContactList() {
        groupList = new LinkedHashMap<Item, ArrayList<Item>>();
        ArrayList<Item> groupsList = fetchGroups();
        for (Item item : groupsList) {
            String[] ids = item.id.split(",");
            ArrayList<Item> groupMembers = new ArrayList<Item>();
            for (int i = 0; i < ids.length; i++) {
                String groupId = ids[i];
                groupMembers.addAll(fetchGroupMembers(groupId));
            }
            item.name = item.name;
            item.memo = "(size: " + groupMembers.size() + ")";

            groupList.put(item, groupMembers);
        }
    }

    private Hashtable<String, String> allMemos = new Hashtable<String, String>();



    // inspired by : http://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Note.html

    private Vector<String> fetchContactIDforname(String name){

        System.out.println(">>> CHECK ITEM : " + name);

        Vector<String> ids = new Vector<String>();
        String where =  CommonDataKinds.Contactables.DISPLAY_NAME+" = ?";

        String[] projection = new String[]{CommonDataKinds.GroupMembership.RAW_CONTACT_ID,Data.DISPLAY_NAME };
        // String[] projection = new String[]{Data.CONTACT_ID,Data.DISPLAY_NAME };
        String[] whereProps = new String[]{ name };


        Cursor cursor = getContentResolver().query(
                Data.CONTENT_URI,
                projection,
                where,
                whereProps,
                Data.DISPLAY_NAME+" COLLATE LOCALIZED ASC");

        while(cursor.moveToNext()){

            String a[] = cursor.getColumnNames();

            StringBuffer sb = new StringBuffer();

            for( String s : a ) {
                String v = cursor.getString(cursor.getColumnIndex(s));
                sb.append(s.toString() + " => " + cursor.getColumnIndex( s ) + "[" + v + "]" + "\n\t" );
            }


            Item item = new Item();
            item.name = cursor.getString(cursor.getColumnIndex(Data.DISPLAY_NAME));
            item.id = cursor.getString(cursor.getColumnIndex(CommonDataKinds.GroupMembership.RAW_CONTACT_ID));

            NoteDAO dao = new NoteDAO( this.getApplicationContext() );
            ArrayList<NoteData> l = dao.get( Long.parseLong( item.id ) );

            if ( l.size() > 0 )
                item.memo = l.get(0).data;
            else
                item.memo = "no memo";

            Cursor phoneFetchCursor = getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{CommonDataKinds.Phone.NUMBER, CommonDataKinds.Phone.DISPLAY_NAME, CommonDataKinds.Phone.TYPE},
                    CommonDataKinds.Phone.CONTACT_ID+"="+item.id,null,null);
            while(phoneFetchCursor.moveToNext()){
                item.phNo = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
                item.phDisplayName = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME));
                item.phType = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(CommonDataKinds.Phone.TYPE));

            }
            phoneFetchCursor.close();
            System.out.println(">>> CHECK ITEM : " + item.toString());

            ids.add( item.id );
        }
        cursor.close();
        return ids;
    }

    private ArrayList<Item> fetchGroupMembers(String groupId){

//        String MEMO = CommonDataKinds.Callable.TIMES_CONTACTED;
        String MEMO = CommonDataKinds.Note.NOTE;

        ArrayList<Item> groupMembers = new ArrayList<Item>();
        String where =  CommonDataKinds.GroupMembership.GROUP_ROW_ID +"="+groupId
                +" AND "
                +CommonDataKinds.GroupMembership.MIMETYPE+"='"
                +CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE+"'";
        String[] projection = new String[]{CommonDataKinds.GroupMembership.RAW_CONTACT_ID,Data.DISPLAY_NAME, MEMO };
        Cursor cursor = getContentResolver().query(Data.CONTENT_URI, projection, where,null,
                Data.DISPLAY_NAME+" COLLATE LOCALIZED ASC");
        while(cursor.moveToNext()){

            String a[] = cursor.getColumnNames();

            /**
             StringBuffer sb = new StringBuffer();

             for( String s : a ) {
             String v = cursor.getString(cursor.getColumnIndex(s));
             sb.append(s.toString() + " => " + cursor.getColumnIndex( s ) + "[" + v + "]" + "\n\t" );
             }
             */

            Item item = new Item();
            item.name = cursor.getString(cursor.getColumnIndex(Data.DISPLAY_NAME));

            item.id = cursor.getString(cursor.getColumnIndex(CommonDataKinds.GroupMembership.RAW_CONTACT_ID));


            NoteDAO dao = new NoteDAO( this.getApplicationContext() );
            ArrayList<NoteData> l = dao.get( Long.parseLong( item.id ) );

            if ( l.size() > 0 )
                item.memo = l.get(0).data;
            else
                item.memo = "no memo";

            Cursor phoneFetchCursor = getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{CommonDataKinds.Phone.NUMBER, CommonDataKinds.Phone.DISPLAY_NAME, CommonDataKinds.Phone.TYPE},
                    CommonDataKinds.Phone.CONTACT_ID+"="+item.id,null,null);
            while(phoneFetchCursor.moveToNext()){
                item.phNo = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
                item.phDisplayName = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME));
                item.phType = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(CommonDataKinds.Phone.TYPE));

            }
            phoneFetchCursor.close();
            groupMembers.add(item);
        }
        cursor.close();
        return groupMembers;
    }


}
