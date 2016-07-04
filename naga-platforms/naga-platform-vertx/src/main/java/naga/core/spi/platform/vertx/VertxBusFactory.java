package naga.core.spi.platform.vertx;

import io.vertx.core.eventbus.EventBus;
import naga.core.bus.Bus;
import naga.core.spi.platform.BusFactory;
import naga.core.bus.BusOptions;

/**
 * @author Bruno Salmon
 */
class VertxBusFactory implements BusFactory {

    private final EventBus eventBus;

    public VertxBusFactory(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Bus createBus(BusOptions options) {
        return new VertxBus(eventBus, options);
    }
}
