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
package java.util.concurrent;

import jsinterop.annotations.*;

import static javaemul.internal.InternalPreconditions.checkNotNull;

import java.util.concurrent.impl.DeferredExecutor;
import java.util.concurrent.impl.Promise;
import java.util.concurrent.impl.Promises;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Emulation of CompletableFuture.
 *
 */
public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {

  public interface AsynchronousCompletionTask { }

  public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
    return supplyAsync(supplier, DEFAULT_EXECUTOR);
  }

  public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier, Executor executor) {
    checkNotNull(supplier);
    checkNotNull(executor);

    CompletableFuture<T> future = new CompletableFuture<>();
    executor.execute(() -> {
      try {
        future.complete(supplier.get());
      } catch (Throwable t) {
        future.completeExceptionally(t);
      }
    });
    return future;
  }

  public static CompletableFuture<Void> runAsync(Runnable action) {
    return runAsync(action, DEFAULT_EXECUTOR);
  }

  public static CompletableFuture<Void> runAsync(Runnable action, Executor executor) {
    return supplyAsync(() -> {
      action.run();
      return null;
    }, executor);
  }

  public static <T> CompletableFuture<T> completedFuture(T value) {
    return new CompletableFuture<>(value);
  }

  public static CompletableFuture<Void> allOf(CompletableFuture<?>... futures) {
    if (futures.length == 0) {
      return completedFuture(null);
    }

    CompletableFuture<Void> future = new CompletableFuture<>();
    and(futures).then((value, reason) -> {
      if (reason != null) {
        future.completeExceptionally(reason);
      } else {
        future.complete(null);
      }
    });
    return future;
  }

  public static CompletableFuture<Object> anyOf(CompletableFuture<?>... futures) {
    if (futures.length == 0) {
      return new CompletableFuture<>();
    }

    CompletableFuture<Object> future = new CompletableFuture<>();
    or(futures).then((value, reason) -> {
      if (reason != null) {
        future.completeExceptionally(reason);
      } else {
        future.complete(value);
      }
    });
    return future;
  }

  private static Promise<Void> and(CompletableFuture<?>... futures) {
    return Promises.IMPL.allOf(toPromises(futures));
  }

  private static Promise<Object> or(CompletableFuture<?>... futures) {
    return Promises.IMPL.anyOf(toPromises(futures));
  }

  private static Promise[] toPromises(CompletableFuture<?>... futures) {
    int length = futures.length;
    Promise[] promises = new Promise[length];
    for (int i = 0; i < length; i++) {
      promises[i] = futures[i].promise;
    }
    return promises;
  }

  private static <T> BiConsumer<? super T, ? super Throwable> runAsync(
      Executor executor, BiConsumer<? super T, ? super Throwable> action) {

    if (executor == null) {
      return action;
    } else {
      return (r, e) -> executor.execute(() -> action.accept(r, e));
    }
  }

  private static final Executor DEFAULT_EXECUTOR = new DeferredExecutor();

  private final Promise<T> promise;
  private boolean done;
  private T value;
  private Throwable reason;

  @JsConstructor
  public CompletableFuture() {
    promise = Promises.IMPL.incomplete();
  }

  private CompletableFuture(T value) {
    promise = Promises.IMPL.completed(value);
    done = true;
  }

  @Override
  public <U> CompletionStage<U> thenApply(Function<? super T, ? extends U> fn) {
    return thenApplyAsync0(fn, null);
  }

  @Override
  public <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
    return thenApplyAsync0(fn, DEFAULT_EXECUTOR);
  }

  @Override
  public <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
    checkNotNull(executor);
    return thenApplyAsync0(fn, executor);
  }

  private <U> CompletionStage<U> thenApplyAsync0(Function<? super T, ? extends U> fn, Executor executor) {
    checkNotNull(fn);
    CompletableFuture<U> future = new CompletableFuture<>();
    onStageComplete((r, e) -> {
      if (e != null) {
        future.completeExceptionally(e);
      } else {
        try {
          future.complete(fn.apply(r));
        } catch (Exception ex) {
          future.completeExceptionally(ex);
        }
      }
    }, executor);
    return future;
  }

  @Override
  public CompletionStage<Void> thenAccept(Consumer<? super T> action) {
    return thenAcceptAsync0(action, null);
  }

  @Override
  public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action) {
    return thenAcceptAsync0(action, DEFAULT_EXECUTOR);
  }

  @Override
  public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
    checkNotNull(executor);
    return thenAcceptAsync0(action, executor);
  }

  private CompletionStage<Void> thenAcceptAsync0(Consumer<? super T> action, Executor executor) {
    checkNotNull(action);
    return thenApplyAsync0((r) -> {
      action.accept(r);
      return null;
    }, executor);
  }

  @Override
  public CompletionStage<Void> thenRun(Runnable action) {
    return thenRunAsync0(action, null);
  }

  @Override
  public CompletionStage<Void> thenRunAsync(Runnable action) {
    return thenRunAsync0(action, DEFAULT_EXECUTOR);
  }

  @Override
  public CompletionStage<Void> thenRunAsync(Runnable action, Executor executor) {
    checkNotNull(executor);
    return thenRunAsync0(action, executor);
  }

  private CompletionStage<Void> thenRunAsync0(Runnable action, Executor executor) {
    checkNotNull(action);
    return thenApplyAsync0((r) -> {
      action.run();
      return null;
    }, executor);
  }

  @Override
  public <U, V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other,
      BiFunction<? super T, ? super U, ? extends V> fn) {

    return thenCombineAsync0(other, fn, null);
  }

  @Override
  public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
      BiFunction<? super T, ? super U, ? extends V> fn) {

    return thenCombineAsync0(other, fn, DEFAULT_EXECUTOR);
  }

  @Override
  public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
      BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
    checkNotNull(executor);
    return thenCombineAsync0(other, fn, executor);
  }

  private <U, V> CompletionStage<V> thenCombineAsync0(CompletionStage<? extends U> other,
      BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {

    checkNotNull(fn);
    CompletableFuture<V> future = new CompletableFuture<>();
    CompletableFuture<T> first = this;
    CompletableFuture<? extends U> second = other.toCompletableFuture();
    and(first, second).then(runAsync(executor, (ignored, e) -> {
      if (e != null) {
        future.completeExceptionally(e);
      } else {
        try {
          future.complete(fn.apply(first.get(), second.get()));
        } catch (Exception ex) {
          future.completeExceptionally(ex);
        }
      }
    }));
    return future;
  }

  @Override
  public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other,
      BiConsumer<? super T, ? super U> action) {

    return thenAcceptBothAsync0(other, action, null);
  }

  @Override
  public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
      BiConsumer<? super T, ? super U> action) {

    return thenAcceptBothAsync0(other, action, DEFAULT_EXECUTOR);
  }

  @Override
  public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
      BiConsumer<? super T, ? super U> action, Executor executor) {

    checkNotNull(executor);
    return thenAcceptBothAsync0(other, action, executor);
  }

  private <U> CompletionStage<Void> thenAcceptBothAsync0(CompletionStage<? extends U> other,
      BiConsumer<? super T, ? super U> action, Executor executor) {

    checkNotNull(action);
    return thenCombineAsync0(other, (a, b) -> {
      action.accept(a, b);
      return null;
    }, executor);
  }

  @Override
  public CompletionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
    return runAfterBothAsync0(other, action, null);
  }

  @Override
  public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
    return runAfterBothAsync0(other, action, DEFAULT_EXECUTOR);
  }

  @Override
  public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
    checkNotNull(executor);
    return runAfterBothAsync0(other, action, executor);
  }

  private CompletionStage<Void> runAfterBothAsync0(CompletionStage<?> other, Runnable action, Executor executor) {
    checkNotNull(action);
    return thenCombineAsync0(other, (a, b) -> {
      action.run();
      return null;
    }, executor);
  }

  @Override
  public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
    return applyToEitherAsync0(other, fn, null);
  }

  @Override
  public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
    return applyToEitherAsync0(other, fn, DEFAULT_EXECUTOR);
  }

  @Override
  public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other,
      Function<? super T, U> fn, Executor executor) {

    checkNotNull(executor);
    return applyToEitherAsync0(other, fn, executor);
  }

  private <U> CompletionStage<U> applyToEitherAsync0(CompletionStage<? extends T> other,
      Function<? super T, U> fn, Executor executor) {

    checkNotNull(fn);
    CompletableFuture<U> future = new CompletableFuture<>();
    or(this, other.toCompletableFuture()).then(runAsync(executor, (r, e) -> {
      if (e != null) {
        future.completeExceptionally(e);
      } else {
        try {
          future.complete(fn.apply((T) r));
        } catch (Exception ex) {
          future.completeExceptionally(ex);
        }
      }
    }));
    return future;
  }

  @Override
  public CompletionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
    return acceptEitherAsync0(other, action, null);
  }

  @Override
  public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
    return acceptEitherAsync0(other, action, DEFAULT_EXECUTOR);
  }

  @Override
  public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other,
      Consumer<? super T> action, Executor executor) {

    checkNotNull(executor);
    return acceptEitherAsync0(other, action, executor);
  }

  private CompletionStage<Void> acceptEitherAsync0(CompletionStage<? extends T> other,
      Consumer<? super T> action, Executor executor) {
    checkNotNull(action);
    return applyToEitherAsync0(other, (r) -> {
      action.accept(r);
      return null;
    }, executor);
  }

  @Override
  public CompletionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
    return runAfterEitherAsync0(other, action, null);
  }

  @Override
  public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
    return runAfterEitherAsync0(other, action, DEFAULT_EXECUTOR);
  }

  @Override
  public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
    checkNotNull(executor);
    return runAfterEitherAsync0(other, action, executor);
  }

  private CompletionStage<Void> runAfterEitherAsync0(CompletionStage<?> other, Runnable action, Executor executor) {
    checkNotNull(action);
    // TODO: cant use applyToEitherAsync0 here
    return ((CompletionStage<Object>) this).applyToEitherAsync(other, (r) -> {
      action.run();
      return null;
    }, executor);
  }

  @Override
  public <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
    return thenComposeAsync0(fn, null);
  }

  @Override
  public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
    return thenComposeAsync0(fn, DEFAULT_EXECUTOR);
  }

  @Override
  public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
    checkNotNull(executor);
    return thenComposeAsync0(fn, executor);
  }

  private <U> CompletionStage<U> thenComposeAsync0(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
    checkNotNull(fn);
    CompletableFuture<U> future = new CompletableFuture<>();
    onStageComplete((r, e) -> {
      if (e != null) {
        future.completeExceptionally(e);
      } else {
        try {
          CompletableFuture<U> newFuture = fn.apply(r).toCompletableFuture();
          newFuture.whenCompleteAsync((r1, ex) -> {
            if (ex != null) {
              future.completeExceptionally(ex);
            } else {
              future.complete(r1);
            }
          }, executor);
        } catch (Exception ex) {
          future.completeExceptionally(ex);
        }
      }
    }, null); // TODO: executor?
    return future;
  }

  @Override
  public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn) {
    checkNotNull(fn);
    return handle((r, e) -> e != null ? fn.apply(e) : r);
  }

  @Override
  public CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
    return whenCompleteAsync0(action, null);
  }

  @Override
  public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
    return whenCompleteAsync0(action, DEFAULT_EXECUTOR);
  }

  @Override
  public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
    checkNotNull(executor);
    return whenCompleteAsync0(action, executor);
  }

  private CompletionStage<T> whenCompleteAsync0(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
    checkNotNull(action);
    return handleAsync0((r, e) -> {
      action.accept(r, e);
      return null;
    }, executor);
  }

  @Override
  public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
    return handleAsync0(fn, null);
  }

  @Override
  public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
    return handleAsync0(fn, DEFAULT_EXECUTOR);
  }

  @Override
  public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
    checkNotNull(executor);
    return handleAsync0(fn, executor);
  }

  private <U> CompletionStage<U> handleAsync0(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
    checkNotNull(fn);
    CompletableFuture<U> future = new CompletableFuture<>();
    onStageComplete((r, e) -> {
      try {
        future.complete(fn.apply(r, e));
      } catch (Exception ex) {
        future.completeExceptionally(ex);
      }
    }, executor);
    return future;
  }

  @Override
  public CompletableFuture<T> toCompletableFuture() {
    return this;
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    if (!isDone()) {
      completeExceptionally(new CancellationException());
      return true;
    }
    return false;
  }

  @Override
  public boolean isCancelled() {
    return reason instanceof CancellationException;
  }

  @Override
  public boolean isDone() {
    return done;
  }

  public boolean isCompletedExceptionally() {
    return reason != null;
  }

  @Override
  public T get() throws InterruptedException, ExecutionException {
    checkNotBlocked();
    if (reason != null) {
      if (reason instanceof CancellationException) {
        throw (CancellationException) reason;
      }
      Throwable cause = null;
      if (reason instanceof CompletionException) {
        cause = reason.getCause();
      }
      if (cause == null) {
        cause = reason;
      }
      throw new ExecutionException(cause);
    }
    return value;
  }

  // TODO: comment
  @Override
  public T get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {

    return get();
  }

  public T join() {
    checkNotBlocked();
    return getJoinValue();
  }

  // TODO: comment
  private void checkNotBlocked() {
    if (!isDone()) {
      throw new UnsupportedOperationException("cannot block");
    }
  }

  public T getNow(T valueIfAbsent) {
    return isDone() ? getJoinValue() : valueIfAbsent;
  }

  private T getJoinValue() {
    if (reason != null) {
      throw wrap(reason);
    }
    return value;
  }

  public void obtrudeValue(T value) {
    completeStage(value, null);
  }

  public void obtrudeException(Throwable e) {
    completeStage(null, checkNotNull(e));
  }

  // TODO: comment
  public int getNumberOfDependents() {
    return 0;
  }

  public boolean complete(T value) {
    if (!isDone()) {
      obtrudeValue(value);
      return true;
    }
    return false;
  }

  public boolean completeExceptionally(Throwable e) {
    if (!isDone()) {
      obtrudeException(e);
      return true;
    }
    return false;
  }

  private void completeStage(T value, Throwable reason) {
    done = true;
    this.value = value;
    this.reason = reason; // TODO: wrap exception here? and nowhere else

    if (reason == null) {
      promise.resolve(value);
    } else {
      promise.reject(wrap(reason));
    }
  }

  private static RuntimeException wrap(Throwable t) {
    if (t instanceof CancellationException) {
      return (CancellationException) t;
    }
    if (t instanceof CompletionException) {
      return (CompletionException) t;
    }
    return new CompletionException(t);
  }

  private void onStageComplete(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
    promise.then(runAsync(executor, action));
  }

}
