// Generated by WebFx

module webfx.tutorial.service.alert {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.tutorial.service.services.alert;
    exports webfx.tutorial.service.services.alert.spi;

    // Used services
    uses webfx.tutorial.service.services.alert.spi.AlertServiceProvider;

}