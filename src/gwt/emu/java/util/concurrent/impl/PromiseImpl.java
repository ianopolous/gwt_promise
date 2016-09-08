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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 *
 */
final class PromiseImpl<V> implements Promise<V> {
  private V value;
  private Throwable reason;
  private boolean done;
  private List<BiConsumer<? super V, ? super Throwable>> callbacks;

  PromiseImpl() {
  }

  PromiseImpl(V value) {
    resolve(value);
  }

  @Override
  public void resolve(V value) {
    complete(value, null);
  }

  @Override
  public void reject(Throwable reason) {
    complete(null, reason);
  }

  @Override
  public void then(BiConsumer<? super V, ? super Throwable> callback) {
    if (done) {
      // TODO: catch exceptions?
      DeferredExecutor.scheduleDeferred(() -> callback.accept(value, reason));
      return;
    }

    if (callbacks == null) {
      callbacks = new ArrayList<>();
    }
    callbacks.add(callback);
  }

  private void complete(V value, Throwable reason) {
    if (!done) {
      this.value = value;
      this.reason = reason;
      done = true;

      if (callbacks != null) {
        DeferredExecutor.scheduleDeferred(() -> {
          for (BiConsumer<? super V, ? super Throwable> callback : callbacks) {
            // TODO: catch exceptions?
            callback.accept(value, reason);
          }
          callbacks = null;
        });
      }
    }
  }
}
