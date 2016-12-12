package naga.providers.toolkit.swing.fx.viewer;

import naga.providers.toolkit.swing.util.JGradientLabel;
import naga.providers.toolkit.swing.util.StyleUtil;
import naga.toolkit.display.DisplaySelection;
import naga.toolkit.fx.ext.cell.renderer.ImageTextRenderer;
import naga.toolkit.fx.ext.cell.renderer.ValueRenderer;
import naga.toolkit.fx.ext.control.DataGrid;
import naga.toolkit.display.DisplayColumn;
import naga.toolkit.display.DisplayResultSet;
import naga.toolkit.fx.scene.Node;
import naga.toolkit.fx.scene.image.ImageView;
import naga.toolkit.fx.scene.text.TextAlignment;
import naga.toolkit.fx.spi.viewer.base.DataGridViewerBase;
import naga.toolkit.fx.spi.viewer.base.DataGridViewerMixin;
import naga.toolkit.properties.markers.SelectionMode;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * @author Bruno Salmon
 */
public class SwingDataGridViewer
        extends SwingRegionViewer<DataGrid, DataGridViewerBase<Object>, DataGridViewerMixin<Object>>
        implements DataGridViewerMixin<Object>, SwingLayoutMeasurable<DataGrid> {

    private final JTable table = createTable();
    private final JScrollPane scrollPane = createTransparentScrollPane(table);
    private final DisplayTableModel tableModel = new DisplayTableModel();

    public SwingDataGridViewer() {
        super(new DataGridViewerBase<>());
        table.setModel(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> this.updateBackDisplaySelection());
    }

    private static JTable createTable() {
        JTable table = new JTable();
        table.setGridColor(StyleUtil.tableGridColor);
        table.setRowHeight(36);
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return table;
    }

    private static JScrollPane createTransparentScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setOpaque(false);
        scrollPane.setFont(null);
        scrollPane.setBorder(null);
        JViewport viewport = scrollPane.getViewport();
        viewport.setOpaque(false);
        viewport.setFont(null);
        viewport.setBorder(null);
        return scrollPane;
    }

    @Override
    public JComponent getSwingComponent() {
        return scrollPane;
    }

    @Override
    public void updateSelectionMode(SelectionMode mode) {
        int swingSelectionMode = 0;
        switch (mode) {
            case DISABLED:
            case SINGLE:
                swingSelectionMode = ListSelectionModel.SINGLE_SELECTION;
                break;
            case MULTIPLE:
                swingSelectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
                break;
        }
        table.getSelectionModel().setSelectionMode(swingSelectionMode);
    }

    private boolean syncingDisplaySelection;

    @Override
    public void updateDisplaySelection(DisplaySelection selection) {
        if (!syncingDisplaySelection) {
            syncingDisplaySelection = true;
            DisplaySelection displaySelection = getNode().getDisplaySelection();
            ListSelectionModel selectionModel = table.getSelectionModel();
            selectionModel.clearSelection();
            if (displaySelection != null)
                displaySelection.forEachRow(row -> selectionModel.addSelectionInterval(row, row));
            syncingDisplaySelection = false;
        }
    }

    private void updateBackDisplaySelection() {
        if (!syncingDisplaySelection) {
            syncingDisplaySelection = true;
            getNode().setDisplaySelection(DisplaySelection.createRowsSelection(table.getSelectedRows()));
            syncingDisplaySelection = false;
        }
    }

    @Override
    public void updateResultSet(DisplayResultSet rs) {
        DataGridViewerBase<Object> base = getNodeViewerBase();
        base.initGrid(rs);
        tableModel.fireTableStructureChanged();
        base.fillGrid(rs);
        table.doLayout();
    }

    @Override
    public void setUpGridColumn(int gridColumnIndex, int rsColumnIndex, DisplayColumn displayColumn) {
        TableColumn tableColumn = table.getColumnModel().getColumn(gridColumnIndex);
        naga.toolkit.display.Label label = displayColumn.getLabel();
        tableColumn.setHeaderRenderer(createTableCellRenderer(ImageTextRenderer.SINGLETON, displayColumn, true));
        tableColumn.setHeaderValue(new Object[]{label.getIconPath(), label.getText()});
        Double prefWidth = displayColumn.getStyle().getPrefWidth();
        if (prefWidth != null) {
            // Applying same prefWidth transformation as the PolymerTable (trying to
            if (label.getText() != null)
                prefWidth = prefWidth * 2.75; // factor compared to JavaFx style (temporary hardcoded)
            prefWidth = prefWidth + 10; // because of the 5px left and right padding
            int width = (int) (double) prefWidth;
            tableColumn.setPreferredWidth(width);
            tableColumn.setMinWidth(width);
            tableColumn.setMaxWidth(width);
        }
        tableColumn.setCellRenderer(createTableCellRenderer(displayColumn.getFxValueRenderer(), displayColumn, false));
    }

    @Override
    public void setCellContent(Object cell, Node content, DisplayColumn displayColumn) {
        // actually not called
    }

    private TableCellRenderer createTableCellRenderer(ValueRenderer valueRenderer, DisplayColumn displayColumn, boolean header) {
        return valueRenderer == null ? null : (jTable, value, isSelected, hasFocus, row, column) -> {
            Component cellComponent;
            String textAlign = displayColumn.getStyle().getTextAlign();
            if (valueRenderer != ImageTextRenderer.SINGLETON)
                cellComponent = toSwingComponent(valueRenderer.renderCellValue(value), getNode().getDrawing(), header || "center".equals(textAlign) ? TextAlignment.CENTER : "right".equals(textAlign) ? TextAlignment.RIGHT  : TextAlignment.LEFT);
            else {
                ImageTextRenderer renderer = ImageTextRenderer.SINGLETON;
                Object[] array = renderer.getAndCheckArray(value);
                ImageView imageView = renderer.getImage(array);
                JLabel imageLabel = (JLabel) toSwingComponent(imageView, getNode().getDrawing(), TextAlignment.LEFT);
                Icon icon = imageLabel == null ? null : imageLabel.getIcon();
                JGradientLabel gradientLabel = new JGradientLabel(renderer.getText(array), icon, SwingConstants.CENTER);
                if (header)
                    gradientLabel.setVerticalGradientColors(Color.WHITE, Color.LIGHT_GRAY);
                cellComponent = gradientLabel;
            }
            String rowStyle = getNodeViewerBase().getRowStyle(row);
            StyleUtil.styleCellComponent(cellComponent, rowStyle, header, textAlign, isSelected);
            return cellComponent;
        };
    }

    private class DisplayTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            DisplayResultSet rs = getNode().getDisplayResultSet();
            return rs == null ? 0 : rs.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return getNodeViewerBase().getGridColumnCount();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DisplayResultSet rs = getNode().getDisplayResultSet();
            return rs == null ? null : rs.getValue(rowIndex, getNodeViewerBase().gridColumnIndexToResultSetColumnIndex(columnIndex));
        }
    }
}