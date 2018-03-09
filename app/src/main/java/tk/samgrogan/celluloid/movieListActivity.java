package tk.samgrogan.celluloid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

/**
 * An activity representing a list of movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link movieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class movieListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Api service;
    private List<MovieResult> mMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        service = Util.getMovieService();

        MovieData thread = new MovieData();

        thread.execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.movie_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, MovieContent.ITEMS, mTwoPane));
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final movieListActivity mParentActivity;
        private final List<MovieContent.MovieItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieContent.MovieItem item = (MovieContent.MovieItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(movieDetailFragment.ARG_ITEM_ID, item.title);
                    movieDetailFragment fragment = new movieDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, movieDetailActivity.class);
                    intent.putExtra(movieDetailFragment.ARG_ITEM_ID, item.title);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(movieListActivity parent,
                                      List<MovieContent.MovieItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mPoster.setImageResource(mValues.get(position).test);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            final ImageView mPoster;

            ViewHolder(View view) {
                super(view);
                mPoster = view.findViewById(R.id.poster);
            }
        }
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
