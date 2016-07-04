package naga.core.orm.expression.term;

import naga.core.orm.expression.Expression;
import naga.core.orm.expression.lci.DataReader;

/**
 * @author Bruno Salmon
 */
public class Any<T> extends BinaryBooleanExpression<T> {

    public Any(Expression<T> left, String operator, Expression<T> right) {
        super(left, operator + " any ", right, 5);
    }

    public boolean evaluateCondition(Object a, Object b, DataReader<T> dataReader) {
        throw new UnsupportedOperationException();
    }

}
