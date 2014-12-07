package com.leslieaerts.contactscraper.util;

import android.content.Context;

import com.leslieaerts.contactscraper.domain.PhoneContact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leslie on 25-10-2014.
 */
public class Socializer {

    private ContactDatabaseAccess scraper;
    private List<PhoneContact> loadedContacts;
    private Thread loader;

    public Socializer(Context context) {
        scraper = new ContactDatabaseAccess(context);

        loadedContacts = new ArrayList<PhoneContact>();
        loader = new Thread(new Runnable() {
            @Override
            public void run() {
                loadedContacts = scraper.getAllPhoneContacts();
            }
        });
        loader.start();
    }

    public PhoneContact getPhoneContactByName(String partialName) {
        return scraper.getPhoneContactByName(partialName);
    }

    public List<PhoneContact> getAllPhoneContactsOld() {
        return scraper.getAllPhoneContacts();
    }

    public List<PhoneContact> getAllPhoneContacts() {

        if (!loader.isAlive()) {
            loader.start();
        }
        try {
            loader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return loadedContacts;
    }

    public List<PhoneContact> getFilteredContacts(String filter) {

        if (!loader.isAlive()) {
            loader.start();
        }

        List<PhoneContact> copyList = new ArrayList<PhoneContact>(loadedContacts);
        List<PhoneContact> filters = new ArrayList<PhoneContact>();

        for (PhoneContact contact : copyList) {
            CharSequence cs = filter;
            if (contact.getDisplayName().contains(cs)) {
                filters.add(contact);
            }
        }
        return filters;
    }

    public void loadPhoneContactsAsync(List<PhoneContact> phoneContacts) {
        throw new UnsupportedOperationException();
    }
}