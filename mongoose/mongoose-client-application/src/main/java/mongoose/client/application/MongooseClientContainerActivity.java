package mongoose.client.application;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import mongoose.client.activity.MongooseButtonFactoryMixin;
import webfx.framework.client.activity.impl.combinations.viewdomain.impl.ViewDomainActivityBase;
import webfx.framework.shared.operation.HasOperationCode;
import webfx.framework.client.operation.action.OperationActionFactoryMixin;
import webfx.framework.client.operations.i18n.ChangeLanguageRequestEmitter;
import webfx.framework.client.operations.route.RouteRequestEmitter;
import webfx.framework.client.ui.action.Action;
import webfx.framework.client.ui.action.ActionBinder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Bruno Salmon
 */
public class MongooseClientContainerActivity extends ViewDomainActivityBase
        implements MongooseButtonFactoryMixin
        , OperationActionFactoryMixin {

    @Override
    public Node buildUi() {
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(ActionBinder.bindChildrenToVisibleActions(new FlowPane(), navigationActions(), this::newButton));
        borderPane.centerProperty().bind(mountNodeProperty());
        return borderPane;
    }

    protected Collection<Action> navigationActions() {
        String[] sortedPossibleNavigationOperations = {
                "RouteBackward",
                "RouteForward",
                // Backend operations
                "RouteToOrganizations",
                "RouteToEvents",
                "RouteToBookings",
                "RouteToStatistics",
                "RouteToPayments",
                "RouteToStatements",
                "RouteToIncome",
                "RouteToLetters",
                "RouteToRoomsGraphic",
                "RouteToDiningAreas",
                "RouteToMonitor",
                "RouteToTester",
                "RouteToUsers",
                "RouteToOperations",
                "RouteToAuthorizations"
        };
        Collection<RouteRequestEmitter> providedEmitters = RouteRequestEmitter.getProvidedEmitters();
        return Stream.concat(
            Arrays.stream(sortedPossibleNavigationOperations)
                .map(operationCode -> providedEmitters.stream().filter(instantiator -> hasRequestOperationCode(instantiator.instantiateRouteRequest(this), operationCode)).findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(instantiator -> newAction(() -> instantiator.instantiateRouteRequest(this)))
            , ChangeLanguageRequestEmitter.getProvidedEmitters().stream()
                .map(instantiator -> newAction(instantiator::emitLanguageRequest))
        ).collect(Collectors.toList());
    }

    private static boolean hasRequestOperationCode(Object request, Object operationCode) {
        return request instanceof HasOperationCode && operationCode.equals(((HasOperationCode) request).getOperationCode());
    }
}
