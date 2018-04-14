package tk.samgrogan.celluloid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

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
    private int listPos;
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

        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MovieAdapter(getApplicationContext(), cards, this);
        recyclerView.setAdapter(mAdapter);



        new GetMovies().execute();


    }

    @Override
    public void recyclerViewListClicked(View view, final int position) {
        listPos = position;
        //showDialog();
        Intent intent = new Intent(getApplicationContext(), MetaBrowse.class);
        startActivity(intent);
        //System.out.println(cards.get(position).getTitle());



    }

    private void showDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Enter Movie Title to find Movie meta data");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AsyncParams params = new AsyncParams(input.getText().toString(), listPos);
                new ApiData().execute(params);

            }
        });

        alertDialog.show();

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
                card.setFilename(file.getAbsolutePath());
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

    private class ApiData extends AsyncTask<AsyncParams, Void, Void>{

        @Override
        protected Void doInBackground(final AsyncParams... params) {
            service.search("0359c81bed7cce4e13cd5a744ea5cfbe", params[0].query).enqueue(new Callback<PageResult>() {
                @Override
                public void onResponse(Call<PageResult> call, Response<PageResult> response) {
                    System.out.println("test");
                    movies = response.body().getResults();
                    System.out.println(movies.get(0).getOverview());
                    MovieResult result = movies.get(0);
                    result.setFilename(cards.get(params[0].position).getFilename());
                    reference.child(result.getOriginalTitle()).setValue(result);
                    movies = null;
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

    private static class AsyncParams{
        private String query;
        private int position;

        public AsyncParams(String query, int position){
            this.query = query;
            this.position = position;
        }
    }
}
