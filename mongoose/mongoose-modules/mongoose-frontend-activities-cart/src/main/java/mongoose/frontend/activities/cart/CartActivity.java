package mongoose.frontend.activities.cart;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import mongoose.client.activities.shared.TranslateFunction;
import mongoose.client.aggregates.CartAggregate;
import mongoose.client.bookingoptionspanel.BookingOptionsPanel;
import mongoose.client.businesslogic.workingdocument.WorkingDocument;
import mongoose.client.sectionpanel.SectionPanelFactory;
import mongoose.frontend.operations.fees.RouteToFeesRequest;
import mongoose.frontend.operations.options.RouteToOptionsRequest;
import mongoose.frontend.operations.payment.RouteToPaymentRequest;
import mongoose.frontend.operations.terms.RouteToTermsRequest;
import mongoose.shared.domainmodel.formatters.PriceFormatter;
import mongoose.shared.entities.Document;
import mongoose.shared.entities.History;
import mongoose.shared.entities.Mail;
import webfx.framework.client.services.i18n.I18n;
import webfx.framework.client.ui.action.Action;
import webfx.framework.client.ui.action.impl.WritableAction;
import webfx.framework.client.ui.controls.dialog.DialogCallback;
import webfx.framework.client.ui.controls.dialog.DialogUtil;
import webfx.framework.client.ui.controls.dialog.GridPaneBuilder;
import webfx.framework.client.ui.layouts.FlexBox;
import webfx.framework.client.ui.layouts.LayoutUtil;
import webfx.framework.shared.expression.lci.DataReader;
import webfx.framework.shared.expression.terms.function.Function;
import webfx.framework.shared.orm.entity.Entities;
import webfx.framework.shared.orm.entity.Entity;
import webfx.framework.shared.orm.entity.UpdateStore;
import webfx.framework.shared.orm.mapping.EntityListToDisplayResultMapper;
import webfx.fxkit.extra.control.DataGrid;
import webfx.fxkit.extra.displaydata.DisplayResult;
import webfx.fxkit.extra.displaydata.DisplaySelection;
import webfx.fxkit.extra.type.PrimType;
import webfx.platform.client.services.uischeduler.UiScheduler;
import webfx.platform.shared.services.log.Logger;
import webfx.platform.shared.util.Arrays;
import webfx.platform.shared.util.Strings;
import webfx.platform.shared.util.collection.Collections;

import java.util.List;

import static webfx.framework.client.ui.util.formatter.FormatterRegistry.registerFormatter;

/**
 * @author Bruno Salmon
 */
final class CartActivity extends CartBasedActivity {

    private final Property<DisplayResult> documentDisplayResultProperty = new SimpleObjectProperty<>();
    private final Property<DisplayResult> paymentDisplayResultProperty = new SimpleObjectProperty<>();
    // Display input & output
    private final Property<DisplaySelection> documentDisplaySelectionProperty = new SimpleObjectProperty<>();

    private Label bookingLabel;
    private BookingOptionsPanel bookingOptionsPanel;
    private WorkingDocument selectedWorkingDocument;
    private final WritableAction cancelBookingAction = new WritableAction(newCancelAction(this::cancelBooking), "*");
    private final WritableAction modifyBookingAction = new WritableAction(newAction("Modify", this::modifyBooking), "*");
    private final WritableAction contactUsAction     = new WritableAction(newAction("ContactUs", this::contactUs), "*");
    private final WritableAction showPaymentsAction  = new WritableAction(newAction("YourPayments", this::showPayments), "*");
    private BorderPane optionsPanel;
    private BorderPane paymentsPanel;
    private FlexBox bottomButtonBar;

