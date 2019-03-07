package Service;

import Entity.Entity;
import Entity.Field;
import Entity.Status;

import java.util.List;

public abstract class EntityService {
    protected static ExecutorService executorService = ExecutorService.getInstance();

    public <E extends Entity> E save(E entity) {
        switch (entity.getStatus()) {
            case NEW:
                return create(entity);
                case DIRTY:
                return commit(entity);
            case DELETED_LOCALLY:
                delete(entity);
                return null;
            case SYNCED:
            default:
                return entity;
        }
    }

    protected  <E extends Entity> boolean delete(E entity) {
        List<Field> primaryFields = entity.getPrimaryKey();
        return executorService.executeDelete(entity.getTableName(), primaryFields);
    }

    protected <E extends Entity> E create(E entity) {
        List<Field> primaryFields = entity.getPrimaryKey();
        List<Field> nonPrimaryFields = entity.getNonPrimaryFields();
        List<Field> populatedFields = executorService.executeInsert(entity.getTableName(), primaryFields, nonPrimaryFields);
        if(populatedFields == null)
            return null;
        Field.applyTo(populatedFields, entity.getFields(), true);
        entity.setStatus(Status.SYNCED);
        return entity;
    }

    protected <E extends Entity> E commit(E entity) {
        List<Field> primaryFields = entity.getPrimaryKey();
        List<Field> nonPrimaryFields = entity.getNonPrimaryFields();
        boolean status = executorService.executeUpdate(entity.getTableName(), nonPrimaryFields, primaryFields);
        if(status)
            entity.setStatus(Status.SYNCED);
        else
            throw new RuntimeException(String.format("Failed to update entity: %s", entity.toString()));
        return entity;
    }
}
