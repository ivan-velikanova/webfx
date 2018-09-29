package mongoose.frontend.activities.terms;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import mongoose.client.bookingprocess.activity.BookingProcessPresentationModel;
import webfx.fxkits.extra.displaydata.DisplayResult;

/**
 * @author Bruno Salmon
 */
final class TermsPresentationModel extends BookingProcessPresentationModel {

    // Display output

    private final Property<DisplayResult> termsLetterDisplayResultProperty = new SimpleObjectProperty<>();
    Property<DisplayResult> termsLetterDisplayResultProperty() { return termsLetterDisplayResultProperty; }

}