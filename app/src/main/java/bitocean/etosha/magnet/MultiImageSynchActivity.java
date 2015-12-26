package bitocean.etosha.magnet;


        import android.app.ProgressDialog;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Color;
        import android.graphics.Matrix;
        import android.os.Bundle;
        import android.app.Activity;
        import android.os.Handler;
        import android.util.Log;
        import android.view.Menu;
        import android.view.View;

        import android.widget.Button;
        import android.view.View.OnClickListener;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.File;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.Vector;

        import bitocean.etosha.magnet.datamodel.ContextModel;
        import bitocean.etosha.magnet.helper.SimpleFileDialog;
        import bitocean.etosha.magnet.semanpics.connector.SemanticGraphStore;
        import bitocean.etosha.magnet.semanpics.connector.impl.smw.MediaWikiService;
        import bitocean.etosha.magnet.semanpics.connector.impl.smw.SMWUser;
        import bitoceanug.etoshamagnet.R;

//import android.view.View;

public class MultiImageSynchActivity extends Activity implements SMWUser {


    String mCurrentPhotoPath;

    File photoFile = null;

    // REPLACE by
    // MediaWikiService wiki = null;
    SemanticGraphStore wiki = null;

    String url = null;

    // Android specific objects ...
    public static Context appContext = null;


    public static ImageView getmImageView() {
        return mImageView2;
    }

    public static ImageView mImageView2;

    Vector<File> files = new Vector<File>();


    ContextModel context = null;

    static SMWUser sMWUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        sMWUser = this;

        appContext= this.getApplicationContext();

        setContentView(R.layout.activity_main);

        mImageView2 = (ImageView) findViewById( R.id.iv2 );

        Log.i("PREP done ... >> ", String.valueOf(mImageView2));


        mImageView2.setBackgroundColor( Color.GREEN );


        context = new ContextModel();

        url = context.wikiserver;

        Log.v("SYNCH", "Setup the default wiki: " + url + " => " + appContext );

        wiki = new MediaWikiService( url, appContext );


        //Button1
        Button dirChooserButton1 = (Button) findViewById(R.id.button7);
        dirChooserButton1.setOnClickListener(new OnClickListener()
        {
            String m_chosen;
            @Override
            public void onClick(View v) {
                /////////////////////////////////////////////////////////////////////////////////////////////////
                //Create FileOpenDialog and register a callback
                /////////////////////////////////////////////////////////////////////////////////////////////////
                SimpleFileDialog FileOpenDialog =  new SimpleFileDialog(MultiImageSynchActivity.this, "FileOpen",
                        new SimpleFileDialog.SimpleFileDialogListener()
                        {
                            @Override
                            public void onChosenDir(String chosenDir)
                            {
                                // The code in this function will be executed when the dialog OK button is pushed
                                m_chosen = chosenDir;

                                files = new Vector<File>();

                                File dir = new File( m_chosen );
                                File childfile[] = dir .listFiles();

                                for (File file2 : childfile) {
                                    Log.i(">> Selected folder is : ", file2.getName());
                                    files.add( file2 );
                                }

                                Toast.makeText(MultiImageSynchActivity.this, "Selected folder: " +
                                        m_chosen + " " + childfile.length + " files.", Toast.LENGTH_LONG).show();

                                currentFoto=0;
                                _angle = 0;

                                showCurrentFoto();
                            }
                        });

                //You can change the default filename using the public variable "Default_File_Name"
                FileOpenDialog.Default_File_Name = "";
                FileOpenDialog.chooseFile_or_Dir();

                /////////////////////////////////////////////////////////////////////////////////////////////////

            }
        });

        //Button11
        Button rotateLeftButton = (Button) findViewById(R.id.button11);
        rotateLeftButton.setOnClickListener(new OnClickListener()
        {
            String m_chosen;
            @Override
            public void onClick(View v) {

                _angle = _angle - 90;
                setPic( _angle, true );

            }
        });

