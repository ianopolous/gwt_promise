/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package java.util.concurrent.impl;

import java.util.function.BiConsumer;

/**
 *
 */
final class NativePromiseImpl<V> implements Promise<V> {

  final JsPromise jsPromise;
  private JsPromise.Resolver resolver;

  NativePromiseImpl() {
    jsPromise = JsPromise.create(r -> resolver = r);
    assert resolver != null;
  }

  NativePromiseImpl(JsPromise promise) {
    this.jsPromise = promise;
  }

  @Override
  public void resolve(V value) {
    assert resolver != null;
    resolver.resolve(value);
  }

  @Override
  public void reject(Throwable reason) {
    assert resolver != null;
    resolver.reject(reason);
  }

  @Override
  public void then(BiConsumer<? super V, ? super Throwable> callback) {
    jsPromise.then(callback);
  }
}
