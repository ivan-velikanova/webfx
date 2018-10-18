package webfx.fxkit.mapper.spi.impl.peer.markers;

import javafx.beans.property.Property;

/**
 * @author Bruno Salmon
 */
public interface HasOpacityProperty {

    Property<Double> opacityProperty();
    default void setOpacity(Double opacity) { opacityProperty().setValue(opacity); }
    default Double getOpacity() { return opacityProperty().getValue(); }

}