        //Button11
        Button nextButton = (Button) findViewById(R.id.button8);
        nextButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {


                nextFoto();

            }
        });




        //Button11
        Button previousButton = (Button) findViewById(R.id.button9);
        previousButton.setOnClickListener(new OnClickListener()
        {
            String m_chosen;
            @Override
            public void onClick(View v) {


                previousFoto();

            }
        });


        //Button10
        Button synchButton = (Button) findViewById(R.id.button10);
        synchButton.setOnClickListener(new OnClickListener() {
            String m_chosen;

            @Override
            public void onClick(View v) {

                try {

                        linkToSMW( sMWUser );

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        //Button12
        Button rotateRightButton = (Button) findViewById(R.id.button12);
        rotateRightButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {

                _angle = _angle + 90;
                setPic( _angle, true );


            }
        });






    }










    // show progress during Upload to Media Wiki ...
    public static ProgressDialog barProgressDialog2;
    Handler updateBarHandler2;

    private void linkToSMW( final SMWUser u ) {

        image = new File( mCurrentPhotoPath );

        Log.i("***** file name is: ", image.getName() + "   " + image.length() );

        barProgressDialog2 = new ProgressDialog(MultiImageSynchActivity.this);

        barProgressDialog2.setTitle("Link and Upload Image ...");
        barProgressDialog2.setMessage("Upload in progress ...");
        barProgressDialog2.setProgressStyle(barProgressDialog2.STYLE_HORIZONTAL);
        barProgressDialog2.setProgress(0);
        barProgressDialog2.setMax(100);
        barProgressDialog2.show();

        barProgressDialog2.setProgress( 5 );

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    //while (barProgressDialog2.getProgress() <= barProgressDialog2.getMax()) {

                    //Thread.sleep(2000);

               //     updateBarHandler2.post(new Runnable() {

          //              public void run() {

                            barProgressDialog2.setProgress( 20 );

                            /**
                             *   BEGIN
                             */
                            // works with fetch ...
                            try {
                                String page = context.getCurrentContext() + "_LATEST";

                                // APPEND funktioniert hier im Moment nicht, weil
                                // ein Asynchroner HTTP Request benutzt wird.

                                // String ct = wiki.getPageText( page );

                                Log.i(">>> File name is: ", image.getName());
                                wiki.login( "kamir", "8cwrr".toCharArray() );
                                barProgressDialog2.setProgress( 50 );

                                String text = pagetext( image );
                                Log.i(">>> Page text is: ", text );
                                wiki.edit(page, text, "...");
                                Log.i(">>> Page was updated ... ", page);
                                barProgressDialog2.setProgress(80);

                                wiki.POST2Wiki(wiki.getAPIUrl(), image, "CONTENTS.data", "REASON.data", appContext, u);

                                Log.i(">>> Image was uploaded ... ", image.getAbsolutePath());
                                barProgressDialog2.setProgress(90);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }


                            /**
                             *   ENDE
                             */


          //              }

            //        });

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

    private String pagetext(File bitmap) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String date = format.format( new Date( System.currentTimeMillis() ));
        String pageText = "  " + date + " : Linked note : [[File:" + bitmap.getName() + "|320px]]";

        return pageText;
    }


    void nextFoto() {

        currentFoto = currentFoto + 1;
        fixId();
    }

    void previousFoto() {

        currentFoto = currentFoto - 1;
        fixId();

    }

    void fixId() {

        int currentN = currentFoto;

        if( currentFoto > files.size() ) currentFoto = 0;
        if( currentFoto < 0 ) currentFoto = files.size() - 1;

        int nextN = currentFoto;

        Log.v("CHANGE FOTO: (" + currentN + " >>> " + nextN +")", "OK");

        showCurrentFoto();
    }

    int currentFoto = 0;

    public void showCurrentFoto() {

        File f = files.elementAt( currentFoto );

        // Log.i("PREP done ... ("+ currentFoto +") " + f.length() + " : ", f.getName());

        TextView textView = (TextView) findViewById( R.id.textView7 );
        textView.setText( f.getName() );

        mCurrentPhotoPath = f.getAbsolutePath();

        // ping();

        setPic( currentFoto, false );

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
        if ( p ) {

            CharSequence seq = "Semantic Media Wiki is OK ...";

            Toast.makeText(getApplicationContext(), seq, Toast.LENGTH_LONG).show();
        }
        else {

            CharSequence seq = "Semantic Media Wiki is not available !";

            Toast.makeText(getApplicationContext(), seq, Toast.LENGTH_LONG).show();
        }
        return p;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    /**
     *
     */
    int _angle = 0;
    private void setPic(int angle, boolean rotate ) {

        // Get the dimensions of the View
        int targetW = mImageView2.getWidth();
        int targetH = mImageView2.getHeight();

        Log.i("SYNCH", "width  => " + targetW);
        Log.i("SYNCH", "heigth => " + targetH);

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

        Log.i("SYNCH", "path => " + mCurrentPhotoPath);

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        if ( rotate ) {
            Matrix mtx = new Matrix();
            mtx.postRotate(angle);
            // Rotating Bitmap
            Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

            if (rotatedBMP != bitmap)
                bitmap.recycle();

            mImageView2.setImageBitmap(rotatedBMP);

            Log.i("SYNCH", "image ready ... ");
        }
        else {
            if ( bitmap == null ) return;
            mImageView2.setImageBitmap(bitmap);

        }
    }

    File image = null;

    @Override
    public void notifyUploadsOK() {
        Log.i("SMWUser", "Uploads OK ... " );
        barProgressDialog2.setProgress(100);

    }

    @Override
    public void closeProgressDialog() {
        Log.i("SMWUser", "Close ProgressBar ... " );

        barProgressDialog2.dismiss();


        // Execute some code after 2 seconds have passed
  /*     Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                barProgressDialog2.dismiss();
            }
        }, 2000);
  */
    }

}
