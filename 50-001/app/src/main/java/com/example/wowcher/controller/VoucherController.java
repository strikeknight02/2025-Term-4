package com.example.wowcher.controller;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.Voucher;
import com.example.wowcher.db.DBSource;
import com.example.wowcher.db.VoucherSource;

import java.util.ArrayList;
import java.util.function.Consumer;

public class VoucherController extends ViewModel {

    private DBSource databaseInstance;
    private VoucherController instance;

    private MutableLiveData<ArrayList<Voucher>> voucherListAll;

    public VoucherController(DBSource databaseInstance) {
        this.databaseInstance = databaseInstance;
    }

    public void getModelInstance(VoucherController instance){
        this.instance = instance;
    }

    public MutableLiveData<ArrayList<Voucher>> getAllVouchers(){
        if (voucherListAll == null){
            voucherListAll = new MutableLiveData<ArrayList<Voucher>>();
        }
        return voucherListAll;
    }

    public void getVouchersforAll(ArrayList<String> redeemedVouchers){
        Consumer<ArrayList<Voucher>> method = (ArrayList<Voucher> vouchers) -> { instance.getAllVouchers().setValue((ArrayList<Voucher>) vouchers); };
        VoucherSource voucherSourceInstance = (VoucherSource) databaseInstance;
        voucherSourceInstance.getAllUserVouchers( method, redeemedVouchers );
    }

    public void getVoucherBySomething(String column, Object comparison){
        Consumer<ArrayList<Voucher>> method = (ArrayList<Voucher> vouchers) -> { instance.getAllVouchers().setValue((ArrayList<Voucher>) vouchers); };
        databaseInstance.getData(column, comparison, method);
    }

    //Update a Voucher attribute
    public void updateVoucher(String voucherId, String column, Object newValue){
        databaseInstance.update(voucherId, column, newValue);
    }

    //Add Voucher to Database
    public void addVoucher(Voucher voucher){
        databaseInstance.create(voucher);
    }

    //Delete a Voucher
    public void deleteVoucher(String voucherId){
        databaseInstance.delete(voucherId);
    }
}
