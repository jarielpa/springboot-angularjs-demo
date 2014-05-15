// Copyright (c) 2014 KMS Technology, Inc.
package com.kms.contact.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kms.contact.domain.Contact;
import com.kms.contact.repository.ContactRepository;

/**
 * @author trungnguyen
 */
@Service
@Transactional(readOnly = true)
public class ContactService {
    private final AtomicInteger idGeneration = new AtomicInteger(1000);
    
    @Autowired
    private ContactRepository contactRepo;

    @Transactional
    public long loadContacts(String filePath) throws IOException {
        long totalRecords = 0;
        try (InputStream input = new FileInputStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            totalRecords = reader
                            .lines()
                            .map(Contact::parseContact)
                            .map(this::saveContact)
                            .count();
        }
        
        return totalRecords;
    }

    public List<Contact> searchContacts(String keyword, int page, int pageSize) {
        keyword = (keyword == null) ? "" : keyword.toLowerCase();
        return contactRepo.searchContacts(keyword, new PageRequest(page, pageSize));
    }

    public Contact getContact(String id) {
        return contactRepo.findOne(id);
    }

    @Transactional
    public Contact saveContact(Contact contact) {
        if (contact.getId() == null) {
            contact.setId(String.valueOf(idGeneration.incrementAndGet()));
        }

        return contactRepo.save(contact);
    }

    @Transactional
    public void deleteContacts(String... ids) {
        contactRepo.deleteContacts(ids);
    }
    
    @Transactional
    public void deleteAllContacts() {
        contactRepo.deleteAllInBatch();
    }
}