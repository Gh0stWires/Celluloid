package tk.samgrogan.celluloid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * An activity representing a single movie detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link movieListActivity}.
 */
public class movieDetailActivity extends AppCompatActivity {

    private String source;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent passedData = getIntent();

        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitle(passedData.getStringExtra("TITLE"));
        setSupportActionBar(toolbar);

        TextView overview = findViewById(R.id.overview);
        ImageView backDrop = findViewById(R.id.backdrop);
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
}



