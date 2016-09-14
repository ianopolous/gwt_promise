package org.client;

import com.google.gwt.typedarrays.shared.*;

import java.util.concurrent.CompletableFuture;

public class Example {

    public static native CompletableFuture<Uint8Array> getUrlBytes(String url)/*-{
    var future = org_client_promises_incomplete__Ljava_util_concurrent_CompletableFuture_2();
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
