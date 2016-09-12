package org.client;

import java.util.concurrent.CompletableFuture;

public class Example {

    public static native CompletableFuture<byte[]> getUrlBytes(String url)/*-{
    console.log("getHTTP");
    var future = new java.util.concurrent.CompletableFuture();
    var prom = new Promise(function(resolve, reject) {
	var req = new XMLHttpRequest();
	req.open('GET', url);
	req.responseType = 'arraybuffer';

	req.onload = function() {
            // This is called even on 404 etc
            // so check the status
            if (req.status == 200) {
		resolve(new Uint8Array(req.response));
            }
            else {
		reject(Error(req.statusText));
            }
	};

	req.onerror = function() {
            reject(Error("Network Error"));
	};

	req.send();
    });
    prom.then(function(result, err) {
        if (err != null)
            future.completeExceptionally(err);
        else
            future.complete(result);
    });
    return future;
    }-*/;
}
