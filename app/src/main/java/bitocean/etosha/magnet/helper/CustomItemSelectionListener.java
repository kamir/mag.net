package bitocean.etosha.magnet.helper;

/**
 * Created by kamir on 06.01.15.
 */

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import bitocean.etosha.magnet.TakeNote;

public class CustomItemSelectionListener implements OnItemSelectedListener {

    /**
     * The listener is initialized with a key an initial value, which defines
     * the initial selection of the list.
     *
     * In case of an selection even, the context object is updated.
     *
     * The value of the property (according to key) is set automatically.
     */


    String label = "Spinner selection:";
    String key = "?";

    public CustomItemSelectionListener( String l, String k ) {
        label = l;
        key = k;
    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {

        Toast.makeText(parent.getContext(),
                label + "\n" + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_LONG).show();

        TakeNote.context.setProperty( key, parent.getItemAtPosition(pos).toString());

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

}