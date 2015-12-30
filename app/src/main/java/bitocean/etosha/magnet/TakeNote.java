package bitocean.etosha.magnet;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import bitocean.etosha.magnet.datamodel.ContextModel;
import bitocean.etosha.magnet.datamodel.POI;
import bitocean.etosha.magnet.helper.LatLonConvert;
import bitocean.etosha.magnet.semanpics.connector.SemanticGraphStore;
import bitocean.etosha.magnet.semanpics.connector.impl.smw.MediaWikiService;
import bitocean.etosha.magnet.semanpics.connector.impl.smw.SMWUser;
import bitocean.etosha.magnet.subactivities.MapsActivity;
import bitocean.etosha.magnet.subactivities.SettingsActivity;
import bitoceanug.etoshamagnet.R;


import static android.location.Criteria.ACCURACY_FINE;


public class TakeNote extends ActionBarActivity implements SMWUser, LocationListener {

    // Used for Debugging
    private static final String TAG = "[TakeNote]";


    // Feedback from SMWBridge comes in here
    private static SMWUser sMWUser = null;

    // This is the core element of the App
    public static ContextModel context = null;

    // the current image must exist as a file
    String mCurrentPhotoPath;
    File photoFile = null;

    // A connector for storing Wiki-pages
    SemanticGraphStore wiki = null;

    // This is the WIKI-URL we use
    String url = null;

    // Android specific object ...
    public static Context appContext = null;

    public static ImageView getmImageView() {
        return mImageView;
    }

    public static ImageView mImageView;
    private static TextView mTextView2;

    // here we show the position in the graph - graph coordinates ...
    private TextView textView;

    private Switch sw1 = null;
    private Switch sw2 = null;

    // show progress during Upload to FPS ...
    public static ProgressDialog barProgressDialog;
    Handler updateBarHandler;

    // show progress during Upload to Media Wiki ...
    public static ProgressDialog barProgressDialog2;
    Handler updateBarHandler2;

    private static TakeNote activity = null;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activity = this;

        sMWUser = this;


        this.updateBarHandler = new Handler();
        this.updateBarHandler2 = new Handler();

        super.onCreate(savedInstanceState);





        context = new ContextModel();
        url = context.wikiserver;

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        appContext= this.getApplicationContext();




        setContentView(R.layout.activity_take_note);

        mImageView = (ImageView) findViewById( R.id.imageView );
        mTextView2 = (TextView) findViewById( R.id.textView2 );
        textView = (TextView) findViewById( R.id.textView );

//        sw1 = (Switch) findViewById( R.id.switch1 );
//        sw2 = (Switch) findViewById( R.id.switch2 );

        mImageView.setBackgroundColor( Color.TRANSPARENT );
        setImageBorderColor( Color.GREEN );

        mImageView.setOnLongClickListener(
                new View.OnLongClickListener() {
                                              public boolean onLongClick(View v) {
                                                  Toast.makeText(v.getContext(), "My tool-tip text", Toast.LENGTH_SHORT).show();
                                                  return true;
                                              }
                                          }
        );

        Log.i(TAG, "Setup the default wiki: " + url);

        wiki = new MediaWikiService( url, TakeNote.appContext );

