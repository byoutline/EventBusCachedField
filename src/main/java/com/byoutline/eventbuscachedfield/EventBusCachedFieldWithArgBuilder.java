package com.byoutline.eventbuscachedfield;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.eventbuscachedfield.internal.EventIBus;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;
import de.greenrobot.event.EventBus;

import javax.annotation.Nullable;
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
public class EventBusCachedFieldWithArgBuilder<RETURN_TYPE, ARG_TYPE> {

    private ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter;
    private ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> successEvent;
    private ResponseEventWithArg<Exception, ARG_TYPE> errorEvent;
    private Provider<String> sessionIdProvider;
    private EventBus bus;
    private ExecutorService valueGetterExecutor;
    private Executor stateListenerExecutor;

    public EventBusCachedFieldWithArgBuilder() {
        bus = EventBusCachedField.defaultBus;
        sessionIdProvider = EventBusCachedField.defaultSessionIdProvider;
        valueGetterExecutor = EventBusCachedField.defaultValueGetterExecutor;
        stateListenerExecutor = EventBusCachedField.defaultStateListenerExecutor;
    }

    public SuccessEvent withValueProvider(ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueProvider) {
        this.valueGetter = valueProvider;
        return new SuccessEvent();
    }

    public class SuccessEvent {

        private SuccessEvent() {
        }

        public ErrorEventSetter withSuccessEvent(ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> successEvent) {
            EventBusCachedFieldWithArgBuilder.this.successEvent = successEvent;
            return new ErrorEventSetter();
        }
    }

    public class ErrorEventSetter {

        private ErrorEventSetter() {
        }

        public OverrideDefaultsSetter withResponseErrorEvent(@Nullable ResponseEventWithArg<Exception, ARG_TYPE> errorEvent) {
            EventBusCachedFieldWithArgBuilder.this.errorEvent = errorEvent;
            return new OverrideDefaultsSetter();
        }

        public EventBusCachedFieldWithArg<RETURN_TYPE, ARG_TYPE> build() {
            return EventBusCachedFieldWithArgBuilder.this.build();
        }
    }

    public class OverrideDefaultsSetter {

        private OverrideDefaultsSetter() {
        }

        public OverrideDefaultsSetter withCustomSessionIdProvider(Provider<String> sessionIdProvider) {
            EventBusCachedFieldWithArgBuilder.this.sessionIdProvider = sessionIdProvider;
            return this;
        }

        public OverrideDefaultsSetter withCustomBus(EventBus bus) {
            EventBusCachedFieldWithArgBuilder.this.bus = bus;
            return this;
        }

        public OverrideDefaultsSetter withCustomValueGetterExecutor(ExecutorService valueGetterExecutor) {
            EventBusCachedFieldWithArgBuilder.this.valueGetterExecutor = valueGetterExecutor;
            return this;
        }

        public OverrideDefaultsSetter withCustomStateListenerExecutor(Executor stateListenerExecutor) {
            EventBusCachedFieldWithArgBuilder.this.stateListenerExecutor = stateListenerExecutor;
            return this;
        }

        public EventBusCachedFieldWithArg<RETURN_TYPE, ARG_TYPE> build() {
            return EventBusCachedFieldWithArgBuilder.this.build();
        }
    }

    public class Builder {

        private Builder() {
        }

        public EventBusCachedFieldWithArg<RETURN_TYPE, ARG_TYPE> build() {
            return EventBusCachedFieldWithArgBuilder.this.build();
        }
    }

    private EventBusCachedFieldWithArg<RETURN_TYPE, ARG_TYPE> build() {
        return new EventBusCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>(sessionIdProvider, valueGetter,
                successEvent, errorEvent, new EventIBus(bus),
                valueGetterExecutor, stateListenerExecutor);
    }
}
