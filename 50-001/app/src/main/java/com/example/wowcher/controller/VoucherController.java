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

    private MutableLiveData<Voucher> redeemedVoucher;

    private MutableLiveData<ArrayList<Voucher>> locationVouchers;

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

    public MutableLiveData<Voucher> getRedeemedVouchers(){
        if (redeemedVoucher == null){
            redeemedVoucher = new MutableLiveData<Voucher>();
        }
        return redeemedVoucher;
    }

    public MutableLiveData<ArrayList<Voucher>> getVouchersBasedOnLocation(){
        if (locationVouchers == null){
            locationVouchers = new MutableLiveData<ArrayList<Voucher>>();
        }
        return locationVouchers;
    }

    public void getVouchersforAll(ArrayList<String> redeemedVouchers){
        Consumer<ArrayList<Voucher>> method = (ArrayList<Voucher> vouchers) -> { instance.getAllVouchers().setValue((ArrayList<Voucher>) vouchers); };
        VoucherSource voucherSourceInstance = (VoucherSource) databaseInstance;
        voucherSourceInstance.getAllUserVouchers( method, redeemedVouchers );
    }

    //TODO CHANGE INTO SOMETHING ELSE
    public void getUserRedeemedVouchers(String column, String redeemedVoucher){
        Consumer<ArrayList<Voucher>> method = (ArrayList<Voucher> vouchers) -> { instance.getRedeemedVouchers().setValue((Voucher) vouchers.get(0)); };
        databaseInstance.getData( column, redeemedVoucher, method);
    }

    public void getVouchersBasedOnLocation(ArrayList<String> locationIds){
        Consumer<ArrayList<Voucher>> method = (ArrayList<Voucher> vouchers) -> { instance.getVouchersBasedOnLocation().setValue((ArrayList<Voucher>) vouchers); };
        VoucherSource voucherSourceInstance = (VoucherSource) databaseInstance;
        voucherSourceInstance.getLocationBasedVouchers( method, locationIds );
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
