package vandyhacks.com.songstalgia;

/**
 * Created by anip on 22/10/17.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Response consists of a list of documents
 */
public class SentimentResponse {

    private List<SentimentResponseDoc> documents = new ArrayList<>();
    private List<Object> errors = new ArrayList<Object>();

    public SentimentResponse() {
    }

    public SentimentResponse(List<SentimentResponseDoc> documents, List<Object> errors) {
        this.documents = documents;
        this.errors = errors;
    }

    public List<SentimentResponseDoc> getDocuments() {
        return documents;
    }

    public void setDocuments(List<SentimentResponseDoc> documents) {
        this.documents = documents;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

}
