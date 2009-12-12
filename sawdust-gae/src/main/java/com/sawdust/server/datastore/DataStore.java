package com.sawdust.server.datastore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jdo.JDOFatalException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.debug.SawdustSystemError;

public final class DataStore
{
   static final boolean                                                          ENABLE_TRANSACTIONS = false;
   
   private static final boolean                                                  DEBUG_LOG           = false;
   
   private static final Logger                                                   LOG                 = Logger.getLogger(DataStore.class
                                                                                                           .getName());
   private final static HashMap<Class<? extends DataObj>, HashMap<Key, DataObj>> objectCache         = new HashMap<Class<? extends DataObj>, HashMap<Key, DataObj>>();
   private static PersistenceManagerFactory                                      pmfInstance;
   
   private static PersistenceManagerFactory init()
   {
      PersistenceManagerFactory _pmfInstance = null;
      try
      {
         try
         {
            _pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");
         }
         catch (javax.jdo.JDOException e)
         {
            LOG.info("Using fallback configuration");
            _pmfInstance = JDOHelper.getPersistenceManagerFactory(initTest());
         }
      }
      catch (Throwable e)
      {
         LOG.info(Util.getFullString(e));
      }
      return _pmfInstance;
   }
   
   public static Properties initTest()
   {
      Properties testProperties = new Properties();
      testProperties.put("javax.jdo.PersistenceManagerFactoryClass",
            "org.datanucleus.store.appengine.jdo.DatastoreJDOPersistenceManagerFactory");
      testProperties.put("javax.jdo.option.ConnectionURL", "appengine");
      testProperties.put("javax.jdo.option.NontransactionalRead", "true");
      testProperties.put("javax.jdo.option.NontransactionalWrite", "true");
      testProperties.put("javax.jdo.option.RetainValues", "true");
      testProperties.put("datanucleus.appengine.autoCreateDatastoreTxns", "true");
      return testProperties;
   }
   
   public static DataObj Add(DataObj obj)
   {
      if (null == obj) return null;
      obj = Cache(obj);
      PersistenceManager em = obj.getEntityManager();
      if (null == em)
      {
         try
         {
            em = create();
            if (null == em) throw new NullPointerException("Could not create PersistenceManager");
            obj.setEntityManager(em);
            if (DEBUG_LOG)
            {
               LOG.info(String.format("Creating new object of type %s with key %s", obj.getClass().toString(), obj.getKey().toString()));
            }
            Transaction currentTransaction = em.currentTransaction();
            if (ENABLE_TRANSACTIONS)
            {
               if (!currentTransaction.isActive())
               {
                  currentTransaction.begin();
               }
            }
            em.makePersistent(obj);
         }
         catch (Throwable e)
         {
            LOG.warning("Datastore exception: " + Util.getFullString(e));
            if (null == e) e = new NullPointerException("Null was thrown!");
            throw new SawdustSystemError(e);
         }
      }
      return obj;
   }
   
   public static DataObj Cache(final DataObj obj)
   {
      final Class<? extends DataObj> c = obj.getClass();
      if (!objectCache.containsKey(c))
      {
         objectCache.put(c, new HashMap<Key, DataObj>());
      }
      final HashMap<Key, DataObj> classCache = objectCache.get(c);
      if (classCache.containsKey(obj.getKey())) return classCache.get(obj.getKey());
      // throw new AssertionFail("Object already in cache!");
      classCache.put(obj.getKey(), obj);
      if (DEBUG_LOG)
      {
         LOG.info(String.format("Adding to cache: type %s with key %s", obj.getClass().toString(), obj.getKey().toString()));
      }
      return obj;
   }
   
   public static void Clear()
   {
      objectCache.clear();
   }
   
   public static PersistenceManager create()
   {
      final PersistenceManager pm = getPmfInstance().getPersistenceManager();
      pm.setDetachAllOnCommit(true);
      return pm;
   }
   
   public static <T extends DataObj> T GetCache(final Class<T> c, final Key key)
   {
      if (objectCache.containsKey(c))
      {
         final HashMap<Key, DataObj> classCache = objectCache.get(c);
         if (classCache.containsKey(key)) return (T) classCache.get(key);
      }
      return null;
   }
   
   public static <T extends DataObj> T Get(final Class<T> c, final Key key)
   {
      final PersistenceManager em = create();
      T returnValue = GetCache(c, key);
      if (null != returnValue) return returnValue;
      try
      {
         returnValue = em.getObjectById(c, key);
      }
      catch (Throwable e)
      {
         LOG.info(Util.getFullString(e));
      }
      if (null != returnValue)
      {
         returnValue.setEntityManager(em);
         if (DEBUG_LOG)
         {
            LOG.info(String.format("Loaded object of type %s with key %s", returnValue.getClass().toString(), returnValue.getKey()
                  .toString()));
         }
      }
      return returnValue;
   }
   
   public static void Save()
   {
      for (final Class<?> x : objectCache.keySet())
      {
         HashMap<Key, DataObj> hashMap = objectCache.get(x);
         Collection<DataObj> values = hashMap.values();
         for (final DataObj obj : values)
         {
            PersistenceManager entityManager = obj.getEntityManager();
            if (!entityManager.isClosed())
            {
               Transaction currentTransaction = entityManager.currentTransaction();
               if (currentTransaction.isActive())
               {
                  currentTransaction.commit();
               }
               entityManager.close();
            }
            else
            {
               LOG.warning("Persistance manager is closed!");
            }
         }
      }
      objectCache.clear();
   }
   
   private DataStore()
   {
   }
   
   protected static void setPmfInstance(PersistenceManagerFactory pmfInstance)
   {
      DataStore.pmfInstance = pmfInstance;
   }
   
   protected static PersistenceManagerFactory getPmfInstance()
   {
      if (null == pmfInstance) pmfInstance = init();
      return pmfInstance;
   };
}
