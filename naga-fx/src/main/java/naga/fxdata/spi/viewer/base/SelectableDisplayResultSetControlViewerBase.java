package naga.fxdata.spi.viewer.base;

import javafx.beans.value.ObservableValue;
import naga.fxdata.SelectableDisplayResultSetControl;
import naga.fx.scene.SceneRequester;

/**
 * @author Bruno Salmon
 */
public abstract class SelectableDisplayResultSetControlViewerBase
        <C, N extends SelectableDisplayResultSetControl, NB extends SelectableDisplayResultSetControlViewerBase<C, N, NB, NM>, NM extends SelectableDisplayResultSetControlViewerMixin<C, N, NB, NM>>

        extends DisplayResultSetControlViewerBase<C, N, NB, NM> {

    @Override
    public void bind(N shape, SceneRequester sceneRequester) {
        super.bind(shape, sceneRequester);
        requestUpdateOnPropertiesChange(sceneRequester
                , node.selectionModeProperty()
                , node.displaySelectionProperty()
        );
    }

    @Override
    public boolean updateProperty(ObservableValue changedProperty) {
        return super.updateProperty(changedProperty)
                || updateProperty(node.selectionModeProperty(), changedProperty, mixin::updateSelectionMode)
                || updateProperty(node.displaySelectionProperty(), changedProperty, mixin::updateDisplaySelection)
                ;
    }
}