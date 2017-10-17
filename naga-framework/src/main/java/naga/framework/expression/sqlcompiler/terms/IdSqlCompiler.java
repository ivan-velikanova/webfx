package naga.framework.expression.sqlcompiler.terms;

import naga.framework.expression.terms.IdExpression;
import naga.type.Types;

/**
 * @author Bruno Salmon
 */
public class IdSqlCompiler extends AbstractTermSqlCompiler<IdExpression> {

    public IdSqlCompiler() {
        super(IdExpression.class);
    }

    @Override
    public void compileExpressionToSql(IdExpression e, Options o) {
        Object compilingClass = o.build.getCompilingClass();
        o.build.addColumnInClause(o.build.getClassAlias(compilingClass, o.modelReader), o.modelReader.getDomainClassPrimaryKeySqlColumnName(compilingClass), e, null, o.clause, o.separator, o.grouped, Types.isBooleanType(e.getType()), o.generateQueryMapping);
    }
}
