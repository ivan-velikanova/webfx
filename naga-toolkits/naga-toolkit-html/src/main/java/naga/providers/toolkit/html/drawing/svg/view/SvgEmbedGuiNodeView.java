package naga.providers.toolkit.html.drawing.svg.view;

import naga.providers.toolkit.html.util.HtmlUtil;
import naga.providers.toolkit.html.util.SvgUtil;
import naga.toolkit.drawing.scene.EmbedGuiNode;
import naga.toolkit.drawing.spi.DrawingRequester;
import naga.toolkit.drawing.spi.view.base.EmbedGuiNodeViewBase;
import naga.toolkit.drawing.spi.view.base.EmbedGuiNodeViewMixin;

/**
 * @author Bruno Salmon
 */
public class SvgEmbedGuiNodeView extends SvgNodeView<EmbedGuiNode, EmbedGuiNodeViewBase, EmbedGuiNodeViewMixin> {

    public SvgEmbedGuiNodeView() {
        super(new EmbedGuiNodeViewBase(), SvgUtil.createSvgElement("foreignObject"));
    }

    @Override
    public void bind(EmbedGuiNode node, DrawingRequester drawingRequester) {
        setElementAttribute("width", "100%");
        setElementAttribute("height", "100%");
        HtmlUtil.setChild(getElement(), node.getGuiNode().unwrapToNativeNode());
        getNodeViewBase().bind(node, drawingRequester);
    }
}