package mongoose.i18n;

import naga.framework.operation.HasOperationCode;
import naga.framework.operation.i18n.ChangeLanguageRequest;
import naga.framework.ui.i18n.I18n;

/**
 * @author Bruno Salmon
 */
public final class EnglishLanguageRequest extends ChangeLanguageRequest implements HasOperationCode {

    private static final String OPERATION_CODE = "ENGLISH";

    public EnglishLanguageRequest(I18n i18n) {
        super("en", i18n);
    }

    @Override
    public Object getOperationCode() {
        return OPERATION_CODE;
    }
}
