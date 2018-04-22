package tk.samgrogan.celluloid;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

/**
 * An activity representing a single movie detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link movieListActivity}.
 */
public class movieDetailActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 123;
    private String source;
    private String title;
    private String filePath;
    private ImageButton play;
    private DatabaseReference reference;
    private PackageManager packageManager;
    private Button fileBtn;
    private boolean playerSetting;
    private boolean check = true;
    private long position = 0;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent passedData = getIntent();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        permCheck();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        playerSetting = preferences.getBoolean("player_pref", true);


        packageManager = getPackageManager();

        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitle(passedData.getStringExtra("TITLE"));
        setSupportActionBar(toolbar);

        TextView overview = findViewById(R.id.overview);
        ImageView backDrop = findViewById(R.id.backdrop);
        play = findViewById(R.id.play_btn);
        fileBtn = findViewById(R.id.add_file);



        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        String baseurl = "http://image.tmdb.org/t/p/w780";
        String fullUrl = baseurl + passedData.getStringExtra("BACKDROP");
        title = passedData.getStringExtra("TITLE");
        position = passedData.getLongExtra("TIME", 0);
        play.setVisibility(View.INVISIBLE);

        if (mAuth.getCurrentUser() != null){
            reference = mDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("movies");
        }



        overview.setText(passedData.getStringExtra("OVERVIEW"));
        Picasso.with(getApplicationContext()).load(fullUrl).into(backDrop);
        source = passedData.getStringExtra("SOURCE");

        if(source != null || filePath != null){
            play.setVisibility(View.VISIBLE);
            fileBtn.setText("Change File Location");

        }else{
            fileBtn.setText("Add File Location");
        }



        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check){
                    findFile();
                }else{
                    reRequest();
                }

            }
        });


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //

        }

    public void permCheck() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case PERMISSION_CODE:{

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    check = true;
                }else {
                    check = false;
                }
                return;

            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        playerSetting = preferences.getBoolean("player_pref", true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        if (item.getItemId() == R.id.settings){
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    public void startPlayback(View view) {
        if (playerSetting) {
            if (isPackageInstalled("org.videolan.vlc", packageManager)) {
                int vlcRequestCode = 42;
                Uri uri = Uri.parse(source);
                Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
                vlcIntent.setPackage("org.videolan.vlc");
                vlcIntent.setDataAndNormalize(uri);
                vlcIntent.putExtra("title", title);
                vlcIntent.putExtra("from_start", false);
                vlcIntent.putExtra("position", position);
                vlcIntent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
                startActivityForResult(vlcIntent, vlcRequestCode);


            }
        }

        else {
            Intent intent = new Intent(getApplicationContext(), MoviePlayer.class);
            if (source != null) {
                intent.putExtra("SOURCE", source);
            } else {
                intent.putExtra("SOURCE", filePath);
            }
            intent.putExtra("TITLE", title);
            startActivity(intent);
        }
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void findFile() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1)
                .withFilter(Pattern.compile(".*\\.mp4$")) // Filtering files and directories by file name using regexp
                // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();

    }

    public void reRequest(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            reference.child(title).child("filename").setValue(filePath);
            fileBtn.setText("Change File Location");
            play.setVisibility(View.VISIBLE);



            // Do anything with file
            System.out.println(filePath);
        }

        if (requestCode == 42 && resultCode == RESULT_OK){
            position = data.getLongExtra("extra_position", 0l);
            reference.child(title).child("movieTime").setValue(data.getLongExtra("extra_position", 0l));
        }
    }
}



