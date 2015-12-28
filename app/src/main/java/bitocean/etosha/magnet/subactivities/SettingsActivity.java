package bitocean.etosha.magnet.subactivities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import bitocean.etosha.magnet.datamodel.ContextModel;
import bitocean.etosha.magnet.helper.CustomItemSelectionListener;
import bitoceanug.etoshamagnet.R;


public class SettingsActivity extends ActionBarActivity {

    Spinner s1 = null;
    Spinner s2 = null;
    Spinner s3 = null;
    Spinner s4 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        // init default settings ...
        s1 = (Spinner)findViewById(R.id.spinner);
        s2 = (Spinner)findViewById(R.id.spinner2);
        s3 = (Spinner)findViewById(R.id.spinner3);
        s4 = (Spinner)findViewById(R.id.spinner4);

        ContextModel.initTaskTypes();

        ContextModel.initProjects(  ContextModel.currentUser  );

//         ContextModel.initUsers();

        List<String> list = new ArrayList<String>();
        list.add("Mirko Kämpf");
        list.add("Doreen Kämpf");
        list.add("Silvio Bochmann");
 //       list.add("Eric");
 //       list.add("Peter");
        list.add("public");

        List<String> list2 = ContextModel.getListProjects();

        // Task Type List
        List<String> list3 = ContextModel.getListTaskTypes();

        // Wiki Server List
        List<String> list4 = ContextModel.getListWikiserver();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list2);

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list3);

        ArrayAdapter<String> dataAdapter4 = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list4);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        dataAdapter2.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        dataAdapter3.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        dataAdapter4.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        s1.setAdapter(dataAdapter);
        s2.setAdapter(dataAdapter2);
        s3.setAdapter(dataAdapter3);
        s4.setAdapter(dataAdapter4);

        // Spinner item selection Listener
        addListenerOnSpinnerItemSelection();

//        WebView wv = (WebView)findViewById(R.id.webView);
//        wv.setBackgroundColor(Color.TRANSPARENT );
//        wv.getSettings().setJavaScriptEnabled(true);
//        wv.loadUrl("http://www.semanpix.de/opendata/wiki");

    }

    // Add spinner data

    public void addListenerOnSpinnerItemSelection(){

        s1.setOnItemSelectedListener(new CustomItemSelectionListener("User: ", "user"));
        s2.setOnItemSelectedListener(new CustomItemSelectionListener("Project: ", "project"));
        s3.setOnItemSelectedListener(new CustomItemSelectionListener("Activity: ", "activity"));
        s4.setOnItemSelectedListener(new CustomItemSelectionListener("Wiki: ", "wiki"));

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
