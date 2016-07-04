package naga.core.spi.toolkit.javafx.charts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Chart;
import naga.core.spi.toolkit.charts.PieChart;
import naga.core.type.Type;
import naga.core.util.Numbers;
import naga.core.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class FxPieChart extends FxChart implements PieChart<Chart> {

    private ObservableList<javafx.scene.chart.PieChart.Data> pieData;
    private Function<Integer, String> seriesNameGetter;

    public FxPieChart() {
        this(createPieChart());
    }

    public FxPieChart(javafx.scene.chart.PieChart pieChart) {
        super(pieChart);
    }

    private static javafx.scene.chart.PieChart createPieChart() {
        javafx.scene.chart.PieChart pieChart = new javafx.scene.chart.PieChart();
        pieChart.setStartAngle(90);
        return pieChart;
    }

    @Override
    protected void createChartData(Type xType, Type yType, int pointPerSeriesCount, int seriesCount, Function<Integer, String> seriesNameGetter) {
        pieData = FXCollections.observableArrayList();
        this.seriesNameGetter = seriesNameGetter;
    }

    @Override
    protected void setChartDataX(Object xValue, int pointIndex) {
    }

    @Override
    protected void setChartDataY(Object yValue, int pointIndex, int seriesIndex) {
        pieData.add(new javafx.scene.chart.PieChart.Data(seriesNameGetter.apply(seriesIndex), Numbers.doubleValue(yValue)));
    }

    @Override
    protected void applyChartData() {
        ((javafx.scene.chart.PieChart) node).setData(pieData);
    }
}
