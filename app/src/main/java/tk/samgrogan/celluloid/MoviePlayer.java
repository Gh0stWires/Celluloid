package tk.samgrogan.celluloid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tk.samgrogan.celluloid.api.MovieResult;

public class MoviePlayer extends AppCompatActivity implements EasyVideoCallback{

    EasyVideoPlayer player;
    private int pos;
    private MovieResult result;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);
        Intent passedData = getIntent();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        if (mAuth.getCurrentUser() != null){
            reference = mDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("movies").child(passedData.getStringExtra("TITLE"));
        }

        String source = passedData.getStringExtra("SOURCE");

        player = findViewById(R.id.player);

        player.setCallback(this);

        player.setSource(Uri.parse(source));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result = dataSnapshot.getValue(MovieResult.class);
                pos = result.getMovieTime();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //player.setInitialPosition(1730495);

    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {
        player.seekTo(pos);

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        System.out.println(player.getCurrentPosition());
        pos = player.getCurrentPosition();
        reference.child("movieTime").setValue(pos);
        System.out.println();



    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }
}
