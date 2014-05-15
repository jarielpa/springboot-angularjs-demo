// Copyright (c) 2014 KMS Technology, Inc.
package com.kms.contact.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

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
    
    @Value("${contacts.photo.resize-width}")
    private int photoWidth;
    
    @Value("${contacts.photo.resize-height}")
    private int photoHeight;
    
    @Autowired
    private MultipartResolver multipartResolver;
    
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
    
    @RequestMapping(value="/{contactId}", method = POST)
    public void uploadPhoto(@PathVariable String contactId, HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);
        
        MultipartFile file = multipartRequest.getFile("file");
        File uploadFile = File.createTempFile("contact-", contactId);
        file.transferTo(uploadFile);
        
        BufferedImage originalImage = ImageIO.read(uploadFile);
        BufferedImage resizedImage = scaleImage(originalImage, photoWidth, photoHeight);
        
        File photoFile = new File(photoDir, contactId + "." + EXT_NAME);
        ImageIO.write(resizedImage, EXT_NAME, photoFile);
    }
    
    private static BufferedImage scaleImage(BufferedImage image, int width, int height) throws IOException {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        image = bilinearScaleOp.filter(image, new BufferedImage(width, height, type));
        return image.getSubimage(0, (height-width)/2, width, width);
    }
}
