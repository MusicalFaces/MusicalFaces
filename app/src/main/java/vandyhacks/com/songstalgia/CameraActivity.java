package vandyhacks.com.songstalgia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.google.gson.Gson;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.FaceRectangle;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.microsoft.projectoxford.face.FaceServiceRestClient;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

/**
 * Created by anip on 21/10/17.
 */

public class CameraActivity extends AppCompatActivity {
    private CameraView cameraView;
    private FloatingActionButton captureButton;
    private Bitmap image;
    private VisionServiceClient client;
    private EmotionServiceClient emotion_client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = (CameraView) findViewById(R.id.camera);
        if (client == null) {
            client = new VisionServiceRestClient(getString(R.string.subscription_key), "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0");
        }
        if (emotion_client == null) {
            emotion_client = new EmotionServiceRestClient(getString(R.string.emotion_subscription_key));
        }
        captureButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                cameraView.captureImage();
            }
        });
//        cameraView.captureImage();
        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                image = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                System.out.println(image);
                new FaceRequest(false).execute();
            }
        });
        Drawable myDrawable = getResources().getDrawable(R.drawable.sad);
        Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
        image = anImage;
        System.out.println("image" + image);
        new FaceRequest(false).execute();
//        new FaceRequest(true).execute();
    }


    @Override
    protected void onStart() {
        super.onStart();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }


    private class ImageRecognizer extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public ImageRecognizer() {
        }

        @Override
        protected String doInBackground(String... args) {

            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            if (e != null) {
                System.out.println("error" + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);
                System.out.println("result" + data);
                System.out.println("Image description: " + result.description.captions.get(0).text.toString() + "\n");
                System.out.println("Image Dominant Colors: " + result.color.accentColor + "\n");
                for(String color : result.color.dominantColors){
                    System.out.println(color + "\n");
                }
                if ( result.faces.size() > 0 ) {
                    new FaceRequest(false).execute();
                }
            }
        }
    }


    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();
        String[] features = {"ImageType", "Color", "Faces", "Adult", "Categories", "Description"};
        String[] details = {};

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.client.analyzeImage(inputStream, features, details);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }

    private List<RecognizeResult> processWithAutoFaceDetection() throws EmotionServiceException, IOException {
        Log.d("emotion", "Start emotion detection with auto-face detection");

        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long startTime = System.currentTimeMillis();
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE STARTS HERE
        // -----------------------------------------------------------------------

        List<RecognizeResult> result = null;
        //
        // Detect emotion by auto-detecting faces in the image.
        //
        result = this.emotion_client.recognizeImage(inputStream);

        String json = gson.toJson(result);
        Log.d("result", json);

        Log.d("emotion", String.format("Detection done. Elapsed time: %d ms", (System.currentTimeMillis() - startTime)));
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE ENDS HERE
        // -----------------------------------------------------------------------
        return result;
    }

    private List<RecognizeResult> processWithFaceRectangles() throws EmotionServiceException, com.microsoft.projectoxford.face.rest.ClientException, IOException {
        Log.d("emotion", "Do emotion detection with known face rectangles");
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long timeMark = System.currentTimeMillis();
        Log.d("emotion", "Start face detection using Face API");
        FaceRectangle[] faceRectangles = null;
        String faceSubscriptionKey = getString(R.string.face_subscription_key);
        FaceServiceRestClient faceClient = new FaceServiceRestClient(faceSubscriptionKey, "https://westcentralus.api.cognitive.microsoft.com/face/v1.0");
        com.microsoft.projectoxford.face.contract.Face[] faces = faceClient.detect(inputStream, false, false, null);
        Log.d("emotion", String.format("Face detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));

        if (faces != null) {
            faceRectangles = new FaceRectangle[faces.length];

            for (int i = 0; i < faceRectangles.length; i++) {
                // Face API and Emotion API have different FaceRectangle definition. Do the conversion.
                com.microsoft.projectoxford.face.contract.FaceRectangle rect = faces[i].faceRectangle;
                faceRectangles[i] = new com.microsoft.projectoxford.emotion.contract.FaceRectangle(rect.left, rect.top, rect.width, rect.height);
            }
        }

        List<RecognizeResult> result = null;
        if (faceRectangles != null) {
            inputStream.reset();

            timeMark = System.currentTimeMillis();
            Log.d("emotion", "Start emotion detection using Emotion API");
            result = this.emotion_client.recognizeImage(inputStream, faceRectangles);

            String json = gson.toJson(result);
            Log.d("emotion", String.format("Emotion detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));
        }
        return result;
    }

    private class FaceRequest extends AsyncTask<String, String, List<RecognizeResult>> {
        // Store error message
        private Exception e = null;
        private boolean useFaceRectangles = false;

        public FaceRequest(boolean useFaceRectangles) {
            this.useFaceRectangles = useFaceRectangles;
        }

        @Override
        protected List<RecognizeResult> doInBackground(String... args) {
            if (this.useFaceRectangles == false) {
                try {
                    return processWithAutoFaceDetection();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            } else {
                try {
                    return processWithFaceRectangles();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<RecognizeResult> result) {
            super.onPostExecute(result);
            // Display based on error existence

            if (this.useFaceRectangles == false) {
//                mEditText.append("\n\nRecognizing emotions with auto-detected face rectangles...\n");
            } else {
//                mEditText.append("\n\nRecognizing emotions with existing face rectangles from Face API...\n");
            }
            if (e != null) {
                System.out.println("Error: " + e.getMessage());
                this.e = null;
            } else {
                if (result.size() == 0) {
                    new ImageRecognizer().execute();
                } else {
                    Integer count = 0;
                    // Covert bitmap to a mutable bitmap by copying it
                    Bitmap bitmapCopy = image.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas faceCanvas = new Canvas(bitmapCopy);
                    faceCanvas.drawBitmap(image, 0, 0, null);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(5);
                    paint.setColor(Color.RED);

                    for (RecognizeResult r : result) {
                        Log.i("hell", String.format("\nFace #%1$d \n", count));
                        Log.i("hell", (String.format("\t anger: %1$.5f\n", r.scores.anger)));
                        Log.i("hell", String.format("\t contempt: %1$.5f\n", r.scores.contempt));
                        Log.i("hell", String.format("\t disgust: %1$.5f\n", r.scores.disgust));
                        Log.i("hell", String.format("\t fear: %1$.5f\n", r.scores.fear));
                        Log.i("hell", String.format("\t happiness: %1$.5f\n", r.scores.happiness));
                        Log.i("hell", String.format("\t neutral: %1$.5f\n", r.scores.neutral));
                        Log.i("hell", String.format("\t sadness: %1$.5f\n", r.scores.sadness));
                        Log.i("hell", String.format("\t surprise: %1$.5f\n", r.scores.surprise));
//                        mEditText.append(String.format("\t face rectangle: %d, %d, %d, %d", r.faceRectangle.left, r.faceRectangle.top, r.faceRectangle.width, r.faceRectangle.height));
                        Intent intent = new Intent(CameraActivity.this, StreamActivity.class);
                        startActivity(intent);
//                        String title = getVideoTitle("https://www.youtube.com/watch?v=fhWaJi1Hsfo");
//                        System.out.println(title);

                    }
                }
            }
        }
    }
    public static String getVideoTitle(String youtubeVideoUrl) {
        try {
            if (youtubeVideoUrl != null) {
                URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                        youtubeVideoUrl + "&format=json"
                );
                System.out.println(embededURL);

                return new JSONObject(IOUtils.toString(embededURL)).getString("title");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
