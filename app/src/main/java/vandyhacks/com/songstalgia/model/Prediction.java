package vandyhacks.com.songstalgia.model;

/**
 * Created by anip on 21/10/17.
 */

public class Prediction {
    private int mood;
    private String caption;
    private int crowdiness;

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getCrowdiness() {
        return crowdiness;
    }

    public void setCrowdiness(int crowdiness) {
        this.crowdiness = crowdiness;
    }
}
