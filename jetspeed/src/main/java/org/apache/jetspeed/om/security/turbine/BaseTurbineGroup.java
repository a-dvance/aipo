package org.apache.jetspeed.om.security.turbine;


import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.torque.TorqueException;
import org.apache.torque.om.BaseObject;
import org.apache.torque.om.ComboKey;
import org.apache.torque.om.DateKey;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.om.StringKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.Transaction;


/**
 * This class was autogenerated by Torque on:
 *
 * [Thu Jun 10 23:17:32 JST 2004]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to TurbineGroup
 */
public abstract class BaseTurbineGroup extends BaseObject
{
    /** The Peer class */
    private static final TurbineGroupPeer peer =
        new TurbineGroupPeer();

      
    /** The value for the groupId field */
    private int groupId;
      
    /** The value for the groupName field */
    private String groupName;
      
    /** The value for the objectdata field */
    private byte[] objectdata;
  
  
    /**
     * Get the GroupId
     * @return int
     */
    public int getGroupId()
    {
        return groupId;
    }

                                              
    /**
     * Set the value of GroupId
     */
    public void setGroupId(int v ) throws TorqueException
    {
    
                  if (this.groupId != v)
              {
            this.groupId = v;
            setModified(true);
        }
    
          
                                  
        // update associated TurbineUserGroupRole
        if (collTurbineUserGroupRoles != null )
        {
            for (int i = 0; i < collTurbineUserGroupRoles.size(); i++)
            {
                ((TurbineUserGroupRole)collTurbineUserGroupRoles.get(i))
                        .setGroupId(v);
            }
        }
                      }

  
    /**
     * Get the GroupName
     * @return String
     */
    public String getGroupName()
    {
        return groupName;
    }

                        
    /**
     * Set the value of GroupName
     */
    public void setGroupName(String v ) 
    {
    
                  if (!ObjectUtils.equals(this.groupName, v))
              {
            this.groupName = v;
            setModified(true);
        }
    
          
              }

  
    /**
     * Get the Objectdata
     * @return byte[]
     */
    public byte[] getObjectdata()
    {
        return objectdata;
    }

                        
    /**
     * Set the value of Objectdata
     */
    public void setObjectdata(byte[] v ) 
    {
    
                  if (!ObjectUtils.equals(this.objectdata, v))
              {
            this.objectdata = v;
            setModified(true);
        }
    
          
              }

  
         
                                
            
    /**
     * Collection to store aggregation of collTurbineUserGroupRoles
     */
    protected List collTurbineUserGroupRoles;

    /**
     * Temporary storage of collTurbineUserGroupRoles to save a possible db hit in
     * the event objects are add to the collection, but the
     * complete collection is never requested.
     */
    protected void initTurbineUserGroupRoles()
    {
        if (collTurbineUserGroupRoles == null)
        {
            collTurbineUserGroupRoles = new ArrayList();
        }
    }

            
    /**
     * Method called to associate a TurbineUserGroupRole object to this object
     * through the TurbineUserGroupRole foreign key attribute
     *
     * @param TurbineUserGroupRole l
     */
    public void addTurbineUserGroupRole(TurbineUserGroupRole l) throws TorqueException
    {
        getTurbineUserGroupRoles().add(l);
        l.setTurbineGroup((TurbineGroup)this);
    }

    /**
     * The criteria used to select the current contents of collTurbineUserGroupRoles
     */
    private Criteria lastTurbineUserGroupRolesCriteria = null;

