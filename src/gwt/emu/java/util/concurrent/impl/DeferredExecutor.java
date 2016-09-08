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
import java.util.concurrent.Executor;

/**
 *
 */
public final class DeferredExecutor implements Executor {

  private static final JsPromise RESOLVED_PROMISE = JsPromise.isSupported() ? JsPromise.resolve(null) : null;

  static void scheduleDeferred(Runnable runnable) {
    if (RESOLVED_PROMISE != null) {
      RESOLVED_PROMISE.then(runnable);
    } else {
      setTimeout(runnable, 0);
    }
  }

  // TODO: $entry?
  private static native void setTimeout(Runnable runnable, int timeout) /*-{
    // TODO: this line does not work in GWTTestCase for some reason
    // setTimeout(runnable.@Runnable::run(), timeout);

    setTimeout(function() { runnable.@Runnable::run()(); }, timeout);
  }-*/;

  private final List<Runnable> tasks = new ArrayList<>();

  @Override
  public void execute(Runnable command) {
    if (tasks.isEmpty()) {
      scheduleDeferred(this::flush);
    }
    tasks.add(command);
  }

  private void flush() {
    for (Runnable task : tasks) {
      // TODO: catch exceptions?
      task.run();
    }
    tasks.clear();
  }
}
