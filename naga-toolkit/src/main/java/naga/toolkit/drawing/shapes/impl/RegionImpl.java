package naga.toolkit.drawing.shapes.impl;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import naga.toolkit.drawing.shapes.*;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public class RegionImpl extends ParentImpl implements Region {

    public RegionImpl() {
    }

    public RegionImpl(Node... nodes) {
        super(nodes);
    }

    private final Property<Double> widthProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> widthProperty() {
        return widthProperty;
    }

    private final Property<Double> heightProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> heightProperty() {
        return heightProperty;
    }

    private final Property<Double> maxWidthProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> maxWidthProperty() {
        return maxWidthProperty;
    }

    private final Property<Double> minWidthProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> minWidthProperty() {
        return minWidthProperty;
    }

    private final Property<Double> maxHeightProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> maxHeightProperty() {
        return maxHeightProperty;
    }

    private final Property<Double> minHeightProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> minHeightProperty() {
        return minHeightProperty;
    }

    private final Property<Double> prefWidthProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> prefWidthProperty() {
        return prefWidthProperty;
    }

    private final Property<Double> prefHeightProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> prefHeightProperty() {
        return prefHeightProperty;
    }

    private final Property<Insets> insetsProperty = new SimpleObjectProperty<>(Insets.EMPTY);
    @Override
    public Property<Insets> insetsProperty() {
        return insetsProperty;
    }

    private final Property<Boolean> snapToPixelProperty = new SimpleObjectProperty<Boolean>(true) {
        @Override
        protected void invalidated() {
            updateSnappedInsets();
            requestParentLayout();
        }
    };
    @Override
    public Property<Boolean> snapToPixelProperty() {
        return snapToPixelProperty;
    }

    /**
     * cached results of snapped insets, this are used a lot during layout so makes sense
     * to keep fast access cached copies here.
     */
    private double snappedTopInset = 0;
    private double snappedRightInset = 0;
    private double snappedBottomInset = 0;
    private double snappedLeftInset = 0;

    /** Called to update the cached snapped insets */
    private void updateSnappedInsets() {
        Insets insets = getInsets();
        if (isSnapToPixel()) {
            snappedTopInset = Math.ceil(insets.getTop());
            snappedRightInset = Math.ceil(insets.getRight());
            snappedBottomInset = Math.ceil(insets.getBottom());
            snappedLeftInset = Math.ceil(insets.getLeft());
        } else {
            snappedTopInset = insets.getTop();
            snappedRightInset = insets.getRight();
            snappedBottomInset = insets.getBottom();
            snappedLeftInset = insets.getLeft();
        }
    }

    /**
     * If this region's snapToPixel property is true, returns a value rounded
     * to the nearest pixel, else returns the same value.
     * @param value the space value to be snapped
     * @return value rounded to nearest pixel
     */
    protected double snapSpace(double value) {
        return snapSpace(value, isSnapToPixel());
    }

    /**
     * If snapToPixel is true, then the value is rounded using Math.round. Otherwise,
     * the value is simply returned. This method will surely be JIT'd under normal
     * circumstances, however on an interpreter it would be better to inline this
     * method. However the use of Math.round here, and Math.ceil in snapSize is
     * not obvious, and so for code maintenance this logic is pulled out into
     * a separate method.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or rounded based on snapToPixel
     */
    private static double snapSpace(double value, boolean snapToPixel) {
        return snapToPixel ? Math.round(value) : value;
    }

    double snapPortion(double value) {
        return snapPortion(value, isSnapToPixel());
    }

    /**
     * If snapToPixel is true, then the value is ceil'd using Math.ceil. Otherwise,
     * the value is simply returned.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or ceil'd based on snapToPixel
     */
    private static double snapSize(double value, boolean snapToPixel) {
        return snapToPixel ? Math.ceil(value) : value;
    }

    /**
     * If this region's snapToPixel property is true, returns a value ceiled
     * to the nearest pixel, else returns the same value.
     * @param value the size value to be snapped
     * @return value ceiled to nearest pixel
     */
    protected double snapSize(double value) {
        return snapSize(value, isSnapToPixel());
    }

    /**
     * If snapToPixel is true, then the value is rounded using Math.round. Otherwise,
     * the value is simply returned.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or rounded based on snapToPixel
     */
    private static double snapPosition(double value, boolean snapToPixel) {
        return snapToPixel ? Math.round(value) : value;
    }

    private static double snapPortion(double value, boolean snapToPixel) {
        if (snapToPixel)
            return value == 0 ? 0 :(value > 0 ? Math.max(1, Math.floor(value)) : Math.min(-1, Math.ceil(value)));
        return value;
    }

    /**
     * Invoked by the region's parent during layout to set the region's
     * width and height. <b>Applications should not invoke this method directly</b>.
     * If an application needs to directly set the size of the region, it should
     * override its size constraints by calling <code>setMinSize()</code>,
     *  <code>setPrefSize()</code>, or <code>setMaxSize()</code> and it's parent
     * will honor those overrides during layout.
     *
     * @param width the target layout bounds width
     * @param height the target layout bounds height
     */
    @Override
    public void resize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    /**
     * Called during layout to determine the minimum width for this node.
     * Returns the value from <code>computeMinWidth(forHeight)</code> unless
     * the application overrode the minimum width by setting the minWidth property.
     *
     * @see #setMinWidth(Double)
     * @return the minimum width that this node should be resized to during layout
     */
    @Override
    public final double minWidth(double height) {
        double override = getMinWidth();
        if (override == USE_COMPUTED_SIZE)
            return super.minWidth(height);
        if (override == USE_PREF_SIZE)
            return prefWidth(height);
        return Double.isNaN(override) || override < 0 ? 0 : override;
    }

    /**
     * Called during layout to determine the minimum height for this node.
     * Returns the value from <code>computeMinHeight(forWidth)</code> unless
     * the application overrode the minimum height by setting the minHeight property.
     *
     * @see #setMinHeight
     * @return the minimum height that this node should be resized to during layout
     */
    @Override
    public final double minHeight(double width) {
        double override = getMinHeight();
        if (override == USE_COMPUTED_SIZE)
            return super.minHeight(width);
        if (override == USE_PREF_SIZE)
            return prefHeight(width);
        return Double.isNaN(override) || override < 0 ? 0 : override;
    }

    /**
     * Called during layout to determine the preferred width for this node.
     * Returns the value from <code>computePrefWidth(forHeight)</code> unless
     * the application overrode the preferred width by setting the prefWidth property.
     *
     * @see #setPrefWidth
     * @return the preferred width that this node should be resized to during layout
     */
    @Override
    public final double prefWidth(double height) {
        double override = getPrefWidth();
        if (override == USE_COMPUTED_SIZE)
            return super.prefWidth(height);
        return Double.isNaN(override) || override < 0 ? 0 : override;
    }

    /**
     * Called during layout to determine the preferred height for this node.
     * Returns the value from <code>computePrefHeight(forWidth)</code> unless
     * the application overrode the preferred height by setting the prefHeight property.
     *
     * @see #setPrefHeight
     * @return the preferred height that this node should be resized to during layout
     */
    @Override
    public final double prefHeight(double width) {
        double override = getPrefHeight();
        if (override == USE_COMPUTED_SIZE)
            return super.prefHeight(width);
        return Double.isNaN(override) || override < 0 ? 0 : override;
    }

    /**
     * Called during layout to determine the maximum width for this node.
     * Returns the value from <code>computeMaxWidth(forHeight)</code> unless
     * the application overrode the maximum width by setting the maxWidth property.
     *
     * @see #setMaxWidth
     * @return the maximum width that this node should be resized to during layout
     */
    @Override
    public final double maxWidth(double height) {
        double override = getMaxWidth();
        if (override == USE_COMPUTED_SIZE)
            return computeMaxWidth(height);
        if (override == USE_PREF_SIZE)
            return prefWidth(height);
        return Double.isNaN(override) || override < 0 ? 0 : override;
    }

    /**
     * Called during layout to determine the maximum height for this node.
     * Returns the value from <code>computeMaxHeight(forWidth)</code> unless
     * the application overrode the maximum height by setting the maxHeight property.
     *
     * @see #setMaxHeight
     * @return the maximum height that this node should be resized to during layout
     */
    @Override
    public final double maxHeight(double width) {
        double override = getMaxHeight();
        if (override == USE_COMPUTED_SIZE)
            return computeMaxHeight(width);
        if (override == USE_PREF_SIZE)
            return prefHeight(width);
        return Double.isNaN(override) || override < 0 ? 0 : override;
    }

    /**
     * Computes the minimum width of this region.
     * Returns the sum of the left and right insets by default.
     * region subclasses should override this method to return an appropriate
     * value based on their content and layout strategy.  If the subclass
     * doesn't have a VERTICAL content bias, then the height parameter can be
     * ignored.
     *
     * @return the computed minimum width of this region
     */
    @Override
    protected double computeMinWidth(double height) {
        return getInsets().getLeft() + getInsets().getRight();
    }

    /**
     * Computes the minimum height of this region.
     * Returns the sum of the top and bottom insets by default.
     * Region subclasses should override this method to return an appropriate
     * value based on their content and layout strategy.  If the subclass
     * doesn't have a HORIZONTAL content bias, then the width parameter can be
     * ignored.
     *
     * @return the computed minimum height for this region
     */
    @Override
    protected double computeMinHeight(double width) {
        return getInsets().getTop() + getInsets().getBottom();
    }

    /**
     * Computes the preferred width of this region for the given height.
     * Region subclasses should override this method to return an appropriate
     * value based on their content and layout strategy.  If the subclass
     * doesn't have a VERTICAL content bias, then the height parameter can be
     * ignored.
     *
     * @return the computed preferred width for this region
     */
    @Override
    protected double computePrefWidth(double height) {
        double w = super.computePrefWidth(height);
        Insets insets = getInsets();
        return insets.getLeft() + w + insets.getRight();
    }

    /**
     * Computes the preferred height of this region for the given width;
     * Region subclasses should override this method to return an appropriate
     * value based on their content and layout strategy.  If the subclass
     * doesn't have a HORIZONTAL content bias, then the width parameter can be
     * ignored.
     *
     * @return the computed preferred height for this region
     */
    @Override
    protected double computePrefHeight(double width) {
        double h = super.computePrefHeight(width);
        Insets insets = getInsets();
        return insets.getTop() + h + insets.getBottom();
    }

    /**
     * Computes the maximum width for this region.
     * Returns Double.MAX_VALUE by default.
     * Region subclasses may override this method to return an different
     * value based on their content and layout strategy.  If the subclass
     * doesn't have a VERTICAL content bias, then the height parameter can be
     * ignored.
     *
     * @return the computed maximum width for this region
     */
    protected double computeMaxWidth(double height) {
        return Double.MAX_VALUE;
    }

    /**
     * Computes the maximum height of this region.
     * Returns Double.MAX_VALUE by default.
     * Region subclasses may override this method to return a different
     * value based on their content and layout strategy.  If the subclass
     * doesn't have a HORIZONTAL content bias, then the width parameter can be
     * ignored.
     *
     * @return the computed maximum height for this region
     */
    protected double computeMaxHeight(double width) {
        return Double.MAX_VALUE;
    }


    /**
     * Utility method to get the top inset which includes padding and border
     * inset. Then snapped to whole pixels if isSnapToPixel() is true.
     *
     * @since JavaFX 8.0
     * @return Rounded up insets top
     */
    public final double snappedTopInset() {
        return snappedTopInset;
    }

    /**
     * Utility method to get the bottom inset which includes padding and border
     * inset. Then snapped to whole pixels if isSnapToPixel() is true.
     *
     * @since JavaFX 8.0
     * @return Rounded up insets bottom
     */
    public final double snappedBottomInset() {
        return snappedBottomInset;
    }

    /**
     * Utility method to get the left inset which includes padding and border
     * inset. Then snapped to whole pixels if isSnapToPixel() is true.
     *
     * @since JavaFX 8.0
     * @return Rounded up insets left
     */
    public final double snappedLeftInset() {
        return snappedLeftInset;
    }

    /**
     * Utility method to get the right inset which includes padding and border
     * inset. Then snapped to whole pixels if isSnapToPixel() is true.
     *
     * @since JavaFX 8.0
     * @return Rounded up insets right
     */
    public final double snappedRightInset() {
        return snappedRightInset;
    }


    double computeChildMinAreaWidth(Node child, Insets margin) {
        return computeChildMinAreaWidth(child, -1, margin, -1, false);
    }

    double computeChildMinAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
        boolean snap = isSnapToPixel();
        double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
        double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
        double alt = -1;
        if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) { // width depends on height
            double top = margin != null ? snapSpace(margin.getTop(), snap) : 0;
            double bottom = (margin != null ? snapSpace(margin.getBottom(), snap) : 0);
            double bo = child.getBaselineOffset();
            double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1 ?
                    height - top - bottom - baselineComplement :
                    height - top - bottom;
            if (fillHeight)
                alt = snapSize(boundedSize(
                        child.minHeight(-1), contentHeight,
                        child.maxHeight(-1)));
            else
                alt = snapSize(boundedSize(
                        child.minHeight(-1),
                        child.prefHeight(-1),
                        Math.min(child.maxHeight(-1), contentHeight)));
        }
        return left + snapSize(child.minWidth(alt)) + right;
    }

    double computeChildMinAreaHeight(Node child, Insets margin) {
        return computeChildMinAreaHeight(child, -1, margin, -1);
    }

    double computeChildMinAreaHeight(Node child, double minBaselineComplement, Insets margin, double width) {
        boolean snap = isSnapToPixel();
        double top = margin != null ? snapSpace(margin.getTop(), snap) : 0;
        double bottom = margin != null ? snapSpace(margin.getBottom(), snap) : 0;

        double alt = -1;
        if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) { // height depends on width
            double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
            double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
            alt = snapSize(width != -1 ? boundedSize(child.minWidth(-1), width - left - right, child.maxWidth(-1)) : child.maxWidth(-1));
        }

        // For explanation, see computeChildPrefAreaHeight
        if (minBaselineComplement != -1) {
            double baseline = child.getBaselineOffset();
            if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT)
                return top + snapSize(child.minHeight(alt)) + bottom + minBaselineComplement;
            return baseline + minBaselineComplement;
        }
        return top + snapSize(child.minHeight(alt)) + bottom;
    }

    double computeChildPrefAreaWidth(Node child, Insets margin) {
        return computeChildPrefAreaWidth(child, -1, margin, -1, false);
    }

    double computeChildPrefAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
        boolean snap = isSnapToPixel();
        double left = margin != null? snapSpace(margin.getLeft(), snap) : 0;
        double right = margin != null? snapSpace(margin.getRight(), snap) : 0;
        double alt = -1;
        if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) { // width depends on height
            double top = margin != null? snapSpace(margin.getTop(), snap) : 0;
            double bottom = margin != null? snapSpace(margin.getBottom(), snap) : 0;
            double bo = child.getBaselineOffset();
            double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1 ?
                    height - top - bottom - baselineComplement :
                    height - top - bottom;
            if (fillHeight) {
                alt = snapSize(boundedSize(
                        child.minHeight(-1), contentHeight,
                        child.maxHeight(-1)));
            } else {
                alt = snapSize(boundedSize(
                        child.minHeight(-1),
                        child.prefHeight(-1),
                        Math.min(child.maxHeight(-1), contentHeight)));
            }
        }
        return left + snapSize(boundedSize(child.minWidth(alt), child.prefWidth(alt), child.maxWidth(alt))) + right;
    }

    double computeChildPrefAreaHeight(Node child, Insets margin) {
        return computeChildPrefAreaHeight(child, -1, margin, -1);
    }

    double computeChildPrefAreaHeight(Node child, double prefBaselineComplement, Insets margin, double width) {
        boolean snap = isSnapToPixel();
        double top = margin != null? snapSpace(margin.getTop(), snap) : 0;
        double bottom = margin != null? snapSpace(margin.getBottom(), snap) : 0;

        double alt = -1;
        if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) { // height depends on width
            double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
            double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
            alt = snapSize(boundedSize(
                    child.minWidth(-1), width != -1 ? width - left - right
                            : child.prefWidth(-1), child.maxWidth(-1)));
        }

        if (prefBaselineComplement != -1) {
            double baseline = child.getBaselineOffset();
            if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT)
                // When baseline is same as height, the preferred height of the node will be above the baseline, so we need to add
                // the preferred complement to it
                return top + snapSize(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt))) + bottom
                        + prefBaselineComplement;
            // For all other Nodes, it's just their baseline and the complement.
            // Note that the complement already contain the Node's preferred (or fixed) height
            return top + baseline + prefBaselineComplement + bottom;
        }
        return top + snapSize(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt))) + bottom;
    }

    double computeChildMaxAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
        double max = child.maxWidth(-1);
        if (max == Double.MAX_VALUE)
            return max;
        boolean snap = isSnapToPixel();
        double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
        double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
        double alt = -1;
        if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) { // width depends on height
            double top = margin != null ? snapSpace(margin.getTop(), snap) : 0;
            double bottom = (margin != null ? snapSpace(margin.getBottom(), snap) : 0);
            double bo = child.getBaselineOffset();
            double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1 ?
                    height - top - bottom - baselineComplement :
                    height - top - bottom;
            if (fillHeight)
                alt = snapSize(boundedSize(
                        child.minHeight(-1), contentHeight,
                        child.maxHeight(-1)));
            else
                alt = snapSize(boundedSize(
                        child.minHeight(-1),
                        child.prefHeight(-1),
                        Math.min(child.maxHeight(-1), contentHeight)));
            max = child.maxWidth(alt);
        }
        // if min > max, min wins, so still need to call boundedSize()
        return left + snapSize(boundedSize(child.minWidth(alt), max, Double.MAX_VALUE)) + right;
    }

    double computeChildMaxAreaHeight(Node child, double maxBaselineComplement, Insets margin, double width) {
        double max = child.maxHeight(-1);
        if (max == Double.MAX_VALUE)
            return max;

        boolean snap = isSnapToPixel();
        double top = margin != null ? snapSpace(margin.getTop(), snap) : 0;
        double bottom = margin != null ? snapSpace(margin.getBottom(), snap) : 0;
        double alt = -1;
        if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) { // height depends on width
            double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
            double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
            alt = snapSize(width != -1? boundedSize(child.minWidth(-1), width - left - right, child.maxWidth(-1)) :
                    child.minWidth(-1));
            max = child.maxHeight(alt);
        }
        // For explanation, see computeChildPrefAreaHeight
        if (maxBaselineComplement != -1) {
            double baseline = child.getBaselineOffset();
            if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT)
                return top + snapSize(boundedSize(child.minHeight(alt), child.maxHeight(alt), Double.MAX_VALUE)) + bottom
                        + maxBaselineComplement;
            return top + baseline + maxBaselineComplement + bottom;
        }
        // if min > max, min wins, so still need to call boundedSize()
        return top + snapSize(boundedSize(child.minHeight(alt), max, Double.MAX_VALUE)) + bottom;
    }

    /* Max of children's minimum area widths */

    double computeMaxMinAreaWidth(List<Node> children, Callback<Node, Insets> margins) {
        return getMaxAreaWidth(children, margins, new double[] { -1 }, false, true);
    }

    double computeMaxMinAreaWidth(List<Node> children, Callback<Node, Insets> margins, double height, boolean fillHeight) {
        return getMaxAreaWidth(children, margins, new double[] { height }, fillHeight, true);
    }

    double computeMaxMinAreaWidth(List<Node> children, Callback<Node, Insets> childMargins, double childHeights[], boolean fillHeight) {
        return getMaxAreaWidth(children, childMargins, childHeights, fillHeight, true);
    }

    /* Max of children's minimum area heights */

    double computeMaxMinAreaHeight(List<Node>children, Callback<Node, Insets> margins, VPos valignment) {
        return getMaxAreaHeight(children, margins, null, valignment, true);
    }

    double computeMaxMinAreaHeight(List<Node>children, Callback<Node, Insets> margins, VPos valignment, double width) {
        return getMaxAreaHeight(children, margins, new double[] { width }, valignment, true);
    }

    double computeMaxMinAreaHeight(List<Node>children, Callback<Node, Insets> childMargins, double childWidths[], VPos valignment) {
        return getMaxAreaHeight(children, childMargins, childWidths, valignment, true);
    }

    /* Max of children's pref area widths */

    double computeMaxPrefAreaWidth(List<Node>children, Callback<Node, Insets> margins) {
        return getMaxAreaWidth(children, margins, new double[] { -1 }, false, false);
    }

    double computeMaxPrefAreaWidth(List<Node>children, Callback<Node, Insets> margins, double height, boolean fillHeight) {
        return getMaxAreaWidth(children, margins, new double[] { height }, fillHeight, false);
    }

    double computeMaxPrefAreaWidth(List<Node>children, Callback<Node, Insets> childMargins, double childHeights[], boolean fillHeight) {
        return getMaxAreaWidth(children, childMargins, childHeights, fillHeight, false);
    }

    /* Max of children's pref area heights */

    double computeMaxPrefAreaHeight(List<Node>children, Callback<Node, Insets> margins, VPos valignment) {
        return getMaxAreaHeight(children, margins, null, valignment, false);
    }

    double computeMaxPrefAreaHeight(List<Node>children, Callback<Node, Insets> margins, double width, VPos valignment) {
        return getMaxAreaHeight(children, margins, new double[] { width }, valignment, false);
    }

    double computeMaxPrefAreaHeight(List<Node>children, Callback<Node, Insets> childMargins, double childWidths[], VPos valignment) {
        return getMaxAreaHeight(children, childMargins, childWidths, valignment, false);
    }

    /* utility method for computing the max of children's min or pref heights, taking into account baseline alignment */
    private double getMaxAreaHeight(List<Node> children, Callback<Node,Insets> childMargins,  double childWidths[], VPos valignment, boolean minimum) {
        double singleChildWidth = childWidths == null ? -1 : childWidths.length == 1 ? childWidths[0] : Double.NaN;
        if (valignment == VPos.BASELINE) {
            double maxAbove = 0;
            double maxBelow = 0;
            for (int i = 0, maxPos = children.size(); i < maxPos; i++) {
                Node child = children.get(i);
                double childWidth = Double.isNaN(singleChildWidth) ? childWidths[i] : singleChildWidth;
                Insets margin = childMargins.call(child);
                double top = margin != null? snapSpace(margin.getTop()) : 0;
                double bottom = margin != null? snapSpace(margin.getBottom()) : 0;
                double baseline = child.getBaselineOffset();

                double childHeight = minimum? snapSize(child.minHeight(childWidth)) : snapSize(child.prefHeight(childWidth));
                if (baseline == BASELINE_OFFSET_SAME_AS_HEIGHT)
                    maxAbove = Math.max(maxAbove, childHeight + top);
                else {
                    maxAbove = Math.max(maxAbove, baseline + top);
                    maxBelow = Math.max(maxBelow,
                            snapSpace(minimum?snapSize(child.minHeight(childWidth)) : snapSize(child.prefHeight(childWidth))) -
                                    baseline + bottom);
                }
            }
            return maxAbove + maxBelow; //remind(aim): ceil this value?
        }
        double max = 0;
        for (int i = 0, maxPos = children.size(); i < maxPos; i++) {
            Node child = children.get(i);
            Insets margin = childMargins.call(child);
            double childWidth = Double.isNaN(singleChildWidth) ? childWidths[i] : singleChildWidth;
            max = Math.max(max, minimum?
                    computeChildMinAreaHeight(child, -1, margin, childWidth) :
                    computeChildPrefAreaHeight(child, -1, margin, childWidth));
        }
        return max;
    }

    /* utility method for computing the max of children's min or pref width, horizontal alignment is ignored for now */
    private double getMaxAreaWidth(List<Node> children, Callback<Node, Insets> childMargins, double childHeights[], boolean fillHeight, boolean minimum) {
        double singleChildHeight = childHeights == null ? -1 : childHeights.length == 1 ? childHeights[0] : Double.NaN;

        double max = 0;
        for (int i = 0, maxPos = children.size(); i < maxPos; i++) {
            Node child = children.get(i);
            Insets margin = childMargins.call(child);
            double childHeight = Double.isNaN(singleChildHeight) ? childHeights[i] : singleChildHeight;
            max = Math.max(max, minimum?
                    computeChildMinAreaWidth(children.get(i), -1, margin, childHeight, fillHeight) :
                    computeChildPrefAreaWidth(child, -1, margin, childHeight, fillHeight));
        }
        return max;
    }

    /**
     * Utility method which positions the child within an area of this
     * region defined by {@code areaX}, {@code areaY}, {@code areaWidth} x {@code areaHeight},
     * with a baseline offset relative to that area.
     * <p>
     * This function does <i>not</i> resize the node and uses the node's layout bounds
     * width and height to determine how it should be positioned within the area.
     * <p>
     * If the vertical alignment is {@code VPos.BASELINE} then it
     * will position the node so that its own baseline aligns with the passed in
     * {@code baselineOffset},  otherwise the baseline parameter is ignored.
     * <p>
     * If {@code snapToPixel} is {@code true} for this region, then the x/y position
     * values will be rounded to their nearest pixel boundaries.
     *
     * @param child the child being positioned within this region
     * @param areaX the horizontal offset of the layout area relative to this region
     * @param areaY the vertical offset of the layout area relative to this region
     * @param areaWidth  the width of the layout area
     * @param areaHeight the height of the layout area
     * @param areaBaselineOffset the baseline offset to be used if VPos is BASELINE
     * @param halignment the horizontal alignment for the child within the area
     * @param valignment the vertical alignment for the child within the area
     *
     */
    protected void positionInArea(Node child, double areaX, double areaY, double areaWidth, double areaHeight,
                                  double areaBaselineOffset, HPos halignment, VPos valignment) {
        positionInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
                Insets.EMPTY, halignment, valignment, isSnapToPixel());
    }

    /**
     * Utility method which positions the child within an area of this
     * region defined by {@code areaX}, {@code areaY}, {@code areaWidth} x {@code areaHeight},
     * with a baseline offset relative to that area.
     * <p>
     * This function does <i>not</i> resize the node and uses the node's layout bounds
     * width and height to determine how it should be positioned within the area.
     * <p>
     * If the vertical alignment is {@code VPos.BASELINE} then it
     * will position the node so that its own baseline aligns with the passed in
     * {@code baselineOffset},  otherwise the baseline parameter is ignored.
     * <p>
     * If {@code snapToPixel} is {@code true} for this region, then the x/y position
     * values will be rounded to their nearest pixel boundaries.
     * <p>
     * If {@code margin} is non-null, then that space will be allocated around the
     * child within the layout area.  margin may be null.
     *
     * @param child the child being positioned within this region
     * @param areaX the horizontal offset of the layout area relative to this region
     * @param areaY the vertical offset of the layout area relative to this region
     * @param areaWidth  the width of the layout area
     * @param areaHeight the height of the layout area
     * @param areaBaselineOffset the baseline offset to be used if VPos is BASELINE
     * @param margin the margin of space to be allocated around the child
     * @param halignment the horizontal alignment for the child within the area
     * @param valignment the vertical alignment for the child within the area
     *
     * @since JavaFX 8.0
     */
    public static void positionInArea(Node child, double areaX, double areaY, double areaWidth, double areaHeight,
                                      double areaBaselineOffset, Insets margin, HPos halignment, VPos valignment, boolean isSnapToPixel) {
        Insets childMargin = margin != null? margin : Insets.EMPTY;

        position(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
                snapSpace(childMargin.getTop(), isSnapToPixel),
                snapSpace(childMargin.getRight(), isSnapToPixel),
                snapSpace(childMargin.getBottom(), isSnapToPixel),
                snapSpace(childMargin.getLeft(), isSnapToPixel),
                halignment, valignment, isSnapToPixel);
    }

    /**
     * Utility method which lays out the child within an area of this
     * region defined by {@code areaX}, {@code areaY}, {@code areaWidth} x {@code areaHeight},
     * with a baseline offset relative to that area.
     * <p>
     * If the child is resizable, this method will resize it to fill the specified
     * area unless the node's maximum size prevents it.  If the node's maximum
     * size preference is less than the area size, the maximum size will be used.
     * If node's maximum is greater than the area size, then the node will be
     * resized to fit within the area, unless its minimum size prevents it.
     * <p>
     * If the child has a non-null contentBias, then this method will use it when
     * resizing the child.  If the contentBias is horizontal, it will set its width
     * first to the area's width (up to the child's max width limit) and then pass
     * that value to compute the child's height.  If child's contentBias is vertical,
     * then it will set its height to the area height (up to child's max height limit)
     * and pass that height to compute the child's width.  If the child's contentBias
     * is null, then it's width and height have no dependencies on each other.
     * <p>
     * If the child is not resizable (Shape, Group, etc) then it will only be
     * positioned and not resized.
     * <p>
     * If the child's resulting size differs from the area's size (either
     * because it was not resizable or it's sizing preferences prevented it), then
     * this function will align the node relative to the area using horizontal and
     * vertical alignment values.
     * If valignment is {@code VPos.BASELINE} then the node's baseline will be aligned
     * with the area baseline offset parameter, otherwise the baseline parameter
     * is ignored.
     * <p>
     * If {@code snapToPixel} is {@code true} for this region, then the resulting x,y
     * values will be rounded to their nearest pixel boundaries and the
     * width/height values will be ceiled to the next pixel boundary.
     *
     * @param child the child being positioned within this region
     * @param areaX the horizontal offset of the layout area relative to this region
     * @param areaY the vertical offset of the layout area relative to this region
     * @param areaWidth  the width of the layout area
     * @param areaHeight the height of the layout area
     * @param areaBaselineOffset the baseline offset to be used if VPos is BASELINE
     * @param halignment the horizontal alignment for the child within the area
     * @param valignment the vertical alignment for the child within the area
     *
     */
    protected void layoutInArea(Node child, double areaX, double areaY,
                                double areaWidth, double areaHeight,
                                double areaBaselineOffset,
                                HPos halignment, VPos valignment) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
                Insets.EMPTY, halignment, valignment);
    }

    /**
     * Utility method which lays out the child within an area of this
     * region defined by {@code areaX}, {@code areaY}, {@code areaWidth} x {@code areaHeight},
     * with a baseline offset relative to that area.
     * <p>
     * If the child is resizable, this method will resize it to fill the specified
     * area unless the node's maximum size prevents it.  If the node's maximum
     * size preference is less than the area size, the maximum size will be used.
     * If node's maximum is greater than the area size, then the node will be
     * resized to fit within the area, unless its minimum size prevents it.
     * <p>
     * If the child has a non-null contentBias, then this method will use it when
     * resizing the child.  If the contentBias is horizontal, it will set its width
     * first to the area's width (up to the child's max width limit) and then pass
     * that value to compute the child's height.  If child's contentBias is vertical,
     * then it will set its height to the area height (up to child's max height limit)
     * and pass that height to compute the child's width.  If the child's contentBias
     * is null, then it's width and height have no dependencies on each other.
     * <p>
     * If the child is not resizable (Shape, Group, etc) then it will only be
     * positioned and not resized.
     * <p>
     * If the child's resulting size differs from the area's size (either
     * because it was not resizable or it's sizing preferences prevented it), then
     * this function will align the node relative to the area using horizontal and
     * vertical alignment values.
     * If valignment is {@code VPos.BASELINE} then the node's baseline will be aligned
     * with the area baseline offset parameter, otherwise the baseline parameter
     * is ignored.
     * <p>
     * If {@code margin} is non-null, then that space will be allocated around the
     * child within the layout area.  margin may be null.
     * <p>
     * If {@code snapToPixel} is {@code true} for this region, then the resulting x,y
     * values will be rounded to their nearest pixel boundaries and the
     * width/height values will be ceiled to the next pixel boundary.
     *
     * @param child the child being positioned within this region
     * @param areaX the horizontal offset of the layout area relative to this region
     * @param areaY the vertical offset of the layout area relative to this region
     * @param areaWidth  the width of the layout area
     * @param areaHeight the height of the layout area
     * @param areaBaselineOffset the baseline offset to be used if VPos is BASELINE
     * @param margin the margin of space to be allocated around the child
     * @param halignment the horizontal alignment for the child within the area
     * @param valignment the vertical alignment for the child within the area
     */
    protected void layoutInArea(Node child, double areaX, double areaY,
                                double areaWidth, double areaHeight,
                                double areaBaselineOffset,
                                Insets margin,
                                HPos halignment, VPos valignment) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight,
                areaBaselineOffset, margin, true, true, halignment, valignment);
    }

    /**
     * Utility method which lays out the child within an area of this
     * region defined by {@code areaX}, {@code areaY}, {@code areaWidth} x {@code areaHeight},
     * with a baseline offset relative to that area.
     * <p>
     * If the child is resizable, this method will use {@code fillWidth} and {@code fillHeight}
     * to determine whether to resize it to fill the area or keep the child at its
     * preferred dimension.  If fillWidth/fillHeight are true, then this method
     * will only resize the child up to its max size limits.  If the node's maximum
     * size preference is less than the area size, the maximum size will be used.
     * If node's maximum is greater than the area size, then the node will be
     * resized to fit within the area, unless its minimum size prevents it.
     * <p>
     * If the child has a non-null contentBias, then this method will use it when
     * resizing the child.  If the contentBias is horizontal, it will set its width
     * first and then pass that value to compute the child's height.  If child's
     * contentBias is vertical, then it will set its height first
     * and pass that value to compute the child's width.  If the child's contentBias
     * is null, then it's width and height have no dependencies on each other.
     * <p>
     * If the child is not resizable (Shape, Group, etc) then it will only be
     * positioned and not resized.
     * <p>
     * If the child's resulting size differs from the area's size (either
     * because it was not resizable or it's sizing preferences prevented it), then
     * this function will align the node relative to the area using horizontal and
     * vertical alignment values.
     * If valignment is {@code VPos.BASELINE} then the node's baseline will be aligned
     * with the area baseline offset parameter, otherwise the baseline parameter
     * is ignored.
     * <p>
     * If {@code margin} is non-null, then that space will be allocated around the
     * child within the layout area.  margin may be null.
     * <p>
     * If {@code snapToPixel} is {@code true} for this region, then the resulting x,y
     * values will be rounded to their nearest pixel boundaries and the
     * width/height values will be ceiled to the next pixel boundary.
     *
     * @param child the child being positioned within this region
     * @param areaX the horizontal offset of the layout area relative to this region
     * @param areaY the vertical offset of the layout area relative to this region
     * @param areaWidth  the width of the layout area
     * @param areaHeight the height of the layout area
     * @param areaBaselineOffset the baseline offset to be used if VPos is BASELINE
     * @param margin the margin of space to be allocated around the child
     * @param fillWidth whether or not the child should be resized to fill the area width or kept to its preferred width
     * @param fillHeight whether or not the child should e resized to fill the area height or kept to its preferred height
     * @param halignment the horizontal alignment for the child within the area
     * @param valignment the vertical alignment for the child within the area
     */
    protected void layoutInArea(Node child, double areaX, double areaY,
                                double areaWidth, double areaHeight,
                                double areaBaselineOffset,
                                Insets margin, boolean fillWidth, boolean fillHeight,
                                HPos halignment, VPos valignment) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset, margin, fillWidth, fillHeight, halignment, valignment, isSnapToPixel());
    }

    /**
     * Utility method which lays out the child within an area of it's
     * parent defined by {@code areaX}, {@code areaY}, {@code areaWidth} x {@code areaHeight},
     * with a baseline offset relative to that area.
     * <p>
     * If the child is resizable, this method will use {@code fillWidth} and {@code fillHeight}
     * to determine whether to resize it to fill the area or keep the child at its
     * preferred dimension.  If fillWidth/fillHeight are true, then this method
     * will only resize the child up to its max size limits.  If the node's maximum
     * size preference is less than the area size, the maximum size will be used.
     * If node's maximum is greater than the area size, then the node will be
     * resized to fit within the area, unless its minimum size prevents it.
     * <p>
     * If the child has a non-null contentBias, then this method will use it when
     * resizing the child.  If the contentBias is horizontal, it will set its width
     * first and then pass that value to compute the child's height.  If child's
     * contentBias is vertical, then it will set its height first
     * and pass that value to compute the child's width.  If the child's contentBias
     * is null, then it's width and height have no dependencies on each other.
     * <p>
     * If the child is not resizable (Shape, Group, etc) then it will only be
     * positioned and not resized.
     * <p>
     * If the child's resulting size differs from the area's size (either
     * because it was not resizable or it's sizing preferences prevented it), then
     * this function will align the node relative to the area using horizontal and
     * vertical alignment values.
     * If valignment is {@code VPos.BASELINE} then the node's baseline will be aligned
     * with the area baseline offset parameter, otherwise the baseline parameter
     * is ignored.
     * <p>
     * If {@code margin} is non-null, then that space will be allocated around the
     * child within the layout area.  margin may be null.
     * <p>
     * If {@code snapToPixel} is {@code true} for this region, then the resulting x,y
     * values will be rounded to their nearest pixel boundaries and the
     * width/height values will be ceiled to the next pixel boundary.
     *
     * @param child the child being positioned within this region
     * @param areaX the horizontal offset of the layout area relative to this region
     * @param areaY the vertical offset of the layout area relative to this region
     * @param areaWidth  the width of the layout area
     * @param areaHeight the height of the layout area
     * @param areaBaselineOffset the baseline offset to be used if VPos is BASELINE
     * @param margin the margin of space to be allocated around the child
     * @param fillWidth whether or not the child should be resized to fill the area width or kept to its preferred width
     * @param fillHeight whether or not the child should e resized to fill the area height or kept to its preferred height
     * @param halignment the horizontal alignment for the child within the area
     * @param valignment the vertical alignment for the child within the area
     * @param isSnapToPixel whether to snap size and position to pixels
     * @since JavaFX 8.0
     */
    public static void layoutInArea(Node child, double areaX, double areaY,
                                    double areaWidth, double areaHeight,
                                    double areaBaselineOffset,
                                    Insets margin, boolean fillWidth, boolean fillHeight,
                                    HPos halignment, VPos valignment, boolean isSnapToPixel) {

        Insets childMargin = margin != null ? margin : Insets.EMPTY;

        double top = snapSpace(childMargin.getTop(), isSnapToPixel);
        double bottom = snapSpace(childMargin.getBottom(), isSnapToPixel);
        double left = snapSpace(childMargin.getLeft(), isSnapToPixel);
        double right = snapSpace(childMargin.getRight(), isSnapToPixel);

        if (valignment == VPos.BASELINE) {
            double bo = child.getBaselineOffset();
            if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT) {
                if (child.isResizable())
                    // Everything below the baseline is like an "inset". The Node with BASELINE_OFFSET_SAME_AS_HEIGHT cannot
                    // be resized to this area
                    bottom += snapSpace(areaHeight - areaBaselineOffset, isSnapToPixel);
                else
                    top = snapSpace(areaBaselineOffset - child.getLayoutBounds().getHeight(), isSnapToPixel);
            } else
                top = snapSpace(areaBaselineOffset - bo, isSnapToPixel);
        }

        if (child.isResizable()) {
            Point2D size = boundedNodeSizeWithBias(child, areaWidth - left - right, areaHeight - top - bottom,
                    fillWidth, fillHeight, TEMP_VEC2D);
            child.resize(snapSize(size.getX(), isSnapToPixel),snapSize(size.getY(), isSnapToPixel));
        }
        position(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
                top, right, bottom, left, halignment, valignment, isSnapToPixel);
    }

    private static void position(Node child, double areaX, double areaY, double areaWidth, double areaHeight,
                                 double areaBaselineOffset,
                                 double topMargin, double rightMargin, double bottomMargin, double leftMargin,
                                 HPos hpos, VPos vpos, boolean isSnapToPixel) {
        double xoffset = leftMargin + computeXOffset(areaWidth - leftMargin - rightMargin,
                child.getLayoutBounds().getWidth(), hpos);
        double yoffset;
        if (vpos == VPos.BASELINE) {
            double bo = child.getBaselineOffset();
            if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT)
                // We already know the layout bounds at this stage, so we can use them
                yoffset = areaBaselineOffset - child.getLayoutBounds().getHeight();
            else
                yoffset = areaBaselineOffset - bo;
        } else
            yoffset = topMargin + computeYOffset(areaHeight - topMargin - bottomMargin,
                    child.getLayoutBounds().getHeight(), vpos);
        double x = snapPosition(areaX + xoffset, isSnapToPixel);
        double y = snapPosition(areaY + yoffset, isSnapToPixel);

        child.relocate(x,y);
    }

    static Point2DImpl TEMP_VEC2D = new Point2DImpl();

    /**
     * Returns the size of a Node that should be placed in an area of the specified size,
     * bounded in it's min/max size, respecting bias.
     *
     * @param node the node
     * @param areaWidth the width of the bounding area where the node is going to be placed
     * @param areaHeight the height of the bounding area where the node is going to be placed
     * @param fillWidth if Node should try to fill the area width
     * @param fillHeight if Node should try to fill the area height
     * @param result Vec2d object for the result or null if new one should be created
     * @return Vec2d object with width(x parameter) and height (y parameter)
     */
    static Point2DImpl boundedNodeSizeWithBias(Node node, double areaWidth, double areaHeight,
                                         boolean fillWidth, boolean fillHeight, Point2DImpl result) {
        if (result == null)
            result = new Point2DImpl();

        Orientation bias = node.getContentBias();

        double childWidth;
        double childHeight;

        if (bias == null) {
            childWidth = boundedSize(
                    node.minWidth(-1), fillWidth ? areaWidth
                            : Math.min(areaWidth, node.prefWidth(-1)),
                    node.maxWidth(-1));
            childHeight = boundedSize(
                    node.minHeight(-1), fillHeight ? areaHeight
                            : Math.min(areaHeight, node.prefHeight(-1)),
                    node.maxHeight(-1));

        } else if (bias == Orientation.HORIZONTAL) {
            childWidth = boundedSize(
                    node.minWidth(-1), fillWidth ? areaWidth
                            : Math.min(areaWidth, node.prefWidth(-1)),
                    node.maxWidth(-1));
            childHeight = boundedSize(
                    node.minHeight(childWidth), fillHeight ? areaHeight
                            : Math.min(areaHeight, node.prefHeight(childWidth)),
                    node.maxHeight(childWidth));

        } else { // bias == VERTICAL
            childHeight = boundedSize(
                    node.minHeight(-1), fillHeight ? areaHeight
                            : Math.min(areaHeight, node.prefHeight(-1)),
                    node.maxHeight(-1));
            childWidth = boundedSize(
                    node.minWidth(childHeight), fillWidth ? areaWidth
                            : Math.min(areaWidth, node.prefWidth(childHeight)),
                    node.maxWidth(childHeight));
        }

        result.set(childWidth, childHeight);
        return result;
    }


    static double computeXOffset(double width, double contentWidth, HPos hpos) {
        switch(hpos) {
            case LEFT:
                return 0;
            case CENTER:
                return (width - contentWidth) / 2;
            case RIGHT:
                return width - contentWidth;
            default:
                throw new AssertionError("Unhandled hPos");
        }
    }

    static double computeYOffset(double height, double contentHeight, VPos vpos) {
        switch(vpos) {
            case BASELINE:
            case TOP:
                return 0;
            case CENTER:
                return (height - contentHeight) / 2;
            case BOTTOM:
                return height - contentHeight;
            default:
                throw new AssertionError("Unhandled vPos");
        }
    }
}