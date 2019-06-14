package webfx.fxkit.javafxgraphics.mapper.highcoupling;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.stage.Window;
import webfx.fxkit.javafxgraphics.mapper.highcoupling.spi.ScenePeer;
import webfx.fxkit.javafxgraphics.mapper.highcoupling.spi.StagePeer;
import webfx.fxkit.javafxgraphics.mapper.highcoupling.spi.WindowPeer;
import webfx.fxkit.javafxgraphics.mapper.highcoupling.spi.FxKitMapperProvider;
import webfx.fxkit.javafxgraphics.mapper.spi.NodePeer;
import webfx.fxkit.javafxgraphics.mapper.spi.NodePeerFactoryRegistry;
import webfx.platform.shared.util.serviceloader.SingleServiceProvider;

import java.util.ServiceLoader;

/**
 * @author Bruno Salmon
 */
public final class FxKitMapper {

    private static FxKitMapperProvider getProvider() {
        return SingleServiceProvider.getProvider(FxKitMapperProvider.class, () -> ServiceLoader.load(FxKitMapperProvider.class));
    }

    public static StagePeer createStagePeer(Stage stage) {
        return getProvider().createStagePeer(stage);
    }

    public static WindowPeer createWindowPeer(Window window) {
        return getProvider().createWindowPeer(window);
    }

    public static ScenePeer createScenePeer(Scene scene) {
        return getProvider().createScenePeer(scene);
    }

    public static <N extends Node, V extends NodePeer<N>> V createNodePeer(N node) {
        return NodePeerFactoryRegistry.createNodePeer(node);
    }

    public static GraphicsContext getGraphicsContext2D(Canvas canvas) {
        return getProvider().getGraphicsContext2D(canvas);
    }
}