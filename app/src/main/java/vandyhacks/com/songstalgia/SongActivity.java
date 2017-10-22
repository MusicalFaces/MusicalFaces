package vandyhacks.com.songstalgia;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import vandyhacks.com.songstalgia.model.SongDetails;
import vandyhacks.com.songstalgia.utilities.NetworkUtils;

/**
 * Created by rishabh on 21/10/17.
 */

public class SongActivity extends AppCompatActivity {

    private TextView songResultsTextView;
    private int mood;
    private ListView listView;
    private SongsAdapter songsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        listView = (ListView) findViewById(R.id.listView);
        mood = getIntent().getIntExtra("mood",0);
        Log.i("hell", String.valueOf(mood));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
        songResultsTextView = (TextView) findViewById(R.id.song_results);
        makeSearchQuery(mood);

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

        protected ArrayList<SongDetails> parseHtml(String searchResults){
            //"\"Hello\""  data-vpi-videoid="'(.*?)'"
            Document doc = Jsoup.parse(searchResults);

            ArrayList<SongDetails> songsList = new ArrayList<SongDetails>();


            //video links
            for (Element e : doc.getElementsByClass("videoPlayer")) {
                SongDetails song = new SongDetails();

                song.setUrl(e.attr("data-vpi-videoid"));
                getVideoInfo(song.getUrl(), song);
                songsList.add(song);
            }

//            int i=0;
//            for (Element e : doc.getElementsByClass("listItem__blather")) {
//
//
//                    if(e.attr("itemprop").contains("description"))
//                        songsList.get(i).setArtist(e.text());
//
//                    i++;
//
//            }
//
//            i=0;
//
//            for (Element e : doc.getElementsByClass("listItem__title")) {
//
////                if((e.child(1).getElementsByClass("listItem_title").contains("name") && e.attr("rel").contains("nofollow")))
//                if(i<30) {
//                    String[] strings = e.html().split("<");
//                    System.out.println("ytytyty" + strings[0] + " " + i);//.("listItem_title").text());
//                    songsList.get(i).setTitle(strings[0]);
//
//                }
//                i++;
//
//
//            }
//
//s

            return songsList;




        }
        // COMPLETED (3) Override onPostExecute to display the results in the TextView
        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {
                final ArrayList<SongDetails> songs = parseHtml(searchResults);
                for(SongDetails song: songs){
                    System.out.println("----------" + song.getArtist() + " " + song.getTitle() + " " + song.getUrl());
                }
                songsAdapter = new SongsAdapter(getApplicationContext(), parseHtml(searchResults));
                listView.setAdapter(songsAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(SongActivity.this,StreamActivity.class);
                        intent.putExtra("url", songs.get(i).getUrl());
                        startActivity(intent);
                    }
                });


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
    private void getVideoInfo(final String url, final SongDetails songDetails){
        RequestQueue queue = Volley.newRequestQueue(this);
        // volley
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "https://www.googleapis.com/youtube/v3/videos?id=" + url + "&key=" +
                        Config.YOUTUBE_API_KEY +
                        "&part=snippet,contentDetails,statistics,status",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            if(jsonArray.length()>0) {
                                JSONObject object = jsonArray.getJSONObject(0);
                                JSONObject snippet = object.getJSONObject("snippet");
                                JSONObject thumbnail = snippet.getJSONObject("thumbnails");
                                JSONObject cover = thumbnail.getJSONObject("default");
                                String[] titleString = snippet.getString("title").split(" - ");
                                songDetails.setUrl(url);
                                songDetails.setCover(cover.getString("url"));
                                if (titleString.length == 2) {
                                    songDetails.setTitle(titleString[1]);
                                    songDetails.setArtist(titleString[0]);
                                } else {
                                    songDetails.setTitle(titleString[0]);
                                    songDetails.setArtist(titleString[0]);
                                }

                                String title = snippet.getString("title");
//                            String img =
                                songsAdapter.notifyDataSetChanged();


                                Log.d("stuff: ", "" + title);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.activity_main, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){};

        // Request (if not using Singleton [RequestHandler]
        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        // requestQueue.add(stringRequest);
        queue.add(stringRequest);
        // Request with RequestHandler (Singleton: if created)
//        RequestHandler.getC(getContext()).addToRequestQueue(stringRequest);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
