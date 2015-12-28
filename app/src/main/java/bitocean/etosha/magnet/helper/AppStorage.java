package bitocean.etosha.magnet.helper;

import android.util.Base64;
import android.util.Log;

import bitocean.etosha.magnet.datamodel.ContextModel;

/**
 * Created by kamir on 27.12.15.
 */
public class AppStorage {

   /**
    *
    * Here we handle or AppState Storage tasks ...
    */
    public String loadEntrypointFromSettings() {

        return ContextModel.getDefaultPageName();

    }

    static String user = "borussia";
    static String pwd = "1836";

    static String authorizationString = "Basic " + Base64.encodeToString(
            (user + ":" + pwd).getBytes(),
            Base64.NO_WRAP); //Base64.NO_WRAP flag

    public static String getAuthorizationString() {

        Log.i( "### AppStorage ### ", authorizationString );
        Log.i( "### AppStorage ### ", "(" + user + "," + pwd + ")" );

        return    authorizationString;
    }



}
