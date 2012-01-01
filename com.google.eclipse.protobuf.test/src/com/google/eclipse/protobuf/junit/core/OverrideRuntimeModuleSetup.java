/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;
import static com.google.inject.util.Modules.override;

import com.google.eclipse.protobuf.*;
import com.google.inject.*;
/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class OverrideRuntimeModuleSetup extends ProtobufStandaloneSetup {
  private final Module module;

  OverrideRuntimeModuleSetup(Module module) {
    this.module = module;
  }

  @Override public Injector createInjector() {
    Module newModule = override(new ProtobufRuntimeModule()).with(module);
    return Guice.createInjector(newModule);
  }
}