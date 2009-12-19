package com.sawdust.server.datastore;

import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.sawdust.engine.service.debug.AssertionFail;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class DataObj
{
   @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        //result = prime * result + serialVersion;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!getClass().isAssignableFrom(obj.getClass())) return false;
        DataObj other = (DataObj) obj;
        if (key == null)
        {
            if (other.key != null) return false;
        }
        else if (!key.equals(other.key)) return false;
        //if (serialVersion != other.serialVersion) return false;
        return true;
    }

private static final Logger LOG = Logger.getLogger(DataObj.class.getName());
                                                    
   public PersistenceManager getEntityManager()
   {
      return entityManager;
   }
   
   public boolean isDirty()
   {
      return isDirty;
   }
   
   protected void setDirty()
   {
      isDirty = true;
   }
   
   public void setEntityManager(final PersistenceManager pentityManager)
   {
      if (null != entityManager) throw new AssertionFail("PersistenceManager is already set");
      DataStore.Cache(this);
      entityManager = pentityManager;
      Transaction currentTransaction = entityManager.currentTransaction();
      
      if (DataStore.ENABLE_TRANSACTIONS)
      {
         if (!currentTransaction.isActive())
         {
            currentTransaction.begin();
         }
      }
   }

   @Persistent Date created;
   @Persistent @PrimaryKey Key key;

   @Persistent
   public // HACK 
   Date updated;
   
   @Persistent int serialVersion;
   
   @NotPersistent boolean isDirty = true; // HACK; this should start out false
   @NotPersistent boolean dirty = false;
   @NotPersistent PersistenceManager entityManager = null;

   public Key getKey()
   {
       return key;
   }


   public DataObj(Key k)
   {
       key = k;
       created = new Date();
       serialVersion = 0;
       update();
   }

   protected DataObj()
   {
       key = null;
       created = new Date();
       serialVersion = 0;
       update();
   }

   protected void update()
   {
       update(!dirty);
       dirty = true;
   }

   protected void update(boolean incrementVersion)
    {
       if(incrementVersion)
       {
           serialVersion++;
       }
       updated = new Date();
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

   public boolean isValid()
   {
       return true;
   }

   public void delete(boolean closeTransaction)
   {
       LOG.info("Deleting " + this.toString());
       PersistenceManager entityManager = getEntityManager();
       if(null == entityManager) 
       {
           entityManager = DataStore.create();
           entityManager.makePersistent(this);
       }
       entityManager.deletePersistent(this);
       if(closeTransaction) entityManager.close();
   }
}
