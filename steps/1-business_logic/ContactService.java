// Copyright (c) 2014 KMS Technology, Inc.
package com.kms.contact.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kms.contact.domain.Contact;

/**
 * @author trungnguyen
 */
@Service
public class ContactService {
    private final AtomicInteger idGeneration = new AtomicInteger(1000);
    private final Map<String, Contact> storage = new HashMap<>();

    public long loadContacts(String filePath) throws IOException {
        long totalRecords = 0;
        try (InputStream input = new FileInputStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            totalRecords = reader
                            .lines()
                            .map(Contact::parseContact)
                            .peek(this::saveContact)
                            .count();
        }
        
        return totalRecords;
    }

    public List<Contact> searchContacts(String keyword, int page, int pageSize) {
        String keywordlw = (keyword == null) ? "" : keyword.toLowerCase();

        return storage
                .values()
                .stream()
                .filter(contact -> {
                    return false || contact.getId().toLowerCase().contains(keywordlw)
                            || contact.getName().toLowerCase().contains(keywordlw)
                            || contact.getFullName().toLowerCase().contains(keywordlw)
                            || contact.getJobTitle().toLowerCase().contains(keywordlw)
                            || contact.getEmail().toLowerCase().contains(keywordlw)
                            || contact.getMobile().toLowerCase().contains(keywordlw)
                            || contact.getSkypeId().toLowerCase().contains(keywordlw);
                })
                .skip(page * pageSize)
                .limit(pageSize)
                .sorted(Comparator.comparing(Contact::getName))
                .collect(Collectors.toList());
    }

    public Contact getContact(String id) {
        return storage.get(id);
    }

    public Contact saveContact(Contact contact) {
        if (contact.getId() == null) {
            contact.setId(String.valueOf(idGeneration.incrementAndGet()));
        }

        return storage.put(contact.getId(), contact);
    }

    public void deleteContacts(String... ids) {
        for (String id : ids) {
            storage.remove(id);
        }
    }
    
    public void deleteAllContacts() {
        storage.clear();
    }
}
