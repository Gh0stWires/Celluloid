package tk.samgrogan.celluloid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghost on 3/5/2018.
 */

public class MovieContent {

    public static final List<MovieContent.MovieItem> ITEMS = new ArrayList<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(MovieItem item) {
        ITEMS.add(item);

    }

    private static MovieItem createDummyItem(int position) {
        return new MovieItem(String.valueOf(position), "Item " + position, R.drawable.test);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static class MovieItem{
        //public final URL poster;
        public final String title;
        public final String desciption;
        public final int test;

        public MovieItem(String title, String desciption, int test){
            this.title = title;
            this.desciption = desciption;
            this.test = test;
        }

        @Override
        public String toString() {
            return desciption;
        }

    }
}