    @Override
    public Node buildUi() {
        BorderPane bookingsPanel = SectionPanelFactory.createSectionPanel("YourBookings");
        DataGrid documentTable = new DataGrid(); //LayoutUtil.setMinMaxHeightToPref(new DataGrid());
        documentTable.setFullHeight(true);
        bookingsPanel.setCenter(documentTable);
        optionsPanel = SectionPanelFactory.createSectionPanel(null, bookingLabel = new Label());
        bookingOptionsPanel = new BookingOptionsPanel();
        optionsPanel.setCenter(bookingOptionsPanel.getGrid());
        paymentsPanel = SectionPanelFactory.createSectionPanel("YourPayments");
        DataGrid paymentTable = new DataGrid();
        paymentTable.setFullHeight(true);
        paymentsPanel.setCenter(paymentTable);

        FlexBox bookingButtonBar = createFlexButtonBar(
                cancelBookingAction
                , modifyBookingAction
                , contactUsAction
                , newAction("TermsAndConditions", this::readTerms)
        );
        optionsPanel.setBottom(LayoutUtil.createPadding(bookingButtonBar));

        bottomButtonBar = createFlexButtonBar(
                newAction("AddAnotherBooking", this::addBooking)
                , showPaymentsAction
                , newAction("MakePayment", this::makePayment)
        );

        LayoutUtil.setUnmanagedWhenInvisible(optionsPanel).setVisible(false);
        LayoutUtil.setUnmanagedWhenInvisible(paymentsPanel).setVisible(false);
        LayoutUtil.setUnmanagedWhenInvisible(bottomButtonBar).setVisible(false);

        // Binding the UI with the presentation model for further state changes
        // User inputs: the UI state changes are transferred in the presentation model
        documentTable.displaySelectionProperty().bindBidirectional(documentDisplaySelectionProperty);
        // User outputs: the presentation model changes are transferred in the UI
        documentTable.displayResultProperty().bind(documentDisplayResultProperty);
        paymentTable.displayResultProperty().bind(paymentDisplayResultProperty);

        syncBookingOptionsPanelIfReady();

        return new BorderPane(LayoutUtil.createVerticalScrollPaneWithPadding(new VBox(20, bookingsPanel, optionsPanel, paymentsPanel, bottomButtonBar)));
    }

    private FlexBox createFlexButtonBar(Action... actions) {
        // not compiling with GWT return new FlexBox(20, 10, Arrays.map(actions, action -> LayoutUtil.setMaxWidthToInfinite(LayoutUtil.setMinWidthToPref(newButton(action))), Node[]::new));
        FlexBox flexButtonBar = new FlexBox(20, 10);
        Arrays.forEach(actions, action -> flexButtonBar.getChildren().add(LayoutUtil.setMaxWidthToInfinite(LayoutUtil.setMinWidthToPref(newButton(action)))));
        return flexButtonBar;
    }

    @Override
    protected void startLogic() {
        super.startLogic();
        new TranslateFunction().register();
        new Function<Document>("documentStatus", null, null, PrimType.STRING, true) {
            @Override
            public Object evaluate(Document document, DataReader<Document> dataReader) {
                return I18n.instantTranslate(getDocumentStatus(document));
            }
        }.register();
        documentDisplaySelectionProperty.addListener((observable, oldValue, selection) -> {
            int selectedRow = selection.getSelectedRow();
            if (selectedRow != -1) {
                setSelectedWorkingDocument(Collections.get(cartService().getCartWorkingDocuments(), selectedRow));
                syncBookingOptionsPanelIfReady();
            }
        });
    }

    private void setSelectedWorkingDocument(WorkingDocument selectedWorkingDocument) {
        this.selectedWorkingDocument = selectedWorkingDocument;
        if (bottomButtonBar != null) {
            boolean visible = selectedWorkingDocument != null;
            optionsPanel.setVisible(visible);
            bottomButtonBar.setVisible(visible);
        }
    }

