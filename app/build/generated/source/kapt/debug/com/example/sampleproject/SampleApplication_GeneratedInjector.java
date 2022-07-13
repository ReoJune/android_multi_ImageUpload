package com.example.sampleproject;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = SampleApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface SampleApplication_GeneratedInjector {
  void injectSampleApplication(SampleApplication sampleApplication);
}
