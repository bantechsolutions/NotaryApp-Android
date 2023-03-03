package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.notaryapp.roomdb.entity.VACustomer;


@Dao
public interface VACustomerDao {

    @Query("SELECT * FROM VACustomer")
    VACustomer getCustomer();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VACustomer addCustomer);

    @Delete
    void delete(VACustomer addCustomer);

    @Query("DELETE  FROM VACustomer")
    void delete();

    @Query("UPDATE VACustomer SET transactionsLeft =:transCount")
    void updateCount(int transCount);

    @Query("UPDATE VACustomer SET stripe_customer_id =:customerId")
    void updateCustomerId(String customerId);

    @Query("UPDATE VACustomer SET current_membership =:memType")
    void updateCurrent_membership(String memType);

    @Query("UPDATE VACustomer SET stripe_active_subscription_id =:sub_id")
    void updateSubscriptionId(String sub_id);

    @Update
    void update(VACustomer addCustomer);

    @Query("UPDATE VACustomer SET cusId= :cusId, stripe_customer_id =:stripe_customer_id,current_membership =:current_membership, total_bought =:total_bought,total_used =:total_used, created_at =:created_at, updated_at =:updated_at,started_at =:started_at,ending_at =:ending_at, `current_date` =:current_date,is_active =:is_active,stripe_active_subscription_id =:stripe_active_subscription_id  WHERE email =:email")
    void update(String cusId,String email, String stripe_customer_id, String current_membership, int total_bought, int total_used, String created_at, String updated_at, String started_at, String ending_at, String current_date, int is_active, String stripe_active_subscription_id);
}
