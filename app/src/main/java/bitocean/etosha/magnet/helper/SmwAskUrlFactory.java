package bitocean.etosha.magnet.helper;

import bitocean.etosha.magnet.datamodel.ContextModel;

/**
 * Created by kamir on 21.06.15.
 */
public class SmwAskUrlFactory {


    public static String getQuery(ContextModel cm, String filter, String value) {
    
        return "http://semanpix.de/oldtimer/wiki/index.php?title=Special:Ask/-5B-5BCategory:-20GeoImage-5D-5D-20-5B-5B"+filter+"::-20"+value+"-5D-5D/-3FDate/-3FLatitude/-3FLongitude/-3FAltitude%3DAltitude(m)/-3FNotes/format%3D-20csv/searchlabel%3D-20Dowload-20CSV-2DFile/offset%3D0";

    }
}