    /**
     * If this collection has already been initialized, returns
     * the collection. Otherwise returns the results of
     * getTurbineUserGroupRoles(new Criteria())
     */
    public List getTurbineUserGroupRoles() throws TorqueException
    {
        if (collTurbineUserGroupRoles == null)
        {
            collTurbineUserGroupRoles = getTurbineUserGroupRoles(new Criteria(10));
        }
        return collTurbineUserGroupRoles;
    }

    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection.
     * Otherwise if this TurbineGroup has previously
     * been saved, it will retrieve related TurbineUserGroupRoles from storage.
     * If this TurbineGroup is new, it will return
     * an empty collection or the current collection, the criteria
     * is ignored on a new object.
     */
    public List getTurbineUserGroupRoles(Criteria criteria) throws TorqueException
    {
        if (collTurbineUserGroupRoles == null)
        {
            if (isNew())
            {
               collTurbineUserGroupRoles = new ArrayList();
            }
            else
            {
                      criteria.add(TurbineUserGroupRolePeer.GROUP_ID, getGroupId() );
                      collTurbineUserGroupRoles = TurbineUserGroupRolePeer.doSelect(criteria);
            }
        }
        else
        {
            // criteria has no effect for a new object
            if (!isNew())
            {
                // the following code is to determine if a new query is
                // called for.  If the criteria is the same as the last
                // one, just return the collection.
                      criteria.add(TurbineUserGroupRolePeer.GROUP_ID, getGroupId() );
                      if (!lastTurbineUserGroupRolesCriteria.equals(criteria))
                {
                    collTurbineUserGroupRoles = TurbineUserGroupRolePeer.doSelect(criteria);
                }
            }
        }
        lastTurbineUserGroupRolesCriteria = criteria;

        return collTurbineUserGroupRoles;
    }

    /**
     * If this collection has already been initialized, returns
     * the collection. Otherwise returns the results of
     * getTurbineUserGroupRoles(new Criteria(),Connection)
     * This method takes in the Connection also as input so that
     * referenced objects can also be obtained using a Connection
     * that is taken as input
     */
    public List getTurbineUserGroupRoles(Connection con) throws TorqueException
    {
        if (collTurbineUserGroupRoles == null)
        {
            collTurbineUserGroupRoles = getTurbineUserGroupRoles(new Criteria(10),con);
        }
        return collTurbineUserGroupRoles;
    }

    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection.
     * Otherwise if this TurbineGroup has previously
     * been saved, it will retrieve related TurbineUserGroupRoles from storage.
     * If this TurbineGroup is new, it will return
     * an empty collection or the current collection, the criteria
     * is ignored on a new object.
     * This method takes in the Connection also as input so that
     * referenced objects can also be obtained using a Connection
     * that is taken as input
     */
    public List getTurbineUserGroupRoles(Criteria criteria,Connection con) throws TorqueException
    {
        if (collTurbineUserGroupRoles == null)
        {
            if (isNew())
            {
               collTurbineUserGroupRoles = new ArrayList();
            }
            else
            {
                       criteria.add(TurbineUserGroupRolePeer.GROUP_ID, getGroupId() );
                       collTurbineUserGroupRoles = TurbineUserGroupRolePeer.doSelect(criteria,con);
             }
         }
         else
         {
             // criteria has no effect for a new object
             if (!isNew())
             {
                 // the following code is to determine if a new query is
                 // called for.  If the criteria is the same as the last
                 // one, just return the collection.
                     criteria.add(TurbineUserGroupRolePeer.GROUP_ID, getGroupId() );
                     if (!lastTurbineUserGroupRolesCriteria.equals(criteria))
                 {
                     collTurbineUserGroupRoles = TurbineUserGroupRolePeer.doSelect(criteria,con);
                 }
             }
         }
         lastTurbineUserGroupRolesCriteria = criteria;

         return collTurbineUserGroupRoles;
     }

                              

              
                    
                    
                                
                                                              
                                        
                    
                    
          
    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection.
     * Otherwise if this TurbineGroup is new, it will return
     * an empty collection; or if this TurbineGroup has previously
     * been saved, it will retrieve related TurbineUserGroupRoles from storage.
     *
     * This method is protected by default in order to keep the public
     * api reasonable.  You can provide public methods for those you
     * actually need in TurbineGroup.
     */
    protected List getTurbineUserGroupRolesJoinTurbineUser(Criteria criteria)
        throws TorqueException
    {
        if (collTurbineUserGroupRoles == null)
        {
            if (isNew())
            {
               collTurbineUserGroupRoles = new ArrayList();
            }
            else
            {
                            criteria.add(TurbineUserGroupRolePeer.GROUP_ID, getGroupId() );
                            collTurbineUserGroupRoles = TurbineUserGroupRolePeer.doSelectJoinTurbineUser(criteria);
            }
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                            criteria.add(TurbineUserGroupRolePeer.GROUP_ID, getGroupId() );
                        if (!lastTurbineUserGroupRolesCriteria.equals(criteria))
            {
                collTurbineUserGroupRoles = TurbineUserGroupRolePeer.doSelectJoinTurbineUser(criteria);
            }
        }
        lastTurbineUserGroupRolesCriteria = criteria;

        return collTurbineUserGroupRoles;
    }
                  
                    
                              
                                
                                                              
                                        
                    
                    
          
    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection.
     * Otherwise if this TurbineGroup is new, it will return
     * an empty collection; or if this TurbineGroup has previously
     * been saved, it will retrieve related TurbineUserGroupRoles from storage.
     *
     * This method is protected by default in order to keep the public
     * api reasonable.  You can provide public methods for those you
     * actually need in TurbineGroup.
     */
    protected List getTurbineUserGroupRolesJoinTurbineGroup(Criteria criteria)
        throws TorqueException
    {
        if (collTurbineUserGroupRoles == null)
        {
            if (isNew())
            {
               collTurbineUserGroupRoles = new ArrayList();
            }
            else
            {
                            criteria.add(TurbineUserGroupRolePeer.GROUP_ID, getGroupId() );
                            collTurbineUserGroupRoles = TurbineUserGroupRolePeer.doSelectJoinTurbineGroup(criteria);
            }
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                            criteria.add(TurbineUserGroupRolePeer.GROUP_ID, getGroupId() );
                        if (!lastTurbineUserGroupRolesCriteria.equals(criteria))
            {
                collTurbineUserGroupRoles = TurbineUserGroupRolePeer.doSelectJoinTurbineGroup(criteria);
            }
        }
        lastTurbineUserGroupRolesCriteria = criteria;

        return collTurbineUserGroupRoles;
    }
                  
                    
                    
                                
                                                              
                                        
                    
                    
          
