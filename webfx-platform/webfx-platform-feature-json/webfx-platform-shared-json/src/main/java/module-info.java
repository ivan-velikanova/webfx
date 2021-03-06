// Generated by WebFx

module webfx.platform.shared.json {

    // Direct dependencies modules
    requires java.base;
    requires webfx.lib.javacupruntime;
    requires webfx.platform.shared.util;

    // Exported packages
    exports dev.webfx.platform.shared.services.json;
    exports dev.webfx.platform.shared.services.json.parser;
    exports dev.webfx.platform.shared.services.json.parser.javacup;
    exports dev.webfx.platform.shared.services.json.parser.jflex;
    exports dev.webfx.platform.shared.services.json.spi;
    exports dev.webfx.platform.shared.services.json.spi.impl.listmap;

    // Used services
    uses dev.webfx.platform.shared.services.json.spi.JsonProvider;

}