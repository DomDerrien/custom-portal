package dderrien.model;

import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

import dderrien.exception.ClientErrorException;

@Index
public abstract class AbstractAuthBase<T> extends AbstractBase<T> {

    @Index private Long ownerId;

    public AbstractAuthBase() {
        super();
    }

    @WriteOnceField
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @OnSave
    protected void prePersist() {
        if (ownerId == null || Long.valueOf(0L).equals(ownerId)) {
            throw new ClientErrorException("Field ownerId is missing");
        }
    }
}