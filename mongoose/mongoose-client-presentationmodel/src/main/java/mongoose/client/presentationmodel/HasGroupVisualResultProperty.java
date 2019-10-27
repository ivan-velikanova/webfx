package mongoose.client.presentationmodel;

import javafx.beans.property.ObjectProperty;
import webfx.extras.visual.VisualResult;

public interface HasGroupVisualResultProperty {

    ObjectProperty<VisualResult> groupVisualResultProperty();
    default VisualResult getGroupVisualResult() { return groupVisualResultProperty().get();}
    default void setGroupVisualResult(VisualResult value) { groupVisualResultProperty().set(value);}

}
