package java.util.concurrent.impl;

import jsinterop.annotations.*;

import java.util.*;
import java.util.function.*;

@JsType(namespace = JsPackage.GLOBAL, isNative = true)
public class Promise<V> {

  native static <V> Promise<V> resolve(V value);
  native static <V> Promise<V> reject(Throwable err);
  native static <V> Promise<List<V>> all(List<Promise<V>> promises);
  native static <V> Promise<V> race(List<Promise<V>> promises);

  native <T> Promise<T> then(Function<? super V, ? extends T> onFulfilled, Function<? super Throwable, ? extends T> onError);

  @JsMethod(name = "catch")
  native <T> Promise<T> _catch(Function<? super Throwable, ? extends T> onError);
}
