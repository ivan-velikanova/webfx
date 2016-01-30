package naga.core.jsoncodec;

import naga.core.spi.json.JsonObject;
import naga.core.util.async.AsyncResult;

/**
 * @author Bruno Salmon
 */
public class SerializableAsyncResult<T> implements AsyncResult<T> {

    private final T result;
    private final Throwable cause;

    public static <T> SerializableAsyncResult<T> getSerializableAsyncResult(AsyncResult<T> asyncResult) {
        if (asyncResult == null || asyncResult instanceof SerializableAsyncResult)
            return (SerializableAsyncResult<T>) asyncResult;
        return new SerializableAsyncResult<T>(asyncResult);
    }

    public SerializableAsyncResult(AsyncResult<T> asyncResult) {
        this(asyncResult.result(), asyncResult.cause());
    }

    public SerializableAsyncResult(T result, Throwable cause) {
        this.result = result;
        this.cause = cause;
    }

    @Override
    public T result() {
        return result;
    }

    @Override
    public Throwable cause() {
        return cause;
    }

    @Override
    public boolean succeeded() {
        return cause == null;
    }

    @Override
    public boolean failed() {
        return cause != null;
    }

    /****************************************************
     *                    Json Codec                    *
     *                    Json Codec                    *
     * *************************************************/

    private static String CODEC_ID = "asyncRes";
    private static String RESULT_KEY = "res";
    private static String ERROR_KEY = "err";

    public static void registerJsonCodec() {
        new AbstractJsonCodec<SerializableAsyncResult>(SerializableAsyncResult.class, CODEC_ID) {

            @Override
            public void encodeToJson(SerializableAsyncResult result, JsonObject json) {
                if (result.cause() != null)
                    json.set(ERROR_KEY, result.cause().getMessage());
                if (result.result() != null)
                    json.set(RESULT_KEY, JsonCodecManager.encodeToJsonObject(result.result()));
            }

            @Override
            public SerializableAsyncResult decodeFromJson(JsonObject json) {
                String errorMessage = json.getString(ERROR_KEY);
                Exception error = errorMessage == null ? null : new Exception(errorMessage);
                return new SerializableAsyncResult<>(
                        JsonCodecManager.decodeFromJsonObject(json.getObject(RESULT_KEY)),
                        error
                );
            }
        };
    }
}
