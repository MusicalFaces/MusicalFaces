package vandyhacks.com.songstalgia.utilities;

/**
 * Created by rishabh on 21/10/17.
 */

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    final static String RANKER_BASE_URL =
            "https://www.ranker.com/list";


    public static URL buildUrl(int mood) {

        String append1 = null;
        String append2 = null;

        switch (mood) {
            case 1: append1 = "songs-about-anger" ;//anger
                    append2 = "ranker-music";
                break;
            case 2:  append1 = "songs-about-anger";//contempt
                    append2 = "ranker-music";
                break;
            case 3:  append1 = "worst-songs-of-2015";//disgust
                     append2 = "jacob-shelton";
                break;
            case 4:  append1 = "best-halloween-songs";//fear
                    append2 = "music-lover";
                break;
            case 5:  append1 = "best-songs-about-happiness";//happiness
                    append2 = "reference";
                break;
            case 6:  append1 = "ten-songs-to-make-you-relax-";
//                "best-songs-about-peace";
                    append2 = "crawdidd88";
                break;
            case 7:  append1 = "songs-about-pain";//sadness
                     append2 = "ranker-music";
                break;
        }
        Uri builtUri = Uri.parse(RANKER_BASE_URL).buildUpon()
                .appendPath(append1)
                .appendPath(append2)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}