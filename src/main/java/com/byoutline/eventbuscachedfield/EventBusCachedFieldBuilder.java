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

    public EventBusCachedFieldBuilder() {
        bus = EventBusCachedField.defaultBus;
        sessionIdProvider = EventBusCachedField.defaultSessionIdProvider;
    }

    public SuccessEvent withValueProvider(Provider<RETURN_TYPE> valueProvider) {
        this.valueGetter = valueProvider;
        return new SuccessEvent();
    }


    public DbCacheBuilderReader<RETURN_TYPE> withDbReader(Provider<RETURN_TYPE> dbValueProvider) {
        return new DbCacheBuilderReader<RETURN_TYPE>(dbValueProvider);
    }

    public static class DbCacheBuilderReader<RETURN_TYPE> {
        private final Provider<RETURN_TYPE> dbValueProvider;

        public DbCacheBuilderReader(Provider<RETURN_TYPE> dbValueProvider) {
            this.dbValueProvider = dbValueProvider;
        }

        public DbCacheBuilderWriter withDbWriter(DbWriter<RETURN_TYPE> dbSaver) {
            return new DbCacheBuilderWriter(dbValueProvider, dbSaver);
        }
    }

    public static class DbCacheBuilderWriter<RETURN_TYPE> {
        private final Provider<RETURN_TYPE> dbValueProvider;
        private final DbWriter<RETURN_TYPE> dbSaver;

        public DbCacheBuilderWriter(Provider<RETURN_TYPE> dbValueProvider, DbWriter<RETURN_TYPE> dbSaver) {
            this.dbValueProvider = dbValueProvider;
            this.dbSaver = dbSaver;
        }
        public EventBusCachedFieldWithArgBuilder.SuccessEvent withApiFetcher(Provider<RETURN_TYPE> apiValueProvider) {
            ProviderWithArg<RETURN_TYPE, FetchType> valueProvider =
                    new DbCachedValueProvider<RETURN_TYPE>(apiValueProvider, dbSaver, dbValueProvider);
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

        public CustomSessionIdProvider withGenericErrorEvent(Object errorEvent) {
            EventBusCachedFieldBuilder.this.errorEvent = ErrorEvent.genericEvent(errorEvent);
            return new CustomSessionIdProvider();
        }

        public CustomSessionIdProvider withResponseErrorEvent(ResponseEvent<Exception> errorEvent) {
            EventBusCachedFieldBuilder.this.errorEvent = ErrorEvent.responseEvent(errorEvent);
            return new CustomSessionIdProvider();
        }

        public EventBusCachedField<RETURN_TYPE> build() {
            EventBusCachedFieldBuilder.this.errorEvent = new ErrorEvent(null, null);
            return EventBusCachedFieldBuilder.this.build();
        }
    }

    public class CustomSessionIdProvider {

        private CustomSessionIdProvider() {
        }

        public CustomBus withCustomSessionIdProvider(Provider<String> sessionIdProvider) {
            EventBusCachedFieldBuilder.this.sessionIdProvider = sessionIdProvider;
            return new CustomBus();
        }

        public EventBusCachedField<RETURN_TYPE> build() {
            return EventBusCachedFieldBuilder.this.build();
        }
    }

    public class CustomBus {

        private CustomBus() {
        }

        public Builder withCustomBus(EventBus bus) {
            EventBusCachedFieldBuilder.this.bus = bus;
            return new Builder();
        }

        public EventBusCachedField<RETURN_TYPE> build() {
            return EventBusCachedFieldBuilder.this.build();
        }
    }

    public class Builder {

        private Builder() {
        }

        public EventBusCachedField<RETURN_TYPE> build() {
            return EventBusCachedFieldBuilder.this.build();
        }
    }

    private EventBusCachedField<RETURN_TYPE> build() {
        return new EventBusCachedField<RETURN_TYPE>(sessionIdProvider, valueGetter, successEvent, errorEvent, new EventIBus(bus));
    }
}
