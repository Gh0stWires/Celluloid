package tk.samgrogan.celluloid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
    private RecyclerView recyclerView;
    private BrowseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meta_browse);
        service = Util.getMovieService();


        recyclerView = findViewById(R.id.browse_view);

        GridLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new BrowseAdapter(getApplicationContext(), mMovies, this);

        recyclerView.setAdapter(mAdapter);

        new GetPosters().execute();




    }

    @Override
    public void recyclerViewListClicked(View view, int position) {

    }


    private class GetPosters extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            service.search("0359c81bed7cce4e13cd5a744ea5cfbe", "Superman").enqueue(new Callback<PageResult>() {
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
