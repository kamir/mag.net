package bitocean.etosha.magnet.semanpics.connector.impl.smw;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MediaWiki {

    /**
     *  Denotes the namespace of images and media, such that there is no
     *  description page. Uses the "Media:" prefix.
     *  @see #FILE_NAMESPACE
     *  @since 0.03
     */
    public static final int MEDIA_NAMESPACE = -2;

    /**
     *  Denotes the namespace of pages with the "Special:" prefix. Note
     *  that many methods dealing with special pages may spew due to
     *  raw content not being available.
     *  @since 0.03
     */
    public static final int SPECIAL_NAMESPACE = -1;

    /**
     *  Denotes the main namespace, with no prefix.
     *  @since 0.03
     */
    public static final int MAIN_NAMESPACE = 0;

    /**
     *  Denotes the namespace for talk pages relating to the main
     *  namespace, denoted by the prefix "Talk:".
     *  @since 0.03
     */
    public static final int TALK_NAMESPACE = 1;

    /**
     *  Denotes the namespace for user pages, given the prefix "User:".
     *  @since 0.03
     */
    public static final int USER_NAMESPACE = 2;

    /**
     *  Denotes the namespace for user talk pages, given the prefix
     *  "User talk:".
     *  @since 0.03
     */
    public static final int USER_TALK_NAMESPACE = 3;

    /**
     *  Denotes the namespace for pages relating to the project,
     *  with prefix "Project:". It also goes by the name of whatever
     *  the project name was.
     *  @since 0.03
     */
    public static final int PROJECT_NAMESPACE = 4;

    /**
     *  Denotes the namespace for talk pages relating to project
     *  pages, with prefix "Project talk:". It also goes by the name
     *  of whatever the project name was, + "talk:".
     *  @since 0.03
     */
    public static final int PROJECT_TALK_NAMESPACE = 5;

    /**
     *  Denotes the namespace for file description pages. Has the prefix
     *  "File:". Do not create these directly, use upload() instead.
     *  @see #MEDIA_NAMESPACE
     *  @since 0.25
     */
    public static final int FILE_NAMESPACE = 6;

    /**
     *  Denotes talk pages for file description pages. Has the prefix
     *  "File talk:".
     *  @since 0.25
     */
    public static final int FILE_TALK_NAMESPACE = 7;

    /**
     *  Denotes the namespace for (wiki) system messages, given the prefix
     *  "MediaWiki:".
     *  @since 0.03
     */
    public static final int MEDIAWIKI_NAMESPACE = 8;

    /**
     *  Denotes the namespace for talk pages relating to system messages,
     *  given the prefix "MediaWiki talk:".
     *  @since 0.03
     */
    public static final int MEDIAWIKI_TALK_NAMESPACE = 9;

    /**
     *  Denotes the namespace for templates, given the prefix "Template:".
     *  @since 0.03
     */
    public static final int TEMPLATE_NAMESPACE = 10;

    /**
     *  Denotes the namespace for talk pages regarding templates, given
     *  the prefix "Template talk:".
     *  @since 0.03
     */
    public static final int TEMPLATE_TALK_NAMESPACE = 11;

    /**
     *  Denotes the namespace for help pages, given the prefix "Help:".
     *  @since 0.03
     */
    public static final int HELP_NAMESPACE = 12;

    /**
     *  Denotes the namespace for talk pages regarding help pages, given
     *  the prefix "Help talk:".
     *  @since 0.03
     */
    public static final int HELP_TALK_NAMESPACE = 13;

    /**
     *  Denotes the namespace for category description pages. Has the
     *  prefix "Category:".
     *  @since 0.03
     */
    public static final int CATEGORY_NAMESPACE = 14;

    /**
     *  Denotes the namespace for talk pages regarding categories. Has the
     *  prefix "Category talk:".
     *  @since 0.03
     */
    public static final int CATEGORY_TALK_NAMESPACE = 15;

    /**
     *  Denotes all namespaces.
     *  @since 0.03
     */
    public static final int ALL_NAMESPACES = 0x09f91102;

    // LOG TYPES

    /**
     *  Denotes all logs.
     *  @since 0.06
     */
    public static final String ALL_LOGS = "";

    /**
     *  Denotes the user creation log.
     *  @since 0.06
     */
    public static final String USER_CREATION_LOG = "newusers";

    /**
     *  Denotes the upload log.
     *  @since 0.06
     */
    public static final String UPLOAD_LOG = "upload";

    /**
     *  Denotes the deletion log.
     *  @since 0.06
     */
    public static final String DELETION_LOG = "delete";

    /**
     *  Denotes the move log.
     *  @since 0.06
     */
    public static final String MOVE_LOG = "move";

    /**
     *  Denotes the block log.
     *  @since 0.06
     */
    public static final String BLOCK_LOG = "block";

    /**
     *  Denotes the protection log.
     *  @since 0.06
     */
    public static final String PROTECTION_LOG = "protect";

    /**
     *  Denotes the user rights log.
     *  @since 0.06
     */
    public static final String USER_RIGHTS_LOG = "rights";

    /**
     *  Denotes the user renaming log.
     *  @since 0.06
     */
    public static final String USER_RENAME_LOG = "renameuser";

    /**
     *  Denotes the page importation log.
     *  @since 0.08
     */
    public static final String IMPORT_LOG = "import";

    /**
     *  Denotes the edit patrol log.
     *  @since 0.08
     */
    public static final String PATROL_LOG = "patrol";

    // PROTECTION LEVELS

    /**
     *  Denotes a non-protected page.
     *  @since 0.09
     */
    public static final String NO_PROTECTION = "all";

    /**
     *  Denotes semi-protection (i.e. only autoconfirmed users can perform a
     *  particular action).
     *  @since 0.09
     */
    public static final String SEMI_PROTECTION = "autoconfirmed";

    /**
     *  Denotes full protection (i.e. only admins can perfom a particular action).
     *  @since 0.09
     */
    public static final String FULL_PROTECTION = "sysop";

    // ASSERTION MODES
    public static final int ASSERT_NONE = 0;

    public static final int ASSERT_BOT = 2;

    public static final int ASSERT_NO_MESSAGES = 4;

    // RC OPTIONS
    public static final int HIDE_ANON = 1;

    public static final int HIDE_BOT = 2;

    public static final int HIDE_SELF = 4;

    public static final int HIDE_MINOR = 8;

    public static final int HIDE_PATROLLED = 16;

    // REVISION OPTIONS
    public static final long NEXT_REVISION = -1L;

    public static final long CURRENT_REVISION = -2L;

    public static final long PREVIOUS_REVISION = -3L;

    // serial version
    private static final long serialVersionUID = -8745212681497644126L;

    public static final String version = "0.0.1";

    // preferences
    public int max = 500;
    public int slowmax = 50;
    public int throttle = 10000; // throttle
    public int maxlag = 5;
    public int assertion = 0; // assertion mode
    public int statusinterval = 100; // status check
    public String useragent = "MediaWikiService.java " + version;
    public boolean zipped = true;
    public boolean markminor = false, markbot = false;
    public boolean resolveredirect = false;


    // the domain of the wiki
     String domain;
     String query;
    String base;
    public String apiUrl;
     String scriptPath = "/w";
     boolean wgCapitalLinks = true;


    // various caches
    HashMap<String, Integer> namespaces = null;
    ArrayList<String> watchlist = null;




    /**
     *  Parses the next XML attribute with the given name.
     *  @param xml the xml to search
     *  @param attribute the attribute to search
     *  @param index where to start looking
     *  @return the value of the given XML attribute, or null if the attribute
     *  is not present
     *  @since 0.28
     */
    protected String parseAttribute(String xml, String attribute, int index)
    {
        // let's hope the JVM always inlines this
        if (xml.contains(attribute + "=\""))
        {
            int a = xml.indexOf(attribute + "=\"", index) + attribute.length() + 2;
            int b = xml.indexOf('\"', a);
            return xml.substring(a, b);
        }
        else
            return null;
    }

    /**
     *  Convenience method for converting a namespace list into String form.
     *  @param sb the url StringBuilder to append to
     *  @param id the request type prefix (e.g. "pl" for prop=links)
     *  @param namespaces the list of namespaces to append
     *  @since 0.27
     */
    protected void constructNamespaceString(StringBuilder sb, String id, int... namespaces)
    {
        int temp = namespaces.length;
        if (temp == 0)
            return;
        sb.append("&");
        sb.append(id);
        sb.append("namespace=");
        for (int i = 0; i < temp - 1; i++)
        {
            sb.append(namespaces[i]);
            sb.append("%7C");
        }
        sb.append(namespaces[temp - 1]);
    }

    /**
     *  Ensures no less than <tt>throttle</tt> milliseconds pass between edits
     *  and other write actions.
     *  @param start the time at which the write method was entered
     *  @since 0.30
     */
    protected void throttle(long start)
    {
        try
        {
            long time = throttle - System.currentTimeMillis() + start;
            if (time > 0)
                Thread.sleep(time);
        }
        catch (InterruptedException e)
        {
            // nobody cares
        }
    }

    /**
     *  Logs a successful result.
     *  @param text string the string to log
     *  @param method what we are currently doing
     *  @param level the level to log at
     *  @since 0.06
     */
    protected void log(Level level, String method, String text)
    {
        Logger logger = Logger.getLogger("wiki");
        logger.logp(level, "MediaWikiService", method, "[{0}] {1}", new Object[] { domain, text });
    }

    /**
     *  Logs a url fetch.
     *  @param url the url we are fetching
     *  @param method what we are currently doing
     *  @since 0.08
     */
    protected void logurl(String url, String method)
    {
//        Logger logger = Logger.getLogger("wiki");
//        logger.logp(Level.INFO, "MediaWikiService", method, "Fetching URL {0}", url);
    }

    /**
     *  Creates a Calendar object with the current time. Wikimedia wikis use
     *  UTC, override this if your wiki is in another timezone.
     *  @return see above
     *  @since 0.26
     */
    public Calendar makeCalendar()
    {
        return new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    }

    /**
     *  Turns a calendar into a timestamp of the format yyyymmddhhmmss. Might
     *  be useful for subclasses.
     *  @param c the calendar to convert
     *  @return the converted calendar
     *  @see #timestampToCalendar
     *  @since 0.08
     */
    protected String calendarToTimestamp(Calendar c)
    {
        return String.format("%04d%02d%02d%02d%02d%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
    }

    /**
     *  Turns a timestamp into a Calendar object. Might be useful for subclasses.
     *
     *  @param timestamp the timestamp to convert
     *  @param api whether the timestamp is of the format yyyy-mm-ddThh:mm:ssZ
     *  as opposed to yyyymmddhhmmss (which is the default)
     *  @return the converted Calendar
     *  @see #calendarToTimestamp
     *  @since 0.08
     */
    protected final Calendar timestampToCalendar(String timestamp, boolean api)
    {
        // TODO: move to Java 1.8
        Calendar calendar = makeCalendar();
        if (api)
            timestamp = convertTimestamp(timestamp);
        int year = Integer.parseInt(timestamp.substring(0, 4));
        int month = Integer.parseInt(timestamp.substring(4, 6)) - 1; // January == 0!
        int day = Integer.parseInt(timestamp.substring(6, 8));
        int hour = Integer.parseInt(timestamp.substring(8, 10));
        int minute = Integer.parseInt(timestamp.substring(10, 12));
        int second = Integer.parseInt(timestamp.substring(12, 14));
        calendar.set(year, month, day, hour, minute, second);
        return calendar;
    }

    /**
     *  Converts a timestamp of the form used by the API (yyyy-mm-ddThh:mm:ssZ)
     *  to the form yyyymmddhhmmss.
     *
     *  @param timestamp the timestamp to convert
     *  @return the converted timestamp
     *  @see #timestampToCalendar
     *  @since 0.12
     */
    protected String convertTimestamp(String timestamp)
    {
        // TODO: remove this once Java 1.8 comes around
        StringBuilder ts = new StringBuilder(timestamp.substring(0, 4));
        ts.append(timestamp.substring(5, 7));
        ts.append(timestamp.substring(8, 10));
        ts.append(timestamp.substring(11, 13));
        ts.append(timestamp.substring(14, 16));
        ts.append(timestamp.substring(17, 19));
        return ts.toString();
    }


    /**
     *  The list of options the user can specify for his/her gender.
     *  @since 0.24
     */
    public enum Gender
    {
        // These names come from the MW API so we can use valueOf() and
        // toString() without any fidgets whatsoever. Java naming conventions
        // aren't worth another 20 lines of code.

        /**
         *  The user self-identifies as a male.
         *  @since 0.24
         */
        male,

        /**
         *  The user self-identifies as a female.
         *  @since 0.24
         */
        female,

        /**
         *  The user has not specified a gender in preferences.
         *  @since 0.24
         */
        unknown;
    }

}
