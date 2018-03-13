package tk.samgrogan.celluloid;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tk.samgrogan.celluloid.models.CardModel;

/**
 * Created by ghost on 3/9/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<CardModel> cards;
    private Context mContext;
    private static RecyclerViewClickListener itemListener;

    public MovieAdapter(Context context, List<CardModel> cards, RecyclerViewClickListener listener){
        this.cards = cards;
        this.mContext = context;
        itemListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meta_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(cards.get(position).getTitle());
        Picasso.with(mContext)
                .load(R.drawable.test)
                .into(holder.poster);


    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private ImageView poster;
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.movie_card);
            poster = itemView.findViewById(R.id.poster);
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getAdapterPosition());

        }
    }
}
