package com.tr.guvencmakina.guvencapp.Listners;

import com.tr.guvencmakina.guvencapp.Enums.ImagePickerEnum;

@FunctionalInterface
public interface IImagePickerLister {
    void onOptionSelected(ImagePickerEnum imagePickerEnum);
}
