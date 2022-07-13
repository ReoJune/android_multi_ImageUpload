package com.example.sampleproject.viewmodel;

import java.lang.System;

@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0002J\u000e\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\bJ\u000e\u0010\u0019\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\bR\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\r8F\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\r8F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u000f\u00a8\u0006\u001a"}, d2 = {"Lcom/example/sampleproject/viewmodel/ImageViewModel;", "Landroidx/lifecycle/ViewModel;", "imageModel", "Lcom/example/sampleproject/model/ImageRepository;", "(Lcom/example/sampleproject/model/ImageRepository;)V", "_imageFile", "Landroidx/lifecycle/MutableLiveData;", "Lcom/example/sampleproject/util/Event;", "Ljava/io/File;", "_resource", "Lcom/example/sampleproject/util/Resource;", "", "imageFile", "Landroidx/lifecycle/LiveData;", "getImageFile", "()Landroidx/lifecycle/LiveData;", "resource", "getResource", "getMultiPartFormRequestBody", "Lokhttp3/RequestBody;", "tag", "", "sendImageFile", "", "file", "uploadImage", "app_debug"})
public final class ImageViewModel extends androidx.lifecycle.ViewModel {
    private com.example.sampleproject.model.ImageRepository imageModel;
    private final androidx.lifecycle.MutableLiveData<com.example.sampleproject.util.Event<java.io.File>> _imageFile = null;
    private final androidx.lifecycle.MutableLiveData<com.example.sampleproject.util.Resource<java.lang.Object>> _resource = null;
    
    @javax.inject.Inject()
    public ImageViewModel(@org.jetbrains.annotations.NotNull()
    com.example.sampleproject.model.ImageRepository imageModel) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.sampleproject.util.Event<java.io.File>> getImageFile() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.sampleproject.util.Resource<java.lang.Object>> getResource() {
        return null;
    }
    
    public final void uploadImage(@org.jetbrains.annotations.NotNull()
    java.io.File file) {
    }
    
    private final okhttp3.RequestBody getMultiPartFormRequestBody(java.lang.String tag) {
        return null;
    }
    
    public final void sendImageFile(@org.jetbrains.annotations.NotNull()
    java.io.File file) {
    }
}