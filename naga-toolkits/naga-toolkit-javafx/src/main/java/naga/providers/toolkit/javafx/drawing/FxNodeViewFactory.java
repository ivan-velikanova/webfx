package naga.providers.toolkit.javafx.drawing;

import naga.providers.toolkit.javafx.drawing.view.*;
import naga.toolkit.drawing.scene.control.impl.ButtonImpl;
import naga.toolkit.drawing.scene.control.impl.CheckBoxImpl;
import naga.toolkit.drawing.scene.control.impl.TextFieldImpl;
import naga.toolkit.drawing.scene.impl.EmbedGuiNodeImpl;
import naga.toolkit.drawing.scene.impl.GroupImpl;
import naga.toolkit.drawing.scene.layout.impl.BorderPaneImpl;
import naga.toolkit.drawing.scene.layout.impl.FlowPaneImpl;
import naga.toolkit.drawing.scene.layout.impl.HBoxImpl;
import naga.toolkit.drawing.scene.layout.impl.VBoxImpl;
import naga.toolkit.drawing.shape.impl.CircleImpl;
import naga.toolkit.drawing.shape.impl.RectangleImpl;
import naga.toolkit.drawing.spi.impl.NodeViewFactoryImpl;
import naga.toolkit.drawing.text.impl.TextImpl;

/**
 * @author Bruno Salmon
 */
class FxNodeViewFactory extends NodeViewFactoryImpl {

    final static FxNodeViewFactory SINGLETON = new FxNodeViewFactory();

    private FxNodeViewFactory() {
        registerNodeViewFactory(RectangleImpl.class, FxRectangleView::new);
        registerNodeViewFactory(CircleImpl.class, FxCircleView::new);
        registerNodeViewFactory(TextImpl.class, FxTextView::new);
        registerNodeViewFactory(EmbedGuiNodeImpl.class, FxEmbedGuiNodeView::new);
        registerNodeViewFactory(GroupImpl.class, FxGroupView::new);
        registerNodeViewFactory(VBoxImpl.class, FxRegionView::new);
        registerNodeViewFactory(HBoxImpl.class, FxRegionView::new);
        registerNodeViewFactory(BorderPaneImpl.class, FxRegionView::new);
        registerNodeViewFactory(FlowPaneImpl.class, FxRegionView::new);
        registerNodeViewFactory(ButtonImpl.class, FxButtonView::new);
        registerNodeViewFactory(CheckBoxImpl.class, FxCheckBoxView::new);
        registerNodeViewFactory(TextFieldImpl.class, FxTextFieldView::new);
    }
}
