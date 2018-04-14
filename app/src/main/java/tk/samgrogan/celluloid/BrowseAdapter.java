package tk.samgrogan.celluloid;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tk.samgrogan.celluloid.api.MovieResult;

/**
 * Created by ghost on 3/9/2018.
 */

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.ViewHolder> {

    private List<MovieResult> cards;
    private Context mContext;
    private static RecyclerViewClickListener itemListener;

    public BrowseAdapter(Context context, List<MovieResult> cards, RecyclerViewClickListener listener){
        this.cards = cards;
        this.mContext = context;
        itemListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String baseurl = "http://image.tmdb.org/t/p/w780";
        String fullUrl = baseurl + cards.get(position).getPosterPath();

        Picasso.with(mContext)
                .load(fullUrl)
                .into(holder.poster);


    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private ImageView poster;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.browse);
            poster = itemView.findViewById(R.id.poster_browse);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getAdapterPosition());

        }
    }
}
