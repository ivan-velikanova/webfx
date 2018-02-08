package naga.framework.orm.entity.lciimpl;

import naga.framework.orm.domainmodel.DomainField;
import naga.framework.orm.entity.Entity;
import naga.framework.orm.entity.EntityStore;
import naga.framework.expression.lci.DataWriter;

/**
 * @author Bruno Salmon
 */
public class EntityDataWriter<E extends Entity> extends EntityDataReader<E> implements DataWriter<E> {

    public EntityDataWriter(EntityStore entityStore) {
        super(entityStore);
    }

    @Override
    public void setDomainFieldValue(E entity, Object fieldId, Object fieldValue) {
        if (fieldId instanceof DomainField)
            fieldId = ((DomainField) fieldId).getId();
        entity.setFieldValue(fieldId, fieldValue);
    }

    @Override
    public void setParameterValue(String name, Object value) {

    }
}
