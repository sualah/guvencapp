package com.tr.guvencmakina.guvencapp.Products.adapter;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;
import com.tr.guvencmakina.guvencapp.Products.ui.fragments.ProductCategoryStepperFirstFragment;

public class ProductsStepperAdapter extends AbstractFragmentStepAdapter {
    String CURRENT_STEP_POSITION_KEY = "products_stepper_key";
    public ProductsStepperAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        Bundle b = new Bundle();
        switch (position){
            case 0:
                final ProductCategoryStepperFirstFragment step = new ProductCategoryStepperFirstFragment();
                b.putInt(CURRENT_STEP_POSITION_KEY, position);
                step.setArguments(b);
                return step;
            case 1:
                final ProductCategoryStepperFirstFragment step1 = new ProductCategoryStepperFirstFragment();
                b.putInt(CURRENT_STEP_POSITION_KEY, position);
                step1.setArguments(b);
                return step1;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        switch (position){
            case 0:
                return new StepViewModel.Builder(context)
                        .setTitle("Add Image") //can be a CharSequence instead
                        .create();
            case 1:
                return new StepViewModel.Builder(context)
                        .setTitle("Category Name") //can be a CharSequence instead
                        .setEndButtonLabel("Submit")
                        .create();
            default:
                return  null;
        }

    }
}
