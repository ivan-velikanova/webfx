package naga.core.orm.expression.lci;

import naga.core.orm.expression.lci.DataReader;

/**
 * Loose coupling interface used by expressions to write date (into domain objects and parameters).
 *
 * @param <T> The java class used for domain objects.
 *
 * @author Bruno Salmon
 */
public interface DataWriter<T> extends DataReader<T> {

    /**
     * Set the field value of a domain object.
     *
     * @param domainObject the domain object
     * @param fieldId the field identifier
     * @param fieldValue the new field value
     */
    void setDomainFieldValue(T domainObject, Object fieldId, Object fieldValue);

    /**
     * Set the value of a parameter.
     * @param name the parameter name
     * @param value the new parameter value
     */
    void setParameterValue(String name, Object value);
}
