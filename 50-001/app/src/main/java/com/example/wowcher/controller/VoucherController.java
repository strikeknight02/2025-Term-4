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

    private MutableLiveData<ArrayList<Voucher>> voucherListAll;

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

    public MutableLiveData<ArrayList<Voucher>> getAllVouchers(){
        if (voucherListAll == null){
            voucherListAll = new MutableLiveData<ArrayList<Voucher>>();
        }
        return voucherListAll;
    }

    public void getVouchersforAll(){
        Consumer<ArrayList<Voucher>> method = (ArrayList<Voucher> vouchers) -> { instance.getAllVouchers().setValue((ArrayList<Voucher>) vouchers); };
        db.getAllData( method );
    }

    //Get Voucher Data
    public void getVoucherForUser(String column, Object comparison){
        Consumer<ArrayList<Voucher>> method = (ArrayList<Voucher> vouchers) -> { instance.getUserVouchers().setValue((ArrayList<Voucher>) vouchers); };
        db.getData(column, comparison, method );
    }

    //Update a Voucher attribute
    public void updateVoucher(String voucherId, String column, Object newValue){
        db.update(voucherId, column, newValue);
    }

    //Add Voucher to Database
    public void addVoucher(Voucher voucher){
        db.create(voucher);
    }

    //Delete a Voucher
    public void deleteVoucher(String voucherId){
        db.delete(voucherId);
    }
}
