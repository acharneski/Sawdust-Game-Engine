package com.sawdust.server.datastore;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.sawdust.engine.service.debug.AssertionFail;

//@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
//@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class DataObj
{

//   @PrimaryKey
//   @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
//   private Long id;
   
   @NotPersistent
   private PersistenceManager entityManager = null;
   
   @NotPersistent
   private boolean            isDirty       = true; // HACK; this should _start
                                                    // out false
                                                    
   public PersistenceManager getEntityManager()
   {
      return entityManager;
   }
   
   public abstract Key getKey();
   
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
}
