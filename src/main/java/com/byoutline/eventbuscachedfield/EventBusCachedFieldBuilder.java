package com.byoutline.eventbuscachedfield;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.dbcache.DbCachedValueProvider;
import com.byoutline.cachedfield.dbcache.DbWriter;
import com.byoutline.cachedfield.dbcache.FetchType;
import com.byoutline.eventbuscachedfield.internal.EventIBus;
import com.byoutline.eventcallback.ResponseEvent;
import com.byoutline.ibuscachedfield.internal.ErrorEvent;
import de.greenrobot.event.EventBus;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Fluent interface builder of {@link EventBusCachedField}. If you do not like
 * fluent interface create {@link EventBusCachedField} by one of its constructors.
 *
 * @param <RETURN_TYPE> Type of object to be cached.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class EventBusCachedFieldBuilder<RETURN_TYPE> {

    private Provider<RETURN_TYPE> valueGetter;
    private ResponseEvent<RETURN_TYPE> successEvent;
    private ErrorEvent errorEvent;
    private Provider<String> sessionIdProvider;
    private EventBus bus;
    private ExecutorService valueGetterExecutor;
    private Executor stateListenerExecutor;

    public EventBusCachedFieldBuilder() {
        bus = EventBusCachedField.defaultBus;
        sessionIdProvider = EventBusCachedField.defaultSessionIdProvider;
        valueGetterExecutor = EventBusCachedField.defaultValueGetterExecutor;
        stateListenerExecutor = EventBusCachedField.defaultStateListenerExecutor;
    }

    public SuccessEvent withValueProvider(Provider<RETURN_TYPE> valueProvider) {
        this.valueGetter = valueProvider;
        return new SuccessEvent();
    }


    public <API_RETURN_TYPE> DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE> withApiFetcher(Provider<API_RETURN_TYPE> apiValueProvider) {
        return new DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE>(apiValueProvider);
    }

    public static class DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE> {
        private final Provider<API_RETURN_TYPE> apiValueProvider;

        public DbCacheBuilderReader(Provider<API_RETURN_TYPE> apiValueProvider) {
            this.apiValueProvider = apiValueProvider;
        }

        public <API_RETURN_TYPE> DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE> withDbWriter(DbWriter<API_RETURN_TYPE> dbSaver) {
            return new DbCacheBuilderWriter(apiValueProvider, dbSaver);
        }
    }

    public static class DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE> {
        private final Provider<API_RETURN_TYPE> apiValueProvider;
        private final DbWriter<API_RETURN_TYPE> dbSaver;

        public DbCacheBuilderWriter(Provider<API_RETURN_TYPE> apiValueProvider, DbWriter<API_RETURN_TYPE> dbSaver) {
            this.apiValueProvider = apiValueProvider;
            this.dbSaver = dbSaver;
        }

        public EventBusCachedFieldWithArgBuilder.SuccessEvent withDbReader(Provider<RETURN_TYPE> dbValueProvider) {
            ProviderWithArg<RETURN_TYPE, FetchType> valueProvider =
                    new DbCachedValueProvider<API_RETURN_TYPE, RETURN_TYPE>(apiValueProvider, dbSaver, dbValueProvider);
            return new EventBusCachedFieldWithArgBuilder<RETURN_TYPE, FetchType>()
                    .withValueProvider(valueProvider);
        }
    }

    public class SuccessEvent {

        private SuccessEvent() {
        }

        public ErrorEventSetter withSuccessEvent(ResponseEvent<RETURN_TYPE> successEvent) {
            EventBusCachedFieldBuilder.this.successEvent = successEvent;
            return new ErrorEventSetter();
        }
    }

    public class ErrorEventSetter {

        private ErrorEventSetter() {
        }

        public OverrideDefaultsSetter withGenericErrorEvent(Object errorEvent) {
            EventBusCachedFieldBuilder.this.errorEvent = ErrorEvent.genericEvent(errorEvent);
            return new OverrideDefaultsSetter();
        }

        public OverrideDefaultsSetter withResponseErrorEvent(ResponseEvent<Exception> errorEvent) {
            EventBusCachedFieldBuilder.this.errorEvent = ErrorEvent.responseEvent(errorEvent);
            return new OverrideDefaultsSetter();
        }

        public EventBusCachedField<RETURN_TYPE> build() {
            EventBusCachedFieldBuilder.this.errorEvent = new ErrorEvent(null, null);
            return EventBusCachedFieldBuilder.this.build();
        }
    }

    public class OverrideDefaultsSetter {

        private OverrideDefaultsSetter() {
        }

        public OverrideDefaultsSetter withCustomSessionIdProvider(Provider<String> sessionIdProvider) {
            EventBusCachedFieldBuilder.this.sessionIdProvider = sessionIdProvider;
            return this;
        }

        public OverrideDefaultsSetter withCustomBus(EventBus bus) {
            EventBusCachedFieldBuilder.this.bus = bus;
            return this;
        }

        public OverrideDefaultsSetter withCustomValueGetterExecutor(ExecutorService valueGetterExecutor) {
            EventBusCachedFieldBuilder.this.valueGetterExecutor = valueGetterExecutor;
            return this;
        }

        public OverrideDefaultsSetter withCustomStateListenerExecutor(Executor stateListenerExecutor) {
            EventBusCachedFieldBuilder.this.stateListenerExecutor = stateListenerExecutor;
            return this;
        }

        public EventBusCachedField<RETURN_TYPE> build() {
            return EventBusCachedFieldBuilder.this.build();
        }
    }

    private EventBusCachedField<RETURN_TYPE> build() {
        return new EventBusCachedField<RETURN_TYPE>(sessionIdProvider, valueGetter,
                successEvent, errorEvent, new EventIBus(bus),
                valueGetterExecutor, stateListenerExecutor);
    }
}
