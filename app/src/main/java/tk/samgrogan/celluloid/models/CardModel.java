package tk.samgrogan.celluloid.models;

/**
 * Created by ghost on 3/9/2018.
 */

public class CardModel {
    String title;
    String poster;
    String filename;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
