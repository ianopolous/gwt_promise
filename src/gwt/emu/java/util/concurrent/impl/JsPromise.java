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

import com.google.gwt.core.client.JavaScriptObject;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * ECMA 6 Promise.
 */
final class JsPromise extends JavaScriptObject {

  /**
   *
   */
  public static class Resolver {

    private final JavaScriptObject fulfillCallback;
    private final JavaScriptObject rejectCallback;

    private Resolver(JavaScriptObject fulfillCallback, JavaScriptObject rejectCallback) {
      this.fulfillCallback = fulfillCallback;
      this.rejectCallback = rejectCallback;
    }

    public void resolve(Object value) {
      call(fulfillCallback, value);
    }

    public void reject(Throwable reason) {
      call(rejectCallback, reason);
    }
  }

  /*
   * Implementation taken from:
   * https://github.com/jakearchibald/es6-promise/blob/master/lib/promise/polyfill.js
   */
  public static native boolean isSupported() /*-{
    return typeof Promise === "function"
        // Some of these methods are missing from
        // Firefox/Chrome experimental implementations
        && "resolve" in Promise
        && "reject" in Promise
        && "all" in Promise
        && "race" in Promise
        // Older version of the spec had a resolver object
        // as the arg rather than a function
        && (function() {
          var resolve;
          new Promise(function(r) { resolve = r; });
          return typeof resolve === "function";
        }());
  }-*/;

  public static native JsPromise all(JsPromise[] promises) /*-{
    return Promise.all(promises);
  }-*/;

  public static native JsPromise race(JsPromise[] promises) /*-{
    return Promise.race(promises);
  }-*/;

  public static native JsPromise reject(Object reason) /*-{
    return Promise.reject(reason);
  }-*/;

  public static native JsPromise resolve(Object value) /*-{
    return Promise.resolve(value);
  }-*/;

  public static native JsPromise create(Consumer<Resolver> resolverConsumer) /*-{
    return new Promise.resolve(function (resolve, reject) {
      var resolver = @Resolver::new(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(resolve, reject);
      resolverConsumer.@Consumer::accept(*)(resolver);
    });
  }-*/;

  private static native void call(JavaScriptObject func, Object param) /*-{
    func(param);
  }-*/;

  protected JsPromise() { }

  // TODO: $entry ?
  public native <V> JsPromise then(BiConsumer<? super V, ? super Throwable> callback) /*-{
    return this.then(function (value) {
      callback.@BiConsumer::accept(*)(value, null);
    }, function (reason) {
      callback.@BiConsumer::accept(*)(null, reason);
    });
  }-*/;

  // TODO: $entry ?
  public native JsPromise then(Runnable callback) /*-{
    var callback = function() { callback.@Runnable::run()(); }
    return this.then(callback.@Runnable::run(), callback.@Runnable::run());
  }-*/;
}
