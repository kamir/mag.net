package bitocean.etosha.magnet.semanpics.connector;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import bitocean.etosha.magnet.semanpics.connector.impl.smw.SMWUser;

/**
 * Created by kamir on 07.01.15.
 */
public interface SemanticGraphStore {


    /**
     * Test, if the service is available ...
     * @return
     */
    public boolean ping();

    /**
     * Show the Domain to the user ...
     * @return
     */
    public String getDomain();

    /**
     * Show the Domain to the user ...
     * @return
     */
    public String getUriRecentChanges();



    /**
     * The full API-Url is the base for all HTTP-Client interactions ...
     */
    public String getAPIUrl();


    /************
     *
     * The following METHODS REQUIRE Authorisation !!!
     *
     *
     *
     ************/



    /**
     * Login to the server via usernam, password pair
     *
     * @param kamir
     * @param chars
     */
    public void login(String kamir, char[] chars) throws IOException, Exception;



    /**
     * Overwrite the content of an existing page in the Graph Store ...
     *
     * @param page
     * @param text
     * @param s
     */
    public void edit(String page, String text, String s) throws Exception;

    /**
     * Overwrite the content of an existing context-page in the Graph Store ...
     * and append a file via local link.
     *
     * @param apiUrl
     * @param uploadedBitmapFile
     * @param text
     * @param comment
     */
    public String POST2Wiki(String apiUrl, File uploadedBitmapFile, String text, String comment, Context appContext, SMWUser u);
}
