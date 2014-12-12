package contactscraper.leslieaerts.com.contactscraper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.leslieaerts.contactscraper.util.ContactListener;
import com.leslieaerts.contactscraper.util.Socializer;
import com.leslieaerts.contactscraper.domain.PhoneContact;

import java.util.ArrayList;
import java.util.List;

import contactscraper.leslieaerts.com.contactscraper.util.ContactAdapter;

/**
 * Created by Leslie on 26-10-2014.
 */
public class ContactFragment extends Fragment {

    ListView list;
    Socializer socializer;
    ContactAdapter contactAdapter;
    List<PhoneContact> contacts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_example, container, false);
        list = (ListView) view.findViewById(R.id.list);
        contacts = new ArrayList<PhoneContact>();
        contactAdapter = new ContactAdapter(getActivity(), contacts);
        list.setAdapter(contactAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socializer = new Socializer(getActivity());
        socializer.setContactListener(new ContactListener() {

            @Override
            public void onContactLoaded(PhoneContact contact) {

            }

            @Override
            public void onAllContactsLoaded(List<PhoneContact> con) {
                contacts.clear();
                contacts.addAll(con);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void setContactList() {
        contacts = socializer.getAllPhoneContacts();
        contactAdapter.notifyDataSetChanged();
    }

    public void setContactList(String s) {

        contacts = socializer.getFilteredContacts(s);
        contactAdapter.notifyDataSetChanged();
    }
}
