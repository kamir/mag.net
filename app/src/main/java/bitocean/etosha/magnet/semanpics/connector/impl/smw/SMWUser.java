package bitocean.etosha.magnet.semanpics.connector.impl.smw;

/**
 *
 * A component which communicates with the SMW should implement
 * this interface.
 *
 * We use some active callbacks.
 *
 * Created by kamir on 25.03.15.
 */
public interface SMWUser {

    void setProgress(int i);

    void notifyUploadsOK();

    void closeProgressDialog();

}
