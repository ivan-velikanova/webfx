// Generated by WebFx

module mongoose.backend.activities.income {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires mongoose.backend.masterslave;
    requires mongoose.client.activity;
    requires mongoose.client.entities;
    requires mongoose.client.presentationmodel;
    requires mongoose.client.util;
    requires mongoose.shared.entities;
    requires webfx.extras.visual;
    requires webfx.extras.visual.controls.grid;
    requires webfx.framework.client.action;
    requires webfx.framework.client.activity;
    requires webfx.framework.client.controls;
    requires webfx.framework.client.domain;
    requires webfx.framework.client.uifilter;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.domain;
    requires webfx.framework.shared.operation;
    requires webfx.framework.shared.router;
    requires webfx.platform.client.windowhistory;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.backend.activities.income;
    exports mongoose.backend.activities.income.routing;
    exports mongoose.backend.operations.routes.income;

    // Provided services
    provides webfx.framework.client.operations.route.RouteRequestEmitter with mongoose.backend.activities.income.RouteToIncomeRequestEmitter;
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.backend.activities.income.IncomeUiRoute;

}