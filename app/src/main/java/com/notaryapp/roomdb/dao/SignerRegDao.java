package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.notaryapp.roomdb.entity.SignerReg;

import java.util.List;

@Dao
public interface SignerRegDao {

    @Query("SELECT * FROM SignerReg")
    List<SignerReg> getSigners();

    @Query("SELECT * FROM SignerReg WHERE witness =1 ORDER BY id DESC LIMIT 1")
    SignerReg getSignersWitness();

    @Query("SELECT * FROM SignerReg WHERE email =:email")
    SignerReg  getSigner(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SignerReg signerReg);

    @Delete
    void delete(SignerReg signerReg);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateUser(SignerReg signerReg);

    @Query("SELECT email FROM SignerReg")
    String getEmail();

    @Query("SELECT email FROM SignerReg WHERE witness=1 ORDER BY id DESC LIMIT 1")
    String getSignerEmail();

    @Query("SELECT firstName FROM SignerReg")
    String getName();

    @Query("SELECT count(*) from SignerReg")
    int getCount();

    @Query("UPDATE SignerReg SET firstname =:firstName,lastname =:lName, phoneNo =:phn ," +
            "addressOne =:address1, addressTwo =:address2,cityName =:city ,stateName =:state, zipCode =:zip WHERE email =:email")
    int updateData(String email, String firstName, String lName, String phn,String address1,String address2,
                   String city,String state,String zip);

    @Query("UPDATE SignerReg SET signatureImagePath =:signatureImage WHERE email =:email")
    int updateSignature(String email, String signatureImage);

    @Query("UPDATE SignerReg SET thumbImagePath =:thumbImage WHERE email =:email")
    int updateThumb(String email, String thumbImage);

    @Query("UPDATE SignerReg SET email =:email")
    void update(String email);

    @Query("DELETE FROM SignerReg WHERE firstName =:fName")
    void deleteLast(String fName);
  //  delete from profile order by id desc limit 1
    @Query("DELETE FROM SignerReg")
    void deleteAll();
}
