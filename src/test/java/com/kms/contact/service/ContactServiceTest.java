package com.kms.contact.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kms.contact.Application;
import com.kms.contact.domain.Contact;
import com.kms.contact.service.ContactService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ContactServiceTest {
    @Autowired
    private ContactService contactService;
    
    @Before
    public void startUp() throws IOException {
        contactService.loadContacts(getClass().getResource("/contacts-test.txt").getFile());
    }
    
    @After
    public void tearDown() {
        contactService.deleteAllContacts();
    }
    
    @Test
    public void testSearchContacts() {
        List<Contact> contacts;
        
        // search all
        contacts = contactService.searchContacts("", 0, 10);
        assertThat(contacts.size(), is(equalTo(5)));
        
        // search by id
        contacts = contactService.searchContacts("00", 0, 10);
        assertThat(contacts.size(), is(equalTo(5)));
        
        // search by name
        contacts = contactService.searchContacts("name1", 0, 10);
        assertThat(contacts.size(), is(equalTo(1)));
        assertThat(contacts.get(0).getId(), is(equalTo("001")));
        
        // search by fullName
        contacts = contactService.searchContacts("fullName2", 0, 10);
        assertThat(contacts.size(), is(equalTo(1)));
        assertThat(contacts.get(0).getId(), is(equalTo("002")));
        
        // search by jobTitle
        contacts = contactService.searchContacts("title3", 0, 10);
        assertThat(contacts.size(), is(equalTo(1)));
        assertThat(contacts.get(0).getId(), is(equalTo("003")));
        
        // search by email
        contacts = contactService.searchContacts("email4", 0, 10);
        assertThat(contacts.size(), is(equalTo(1)));
        assertThat(contacts.get(0).getId(), is(equalTo("004")));
        
        // search by skypeId
        contacts = contactService.searchContacts("skypeId5", 0, 10);
        assertThat(contacts.size(), is(equalTo(1)));
        assertThat(contacts.get(0).getId(), is(equalTo("005")));
    }
    
    @Test
    public void testSaveContact() {
        // create
        Contact contact = new Contact("new name", "new full name");
        contactService.saveContact(contact);
        
        List<Contact> contacts;
        contacts = contactService.searchContacts("new name", 0, 10);
        assertThat(contacts.size(), is(equalTo(1)));
        contact = contacts.get(0);
        assertThat(contact.getFullName(), is(equalTo("new full name")));
        
        // update
        contact = contactService.getContact("001");
        assertNotNull(contact);
        contact.setFullName("update full name");
        contactService.saveContact(contact);
        contact = contactService.getContact("001");
        assertThat(contact.getFullName(), is(equalTo("update full name")));
    }
    
    @Test
    public void testDeleteContact() {
        List<Contact> contacts;
        contacts = contactService.searchContacts("", 0, 10);
        assertThat(contacts.size(), is(equalTo(5)));
        
        contactService.deleteContacts("001", "002");
        
        contacts = contactService.searchContacts("", 0, 10);
        assertThat(contacts.size(), is(equalTo(3)));
        
        contacts.forEach(contact -> {
            assertThat(contact.getId(), is(not("001")));
            assertThat(contact.getId(), is(not("00")));
        });
    }
}
