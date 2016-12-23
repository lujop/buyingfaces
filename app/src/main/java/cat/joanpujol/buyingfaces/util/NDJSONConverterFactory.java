package cat.joanpujol.buyingfaces.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Adapter to read NDJSON format that is not provided by Retrofit.
 * Based in GSONConverterFactory. Simply adding JSON Array at begin and end and changing new lines for ','
 * @author lujop
 */

public class NDJSONConverterFactory extends Converter.Factory {
    private Gson gson = new Gson();


    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new NDJSONResponseBodyConverter<>(gson,gson.getAdapter(TypeToken.get(type)));
    }

    final class NDJSONResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        NDJSONResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override public T convert(ResponseBody value) throws IOException {
            String response = value.string();
            String jsonResponse = "[" + response.replace("\n",",") +"]";
            Log.d("faces","Retrieved from API jsonResponse="+jsonResponse);
            JsonReader jsonReader = new JsonReader(new StringReader(jsonResponse));
            jsonReader.setLenient(true);
            try {
                return adapter.read(jsonReader);
            } finally {
                value.close();
            }
        }
    }
}
