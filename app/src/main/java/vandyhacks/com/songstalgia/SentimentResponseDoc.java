package vandyhacks.com.songstalgia;

/**
 * Created by anip on 22/10/17.
 */

public class SentimentResponseDoc {

    private Double score;
    private String id;

    public SentimentResponseDoc() {
    }

    public SentimentResponseDoc(Double score, String id) {
        this.score = score;
        this.id = id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}