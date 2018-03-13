package tk.samgrogan.celluloid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;

public class MoviePlayer extends AppCompatActivity implements EasyVideoCallback{

    EasyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);
        Intent passedData = getIntent();

        String source = passedData.getStringExtra("SOURCE");

        player = findViewById(R.id.player);

        player.setCallback(this);

        player.setSource(Uri.parse(source));
        //player.setInitialPosition(1730495);

    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {
        player.seekTo(1730495);

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        System.out.println(player.getCurrentPosition());

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
