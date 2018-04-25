package tk.samgrogan.celluloid;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tk.samgrogan.celluloid.api.Api;
import tk.samgrogan.celluloid.api.MovieResult;
import tk.samgrogan.celluloid.api.PageResult;

public class MetaBrowse extends AppCompatActivity implements RecyclerViewClickListener{

    private List<MovieResult> mMovies = new ArrayList<>();
    private Api service;
    private String searchEntry;
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private BrowseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent passData = getIntent();
        setContentView(R.layout.activity_meta_browse);
        service = Util.getMovieService();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        if (mAuth.getCurrentUser() != null){
            reference = mDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("movies");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Browse");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        searchEntry = passData.getStringExtra("TITLEENTRY");

        recyclerView = findViewById(R.id.browse_view);

        GridLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new BrowseAdapter(getApplicationContext(), mMovies, this);

        recyclerView.setAdapter(mAdapter);

        new GetPosters().execute(searchEntry);




    }

    @Override
    public void recyclerViewListClicked(View view, int position) {
        MovieResult result = mMovies.get(position);
        reference.child(result.getOriginalTitle()).setValue(result);
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK);
        finish();

    }


    private class GetPosters extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            service.search("0359c81bed7cce4e13cd5a744ea5cfbe", strings[0]).enqueue(new Callback<PageResult>() {
                @Override
                public void onResponse(Call<PageResult> call, Response<PageResult> response) {
                    mMovies.addAll(response.body().getResults());
                    System.out.println();
                    mAdapter.notifyDataSetChanged();



                }

                @Override
                public void onFailure(Call<PageResult> call, Throwable t) {

                }
            });

            return null;
        }



    }





}
