/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui;

import static com.google.eclipse.protobuf.ui.util.Workbenches.activeWorkbenchWindow;
import static com.google.inject.name.Names.named;
import static org.eclipse.ui.PlatformUI.isWorkbenchRunning;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.ui.LanguageSpecific;
import org.eclipse.xtext.ui.editor.*;
import org.eclipse.xtext.ui.editor.model.XtextDocumentProvider;
import org.eclipse.xtext.ui.editor.outline.actions.IOutlineContribution;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;
import org.eclipse.xtext.ui.editor.quickfix.XtextQuickAssistProcessor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.*;
import org.eclipse.xtext.ui.validation.IResourceUIValidatorExtension;

import com.google.eclipse.protobuf.resource.IResourceVerifier;
import com.google.eclipse.protobuf.scoping.IFileUriResolver;
import com.google.eclipse.protobuf.ui.builder.nature.AutoAddNatureEditorCallback;
import com.google.eclipse.protobuf.ui.documentation.ProtobufDocumentationProvider;
import com.google.eclipse.protobuf.ui.editor.*;
import com.google.eclipse.protobuf.ui.editor.hyperlinking.ProtobufHyperlinkDetector;
import com.google.eclipse.protobuf.ui.editor.model.ProtobufDocumentProvider;
import com.google.eclipse.protobuf.ui.editor.spelling.ProtobufReconciler;
import com.google.eclipse.protobuf.ui.editor.syntaxcoloring.*;
import com.google.eclipse.protobuf.ui.internal.ProtobufActivator;
import com.google.eclipse.protobuf.ui.outline.*;
import com.google.eclipse.protobuf.ui.parser.PreferenceDrivenProtobufParser;
import com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferences;
import com.google.eclipse.protobuf.ui.preferences.editor.ignore.IgnoredExtensionsPreferences;
import com.google.eclipse.protobuf.ui.preferences.editor.numerictag.NumericTagPreferences;
import com.google.eclipse.protobuf.ui.preferences.editor.save.SaveActionsPreferences;
import com.google.eclipse.protobuf.ui.preferences.general.GeneralPreferences;
import com.google.eclipse.protobuf.ui.preferences.misc.MiscellaneousPreferences;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.eclipse.protobuf.ui.quickfix.ProtobufQuickAssistProcessor;
import com.google.eclipse.protobuf.ui.resource.*;
import com.google.eclipse.protobuf.ui.scoping.FileUriResolver;
import com.google.eclipse.protobuf.ui.validation.*;
import com.google.inject.Binder;

/**
 * Registers components to be used within the IDE.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufUiModule extends AbstractProtobufUiModule {
  public ProtobufUiModule(AbstractUIPlugin plugin) {
    super(plugin);
    setValidationTrigger(activeWorkbenchWindow(), plugin);
  }

  public Class<? extends IFileUriResolver> bindFileUriResolver() {
    return FileUriResolver.class;
  }

  public Class<? extends IHighlightingConfiguration> bindHighlightingConfiguration() {
    return HighlightingConfiguration.class;
  }

  @Override public Class<? extends IContentOutlinePage> bindIContentOutlinePage() {
    return ProtobufOutlinePage.class;
  }

  public Class<? extends IEObjectDocumentationProvider> bindIEObjectDocumentationProvider() {
    return ProtobufDocumentationProvider.class;
  }

  @Override public Class<? extends IHyperlinkDetector> bindIHyperlinkDetector() {
    return ProtobufHyperlinkDetector.class;
  }

  public Class<? extends IParser> bindIParser() {
    return PreferenceDrivenProtobufParser.class;
  }

  @Override public Class<? extends IReconciler> bindIReconciler() {
    return ProtobufReconciler.class;
  }

  public Class<? extends IResourceServiceProvider> bindIResourceServiceProvider() {
    return ProtobufServiceProvider.class;
  }

  public Class<? extends IResourceVerifier> bindIResourceVerifier() {
    return ResourceVerifier.class;
  }

  public Class<? extends IResourceUIValidatorExtension> bindIResourceUIValidatorExtension() {
    return ProtobufResourceUIValidatorExtension.class;
  }

  public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
    return ProtobufSemanticHighlightingCalculator.class;
  }

  @Override public Class<? extends IXtextEditorCallback> bindIXtextEditorCallback() {
    return AutoAddNatureEditorCallback.class;
  }

  public Class<? extends XtextDocumentProvider> bindXtextDocumentProvider() {
    return ProtobufDocumentProvider.class;
  }

  public Class<? extends XtextQuickAssistProcessor> bindXtextQuickAssistProcessor(){
    return ProtobufQuickAssistProcessor.class;
  }

  public void configureFileOutsideWorkspaceIconUpdater(Binder binder) {
    binder.bind(IXtextEditorCallback.class)
          .annotatedWith(named("FileOutsideWorkspaceIconUpdater"))
          .to(FileOutsideWorkspaceIconUpdater.class);
  }

  @Override public void configureLanguageSpecificURIEditorOpener(Binder binder) {
    if (!isWorkbenchRunning()) {
      return;
    }
    binder.bind(IURIEditorOpener.class)
          .annotatedWith(LanguageSpecific.class)
          .to(ProtobufUriEditorOpener.class);
  }

  public void configurePreferencesInitializers(Binder binder) {
    configurePreferenceInitializer(binder, "compilerPreferences", CompilerPreferences.Initializer.class);
    configurePreferenceInitializer(binder, "generalPreferences", GeneralPreferences.Initializer.class);
    configurePreferenceInitializer(binder, "ignoredExtensions", IgnoredExtensionsPreferences.Initializer.class);
    configurePreferenceInitializer(binder, "numericTagPreferences", NumericTagPreferences.Initializer.class);
    configurePreferenceInitializer(binder, "miscellaneousPreferences", MiscellaneousPreferences.Initializer.class);
    configurePreferenceInitializer(binder, "pathsPreferences", PathsPreferences.Initializer.class);
    configurePreferenceInitializer(binder, "saveActionsPreferences", SaveActionsPreferences.Initializer.class);
  }

  private void configurePreferenceInitializer(Binder binder, String name,
      Class<? extends IPreferenceStoreInitializer> initializerType) {
    binder.bind(IPreferenceStoreInitializer.class).annotatedWith(named(name)).to(initializerType);
  }

  @Override public void configureToggleLinkWithEditorOutlineContribution(Binder binder) {
    binder.bind(IOutlineContribution.class)
          .annotatedWith(IOutlineContribution.LinkWithEditor.class)
          .to(LinkWithEditor.class);
  }

  private void setValidationTrigger(IWorkbenchWindow w, AbstractUIPlugin plugin) {
    if (w == null || !(plugin instanceof ProtobufActivator)) {
      return;
    }
    w.getPartService().addPartListener(new ValidateFileOnActivation());
  }
}
