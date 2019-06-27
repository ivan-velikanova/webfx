package mongoose.backend.activities.roomsgraphic;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import mongoose.backend.controls.bookingdetailspanel.BookingDetailsPanel;
import mongoose.backend.controls.masterslave.MasterSlaveView;
import mongoose.client.activity.eventdependent.EventDependentViewDomainActivity;
import mongoose.client.presentationmodel.HasSelectedDocumentProperty;
import mongoose.shared.entities.Document;
import mongoose.shared.entities.DocumentLine;
import mongoose.shared.entities.Site;
import webfx.framework.client.ui.filter.ReactiveExpressionFilterFactoryMixin;
import webfx.framework.client.ui.layouts.FlexBox;
import webfx.framework.client.ui.layouts.LayoutUtil;
import webfx.framework.shared.orm.entity.Entity;
import webfx.framework.shared.orm.entity.EntityId;
import webfx.fxkit.extra.controls.displaydata.datagrid.DataGrid;
import webfx.fxkit.extra.controls.displaydata.datagrid.SkinnedDataGrid;
import webfx.fxkit.extra.util.ImageStore;
import webfx.fxkit.util.properties.ObservableLists;
import webfx.fxkit.util.properties.Properties;

import java.util.Objects;

final class RoomsGraphicActivity extends EventDependentViewDomainActivity implements
        ReactiveExpressionFilterFactoryMixin,
        HasSelectedDocumentProperty {

    private final ObjectProperty<Document> selectedDocumentProperty = new SimpleObjectProperty<>();
    @Override public ObjectProperty<Document> selectedDocumentProperty() { return selectedDocumentProperty; }

    @Override
    public Node buildUi() {
        TabPane sitesTabPane = new TabPane();
        ObservableEntitiesMapper<Site, SiteTabController> sitesToTabControllersMapper = new ObservableEntitiesMapper<>(SiteTabController::new, (site, controller) -> controller.update(site), null);
        ObservableLists.bindConverted(sitesTabPane.getTabs(), sitesToTabControllersMapper.getMappedObservableList(), SiteTabController::getTab);
        MasterSlaveView masterSlaveView = new MasterSlaveView(sitesTabPane, MasterSlaveView.createAndBindSlaveViewIfApplicable(this, this, null).buildUi());
        masterSlaveView.slaveVisibleProperty().bind(Properties.compute(selectedDocumentProperty(), Objects::nonNull));
        // Setting up the master filter that controls the content displayed in the master view
        this.<Site>createReactiveExpressionFilter("{class: 'Site', alias: 's', fields: 'icon,name', where: `exists(select ResourceConfiguration where resource.site=s and item.family.code='acco')`, orderBy: 'ord,id'}")
                // Applying the event condition
                .combineIfNotNullOtherwiseForceEmptyResult(getPresentationModel().eventIdProperty(), eventId -> "{where:  `event=" + eventId + "`}")
                .setEntitiesHandler(sitesToTabControllersMapper::updateFromEntities)
                // Activating server push notification
                .setPush(true)
                // Everything set up, let's start now!
                .start();
        return masterSlaveView.buildUi();
    }

    class SiteTabController {

        final TabPane itemTabPane = new TabPane();
        final Tab siteTab = new Tab(null, itemTabPane);

        SiteTabController(Site site) {
            siteTab.setClosable(false);
            ObservableEntitiesMapper<Entity, ItemTabController> resourcesToItemTabControllersMapper = new ObservableEntitiesMapper<>(ItemTabController::new, (rc, controller) -> controller.update(rc), null);
            ObservableLists.bindConverted(itemTabPane.getTabs(), resourcesToItemTabControllersMapper.getMappedObservableList(), ItemTabController::getTab);
            Object sitePk = site.getPrimaryKey();
            createReactiveExpressionFilter("{class: 'ResourceConfiguration', fields: 'item.icon,item.name,resource.site', where: `resource.site=" + sitePk + " and item.family.code='acco'`, groupBy: 'item', orderBy: 'item.ord,item.id'}")
                    .setEntitiesHandler(resourcesToItemTabControllersMapper::updateFromEntities)
                    .bindActivePropertyTo(siteTab.selectedProperty())
                    .setPush(true)
                    .start();
            update(site);
        }

        void update(Site site) {
            siteTab.setText(site.getName());
            siteTab.setGraphic(ImageStore.createImageView(site.getIcon()));
        }

        Tab getTab() {
            return siteTab;
        }
    }

    class ItemTabController {

        final Pane resourcesBoxContainer = new FlexBox(10, 10);
        final Tab itemTab = new Tab(null, LayoutUtil.createVerticalScrollPane(resourcesBoxContainer));

        ItemTabController(Entity resourceConfiguration) {
            resourcesBoxContainer.setPadding(new Insets(10));
            itemTab.setClosable(false);
            ObservableEntitiesMapper<Entity, ResourceBoxController> resourcesToBoxControllersMapper = new ObservableEntitiesMapper<>(ResourceBoxController::new, (rc, box) -> box.update(rc), null);
            ObservableLists.bindConverted(resourcesBoxContainer.getChildren(), resourcesToBoxControllersMapper.getMappedObservableList(), ResourceBoxController::getNode);
            createReactiveExpressionFilter("{class: 'ResourceConfiguration', fields: 'resource.name,online', where: `resource.site=" + ((EntityId) resourceConfiguration.evaluate("resource.site")).getPrimaryKey() + " and item=" + ((EntityId) resourceConfiguration.evaluate("item")).getPrimaryKey() + "`, orderBy: 'name'}")
                    .setEntitiesHandler(resourcesToBoxControllersMapper::updateFromEntities)
                    .bindActivePropertyTo(itemTab.selectedProperty())
                    .setPush(true)
                    .start();
            update(resourceConfiguration);
        }

        void update(Entity resourceConfiguration) {
            itemTab.setText((String) resourceConfiguration.evaluate("item.name"));
            itemTab.setGraphic(ImageStore.createImageView((String) resourceConfiguration.evaluate("item.icon")));
        }

        Tab getTab() {
            return itemTab;
        }
    }

    private final static CornerRadii BOX_RADII = new CornerRadii(8);
    private final static Color ONLINE_BOX_COLOR = Color.color(0, 0.8, 0);
    private final static Color OFFLINE_BOX_COLOR = Color.ORANGE.darker();
    private final static Border BOX_BORDER = new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, BOX_RADII, null));
    private final static Background ONLINE_BOX_BACKGROUND  = new Background(new BackgroundFill(ONLINE_BOX_COLOR, BOX_RADII, null));
    private final static Background ONLINE_PEOPLE_BACKGROUND  = new Background(new BackgroundFill(ONLINE_BOX_COLOR.brighter(), null, null));
    private final static Background OFFLINE_BOX_BACKGROUND = new Background(new BackgroundFill(OFFLINE_BOX_COLOR, BOX_RADII, null));
    private final static Background OFFLINE_PEOPLE_BACKGROUND  = new Background(new BackgroundFill(OFFLINE_BOX_COLOR.brighter(), null, null));

    private DataGrid peopleBoxFocus;

    class ResourceBoxController {

        private final Label label = new Label();
        private DataGrid peopleBox = new SkinnedDataGrid();
        private final VBox box = new VBox(LayoutUtil.setMaxWidthToInfinite(label), peopleBox);

        ResourceBoxController(Entity resourceConfiguration) {
            label.setTextFill(Color.WHITE);
            label.setAlignment(Pos.CENTER);
            peopleBox.setHeaderVisible(false);
            peopleBox.setFullHeight(true);
            VBox.setMargin(label, new Insets(2));
            VBox.setMargin(peopleBox, new Insets(0, 5, 5, 5));
            VBox.setVgrow(peopleBox, Priority.ALWAYS);
            box.setMinWidth(150);
            RoomsGraphicActivity.this.<DocumentLine>createReactiveExpressionFilter("{class: 'DocumentLine', columns: 'document.<ident>', where: `resourceConfiguration=" + resourceConfiguration.getPrimaryKey() + " and !cancelled`, orderBy: 'id'}")
                    // Always loading the fields required for viewing the booking details
                    .combine("{fields: `document.(" + BookingDetailsPanel.REQUIRED_FIELDS_STRING_FILTER + ")`}")
                    .displayResultInto(peopleBox.displayResultProperty())
                    .applyDomainModelRowStyle()
                    .setPush(true)
                    .setSelectedEntityHandler(peopleBox.displaySelectionProperty(), dl -> {
                        if (dl != null) {
                            setSelectedDocument(dl.getDocument());
                            if (peopleBox != peopleBoxFocus && peopleBoxFocus != null) {
                                DataGrid previousFocus = peopleBoxFocus;
                                peopleBoxFocus = null;
                                previousFocus.setDisplaySelection(null);
                            }
                            peopleBoxFocus = peopleBox;
                        } else if (peopleBox == peopleBoxFocus)
                            setSelectedDocument(null);
                    })
                    .start();
            update(resourceConfiguration);
        }

        void update(Entity resourceConfiguration) {
            label.setText((String) resourceConfiguration.evaluate("resource.name"));
            Boolean online = resourceConfiguration.getBooleanFieldValue("online");
            box.setBackground(online ? ONLINE_BOX_BACKGROUND : OFFLINE_BOX_BACKGROUND);
            box.setBorder(BOX_BORDER);
            peopleBox.setBackground(online ? ONLINE_PEOPLE_BACKGROUND : OFFLINE_PEOPLE_BACKGROUND);
        }

        Node getNode() {
            return box;
        }
    }
}
