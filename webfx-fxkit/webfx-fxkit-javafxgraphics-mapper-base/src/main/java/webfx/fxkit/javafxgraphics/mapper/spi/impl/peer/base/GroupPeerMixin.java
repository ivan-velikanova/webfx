package webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.base;

import javafx.scene.Group;

/**
 * @author Bruno Salmon
 */
public interface GroupPeerMixin
        <N extends Group, NB extends GroupPeerBase<N, NB, NM>, NM extends GroupPeerMixin<N, NB, NM>>

        extends NodePeerMixin<N, NB, NM> {
}