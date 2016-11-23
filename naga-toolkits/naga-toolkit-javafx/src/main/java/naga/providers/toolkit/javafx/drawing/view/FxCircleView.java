package naga.providers.toolkit.javafx.drawing.view;

import naga.toolkit.drawing.shapes.Circle;
import naga.toolkit.drawing.spi.DrawingRequester;
import naga.toolkit.drawing.spi.view.CircleView;

/**
 * @author Bruno Salmon
 */
public class FxCircleView extends FxShapeViewImpl<Circle, javafx.scene.shape.Circle> implements CircleView {

    @Override
    public void bind(Circle c, DrawingRequester drawingRequester) {
        setAndBindNodeProperties(c, new javafx.scene.shape.Circle());
        fxNode.centerXProperty().bind(c.centerXProperty());
        fxNode.centerYProperty().bind(c.centerYProperty());
        fxNode.radiusProperty().bind(c.radiusProperty());
    }
}