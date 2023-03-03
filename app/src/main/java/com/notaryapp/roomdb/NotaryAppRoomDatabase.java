package com.notaryapp.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.notaryapp.roomdb.dao.AddLicenseDao;
import com.notaryapp.roomdb.dao.AddStampDao;
import com.notaryapp.roomdb.dao.AllTransactionsDao;
import com.notaryapp.roomdb.dao.ClientRegDao;
import com.notaryapp.roomdb.dao.DocumentsImageDao;
import com.notaryapp.roomdb.dao.IdentityTypeDao;
import com.notaryapp.roomdb.dao.InfoDao;
import com.notaryapp.roomdb.dao.JournalFeesDao;
import com.notaryapp.roomdb.dao.JumioKeysDao;
import com.notaryapp.roomdb.dao.JumioScanDetailsDao;
import com.notaryapp.roomdb.dao.LADPartiesDao;
import com.notaryapp.roomdb.dao.LicenseStampCheckDao;
import com.notaryapp.roomdb.dao.ProfilePictureDao;
import com.notaryapp.roomdb.dao.SealAddedDao;
import com.notaryapp.roomdb.dao.SignDocsDao;
import com.notaryapp.roomdb.dao.SignerRegDao;
import com.notaryapp.roomdb.dao.StatesDao;
import com.notaryapp.roomdb.dao.TransactionsDao;
import com.notaryapp.roomdb.dao.UserLocationDao;
import com.notaryapp.roomdb.dao.UserNoteDao;
import com.notaryapp.roomdb.dao.UserRegDao;
import com.notaryapp.roomdb.dao.VACustomerDao;
import com.notaryapp.roomdb.dao.VaLicenseDao;
import com.notaryapp.roomdb.dao.ValidateId_SelectIDDao;
import com.notaryapp.roomdb.dao.NotaryApp_SelectIDDao;
import com.notaryapp.roomdb.dao.WitnessRegDao;
import com.notaryapp.roomdb.dao.Witness_SelectIDDao;
import com.notaryapp.roomdb.entity.AddLicense;
import com.notaryapp.roomdb.entity.AddStamp;
import com.notaryapp.roomdb.entity.AllTransactions;
import com.notaryapp.roomdb.entity.ClientReg;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.roomdb.entity.IdentityType;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.JournalFees;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.roomdb.entity.LADParties;
import com.notaryapp.roomdb.entity.LicenseStampCheck;
import com.notaryapp.roomdb.entity.ProfilePicture;
import com.notaryapp.roomdb.entity.SealAdded;
import com.notaryapp.roomdb.entity.SignDocs;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.States;
import com.notaryapp.roomdb.entity.Transactions;
import com.notaryapp.roomdb.entity.UserLocation;
import com.notaryapp.roomdb.entity.UserNote;
import com.notaryapp.roomdb.entity.UserReg;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.roomdb.entity.VaLicense;
import com.notaryapp.roomdb.entity.ValidateId_IdentityType;
import com.notaryapp.roomdb.entity.NotaryApp_IdentityType;
import com.notaryapp.roomdb.entity.WitnessReg;
import com.notaryapp.roomdb.entity.Witness_IdentityType;

@Database(entities = {UserReg.class , ClientReg.class , IdentityType.class, AddLicense.class, AddStamp.class,
        ValidateId_IdentityType.class, SignerReg.class, WitnessReg.class,LicenseStampCheck.class,SignDocs.class,
        UserLocation.class,JumioKeys.class,JumioScanDetails.class,Transactions.class,Info.class,
         VaLicense.class, AllTransactions.class, DocumentsModel.class, NotaryApp_IdentityType.class,
        Witness_IdentityType.class, SealAdded.class, ProfilePicture.class, VACustomer.class, States.class,
        LADParties.class, JournalFees.class, UserNote.class}, version = 4, exportSchema = false)

public abstract class notaryappRoomDatabase extends RoomDatabase{
    public abstract UserRegDao userRegDao();
    public abstract ClientRegDao clientRegDao();
    public abstract AddLicenseDao addLicenseDao();
    public abstract AddStampDao addStampDao();
    public abstract ValidateId_SelectIDDao validateIdIdentityTypeDao();
    public abstract SignerRegDao signerRegDao();
    public abstract WitnessRegDao witnessRegDao();
    public abstract LicenseStampCheckDao licenseStampCheckDao();
    public abstract JumioKeysDao jumioKeysDao();
    public abstract JumioScanDetailsDao scanDetailsDao();
    public abstract SignDocsDao signDocsDao();
    public abstract UserLocationDao userLocationDao();
    public abstract InfoDao infoDao();
    public abstract TransactionsDao transactionsDao();
    public abstract VaLicenseDao vaLicenseDao();
    public abstract AllTransactionsDao allTransactionsDao();
    public abstract IdentityTypeDao identityTypeDao();
    public abstract DocumentsImageDao documentsImageDao();
    public abstract NotaryApp_SelectIDDao NotaryAppSelectIDDao();
    public abstract Witness_SelectIDDao witnessSelectIDDao();
    public abstract SealAddedDao sealAddedDao();
    public abstract ProfilePictureDao saveProfilePic();
    public abstract VACustomerDao vaCustomerDao();
    public abstract StatesDao statesDao();
    public abstract LADPartiesDao ladPartiesDao();
    public abstract JournalFeesDao journalFeesDao();
    public abstract UserNoteDao userNoteDao();
}
