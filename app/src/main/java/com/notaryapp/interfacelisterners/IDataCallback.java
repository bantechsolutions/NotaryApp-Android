package com.notaryapp.interfacelisterners;

import com.notaryapp.roomdb.entity.AllTransactions;

import java.util.List;

public interface IDataCallback {
    void onFragmentCreated( List<AllTransactions> listData);
}
