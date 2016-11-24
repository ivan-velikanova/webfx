package naga.providers.toolkit.javafx.drawing.view;

import naga.toolkit.drawing.shape.Rectangle;
import naga.toolkit.drawing.spi.DrawingRequester;
import naga.toolkit.drawing.spi.view.RectangleView;

/**
 * @author Bruno Salmon
 */
public class FxRectangleView extends FxShapeViewImpl<Rectangle, javafx.scene.shape.Rectangle> implements RectangleView {

    @Override
    public void bind(Rectangle r, DrawingRequester drawingRequester) {
        super.bind(r, drawingRequester);
        fxNode.xProperty().bind(r.xProperty());
        fxNode.yProperty().bind(r.yProperty());
        fxNode.widthProperty().bind(r.widthProperty());
        fxNode.heightProperty().bind(r.heightProperty());
        fxNode.arcWidthProperty().bind(r.arcWidthProperty());
        fxNode.arcHeightProperty().bind(r.arcHeightProperty());
    }

    @Override
    javafx.scene.shape.Rectangle createFxNode(Rectangle node) {
        return new javafx.scene.shape.Rectangle();
    }
}
