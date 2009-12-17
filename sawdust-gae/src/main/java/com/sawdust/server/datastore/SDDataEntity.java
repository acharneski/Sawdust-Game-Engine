package com.sawdust.server.datastore;

import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class SDDataEntity extends DataObj
{
    private static final Logger LOG = Logger.getLogger(SDDataEntity.class.getName());

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private Date created;

    @Persistent
    private Date updated;

    @Persistent
    private int serialVersion;

    @NotPersistent
    private boolean dirty = false;

    public SDDataEntity()
    {
        created = new Date();
        serialVersion = 0;
        update();
    }

    protected void update()
    {
        if (!dirty)
        {
            LOG.warning("Updated");
            dirty = true;
            serialVersion++;
            updated = new Date();
        }
    }

    protected void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getCreated()
    {
        return created;
    }

    protected void setUpdated(Date updated)
    {
        this.updated = updated;
    }

    public Date getUpdated()
    {
        return updated;
    }

    protected void setSerialVersion(int serialVersion)
    {
        this.serialVersion = serialVersion;
    }

    public int getSerialVersion()
    {
        return serialVersion;
    }

    protected void setDirty(boolean dirty)
    {
        this.dirty = dirty;
    }

    public boolean getDirty()
    {
        return dirty;
    }

}
