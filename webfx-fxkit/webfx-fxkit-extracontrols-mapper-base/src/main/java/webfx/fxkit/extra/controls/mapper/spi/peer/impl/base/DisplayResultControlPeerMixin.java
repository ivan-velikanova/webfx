package webfx.fxkit.extra.controls.mapper.spi.peer.impl.base;

import webfx.fxkit.extra.controls.displaydata.DisplayResultControl;
import webfx.fxkit.extra.displaydata.DisplayResult;
import webfx.fxkit.javafxcontrols.mapper.spi.impl.peer.base.ControlPeerMixin;

/**
 * @author Bruno Salmon
 */
public interface DisplayResultControlPeerMixin
        <C, N extends DisplayResultControl, NB extends DisplayResultControlPeerBase<C, N, NB, NM>, NM extends DisplayResultControlPeerMixin<C, N, NB, NM>>

        extends ControlPeerMixin<N, NB, NM> {

    void updateResult(DisplayResult rs);

}