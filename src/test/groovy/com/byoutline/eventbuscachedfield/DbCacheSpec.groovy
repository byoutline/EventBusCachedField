package com.byoutline.eventbuscachedfield

import com.byoutline.cachedfield.dbcache.DbWriter
import com.byoutline.cachedfield.dbcache.FetchType
import com.byoutline.ibuscachedfield.events.ResponseEventWithArgImpl
import de.greenrobot.event.EventBus
import spock.lang.Shared

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class DbCacheSpec extends spock.lang.Specification {
    @Shared
    String value = "value"
    @Shared
    String differentValue = "different value"
    @Shared
    Exception exception = new RuntimeException("Cached Field test exception")
    ResponseEventWithArgImpl<String, FetchType> successEvent
    ResponseEventWithArgImpl<Exception, FetchType> errorEvent
    EventBus bus

    def setup() {
        bus = Mock()
        successEvent = new ResponseEventWithArgImpl<>()
        errorEvent = new ResponseEventWithArgImpl<>()

        EventBusCachedField.init(MockFactory.getSameSessionIdProvider(), bus)
    }


    def "should post value from API"() {
        given:
        def dbSaver = {} as DbWriter
        EventBusCachedFieldWithArg<String, FetchType> field = EventBusCachedField.<String> builder()
                .withDbReader(MockFactory.getStringGetter(value))
                .withDbWriter(dbSaver)
                .withApiFetcher(MockFactory.getStringGetter(value))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build();

        when:
        EventBusCachedFieldWithArgSpec.postAndWaitUntilFieldStopsLoading(field, FetchType.API)

        then:
        value == successEvent.getResponse()
        FetchType.API == successEvent.getArgValue()
    }

    def "should post value from DB"() {
        given:
        def dbSaver = {} as DbWriter
        EventBusCachedFieldWithArg<String, FetchType> field = EventBusCachedField.<String> builder()
                .withDbReader(MockFactory.getStringGetter(differentValue))
                .withDbWriter(dbSaver)
                .withApiFetcher(MockFactory.getStringGetter(value))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build();

        when:
        EventBusCachedFieldWithArgSpec.postAndWaitUntilFieldStopsLoading(field, FetchType.DB)

        then:
        differentValue == successEvent.getResponse()
        FetchType.DB == successEvent.getArgValue()
    }
}
