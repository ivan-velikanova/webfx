package naga.providers.platform.client.gwt.services.resource;

import com.google.gwt.resources.client.TextResource;
import naga.platform.services.resource.spi.ResourceServiceProvider;
import naga.util.async.Future;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class GwtResourceServiceProviderImpl implements ResourceServiceProvider {

    private List<GwtBundle> bundles = new ArrayList<>();

    public void register(GwtBundle bundle) {
        bundles.add(bundle);
    }

    @Override
    public Future<String> getText(String resourcePath) {
        for (GwtBundle bundle : bundles) {
            TextResource textResource = bundle.getTextResource(resourcePath);
            if (textResource != null)
                return Future.succeededFuture(textResource.getText());
        }
        return Future.succeededFuture(null);
    }
}