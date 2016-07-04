package naga.core.spi.toolkit.javafx.gauges;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.beans.property.Property;
import naga.core.spi.toolkit.javafx.JavaFxToolkit;
import naga.core.spi.toolkit.javafx.node.FxNode;
import naga.core.spi.toolkit.property.ConvertedProperty;

/**
 * @author Bruno Salmon
 */
public class FxGauge extends FxNode<Gauge> implements naga.core.spi.toolkit.gauges.Gauge<Gauge> {

    public FxGauge() {
        this(createGauge());
    }

    public FxGauge(Gauge gauge) {
        super(gauge);
    }

    private static Gauge createGauge() {
        return GaugeBuilder.create().build();
    }


    private ConvertedProperty<Integer, Number> valueProperty = JavaFxToolkit.numberToIntegerProperty(node.valueProperty());
    @Override
    public Property<Integer> valueProperty() {
        return valueProperty;
    }

    private ConvertedProperty<Integer, Number> minProperty = JavaFxToolkit.numberToIntegerProperty(node.minValueProperty());
    @Override
    public Property<Integer> minProperty() {
        return minProperty;
    }

    private ConvertedProperty<Integer, Number> maxProperty = JavaFxToolkit.numberToIntegerProperty(node.maxValueProperty());
    @Override
    public Property<Integer> maxProperty() {
        return maxProperty;
    }

}
