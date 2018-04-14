package tk.samgrogan.celluloid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tk.samgrogan.celluloid.api.Api;
import tk.samgrogan.celluloid.api.MovieResult;
import tk.samgrogan.celluloid.api.PageResult;
import tk.samgrogan.celluloid.models.CardModel;

/**
 * An activity representing a list of movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link movieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class movieListActivity extends AppCompatActivity implements RecyclerViewClickListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Api service;
    private FireMovieAdapter mAdapter;
    private RecyclerView recyclerView;
    private List<MovieResult> mMovies = new ArrayList<>();
    private DatabaseReference reference;
    private List<CardModel> cards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        if (mAuth.getCurrentUser() != null){
            reference = mDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("movies");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        service = Util.getMovieService();

        recyclerView = findViewById(R.id.movie_card_view);

        GridLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new FireMovieAdapter(getApplicationContext(), mMovies, this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MetaGather.class);
                startActivity(intent);
            }
        });



        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MovieResult movieResult = dataSnapshot.getValue(MovieResult.class);
                mMovies.add(movieResult);
                recyclerView.setAdapter(mAdapter);
                System.out.println();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if (mAuth.getCurrentUser() != null){
            reference.addChildEventListener(childEventListener);
        }



    }


    @Override
    public void recyclerViewListClicked(View view, int position) {
        /*

        Intent intent = new Intent(getApplicationContext(),MoviePlayer.class);

        startActivity(intent);*/
        String movieSource = mMovies.get(position).getFilename();
        String movieOverview = mMovies.get(position).getOverview();
        String movieTitle = mMovies.get(position).getTitle();
        String movieDrop = mMovies.get(position).getBackdropPath();

        Intent intent = new Intent(getApplicationContext(), movieDetailActivity.class);
        intent.putExtra("SOURCE", movieSource);
        intent.putExtra("OVERVIEW", movieOverview);
        intent.putExtra("TITLE", movieTitle);
        intent.putExtra("BACKDROP", movieDrop);
        startActivity(intent);

    }


    public class MovieData extends AsyncTask<Void,Void, Void>{
        File folder;
        List<File> files = new ArrayList<>();

        private void checkFiles(File dir, List<File> files) {
            String extensionOne = ".mp4";
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isDirectory()) {
                        //if this is a directory, loop over the files in the directory
                        checkFiles(fileList[i], files);
                    } else {
                        if (fileList[i].getName().endsWith(extensionOne) ) {
                            //this is the file you want, do whatever with it here
                            files.add(fileList[i]);
                        }

                    }
                }
            }
        }


        @Override
        protected Void doInBackground(Void... voids) {
            folder = new File(Environment.getExternalStorageDirectory(), "/Movies");
            checkFiles(folder, files);


            for (int i = 0; i < files.size(); i++){
                File file = files.get(i);
                String path = file.getName();
                String withext = FilenameUtils.getName(path);
                String query = FilenameUtils.removeExtension(withext);
                service.search("0359c81bed7cce4e13cd5a744ea5cfbe", query).enqueue(new Callback<PageResult>() {
                    @Override
                    public void onResponse(Call<PageResult> call, Response<PageResult> response) {
                        System.out.println("test");
                        mMovies = response.body().getResults();
                        System.out.println(mMovies.get(0).getOverview());
                        //System.out.println(response.body().getMovies());

                    }

                    @Override
                    public void onFailure(Call<PageResult> call, Throwable t) {
                        System.out.println(t);

                    }
                });
            }

            return null;
        }
    }
}
