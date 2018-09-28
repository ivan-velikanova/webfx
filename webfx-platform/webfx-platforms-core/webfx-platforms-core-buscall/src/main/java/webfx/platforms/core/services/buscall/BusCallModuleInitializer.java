package webfx.platforms.core.services.buscall;

import webfx.platforms.core.services.appcontainer.spi.ApplicationModuleInitializer;
import webfx.platforms.core.services.bus.BusService;
import webfx.platforms.core.services.buscall.spi.BusCallEndpoint;
import webfx.platforms.core.services.log.Logger;
import webfx.platforms.core.util.collection.Collections;

import java.util.List;
import java.util.ServiceLoader;

/**
 * @author Bruno Salmon
 */
public final class BusCallModuleInitializer implements ApplicationModuleInitializer {

    @Override
    public String getModuleName() {
        return "webfx-platforms-core-buscall";
    }

    @Override
    public int getInitLevel() {
        return JOBS_START_INIT_LEVEL;
    }

    @Override
    public void initModule() {
        StringBuilder sb = new StringBuilder();
        List<BusCallEndpoint> endpoints = Collections.listOf(ServiceLoader.load(BusCallEndpoint.class));
        for (BusCallEndpoint endpoint : endpoints) {
            BusCallService.registerBusCallEndpoint(endpoint);
            sb.append(sb.length() == 0 ? endpoints.size() + " endpoints provided for addresses: " : ", ").append(endpoint.getAddress());
        }
        Logger.log(sb);
        // Initializing the bus immediately to make the connection connection process happen while the application is initializing
        BusService.bus(); // Instantiating the bus (if not already done) is enough to open the connection
    }
}
