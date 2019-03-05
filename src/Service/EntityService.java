package Service;

import Entity.Entity;
import Entity.Field;

import java.util.List;

public abstract class EntityService {
    protected static ExecutorService executorService = ExecutorService.getInstance();

    public <E extends Entity> E save(E entity) {
        switch (entity.getStatus()) {
            case NEW:
                return create(entity);
            case UP_TO_DATE:
                return entity;
            case DIRTY:
                return commit(entity);
            case INVALID:
                return null;
            case DELETED_FROM_REMOTE:
                return null;
            case DELETED_LOCALLY:
                return null;
            default:
                return entity;
        }
    }

    public <E extends Entity> E delete(E entity) {
        switch (entity.getStatus()) {
            case DELETED_FROM_REMOTE:
                return null;
            default:
                return remove(entity);
        }
    }

    protected <E extends Entity> E create(E entity) {
        return executorService.executeInsert(entity);
    }

    protected <E extends Entity> E commit(E entity) {
        return executorService.executeUpdate(entity);
    }

    protected <E extends Entity> E remove(E entity) {
        return executorService.executeDelete(entity);
    }

    public <E extends Entity> E populateByPrimaryKey(List<Field> primaryKeyFields) {
        executorService.executeFetchByPK(e);
    }

    public <E extends Entity> E populatePrimaryKeyUsingBody(Entity e) {
        executorService.executeFetchByBodyMatch(e);
    }

}
