package com.byoutline.eventbuscachedfield;

import com.byoutline.cachedfield.CachedField;
import com.byoutline.cachedfield.CachedFieldImpl;
import com.byoutline.cachedfield.ErrorListener;
import com.byoutline.cachedfield.SuccessListener;
import com.byoutline.eventbuscachedfield.internal.EventIBus;
import com.byoutline.eventcallback.ResponseEvent;
import com.byoutline.ibuscachedfield.internal.ErrorEvent;
import com.byoutline.ibuscachedfield.internal.IBusErrorListener;
import com.byoutline.ibuscachedfield.internal.IBusSuccessListener;
import de.greenrobot.event.EventBus;

import javax.inject.Provider;

/**
 * {@link CachedField} implementation that posts calculated result on Otto bus. <br />
 * Use {@link #builder()} to create instances.
 *
 * @param <RETURN_TYPE> Type of cached value.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class EventBusCachedField<RETURN_TYPE> extends CachedFieldImpl<RETURN_TYPE> {

    static Provider<String> defaultSessionIdProvider;
    static EventBus defaultBus;

    EventBusCachedField(Provider<String> sessionIdProvider, Provider<RETURN_TYPE> valueGetter, ResponseEvent<RETURN_TYPE> successEvent, ErrorEvent errorEvent, EventIBus bus) {
        super(sessionIdProvider,
                valueGetter,
                new IBusSuccessListener<RETURN_TYPE>(bus, successEvent),
                new IBusErrorListener(bus, errorEvent));
    }

    public static <RETURN_TYPE> EventBusCachedFieldBuilder<RETURN_TYPE> builder() {
        return new EventBusCachedFieldBuilder<RETURN_TYPE>();
    }

    public static void init(Provider<String> defaultSessionIdProvider, EventBus defaultBus) {
        EventBusCachedField.defaultSessionIdProvider = defaultSessionIdProvider;
        EventBusCachedField.defaultBus = defaultBus;
    }
}
