package java.util.concurrent;

import jsinterop.annotations.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/** Emulation of CompletableFuture using native Promises
 *
 */
public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {

  public static <T> CompletableFuture<T> completedFuture(T value) {
    return new CompletableFuture<T>(value);
  }

  private final List<CompletableFuture<? extends Object>> thenables = new ArrayList<>();
  private T value;
  private Throwable reason;

  public CompletableFuture() {}

  private CompletableFuture(T value) {
    this.value = value;
  }

  @Override
  public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> fn) {
    throw new IllegalStateException("Unimplemented 1!");
  }

  @Override
  public CompletableFuture<Void> thenAccept(Consumer<? super T> action) {
    throw new IllegalStateException("Unimplemented 2!");
  }

  @Override
  public <U, V> CompletableFuture<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
    throw new IllegalStateException("Unimplemented!");
  }

  @Override
  public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
    throw new IllegalStateException("Unimplemented 3!");
  }

  @JsMethod
  public boolean complete(T value) {
    throw new IllegalStateException("Unimplemented 4!");
  }

  @JsMethod
  public boolean completeExceptionally(Throwable e) {
    throw new IllegalStateException("Unimplemented 5!");
  }

  @Override
  public boolean isDone() {
    throw new IllegalStateException("Unimplemented!");
  }

  @Override
  public boolean isCancelled() {
    throw new IllegalStateException("Unimplemented!");
  }

  @Override
  public boolean cancel(boolean cancel) {
    throw new IllegalStateException("Unimplemented!");
  }

  @Override
  public CompletableFuture<T> toCompletableFuture() {
    return this;
  }

  @Override
  public T get(long t, TimeUnit unit) {
    throw new IllegalStateException("Not possible to call synchronous get() in JS!");
  }

  @Override
  public T get() {
    throw new IllegalStateException("Not possible to call synchronous get() in JS!");
  }

  public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> thenRun(Runnable action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> thenRunAsync(Runnable action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> thenRunAsync(Runnable action, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn) {     throw new IllegalStateException("Unimplemented!");   }

  public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {     throw new IllegalStateException("Unimplemented!");   }

  public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {     throw new IllegalStateException("Unimplemented!");   }

  public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {     throw new IllegalStateException("Unimplemented!");   }

}
