package webfx.platform.shared.datascope;

import java.util.Map;

/**
 * @author Bruno Salmon
 */
public final class SchemaScope implements KeyDataScope {

    public static String KEY = "schema";

    private final Map<Object /*classId*/, ClassScope> classScopes;

    public SchemaScope(Map<Object, ClassScope> classScopes) {
        this.classScopes = classScopes;
    }

    @Override
    public Object getKey() {
        return KEY;
    }

    @Override
    public boolean intersects(DataScope otherScope) {
        return otherScope instanceof SchemaScope && intersects((SchemaScope) otherScope);
    }

    public boolean intersects(SchemaScope schemaScope) {
        for (ClassScope classScope1 : classScopes.values()) {
            ClassScope classScope2 = schemaScope.classScopes.get(classScope1.classId);
            if (classScope2 != null && classScope1.intersects(classScope2))
                return true;
        }
        return false;
    }

    public static SchemaScopeBuilder builder() {
        return new SchemaScopeBuilder();
    }

    public static final class ClassScope {
        private final Object classId;
        Object[] fieldIds; // may be null (=> means any field), otherwise list of fields

        public ClassScope(Object classId, Object[] fieldIds) {
            this.classId = classId;
            this.fieldIds = fieldIds;
        }

        public boolean intersects(ClassScope classScope) {
            if (!classId.equals(classScope.classId))
                return false;
            if (fieldIds == null || classScope.fieldIds == null)
                return true;
            return ScopeUtil.arraysIntersect(fieldIds, classScope.fieldIds);
        }
    }

}
