// Copyright (c) 2014 KMS Technology, Inc.
package com.kms.contact.service;

import org.springframework.stereotype.Component;

/**
 * @author trungnguyen
 */
@Component
public class ContactService {
    public String ping() {
        return "pong";
    }
}