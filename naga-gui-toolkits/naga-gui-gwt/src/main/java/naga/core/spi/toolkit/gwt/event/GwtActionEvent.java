package naga.core.spi.toolkit.gwt.event;

import com.google.gwt.event.dom.client.ClickEvent;
import naga.core.spi.toolkit.event.ActionEvent;

/**
 * @author Bruno Salmon
 */
public class GwtActionEvent implements ActionEvent {

    private final ClickEvent clickEvent;

    public GwtActionEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }
}
