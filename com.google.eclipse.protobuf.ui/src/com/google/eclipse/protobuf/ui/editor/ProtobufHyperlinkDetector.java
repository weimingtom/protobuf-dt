/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import static org.eclipse.emf.common.util.URI.createURI;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hyperlinking.DefaultHyperlinkDetector;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.inject.Inject;

/**
 * Represents an implementation of interface <code>{@link IHyperlinkDetector}</code> to find and convert
 * {@link CrossReference elements}, at a given location, to {@code IHyperlink}.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufHyperlinkDetector extends DefaultHyperlinkDetector {

  @Inject private EObjectAtOffsetHelper eObjectAtOffsetHelper;

  @Override public IHyperlink[] detectHyperlinks(ITextViewer textViewer, final IRegion region,
      final boolean canShowMultipleHyperlinks) {
    IXtextDocument document = (IXtextDocument)textViewer.getDocument();
    IHyperlink[] importHyperlinks = importHyperlinks(document, region);
    if (importHyperlinks != null) return importHyperlinks;
    return document.readOnly(new IUnitOfWork<IHyperlink[], XtextResource>() {
      public IHyperlink[] exec(XtextResource resource) {
        return getHelper().createHyperlinksByOffset(resource, region.getOffset(), canShowMultipleHyperlinks);
      }
    });
  }

  private IHyperlink[] importHyperlinks(final IXtextDocument document, final IRegion region) {
    return document.readOnly(new IUnitOfWork<IHyperlink[], XtextResource>() {
      public IHyperlink[] exec(XtextResource resource) {
        EObject resolved = eObjectAtOffsetHelper.resolveElementAt(resource, region.getOffset());
        if (!(resolved instanceof Import)) return null;
        Import anImport = (Import) resolved;
        try {
          int lineNumber = document.getLineOfOffset(region.getOffset());
          int lineLength = document.getLineLength(lineNumber);
          document.get(region.getOffset(), lineLength - region.getOffset());
        } catch (BadLocationException e) {
        }
        String importUri = anImport.getImportURI();
        IHyperlink hyperlink = new ImportHyperlink(createURI(importUri), importUri, region);
        return new IHyperlink[] { hyperlink };
      }
    });
  }
}