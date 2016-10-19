package naga.providers.toolkit.html.drawing.view;

import elemental2.Element;

/**
 * @author Bruno Salmon
 */
public interface SvgShapeView {

    void syncSvgPropertiesFromShape();

    Element getSvgShapeElement();

}