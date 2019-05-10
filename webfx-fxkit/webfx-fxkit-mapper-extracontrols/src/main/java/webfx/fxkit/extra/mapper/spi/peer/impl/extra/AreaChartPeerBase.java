package webfx.fxkit.extra.mapper.spi.peer.impl.extra;

import webfx.fxkit.extra.controls.displaydata.chart.AreaChart;

/**
 * @author Bruno Salmon
 */
public class AreaChartPeerBase
        <C, N extends AreaChart, NB extends AreaChartPeerBase<C, N, NB, NM>, NM extends AreaChartPeerMixin<C, N, NB, NM>>

        extends ChartPeerBase<C, N, NB, NM> {
}