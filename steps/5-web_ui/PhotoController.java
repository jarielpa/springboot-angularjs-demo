// Copyright (c) 2014 KMS Technology, Inc.
package com.kms.contact.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * @author trungnguyen
 */
@RestController
@RequestMapping(value = "/rest/photos")
public class PhotoController extends BaseController {
    private static final String EXT_NAME = "png";
    private static final String DEFAULT_PHOTO = "contact-photo.png";
    
    @Value("${contacts.photo.storage}")
    private String photoDir;
    
    @RequestMapping(value="/{contactId}", method = GET)
    public HttpEntity<byte[]> getPhoto(@PathVariable String contactId, WebRequest request) throws IOException {
        File photoFile = new File(photoDir, contactId + "." + EXT_NAME);
        if (!photoFile.exists()) {
            photoFile = new File(photoDir, DEFAULT_PHOTO);
        }
        
        if (request.checkNotModified(photoFile.lastModified())) {
            return null; // return 304 code
        }
        
        byte[] photo = Files.readAllBytes(Paths.get(photoFile.getPath()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(photo.length);
        headers.setLastModified(photoFile.lastModified());
        return new HttpEntity<byte[]>(photo, headers);
    }
}