    /**
     * If this collection has already been initialized with
     * an identical criteria, it returns the collection.
     * Otherwise if this TurbineGroup is new, it will return
     * an empty collection; or if this TurbineGroup has previously
     * been saved, it will retrieve related TurbineUserGroupRoles from storage.
     *
     * This method is protected by default in order to keep the public
     * api reasonable.  You can provide public methods for those you
     * actually need in TurbineGroup.
     */
    protected List getTurbineUserGroupRolesJoinTurbineRole(Criteria criteria)
        throws TorqueException
    {
        if (collTurbineUserGroupRoles == null)
        {
            if (isNew())
            {
               collTurbineUserGroupRoles = new ArrayList();
            }
            else
            {
                            criteria.add(TurbineUserGroupRolePeer.GROUP_ID, getGroupId() );
                            collTurbineUserGroupRoles = TurbineUserGroupRolePeer.doSelectJoinTurbineRole(criteria);
            }
        }
        else
        {
            // the following code is to determine if a new query is
            // called for.  If the criteria is the same as the last
            // one, just return the collection.
            boolean newCriteria = true;
                            criteria.add(TurbineUserGroupRolePeer.GROUP_ID, getGroupId() );
                        if (!lastTurbineUserGroupRolesCriteria.equals(criteria))
            {
                collTurbineUserGroupRoles = TurbineUserGroupRolePeer.doSelectJoinTurbineRole(criteria);
            }
        }
        lastTurbineUserGroupRolesCriteria = criteria;

        return collTurbineUserGroupRoles;
    }
                            


          
    private static List fieldNames = null;

    /**
     * Generate a list of field names.
     */
    public static synchronized List getFieldNames()
    {
        if (fieldNames == null)
        {
            fieldNames = new ArrayList();
              fieldNames.add("GroupId");
              fieldNames.add("GroupName");
              fieldNames.add("Objectdata");
              fieldNames = Collections.unmodifiableList(fieldNames);
        }
        return fieldNames;
    }

    /**
     * Retrieves a field from the object by name passed in
     * as a String.
     */
    public Object getByName(String name)
    {
          if (name.equals("GroupId"))
        {
                return new Integer(getGroupId());
            }
          if (name.equals("GroupName"))
        {
                return getGroupName();
            }
          if (name.equals("Objectdata"))
        {
                return getObjectdata();
            }
          return null;
    }
    
