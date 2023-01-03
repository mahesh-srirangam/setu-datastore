package com.fareyeconnect.controller;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author srirangam
 * @since 30/12/22 10:09 am
 */

@Path("/prices")
public class PriceResource {
    @Inject
    @Channel("price-stream")
    Publisher<Double> prices;

    @Inject
    PriceConverter converter;

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS) // denotes that server side events (SSE) will be produced
    public Publisher<Double> stream() {
        converter.start();
        return prices;
    }
}
