package mongoose.server.jobs.sessioncloser;

import mongoose.shared.domainmodel.loader.DomainModelSnapshotLoader;
import webfx.platforms.core.services.appcontainer.spi.ApplicationJob;
import webfx.platforms.core.services.log.Logger;
import webfx.platforms.core.services.push.server.PushClientDisconnectListener;
import webfx.platforms.core.services.push.server.PushServerService;
import webfx.platforms.core.services.update.UpdateArgument;
import webfx.platforms.core.services.update.UpdateService;

/**
 * @author Bruno Salmon
 */
public final class MongooseServerUnresponsiveClientSessionCloserJob implements ApplicationJob {

    private PushClientDisconnectListener disconnectListener;

    @Override
    public void onStart() {
        Object dataSourceId = DomainModelSnapshotLoader.getDataSourceModel().getId();
        PushServerService.addPushClientDisconnectListener(disconnectListener = pushClientId ->
                UpdateService.executeUpdate(new UpdateArgument("update session_connection set \"end\"=now() where process_id=?", new Object[]{pushClientId}, dataSourceId))
                        .setHandler(ar -> {
                            if (ar.failed())
                                Logger.log("Error while closing session for pushClientId=" + pushClientId, ar.cause());
                            else
                                Logger.log("Closed session for pushClientId=" + pushClientId);
                        }));
    }

    @Override
    public void onStop() {
        PushServerService.removePushClientDisconnectListener(disconnectListener);
    }

}
