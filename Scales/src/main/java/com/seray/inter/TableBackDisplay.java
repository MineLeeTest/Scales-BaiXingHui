package com.seray.inter;

import com.seray.cache.CacheHelper;
import com.seray.sjc.annotation.DisplayType;
import com.seray.view.presentation.WeightPresentation;

public class TableBackDisplay extends BackDisplayBase {

    private static TableBackDisplay mInstance = null;

    private static DisplayController mDisplayController = DisplayController.getInstance();

    private static WeightPresentation presentation;

    private TableBackDisplay() {
    }

    public static TableBackDisplay getInstance() {

        if (mInstance == null) {
            mInstance = new TableBackDisplay();
        }

        mInstance.isOpen = false;

        return mInstance;
    }

    public static TableBackDisplay getInstance(boolean isOpen, @DisplayType int type) {

        if (mInstance == null) {
            mInstance = new TableBackDisplay();
        }

        mInstance.isOpen = isOpen;

        if (isOpen && type == DisplayType.DISPLAY_WEIGHT) {
            presentation = (WeightPresentation) mDisplayController.getDisplayPresentation(type);
        }

        return mInstance;
    }

    @Override
    public void showIsStable(boolean isStable) {
        if (!this.isOpen || presentation == null)
            return;
        presentation.setStableView(isStable);
    }

    @Override
    public void showIsZero(boolean isZero) {
    }

    @Override
    public void showPrice(String price) {
        if (!this.isOpen || presentation == null)
            return;
        presentation.setPriceContent(price);
    }

    @Override
    public void showTare(String tare) {
    }

    @Override
    public void showAmount(String amount) {
        if (!this.isOpen || presentation == null)
            return;
        presentation.setAmountContent(amount);
    }

    @Override
    public void showWeight(String weight) {
        if (!this.isOpen || presentation == null)
            return;
        presentation.setWeightContent(weight);
    }

    @Override
    public void showWeightType(int type) {
        if (!this.isOpen || presentation == null)
            return;
        presentation.setWeightType(type);
    }

    @Override
    public void showCustomerName(String name) {
        if (!this.isOpen || presentation == null)
            return;
        presentation.setCustomerName(name);
    }

    @Override
    public void showProductName(String name) {
        if (!this.isOpen || presentation == null)
            return;
        presentation.setProductName(name);
    }

    @Override
    public void showSubtotal(String subtotal) {
        if (!this.isOpen || presentation == null)
            return;
        presentation.setSubtotalContent(subtotal);
    }

    @Override
    public void showSubtotalAmount(String amount) {
        if (!this.isOpen || presentation == null)
            return;
        presentation.setSubtotalAmount(amount);
    }

    @Override
    public void initBackDisplay() {
        if (!this.isOpen || presentation == null)
            return;
        presentation.setWeightContent("0.000");
        presentation.setPriceContent("0.00");
        presentation.setAmountContent("0.00");
        presentation.setSubtotalContent("0.00");
        presentation.setSubtotalAmount("0");
        presentation.setCustomerName("客户1");
//        presentation.setQRContent();
    }

    @Override
    public void showMemberPrice(String memberPrice) {
    }

    @Override
    public void showPlace(String place) {
        super.showPlace(place);
        if (!this.isOpen || presentation == null)
            return;
        presentation.setPlaceContent(place);
    }

    @Override
    public void showQRImage(String content) {
    }
}
