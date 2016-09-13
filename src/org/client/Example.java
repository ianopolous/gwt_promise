package org.client;

import java.util.concurrent.CompletableFuture;

public class Example {

    public static native CompletableFuture<byte[]> getUrlBytes(String url)/*-{
    console.log("getHTTP");
    var future = org_client_promises_incomplete__Ljava_util_concurrent_CompletableFuture_2();
    console.log("getHTTP made future");
    var prom = new Promise(function(resolve, reject) {
	var req = new XMLHttpRequest();
	req.open('GET', url);
	req.responseType = 'arraybuffer';

	req.onload = function() {
	        console.log("getHTTP returned");
            // This is called even on 404 etc
            // so check the status
            if (req.status == 200) {
            console.log("getHTTP resolving");
		    resolve(new Uint8Array(req.response));
            }
            else {
            console.log("getHTTP rejecting");
		    reject(Error(req.statusText));
        }
	};

	req.onerror = function() {
	    console.log("getHTTP threw");
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
