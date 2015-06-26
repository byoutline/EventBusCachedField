package com.byoutline.eventbuscachedfield

import com.byoutline.eventcallback.ResponseEvent
import com.byoutline.eventcallback.ResponseEventImpl
import com.google.common.util.concurrent.MoreExecutors
import de.greenrobot.event.EventBus
import spock.lang.Shared

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CustomExecutorsSpec extends spock.lang.Specification {
    @Shared
    String value = "value"
    ResponseEvent<String> successEvent
    ResponseEvent<Exception> errorEvent
    EventBus bus

    def setup() {
        bus = Mock()
        successEvent = new ResponseEventImpl<>()
        errorEvent = new ResponseEventImpl<>()

        EventBusCachedField.init(MockFactory.getSameSessionIdProvider(), bus)
    }

    def "should use passed executor for loading data"() {
        given:
        boolean called = false
        ExecutorService executor = [
                submit: { called = true; return new FutureTask((Runnable) it, null); }
        ] as ExecutorService
        EventBusCachedField field = EventBusCachedField.builder()
                .withValueProvider(MockFactory.getStringGetter(value))
                .withSuccessEvent(successEvent)
                .withGenericErrorEvent(errorEvent)
                .withCustomValueGetterExecutor(executor)
                .build();

        when:
        field.postValue()

        then:
        called
    }

    def "should use passed executor for state listener"() {
        given:
        boolean called = false
        Executor stateListenersExecutor = { called = true; it.run() } as Executor
        EventBusCachedField field = EventBusCachedField.builder()
                .withValueProvider(MockFactory.getStringGetter(value))
                .withSuccessEvent(successEvent)
                .withGenericErrorEvent(errorEvent)
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .withCustomStateListenerExecutor(stateListenersExecutor)
                .build();

        when:
        field.postValue()

        then:
        called
    }
}