        /**
         * Take Note Button
         */
        ImageButton tn = (ImageButton) findViewById(R.id.button);
        tn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                takePhoto();


//                AlertDialog alertDialog = new AlertDialog.Builder( activity ) .create(); //Read Update
//                alertDialog.setTitle("Hi,");
//                alertDialog.setMessage("I take a note for you!");
//
//                alertDialog.setButton("Continue ...", new DialogInterface.OnClickListener() {
//                     public void onClick(DialogInterface dialog, int which) {
//
//                         takePhoto();
//
//                    }
//                });
//
//                alertDialog.show();
            }

        });

        /**
         * Create New Button
         */
        ImageButton btnF = (ImageButton) findViewById(R.id.button6);

        btnF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                openContextSelector2();
            }
        });


        /**
         * Clear Button
         */
        ImageButton btn = (ImageButton) findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder( activity ) .create(); //Read Update
                alertDialog.setTitle("Aufräumen");
                alertDialog.setMessage("Stelle den Ausgangszustand her und prüfe den Webserver.");

                alertDialog.setButton("Continue ...", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            clearImage();
                            TakeNote.context.setCatNode( false );
                        }
                        catch (Exception ex) {

                        }

                    }
                });

                alertDialog.show();
            }

        });

        /**
         * Upload FPS Button
         */
        ImageButton btn4 = (ImageButton) findViewById(R.id.button4);

        btn4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                uploadImageToFPS();

            }

        });

        /**
         * Select Context Button
         */
        ImageButton btn3 = (ImageButton) findViewById(R.id.button3);

        btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                _openContextSelector();

            }

        });



        /**
         * Upload SMW Button
         */
        ImageButton btn5 = (ImageButton) findViewById(R.id.button5);

        btn5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                _linkToSMW(sMWUser);

            }

        });



        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Define the criteria how to select the location provider
        criteria = new Criteria();
        criteria.setAccuracy(ACCURACY_FINE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        latitude = new TextView(appContext);
        longitude = new TextView(appContext);




        //startService(new Intent(this, AutoUpdateService.class));

        disableUploadButtons();

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                //handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }

        updateContextInfo();

        ping();

    }


    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";

    private String _saveImageLocally(Bitmap _bitmap, File f) {
        try {
            FileOutputStream out = new FileOutputStream(f);
            _bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);

            System.out.println( ">>> SAVED file : " + f.toString() );
            System.out.println( ">   size       : " + f.length() );

            out.flush();
            out.close();

        }
        catch (Exception e) {
            // handle exception
        }

        return f.getAbsolutePath();
    }

    /**
     *
     *
     * @param intent
     */
    void handleSendImage(Intent intent) {

        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (imageUri != null) {

            // Update UI to reflect image being shared

            try {

                _setPic(imageUri);


            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void openBrowser( String url) {

        if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
            url = HTTP + url;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        startActivity( Intent.createChooser(intent, "Chose browser") );

    }

    private void createNewPixnodeInCurrentContext() {

        TakeNote.context.setCatNode( true );

        refreshGUIStatus();

    }


    /**
     * We have to ping the systems we plan to use.
     * Only if all are ready we go on. Otherwise,
     * we notify the user with a color indicator.
     *
     * @return
     */
    public boolean ping() {
        boolean p = wiki.ping();
        if ( p )
            notifyUploadsOK();
        else {
            notifyConnectivityProblem();
        }
        return p;
    }

    private void updateContextInfo() {

        mTextView2.setText( url + "\n[[Activity-Type:" + context.getCurrentContext() + "]] [[Project:" + this.context.getCurrentProject() + "]][[User:" + this.context.getCurrentUser() + "]]"  );

        notifyQueryOK();

    }

    public void notifyStartUploading() {
        mTextView2.setBackgroundColor( state_Uploading );
    }

    public void notifyUploadsOK() { mTextView2.setBackgroundColor( state_READY_TO_Snap ); }

    @Override
    public void closeProgressDialog() {
        barProgressDialog2.dismiss();
    }

    public void notifyConnectivityProblem() { mTextView2.setBackgroundColor( state_Error ); }

    public void notifyStartQuery() {
        mTextView2.setBackgroundColor( state_Processing_Query );
    }

    public void notifyQueryOK() {
        mTextView2.setBackgroundColor( state_READY_TO_Snap );
    }

    static int state_READY_TO_Snap = Color.GREEN;
    static int state_READY_TO_Upload = Color.MAGENTA;
    static int state_Uploading = Color.YELLOW;
    static int state_Processing_Query = Color.MAGENTA;
    static int state_Error = Color.RED;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_note, menu);
        return super.onCreateOptionsMenu(menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
  //      if (id == R.id.action_settings) {
  //          return true;
  //      }

        switch (item.getItemId()) {
            case R.id.button2:
                clearImage();
                return true;

            case R.id.button3:
                _openContextSelector();
                return true;
            case R.id.action_context:
                selectActionContext();
                return true;
            case R.id.action_settings:
                modifyContextModel();
                return true;
            case R.id.action_vl:
                viewRecentChanges();
                return true;
            // https://github.com/kamir/mag.net/wiki/MAG.net---Quickstart-Guide
            case R.id.action_help:
                viewHelpInWiki();
                return true;

//            case R.id.button4:
//                uploadImageToFPS();
//                return true;
//            case R.id.button5:
//                linkToSMW();
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewRecentChanges() {

        String urlRC = wiki.getUriRecentChanges();

        openBrowser( urlRC );

    }

    private void viewHelpInWiki() {

        String urlRC = "https://github.com/kamir/mag.net/wiki/MAG.net---Quickstart-Guide";

        openBrowser( urlRC );

    }


    private void modifyContextModel() {

        Intent intent;
        intent = new Intent( this, SettingsActivity.class);
        startActivity(intent);

    }

    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;

    static private TextView latitude;
    static private TextView longitude;
    private TextView provText;

    Location ll = null;
    @Override
    public void onLocationChanged(Location location) {

        ll = location;

        latitude.setText( "" + location.getLatitude() );
        longitude.setText( "" + location.getLongitude() );

        textView.setText( LatLonConvert.getLabels(location) );

        context.onLocationChanged( location );
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

        textView.setText( "Use Provider: " + provider);

        if ( ll != null ) {

            latitude.setText("" + ll.getLatitude());
            longitude.setText("" + ll.getLongitude());

            textView.setText(LatLonConvert.getLabels(ll));
        }

        context.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        textView.setText( "No LOCATION Provider.");
        context.onProviderDisabled( provider );
    }

    /**
     * The context-bar is on lower border of the screen and show the current context.
     * Especially, user, Project and activity type ar shown.
     */
    public static void refreshContextBar() {

        // in simple text mode we can use the simple version
        activity.updateContextInfo();



    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // Initialize the location fields
            latitude.setText("Latitude: "+String.valueOf(location.getLatitude()));
            longitude.setText("Longitude: "+String.valueOf(location.getLongitude()));
            provText.setText(provider + " provider has been selected.");

            Toast.makeText(TakeNote.this,  "Location changed!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(TakeNote.this, provider + "'s status changed to "+status +"!",
                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(TakeNote.this, "Provider " + provider + " enabled!",
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(TakeNote.this, "Provider " + provider + " disabled!",
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * The TakeNote tool knows the current location of our activity.
     *
     * @return
     */
    public static POI getContextualLocation() {
        POI here = context.getGeoContext();
        return here;
    }

    private void selectActionContext() {

        Intent intent;
        intent = new Intent( this, MapsActivity.class);
        startActivity(intent);

    }

    boolean status = false;
    private void toggleStatus() {
        status = !status;
        if( status ) notifyConnectivityProblem();
        else notifyUploadsOK();
    }


    private void simplyClearImage() {

        setImageBorderColor(Color.TRANSPARENT);

        disableUploadButtons();

    }

    private void clearImage() {

        this.mImageView.setImageResource(android.R.color.transparent);
        this.mImageView.setBackgroundColor(Color.TRANSPARENT);

        setImageBorderColor(Color.TRANSPARENT);

        /**
         * Show the content in the next neighborhood in Google Maps.
         */

//        String label = "bitOcean UG";
//        String uriBegin = "geo:51.32,11.93";
//        String query = "51.32,11.93(" + label + ")";
//        String encodedQuery = Uri.encode( query  );
//        String uriString = uriBegin + "?q=" + encodedQuery;
//        Uri uri = Uri.parse( uriString );
//        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri );
//        startActivity( intent );

        // and now do a ping() for fun ...
        if ( ping() ) this.notifyUploadsOK();
        else this.notifyConnectivityProblem();

        TakeNote.context.setCatNode( false );

        refreshGUIStatus();

        disableUploadButtons();


    }

    private void setImageBorderColor(int color) {

        mImageView.setBackgroundColor( Color.BLACK );

    }



    private void _openContextSelector() {

        notifyStartQuery();

        final EditText myEditText = new EditText(this);
        myEditText.setText(  TakeNote.context.getCurrentContext() );
        AlertDialog alertDialog = new AlertDialog.Builder( activity ) .create(); //Read Update
        alertDialog.setTitle("Context Selection");
        alertDialog.setMessage("I take a note for you!");
        alertDialog.setView( myEditText );

        alertDialog.setButton("Continue ...", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                try {

                    TakeNote.context.setCurrentProject( myEditText.getText().toString() );

                    updateContextInfo();

                }
                catch (Exception ex) {

                }

            }
        });

        alertDialog.show();

    }


    private void openContextSelector2() {

        notifyStartQuery();

        final EditText myEditText = new EditText(this);
        myEditText.setText(  ""  );
        AlertDialog alertDialog = new AlertDialog.Builder( activity ) .create(); //Read Update
        alertDialog.setTitle("Neuer Projekt-Ordner ...");
        alertDialog.setMessage("Erfasse das Kennzeichen oder einen Namen!");
        alertDialog.setView( myEditText );

        alertDialog.setButton("Weiter >>", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                try {


//                    TakeNote.context.setCurrentProject( myEditText.getText().toString() );
// auch das Neue Projekt in der Liste anzeigen und auswhlen
                    TakeNote.context._addNewProject( myEditText.getText().toString() );
                    Log.i(TAG, " define new project: " + myEditText.getText().toString() );


                    updateContextInfo();

                    try {

                        simplyClearImage();

                        createNewPixnodeInCurrentContext();

                        refreshGUIStatus();

                    }
                    catch (Exception ex) {

                    }

                }
                catch (Exception ex) {

                }

            }
        });

        alertDialog.show();
    }





    private void takePhoto() {

        notifyStartQuery();

        dispatchTakePictureIntent();

    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = _createImageFile();
            }
            catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {



                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "onActivityResult: " + this);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {

            _setPic();

        }
    }

    /**
     * http://developer.android.com/training/camera/photobasics.html
     */
    private File _createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_" + context.getCurentPageName();

        String storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/etosha.magnet";

        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        image = new File(storageDir + "/" + imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.i(TAG, "photo path = " + mCurrentPhotoPath);

        return image;
    }

    File image = null;

    private void uploadImageToFPS() {

        try {


            /**
             * NOTE: FPS
             *
             *    Here we have to ping the FPS Service and not the SMW!!!
             *
             */
            // if ( pingFPS() ) {
            if ( ping() ) {

                notifyStartUploading();

                Log.i(TAG, "image path => " + image.getName() + " - " + image.exists() );

                sendPhotoFile(image);

            }
            else {

                CharSequence seq = "Semantic Media Wiki: " + wiki.getDomain() + " is not available ...";
                Toast.makeText( getApplicationContext() , seq , Toast.LENGTH_LONG).show();

            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     *   Traditional approach
     */
    private void _setPic() {

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        Log.i(TAG, "width  => " + targetW);
        Log.i(TAG, "heigth => " + targetH);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor << 1;
        bmOptions.inPurgeable = true;

        Log.i(TAG, "path => " + mCurrentPhotoPath);

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        Matrix mtx = new Matrix();
        mtx.postRotate(90);
        // Rotating Bitmap
        Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

        if (rotatedBMP != bitmap)
            bitmap.recycle();

        mImageView.setImageBitmap(rotatedBMP);

        notifyQueryOK();

        enableUploadButtons();
    }

    /**
     *   Traditional approach
     */
    private void _setPic(Uri imageUri) throws FileNotFoundException, IOException {

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        Log.i(TAG, "width  => " + targetW);
        Log.i(TAG, "heigth => " + targetH);


        InputStream in = getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(in);



        Matrix mtx = new Matrix();
//        mtx.postRotate(90);

        // Rotating Bitmap
        Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

        File f = _createImageFile();
        _saveImageLocally( rotatedBMP , f );


        if (rotatedBMP != bitmap)
            bitmap.recycle();

        mImageView.setImageBitmap(rotatedBMP);

        notifyQueryOK();

        enableUploadButtons();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void refreshGUIStatus() {

        ImageButton btn6 = (ImageButton) findViewById(R.id.button6);

        if( TakeNote.context.getCATNODE() ) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.folder250a);
            Drawable d = new BitmapDrawable(getResources(), bm);
            btn6.setBackground( d );
            btn6.setEnabled( false );
        }
        else {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.folder250);
            Drawable d = new BitmapDrawable(getResources(), bm);
            btn6.setBackground( d );
            btn6.setEnabled( true );
        }

    }



    public void callbackWithFilename( String chosenDir ) {

        Log.i(TAG, "### Selected: " + chosenDir );

    }



    void enableUploadButtons() {
        ImageButton btn4 = (ImageButton) findViewById(R.id.button4);
        ImageButton btn5 = (ImageButton) findViewById(R.id.button5);
        btn4.setEnabled( true );
        btn5.setEnabled( true );
    }

    void disableUploadButtons() {

        ImageButton btn4 = (ImageButton) findViewById(R.id.button4);
        ImageButton btn5 = (ImageButton) findViewById(R.id.button5);

        btn4.setEnabled(false);
        btn5.setEnabled( false );


    }



    /*
    private void __sendPhoto(Bitmap bitmap) throws Exception {
        new UploadTask().execute(bitmap);
    }
    */

    static File _imageToUpload = null;
    private void sendPhotoFile(File image) throws Exception {


//        if ( !sw1.isChecked() ) return;

        _imageToUpload = image;

        barProgressDialog = new ProgressDialog(TakeNote.this);

        barProgressDialog.setTitle("Upload Image to FPS ...");
        barProgressDialog.setMessage("Upload in progress ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);
        barProgressDialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

 //                   while (barProgressDialog.getProgress() <= barProgressDialog.getMax()) {

                        updateBarHandler.post(new Runnable() {

                            public void run() {

                                barProgressDialog.incrementProgressBy(2);

                                uploadImageToFileProcessingService(_imageToUpload);

                            }

                        });

//                        if (barProgressDialog.getProgress() == barProgressDialog.getMax()) {
//
//                            barProgressDialog.dismiss();
//
//                        }
   //                 }
                }
                catch (Exception e) {

                }
            }
        }).start();



        //// new UploadTaskFile().execute(image);
        // uploadAndLinkImage(image);
    }

    private void uploadImageToFileProcessingService( File bitmap ) {

        uploadedBitmapFile = bitmap;

        //Toast.makeText( getApplicationContext() , bitmap.getAbsolutePath() , Toast.LENGTH_LONG).show();
        Log.i(TAG, "FILE-Upload-Task: " +  bitmap.getAbsolutePath() + "   isReadable:" + bitmap.canRead() );

        Log.i("POST.F ####### ", "***** 1" );

        InputStream inputStream = null;
        String result = "";


        // create HttpClient
        HttpClient httpclient = new DefaultHttpClient();



        barProgressDialog.setProgress(25);

        Log.i("POST.F ####### ", "***** 2" );

        AsyncHttpClient myClient = new AsyncHttpClient();

        try {

            Log.i("POST.F ####### ", "***** 3" );

            RequestParams params = new RequestParams();

            params.put("userfile", bitmap);


            /**
             *
             * NOTE: FPS
             * ---------
             *
             * This is the upload procedure, which copies the image to the File Processing Service.
             *
             * On the server side, we us a PHP script. It is located in the scripts folder in the
             * backend2 module.
             *
             * In Local-Network-Mode we only interact with a MAG.net server inside the locale
             * environment, no public serves are used in this mode.
             *
             */
            String url = "http://www.semanpix.de/test/php/test.php";
            // String urlLOCALMODE = "http://" + SMWSeverIP + "/magnet/fps/fileUpload.php";

            Log.i("POST.F ####### ", "***** 4 " + url );

            myClient.post(url, params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"

                    barProgressDialog.setProgress(100);

                    Log.i("POST.F ####### ", "***** 5 >> " + response.length);

                    String s = new String(response);
                    Log.i("RESPONSE : ", s);

                    barProgressDialog.dismiss();

                    CharSequence seq = "FPS Server upload done ... ";
                    Toast.makeText( getApplicationContext() , seq , Toast.LENGTH_LONG).show();

                    notifyUploadsOK();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }

            });


        }
        catch(Exception e) {

            Log.i(TAG, "ERROR : " + e.getMessage() );


        }





    }

    /**
     *  In this method we create the WIKI page content. Later we need some better
     *  template based MarkDown rendering for the context.
     *
     *  Let's see how it works !!!
     */
    private String _pagetext(File bitmap) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.i(TAG, "File: (bitmap) " + bitmap );

        String date = format.format( new Date( System.currentTimeMillis() ));
        String pageText = "[[snaptime::" + date + "]]<br/>\n Linked PixNode:<br/>\n" +
                " [[File:" + bitmap.getName() + "|320px]]";

        if ( context.getCATNODE() ) {
            
            pageText = pageText + "\n" +  "<br/>[[projectStart::" + date + "| Projektstart:" + date +"]]";
            pageText = pageText + "\n" +  "[[Category:CATNODE]]";
            pageText = pageText  + "\n" +
                       "----\n" +
                       "{{#ask: [[belongsToProject::{{PAGENAME}}]]\n" +
                       "| format=table\n" +
                       "}}\n" +
                       "----";
        }

        if ( isPOI() ) {

            // here we take a default String or the real location ... as POI.
            pageText = pageText + "\n" + context.getGeoContextAnnotation().getLabel();
            pageText = pageText + "\n" +  "[[Category:POI]]";
        }

        pageText = pageText + "\n" +  "[[modelVersion::"+ ContextModel.modelVersion +"| ]]";
        pageText = pageText + "\n" +  "[[isOwnedBy::"+ context.getCurrentUser() +"| ]]";
        pageText = pageText + "\n" +  "[[belongsToProject::"+ context.getCurrentProject() +"| ]]";

        pageText = pageText + "\n" +  "__SHOWFACTBOX__";

        return pageText;
    }

    private boolean isPOI() {
        CheckBox cb = (CheckBox) findViewById( R.id.isPOIButton );
        return cb.isChecked();
    }


    private class UploadTask extends AsyncTask<Bitmap, Void, Void> {

        protected Void doInBackground(Bitmap... bitmaps) {
            if (bitmaps[0] == null)
                return null;
            setProgress(0);

            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream

            DefaultHttpClient httpclient = new DefaultHttpClient();

            try {

                HttpPost httppost = new HttpPost(
                        "http://semanpix.de/test/php/test.php"); // server

                MultipartEntity reqEntity = new MultipartEntity();

                httppost.setHeader("Content-Type", "multipart/form-data");

                ContentBody imageFile;
                imageFile = new InputStreamBody(  in, System.currentTimeMillis() + ".jpg"  );

                reqEntity.addPart("userfile", imageFile );

                httppost.setEntity(reqEntity );

                Log.i(TAG, "request " + httppost.getRequestLine());

                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);


                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (response != null)
                        Log.i(TAG, "response " + response.getStatusLine().toString());
                        Log.i(TAG, "response " + response.toString() );

                } finally {

                }
            } finally {

            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            CharSequence seq = "Upload was done ...";
            Toast.makeText( getApplicationContext() , seq , Toast.LENGTH_LONG).show();


        }
    }

    private void _linkToSMW( final SMWUser u) {

//                    if ( sw2.isChecked() ) {
//                        return;
//                    }

        if( barProgressDialog != null ) barProgressDialog.dismiss();


        barProgressDialog2 = new ProgressDialog(TakeNote.this);

        barProgressDialog2.setTitle("Link and Upload Image ...");
        barProgressDialog2.setMessage("Upload in progress ...");
        barProgressDialog2.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog2.setProgress(0);
        barProgressDialog2.setMax(20);
        barProgressDialog2.show();

        notifyStartUploading();

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    //while (barProgressDialog2.getProgress() <= barProgressDialog2.getMax()) {

                        //Thread.sleep(2000);

                        updateBarHandler2.post(new Runnable() {

                            public void run() {

                                barProgressDialog2.setProgress( 10 );

                                /**
                                 *   BEGIN
                                 */
                                // works with fetch ...
                                try {

                                    String page = context.getDefaultPageName() + "_LATEST";
                                    String note = "page added ...";

                                    if ( context.getCATNODE() ) {
                                        page = context.getCurrentProject();
                                        note = "Projekt erstellt.";
                                    }

                                    // APPEND funktioniert hier im Moment nicht, weil
                                    // ein Asynchroner HTTP Request benutzt wird.

                                    // String ct = wiki.getPageText( page );

                                    wiki.login( "kamir", "8cwrr".toCharArray() );

                                    barProgressDialog2.setProgress( 20 );

                                    String text = _pagetext( uploadedBitmapFile );

                                    wiki.edit( page , text, note );

                                    barProgressDialog2.setProgress( 30 );

                                    wiki.POST2Wiki(wiki.getAPIUrl(), uploadedBitmapFile, "CONTENTS.data", "REASON.data", TakeNote.appContext, u);

                                    barProgressDialog2.setProgress( 40 );


                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }


                                /**
                                 *   ENDE
                                 */


                            }

                        });

                        if (barProgressDialog2.getProgress() == barProgressDialog2.getMax()) {

                            barProgressDialog2.dismiss();

                        }
                    //}
                }
                catch (Exception e) {
                }
            }
        }).start();









    }



    /**
     *
     * Instead of a ContetnBody, created from a Stream we use the File to
     * construct the HTTPRequests parts.
     *
     */
    File uploadedBitmapFile = null;
    private class UploadTaskFile2 extends AsyncTask<File, Void, Void> {

        protected Void doInBackground(File... files) {
            if (files[0] == null)
                return null;
            setProgress(0);

            // ...

            return null;
        }

        private String _pagetext(File bitmap) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String date = format.format( new Date( System.currentTimeMillis() ));
            String pageText = "  " + date + " : Linked note : [[File:" + bitmap.toString() + "]]";

            return pageText;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            CharSequence seq = "File upload was a success!";
            Toast.makeText( getApplicationContext() , seq , Toast.LENGTH_LONG).show();
        }


    }

}
