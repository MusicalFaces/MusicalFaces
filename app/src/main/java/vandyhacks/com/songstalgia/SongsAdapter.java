package vandyhacks.com.songstalgia;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vandyhacks.com.songstalgia.model.SongDetails;

/**
 * Created by anip on 22/10/17.
 */

class SongsAdapter extends ArrayAdapter<SongDetails> implements View.OnClickListener {
    Context mContext;
    private ArrayList<SongDetails> songs;
    public SongsAdapter(Context context, ArrayList<SongDetails> songs) {
        super(context, R.layout.song_item, songs);
        this.songs = songs;
        this.mContext = context;
    }

    private static class ViewHolder {
        TextView title;
        TextView artist;
        ImageView cover;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        final View result;
        if(convertView==null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.song_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.artist);
            viewHolder.cover = (ImageView) convertView.findViewById(R.id.cover);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        getVideoInfo(songs.get(position).getUrl(), songs.get(position));
        viewHolder.title.setText(songs.get(position).getTitle());
        viewHolder.artist.setText(songs.get(position).getArtist());
        Picasso.with(getContext()).load(songs.get(position).getCover()).centerCrop().into(viewHolder.cover);
//        viewHolder.info.setOnClickListener(this);
//        viewHolder.info.setTag(position);

        return result;

    }

    @Override
    public void onClick(View view) {
        Log.i("hell","onClick");
        int position=(Integer) view.getTag();
        Object object= getItem(position);
        SongDetails songDetails = (SongDetails) object;
        Intent intent = new Intent(getContext(),StreamActivity.class);
        intent.putExtra("url", songDetails.getUrl());
        getContext().startActivity(intent);

    }
    private void getVideoInfo(String url, final SongDetails songDetails){
        RequestQueue queue = Volley.newRequestQueue(getContext());
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

                            JSONObject object = jsonArray.getJSONObject(0);
                            JSONObject snippet = object.getJSONObject("snippet");
                            JSONObject thumbnail = snippet.getJSONObject("thumbnails");
                            JSONObject cover = thumbnail.getJSONObject("default");
                            String[] titleString = snippet.getString("title").split(" - ");
                            songDetails.setCover(cover.getString("url"));
                            songDetails.setTitle(titleString[1]);
                            songDetails.setArtist(titleString[0]);
                            String title = snippet.getString("title");
//                            String img =



                            Log.d("stuff: ", "" + title);


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
}
