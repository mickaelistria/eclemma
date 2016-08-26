/*******************************************************************************
 * Copyright (c) 2006, 2016 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.eclemma.internal.core.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.eclemma.core.JavaProjectKit;

/**
 * Tests for {@link MethodLocator}.
 */
public class MethodLocatorTest {

  private JavaProjectKit javaProject;

  private MethodLocator methodLocator;

  @Before
  public void setup() throws Exception {
    javaProject = new JavaProjectKit();
    javaProject.enableJava5();
    final IPackageFragmentRoot root = javaProject.createSourceFolder("src");
    final ICompilationUnit compilationUnit = javaProject.createCompilationUnit(
        root, "testdata/src", "methodlocator/Samples.java");
    JavaProjectKit.waitForBuild();
    javaProject.assertNoErrors();
    methodLocator = new MethodLocator(compilationUnit.getTypes()[0]);
  }

  @After
  public void teardown() throws Exception {
    javaProject.destroy();
  }

  private final void assertMethod(final String expectedKey, final String name,
      final String signature) {
    final IMethod method = methodLocator.findMethod(name, signature);
    assertNotNull(method);
    assertEquals(expectedKey, method.getKey());
  }

  @Test
  public void testUnambiguousConstructor() {
    assertMethod("Lmethodlocator/Samples;.Samples()V", "<init>", "()V");
  }

  @Test
  public void testAmbiguousConstructor1() {
    assertMethod("Lmethodlocator/Samples;.Samples(QString;)V", "<init>",
        "(Ljava/lang/String;)V");
  }

  @Test
  public void testAmbiguousConstructor2() {
    assertMethod("Lmethodlocator/Samples;.Samples(I)V", "<init>", "(I)V");
  }

  @Test
  public void testUnambiguousMethod() {
    assertMethod("Lmethodlocator/Samples;.m1(QString;)V", "m1",
        "(Ljava/lang/String;)V");
  }

  @Test
  public void testAmbiguousMethod1() {
    assertMethod("Lmethodlocator/Samples;.m2(QInteger;)V", "m2",
        "(Ljava/lang/Integer;)V");
  }

  @Test
  public void testAmbiguousMethod2() {
    assertMethod("Lmethodlocator/Samples;.m2(QNumber;)V", "m2",
        "(Ljava/lang/Number;)V");
  }

}
