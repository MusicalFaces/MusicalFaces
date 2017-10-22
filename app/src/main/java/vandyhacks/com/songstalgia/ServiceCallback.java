package vandyhacks.com.songstalgia;

/**
 * Created by anip on 22/10/17.
 */

import android.util.Log;

import com.microsoft.projectoxford.face.common.ServiceError;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Custom callback, wrapper for Retrofit callback
 *
 * @param <T>
 */
public abstract class ServiceCallback<T> implements Callback<ResponseBody> {

    private static final String TAG = ServiceCallback.class.getSimpleName();
    private Retrofit retrofit;

    public ServiceCallback( Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Override
    public void onResponse(Call call, Response response) {

        if(!response.isSuccessful()) {
            ServiceError error = convertError(response);
            Log.e(TAG, error.toString());
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {

        if (t instanceof IOException){
            Log.e(TAG, "Error connecting to the server.");
        } else {
            Log.e(TAG, "Unexpected error - " + t.getLocalizedMessage());
        }

    }

    /**
     * Convert error body to ServiceError object
     * @param response
     * @return
     */
    public ServiceError convertError(Response response) {
        Converter<ResponseBody, ServiceError> converter = retrofit.responseBodyConverter(ServiceError.class, new Annotation[0]);
        ServiceError serviceError = null;

        try {
            serviceError = converter.convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serviceError;
    }
}

