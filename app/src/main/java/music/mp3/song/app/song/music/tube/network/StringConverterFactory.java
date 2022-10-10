package music.mp3.song.app.song.music.tube.network;


import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

class StringConverterFactory extends Converter.Factory {

    private StringConverterFactory() {
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (type == String.class) {
            return new StringConverter();
        }
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    static StringConverterFactory create() {
        return new StringConverterFactory();
    }

    private static class StringConverter implements Converter<ResponseBody, String> {

        @Override
        public String convert(@NonNull ResponseBody value) throws IOException {
            return value.string();
        }
    }
}
