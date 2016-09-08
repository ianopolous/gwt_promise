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

/**
 *
 */
final class NativePromisesImpl implements Promises {

  @Override
  public Promise<Void> allOf(Promise[] promises) {
    return wrap(JsPromise.all(unwrap(promises)));
  }

  @Override
  public Promise<Object> anyOf(Promise[] promises) {
    return wrap(JsPromise.race(unwrap(promises)));
  }

  @Override
  public <V> Promise<V> completed(V value) {
    return wrap(JsPromise.resolve(value));
  }

  @Override
  public <V> Promise<V> incomplete() {
    return new NativePromiseImpl<>();
  }

  private static <V> Promise<V> wrap(JsPromise promise) {
    return new NativePromiseImpl<>(promise);
  }

  private static JsPromise[] unwrap(Promise[] promises) {
    int length = promises.length;
    JsPromise[] jsPromises = new JsPromise[length];
    for (int i = 0; i < length; ++i) {
      jsPromises[i] = ((NativePromiseImpl) promises[i]).jsPromise;
    }
    return jsPromises;
  }
}