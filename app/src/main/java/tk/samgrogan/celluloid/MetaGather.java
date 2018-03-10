package tk.samgrogan.celluloid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
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

public class MetaGather extends AppCompatActivity implements RecyclerViewClickListener{

    private List<CardModel> cards = new ArrayList<>();
    private MovieAdapter mAdapter;
    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private List<MovieResult> movies;
    private Api service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meta_gather);
        service = Util.getMovieService();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        if (mAuth.getCurrentUser() != null){
            reference = mDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("movies");
        }


        recyclerView = findViewById(R.id.movie_card_view);

        GridLayoutManager gridLayoutManager;
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new MovieAdapter(getApplicationContext(), cards, this);
        recyclerView.setAdapter(mAdapter);



        new GetMovies().execute();


    }

    @Override
    public void recyclerViewListClicked(View view, int position) {
        //System.out.println(cards.get(position).getTitle());
        new ApiData().execute(cards.get(position).getTitle());
    }


    private class GetMovies extends AsyncTask<Void, Void, Void>{
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
                CardModel card = new CardModel();
                card.setTitle(query);
                cards.add(card);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.setAdapter(mAdapter);
        }
    }

    private class ApiData extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... query) {
            service.search("0359c81bed7cce4e13cd5a744ea5cfbe", query[0]).enqueue(new Callback<PageResult>() {
                @Override
                public void onResponse(Call<PageResult> call, Response<PageResult> response) {
                    System.out.println("test");
                    movies = response.body().getResults();
                    System.out.println(movies.get(0).getOverview());
                    MovieResult result = movies.get(0);
                    reference.child(result.getOriginalTitle()).setValue(result);
                    //System.out.println(response.body().getMovies());

                }

                @Override
                public void onFailure(Call<PageResult> call, Throwable t) {
                    System.out.println(t);

                }
            });
            return null;
        }


    }
}
