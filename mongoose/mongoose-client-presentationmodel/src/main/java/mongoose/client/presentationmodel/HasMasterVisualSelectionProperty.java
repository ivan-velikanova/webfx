package mongoose.client.presentationmodel;

import javafx.beans.property.ObjectProperty;
import webfx.extras.visual.VisualSelection;

public interface HasMasterVisualSelectionProperty {

    ObjectProperty<VisualSelection> masterVisualSelectionProperty();
    
    default VisualSelection getMasterVisualSelection() { return masterVisualSelectionProperty().getValue(); }
    
    default void setMasterVisualSelection(VisualSelection value) { masterVisualSelectionProperty().setValue(value); }
    
}
