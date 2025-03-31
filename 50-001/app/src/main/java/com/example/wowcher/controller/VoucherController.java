package com.example.wowcher.controller;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.Voucher;
import com.example.wowcher.db.DBSource;

import java.util.ArrayList;
import java.util.function.Consumer;

public class VoucherController extends ViewModel {

    private DBSource db;
    private VoucherController instance;
    private MutableLiveData<ArrayList<Voucher>> voucherList;

    public VoucherController(DBSource databaseInstance) {
        this.db = databaseInstance;
    }

    public void getModelInstance(VoucherController instance){
        this.instance = instance;
    }

    public MutableLiveData<ArrayList<Voucher>> getUserVouchers(){
        if (voucherList == null){
            voucherList = new MutableLiveData<ArrayList<Voucher>>();
        }
        return voucherList;
    }

    public void getVoucherForUser(String column, Object comparison){
        Consumer<ArrayList<Voucher>> method = (ArrayList<Voucher> vouchers) -> { instance.getUserVouchers().setValue((ArrayList<Voucher>) vouchers); };
        db.getData(column, comparison, method );
    }



}
