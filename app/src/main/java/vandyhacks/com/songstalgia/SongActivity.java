package vandyhacks.com.songstalgia;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

import vandyhacks.com.songstalgia.utilities.NetworkUtils;

/**
 * Created by rishabh on 21/10/17.
 */

public class SongActivity extends AppCompatActivity {

    private TextView songResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        songResultsTextView = (TextView) findViewById(R.id.song_results);

        makeSearchQuery(3);
    }

    private void makeSearchQuery(int mood) {


        URL searchUrl = NetworkUtils.buildUrl(mood);

        new QueryTask().execute(searchUrl);
    }

    // COMPLETED (1) Create a class called GithubQueryTask that extends AsyncTask<URL, Void, String>
    public class QueryTask extends AsyncTask<URL, Void, String> {

        // COMPLETED (2) Override the doInBackground method to perform the query. Return the results. (Hint: You've already written the code to perform the query)
        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        // COMPLETED (3) Override onPostExecute to display the results in the TextView
        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {

                songResultsTextView.setText(searchResults);

                /*
                Context context = getApplicationContext();

                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context,searchResults , duration);
                toast.show();
                */
            }
        }
    }
}