    private void autoSelectWorkingDocument() {
        UiScheduler.runInUiThread(() -> {
            int selectedIndex = indexOfWorkingDocument(selectedWorkingDocument);
            CartAggregate cartAggregate = cartService();
            if (selectedIndex == -1 && cartAggregate.getEventAggregate() != null)
                selectedIndex = indexOfWorkingDocument(cartAggregate.getEventAggregate().getWorkingDocument());
            documentDisplaySelectionProperty.setValue(DisplaySelection.createSingleRowSelection(Math.max(0, selectedIndex)));
            updatePaymentsVisibility();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        autoSelectWorkingDocument();
    }

    @Override
    protected void onCartLoaded() {
        CartAggregate cartAggregate = cartService();
        if (cartAggregate.getEventAggregate() != null)
            registerFormatter("priceWithCurrency", new PriceFormatter(getEvent()));
        displayEntities(cartAggregate.getCartDocuments(), "[" +
                        "'ref'," +
                        "'person_name'," +
                        "{expression: 'price_net', format: 'priceWithCurrency'}," +
                        "{expression: 'price_deposit', format: 'priceWithCurrency'}," +
                        "{expression: 'price_balance', format: 'priceWithCurrency'}," +
                        "{expression: 'documentStatus(this)', label: 'Status', textAlign: 'center'}" +
                        "]"
                , "Document", documentDisplayResultProperty);
        displayEntities(cartAggregate.getCartPayments(), "[" +
                        "{expression: 'date', format: 'dateTime'}," +
                        "{expression: 'document.ref', label: 'Booking ref'}," +
                        "{expression: 'translate(method)', label: 'Method', textAlign: 'center'}," +
                        "{expression: 'amount', format: 'priceWithCurrency'}," +
                        "{expression: 'translate(pending ? `PendingStatus` : successful ? `SuccessfulStatus` : `FailedStatus`)', label: 'Status', textAlign: 'center'}" +
                        "]"
                , "MoneyTransfer", paymentDisplayResultProperty);
        autoSelectWorkingDocument();
    }

    private int indexOfWorkingDocument(WorkingDocument workingDocument) {
        if (workingDocument == null)
            return -1;
        return Collections.indexOf(cartService().getCartWorkingDocuments(), wd -> Entities.sameId(wd.getDocument(), workingDocument.getDocument()));
    }

    private void displayEntities(List<? extends Entity> entities, String columnsDefinition, Object classId, Property<DisplayResult> displayResultProperty) {
        displayResultProperty.setValue(EntityListToDisplayResultMapper.createDisplayResult(entities, columnsDefinition
                , getDataSourceModel().getDomainModel(), classId));
    }

    private void syncBookingOptionsPanelIfReady() {
        if (bookingOptionsPanel != null && selectedWorkingDocument != null) {
            bookingOptionsPanel.syncUiFromModel(selectedWorkingDocument);
            Document selectedDocument = selectedWorkingDocument.getDocument();
            bookingLabel.setText(selectedDocument.getFullName() + " - " + I18n.instantTranslate("Status:") + " " + I18n.instantTranslate(getDocumentStatus(selectedDocument)));
            disableCancelModifyButton(selectedDocument.isCancelled());
            updatePaymentsVisibility();
        }
    }

    private void disableCancelModifyButton(boolean disable) {
        cancelBookingAction.setDisabled(disable);
        modifyBookingAction.setDisabled(disable);
        contactUsAction.setDisabled(disable || selectedWorkingDocument == null || selectedWorkingDocument.getDocument().getEmail() == null);
    }

    private void updatePaymentsVisibility() {
        if (paymentsPanel != null) {
            if (Collections.isEmpty(cartService().getCartPayments())) {
                showPaymentsAction.setVisible(false);
                paymentsPanel.setVisible(false);
            } else
                showPaymentsAction.setVisible(!paymentsPanel.isVisible());
        }
    }

    private String getDocumentStatus(Document document) { // TODO: return a structure instead with also background and message to display in the booking panel (like in javascript version)
        if (document == null)
            return null;
        if (document.isCancelled())
            return "CancelledStatus";
        if (!document.isConfirmed()) {
            if (documentNeedsDeposit(document) && !hasPendingPayment(document))
                return "IncompleteStatus";
            return "InProgressStatus";
        }
        if (documentHasBalance(document))
            return "ConfirmedStatus";
        return "CompleteStatus";
    }

    private boolean documentNeedsDeposit(Document document) {
        return document.getPriceDeposit() < document.getPriceMinDeposit();
    }

    private boolean documentHasBalance(Document document) {
        return document.getPriceDeposit() < document.getPriceNet();
    }

    private boolean hasPendingPayment(Document document) {
        return Collections.anyMatch(cartService().getCartPayments(), mt -> mt.getDocument() == document && mt.isPending());
    }

    private void modifyBooking() {
        new RouteToOptionsRequest(selectedWorkingDocument, getHistory()).execute();
        setSelectedWorkingDocument(null);
    }

    private void cancelBooking() {
        DialogUtil.showModalNodeInGoldLayout(new GridPaneBuilder()
                        .addNodeFillingRow(newLabel("BookingCancellation"))
                        .addNodeFillingRow(newLabel("ConfirmBookingCancellation"))
                        .addButtons("YesBookingCancellation", dialogCallback -> {
                                    disableCancelModifyButton(true);
                                    Document selectedDocument = selectedWorkingDocument.getDocument();
                                    UpdateStore updateStore = UpdateStore.createAbove(selectedDocument.getStore());
                                    Document updatedDocument = updateStore.updateEntity(selectedDocument);
                                    updatedDocument.setCancelled(true);
                                    updateStore.executeUpdate().setHandler(ar -> {
                                        if (ar.succeeded()) {
                                            reloadCart();
                                            dialogCallback.closeDialog();
                                        }
                                    });
                                },
                                "NoBookingCancellation", DialogCallback::closeDialog)
                , (Pane) getNode());
    }

    private void contactUs() {
        TextField subjectTextField = newTextFieldWithPrompt("SubjectPlaceholder");
        TextArea bodyTextArea = newTextAreaWithPrompt("YourMessagePlaceholder");
        DialogUtil.showModalNodeInGoldLayout(new GridPaneBuilder()
                        .addNodeFillingRow(SectionPanelFactory.createSectionPanel("Subject", subjectTextField))
                        .addNodeFillingRowAndHeight(SectionPanelFactory.createSectionPanel("YourMessage", bodyTextArea))
                        .addButtons("Send", dialogCallback -> {
                                    Document doc = selectedWorkingDocument.getDocument();
                                    UpdateStore updateStore = UpdateStore.createAbove(doc.getStore());
                                    Mail mail = updateStore.insertEntity(Mail.class);
                                    mail.setDocument(doc);
                                    mail.setFromName(doc.getFullName());
                                    mail.setFromEmail(doc.getEmail());
                                    mail.setSubject("[" + doc.getRef() + "] " + subjectTextField.getText());
                                    String cartUrl = getHistory().getCurrentLocation().getPath();
                                    // building mail content
                                    String content = bodyTextArea.getText()
                                            + "\n-----\n"
                                            + doc.getEvent().getName() + " - #" + doc.getRef()
                                            + " - <a href=mailto:'" + doc.getEmail() + "'>" + doc.getFullName() + "</a>\n"
                                            + "<a href='" + cartUrl + "'>" + cartUrl + "</a>";
                                    content = Strings.replaceAll(content, "\r", "<br/>");
                                    content = Strings.replaceAll(content, "\n", "<br/>");
                                    content = "<html>" + content + "</html>";
                                    // setting mail content
                                    mail.setContent(content);
                                    mail.setOut(false); // indicate that this mail is not an outgoing email (sent to booker) but an ingoing mail (sent to registration team)
                                    History history = updateStore.insertEntity(History.class); // new server history entry
                                    history.setDocument(doc);
                                    history.setMail(mail);
                                    history.setUsername("online");
                                    history.setComment("Sent '" + subjectTextField.getText() + "'");
                                    updateStore.executeUpdate().setHandler(ar -> {
                                        if (ar.failed())
                                            Logger.log("Error", ar.cause());
                                        else {
                                            dialogCallback.closeDialog();
                                        }
                                    });
                                },
                                "Cancel", DialogCallback::closeDialog)
                , (Pane) getNode(), 0.9, 0.8);
    }

    private void readTerms() {
        //new TermsDialog(getEventId(), getDataSourceModel(), (Pane) getNode()).show();
        new RouteToTermsRequest(getEventId(), getHistory()).execute();
    }

    private void addBooking() {
        new RouteToFeesRequest(getEventId(), getHistory()).execute();
    }

    private void showPayments() {
        paymentsPanel.setVisible(true);
        showPaymentsAction.setVisible(false);
    }

    private void makePayment() {
        new RouteToPaymentRequest(getCartUuid(), getHistory()).execute();
    }
}
