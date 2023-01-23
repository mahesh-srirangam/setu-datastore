package com.fareyeconnect.service;

import com.fareyeconnect.tool.Helper;
import com.fareyeconnect.util.rest.ResponseDto;
import com.fareyeconnect.util.rest.RestService;
import io.netty.handler.codec.http.HttpMethod;
import io.quarkus.example.FileRequest;
import io.quarkus.example.FileService;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ImageHelper {

    @GrpcClient("image")
    FileService fileService;

    @Inject
    RestService restService;

//    @Helper(description = "Returns random uuid")
//    public Uni<String> grpc(String url, String path) {
//        Uni<String> uni = fileService.uploadFile(FileRequest.newBuilder().setUrl(url).setPath(path).build()).onItem().transform(reply -> reply.getUrl());
//        return uni;
//    }
//
//    @Helper(description = "Returns random uuid")
//    public String rest(String url, String path) {
//        Map<String, String> map = new HashMap<>();
//        map.put("url",url);
//        map.put("path", path);
//        MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
//        multivaluedMap.add("Content-Type", MediaType.APPLICATION_JSON);
//        multivaluedMap.add("gateway-id", "1");
//        multivaluedMap.add("gateway-organizationId", "1");
//        multivaluedMap.add("gateway-permission", "1");
//        multivaluedMap.add("gateway-email", "1");
//        ResponseDto responseDto = restService.invoke("http://localhost:3001/uploadFile", HttpMethod.POST, map, multivaluedMap);
//        return responseDto.getResponse();
//    }
}