    /**
     * Retrieves a field from the object by name passed in
     * as a String.  The String must be one of the static
     * Strings defined in this Class' Peer.
     */
    public Object getByPeerName(String name)
    {
          if (name.equals(TurbineGroupPeer.GROUP_ID ))
        {
                return new Integer(getGroupId());
            }
          if (name.equals(TurbineGroupPeer.GROUP_NAME ))
        {
                return getGroupName();
            }
          if (name.equals(TurbineGroupPeer.OBJECTDATA ))
        {
                return getObjectdata();
            }
          return null;
    }

    /**
     * Retrieves a field from the object by Position as specified
     * in the xml schema.  Zero-based.
     */
    public Object getByPosition(int pos)
    {
            if ( pos == 0 )
        {
                return new Integer(getGroupId());
            }
              if ( pos == 1 )
        {
                return getGroupName();
            }
              if ( pos == 2 )
        {
                return getObjectdata();
            }
              return null;
    }
     
    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
     */
    public void save() throws Exception
    {
          save(TurbineGroupPeer.getMapBuilder()
                .getDatabaseMap().getName());
      }

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
       * Note: this code is here because the method body is
     * auto-generated conditionally and therefore needs to be
     * in this file instead of in the super class, BaseObject.
       */
    public void save(String dbName) throws TorqueException
    {
        Connection con = null;
          try
        {
            con = Transaction.begin(dbName);
            save(con);
            Transaction.commit(con);
        }
        catch(TorqueException e)
        {
            Transaction.safeRollback(con);
            throw e;
        }
      }

      /** flag to prevent endless save loop, if this object is referenced
        by another object which falls in this transaction. */
    private boolean alreadyInSave = false;
      /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.  This method
     * is meant to be used as part of a transaction, otherwise use
     * the save() method and the connection details will be handled
     * internally
     */
    public void save(Connection con) throws TorqueException
    {
          if (!alreadyInSave)
        {
            alreadyInSave = true;


  
            // If this object has been modified, then save it to the database.
            if (isModified())
            {
                if (isNew())
                {
                    TurbineGroupPeer.doInsert((TurbineGroup)this, con);
                    setNew(false);
                }
                else
                {
                    TurbineGroupPeer.doUpdate((TurbineGroup)this, con);
                }

                      if (isCacheOnSave())
                {
                    TurbineGroupManager.putInstance(this);
                }
              }

                                      
                            if (collTurbineUserGroupRoles != null )
            {
                for (int i = 0; i < collTurbineUserGroupRoles.size(); i++)
                {
                    ((TurbineUserGroupRole)collTurbineUserGroupRoles.get(i)).save(con);
                }
            }
                          alreadyInSave = false;
        }
      }

    /**
     * Specify whether to cache the object after saving to the db.
     * This method returns false
     */
    protected boolean isCacheOnSave()
    {
        return true;
    }

                        
      /**
     * Set the PrimaryKey using ObjectKey.
     *
     * @param ObjectKey groupId
     */
    public void setPrimaryKey(ObjectKey groupId)
        throws TorqueException {
            setGroupId(((NumberKey)groupId).intValue());
        }

    /**
     * Set the PrimaryKey using a String.
     */
    public void setPrimaryKey(String key) throws TorqueException
    {
            setGroupId(Integer.parseInt(key));
        }

  
    /**
     * returns an id that differentiates this object from others
     * of its class.
     */
    public ObjectKey getPrimaryKey()
    {
          return SimpleKey.keyFor(getGroupId());
      }
 

    /**
     * Makes a copy of this object.
     * It creates a new object filling in the simple attributes.
       * It then fills all the association collections.
       */
      public TurbineGroup copy() throws TorqueException
    {
        TurbineGroup copyObj = new TurbineGroup();
            copyObj.setGroupId(groupId);
          copyObj.setGroupName(groupName);
          copyObj.setObjectdata(objectdata);
  
                      copyObj.setGroupId(0);
                        
                                      
                
        List v = getTurbineUserGroupRoles();
        for (int i = 0; i < v.size(); i++)
        {
            TurbineUserGroupRole obj = (TurbineUserGroupRole) v.get(i);
            copyObj.addTurbineUserGroupRole(obj.copy());
        }
                            return copyObj;
    }

    /**
     * returns a peer instance associated with this om.  Since Peer classes
     * are not to have any instance attributes, this method returns the
     * same instance for all member of this class. The method could therefore
     * be static, but this would prevent one from overriding the behavior.
     */
    public TurbineGroupPeer getPeer()
    {
        return peer;
    }
}
