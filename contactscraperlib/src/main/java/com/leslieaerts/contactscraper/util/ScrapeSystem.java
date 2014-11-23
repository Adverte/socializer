package com.leslieaerts.contactscraper.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

import com.leslieaerts.contactscraper.domain.PhoneContact;

import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leslie on 26-10-2014.
 */
public class ScrapeSystem {
    private static final String[] BASIC_CONTACT_PROJECTION = {
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Contacts.DISPLAY_NAME_PRIMARY
    };

    private static final String[] DETAILED_CONTACT_PROJECTION = {
            ContactsContract.Data._ID,
            ContactsContract.Data.LOOKUP_KEY,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY
    };
    private final Context context;

    public ScrapeSystem(Context context) {
        this.context = context;
    }

    public Bitmap getContactPhotoByContactId(String id) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream photoInputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri);
        Bitmap photo = BitmapFactory.decodeStream(photoInputStream);
        if (photo != null) {
            return photo;
        }
        return null;
    }

    public List<PhoneContact> getAllPhoneContacts() {
        List<PhoneContact> contacts = new ArrayList<PhoneContact>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, new String[]{Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY}, null, null, null);

        if (idCursor.moveToFirst()) {
            do {
                String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts._ID));
                String name = idCursor.getString(idCursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY));
                Bitmap photo = getContactPhotoByContactId(contactId);

                PhoneContact contact = new PhoneContact();
                contact.setContactId(contactId);

                contact.setDisplayName(name);
                contact.setPhoto(photo);

                //Phone stuff
                Map<String, String> phoneMap = getContactPhoneNumbersByContactId(contactId);
                contact.addPhoneNumbers(phoneMap);

                //Email stuff
                Map<String, String> emailsMap = getContactEmailByContactId(contactId);
                contact.addEmailAddresses(emailsMap);

                contacts.add(contact);
            } while (idCursor.moveToNext());
        }
        idCursor.close();

        return contacts;
    }

    private Map<String, String> getContactPhoneNumbersByContactId(String contactId) {
        Map<String, String> phoneNumbers = new HashMap<String, String>();

        Cursor phoneCursor = context.getContentResolver().query(Phone.CONTENT_URI, new String[]{Phone.NUMBER, Phone.TYPE}, Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);
        if (phoneCursor.moveToFirst()) {
            do {
                String phoneNumber = null;
                String phoneNumberType = null;
                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
                phoneNumberType = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.TYPE));
                phoneNumbers.put(phoneNumberType, phoneNumber);
            } while (phoneCursor.moveToNext());
        }
        phoneCursor.close();
        return phoneNumbers;
    }

    private Map<String, String> getContactEmailByContactId(String contactId) {
        Cursor emailCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, new String[]{Phone.NUMBER, Phone.TYPE}, Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);
        Map<String, String> emails = new HashMap<String, String>();
        if (emailCursor.moveToFirst()) {
            do {

                String emailAddress = null;
                String emailType = null;
                emailAddress = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                emailType = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                emails.put(emailType, emailAddress);
            } while (emailCursor.moveToNext());
        }
        emailCursor.close();
        return emails;
    }

    public String getContactEmail(String key) {
        return null;
    }

    public PhoneContact getPhoneContactByName(String name) {
        PhoneContact contact = new PhoneContact();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(Contacts.CONTENT_URI, new String[]{Contacts.LOOKUP_KEY}, Contacts.DISPLAY_NAME_PRIMARY + "LIKE ?", new String[]{name}, null);

        if (c.moveToFirst()) {

            while (c.moveToNext()) {
                String lookup = c.getString(0);
                contact.setLookupKey(lookup);
            }
            c.close();
        }
        return new PhoneContact();
        // c = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.  }, Contacts.look+ " = ?", new String[]{firstName}, null);
    }

//    private Cursor createCursor(Uri table, String[] projection, String[] selection)
//    {
//        ContentResolver contentResolver = context.getContentResolver();
//        return contentResolver.query(table, projection, Contacts.DISPLAY_NAME_PRIMARY + "LIKE ?", selection, null);
//    }
}


