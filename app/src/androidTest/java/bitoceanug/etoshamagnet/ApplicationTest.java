package bitoceanug.etoshamagnet;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runner.RunWith;

import bitocean.etosha.magnet.datamodel.ContextModel;
import bitocean.etosha.magnet.datamodel.POI;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(Runner.class)
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
    }

    /**
     * Test the POI -
     */
    @Test
    public void testNextActivityWasLaunchedWithIntent() {

        System.out.println("*** What is HERE ??? " );

        POI here = new POI("DEFAULT LOCATION 2,01 JANUARY 2016,52° 11' 00.00 N,11° 12' 11.11 E, 340.5,DEFAULT;LOCATION", 1);

        System.out.println("HERE >>> " + here);

        assertNotNull(here);

        // assertNull(here);

    }


}