package vandyhacks.com.songstalgia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;

/**
 * Created by anip on 22/10/17.
 */

class ImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button revengeButton;
    private Button helpButton;
    private MoodHashes moodHashes;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_decision_layout);
        String image = getIntent().getStringExtra("image");
        Intent intent =getIntent();
        final int mood = getIntent().getIntExtra("mood",0);
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("image");
        revengeButton = (Button) findViewById(R.id.revengeButton);
        helpButton = (Button) findViewById(R.id.helpButton);
        imageView = (ImageView) findViewById(R.id.imageView);
//        imageView.setImageBitmap(bitmap);
        Gson gson = new Gson();
        String strObj = intent.getStringExtra("image");
//        Bitmap obj = gson.fromJson(strObj, Bitmap.class);
//        imageView.setImageBitmap(obj);
//        if(intent.hasExtra("byteArray")) {
////            ImageView previewThumbnail = new ImageView(this);
//            Bitmap b = BitmapFactory.decodeByteArray(
//                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
//            imageView.setImageBitmap(b);
//        }
        moodHashes = new MoodHashes();
        revengeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                System.out.println("---------------------------");
                Log.i("hell_fdfd", String.valueOf(mood));
                int mappedMood = moodHashes.getrevengeHashValue(mood);
                System.out.println("--------------------" + mappedMood);
                Intent intent = new Intent(ImageActivity.this, SongActivity.class);
                intent.putExtra("mood", mappedMood);
                startActivity(intent);
                finish();

            }
        });



        helpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(ImageActivity.this, SongActivity.class);
                int mappedMood = moodHashes.getHelpHashValue(mood);
                intent.putExtra("mood", mappedMood);
                startActivity(intent);
                finish();
            }
        });

    }
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
