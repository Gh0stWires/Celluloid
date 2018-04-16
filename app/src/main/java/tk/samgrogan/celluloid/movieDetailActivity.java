package tk.samgrogan.celluloid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    private String source;
    private String title;
    private String filePath;
    private DatabaseReference reference;
    private Button fileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent passedData = getIntent();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        if (mAuth.getCurrentUser() != null){
            reference = mDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("movies");
        }

        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitle(passedData.getStringExtra("TITLE"));
        setSupportActionBar(toolbar);

        TextView overview = findViewById(R.id.overview);
        ImageView backDrop = findViewById(R.id.backdrop);
        ImageButton play = findViewById(R.id.play_btn);
        fileBtn = findViewById(R.id.add_file);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        String baseurl = "http://image.tmdb.org/t/p/w780";
        String fullUrl = baseurl + passedData.getStringExtra("BACKDROP");
        title = passedData.getStringExtra("TITLE");

        overview.setText(passedData.getStringExtra("OVERVIEW"));
        Picasso.with(getApplicationContext()).load(fullUrl).into(backDrop);
        source = passedData.getStringExtra("SOURCE");

        if(source == null){
            play.setVisibility(View.INVISIBLE);
            fileBtn.setText("Add File Location");
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MoviePlayer.class);
                intent.putExtra("SOURCE", source);
                startActivity(intent);
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

    public void startPlayback(View view) {
        Intent intent = new Intent(getApplicationContext(), MoviePlayer.class);
        intent.putExtra("SOURCE", source);
        intent.putExtra("TITLE", title);
        startActivity(intent);
    }

    public void findFile(View view) {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1)
                .withFilter(Pattern.compile(".*\\.mp4$")) // Filtering files and directories by file name using regexp
                // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            reference.child(title).child("filename").setValue(filePath);
            fileBtn.setText("Change File Location");


            // Do anything with file
            System.out.println(filePath);
        }
    }
}



