package com.katsuna.widgets.commons.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import com.katsuna.widgets.commons.ui.adapters.models.ContactListItemModel;
import com.katsuna.widgets.commons.utils.Separator;

import java.util.ArrayList;
import java.util.List;

public abstract class ContactsAdapterBase extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    protected static final int NO_CONTACT_POSITION = -1;
    private final ContactFilter mFilter = new ContactFilter();
    protected List<ContactListItemModel> mOriginalContacts;
    protected List<ContactListItemModel> mFilteredContacts;
    protected int mSelectedContactPosition = NO_CONTACT_POSITION;
    protected int mSelectedFromSearchPosition = NO_CONTACT_POSITION;

    public void selectContactAtPosition(int position) {
        mSelectedContactPosition = position;
        notifyItemChanged(position);
    }

    public void deselectContact() {
        selectContactAtPosition(NO_CONTACT_POSITION);
    }

    public int getPositionByContactId(long contactId) {
        int position = NO_CONTACT_POSITION;
        for (int i = 0; i < mFilteredContacts.size(); i++) {
            if (mFilteredContacts.get(i).getContact().getId() == contactId) {
                position = i;
                break;
            }
        }
        return position;
    }

    public int getPositionByStartingLetter(String letter) {
        int position = NO_CONTACT_POSITION;
        for (int i = 0; i < mFilteredContacts.size(); i++) {
            //don't focus on premium contacts
            ContactListItemModel model = mFilteredContacts.get(i);
            if (model.isPremium()) {
                continue;
            }

            if (mFilteredContacts.get(i).getContact().getFirstLetterNormalized().startsWith(letter)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void focusFromSearch(int position) {
        int prevFocused = mSelectedFromSearchPosition;
        notifyItemChanged(prevFocused);
        mSelectedFromSearchPosition = position;
        notifyItemChanged(position);
    }

    public void unfocusFromSearch() {
        int prevFocused = mSelectedFromSearchPosition;
        mSelectedFromSearchPosition = NO_CONTACT_POSITION;
        notifyItemChanged(prevFocused);
    }

    @Override
    public int getItemCount() {
        return mFilteredContacts.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void resetFilter() {
        mFilteredContacts = mOriginalContacts;
        notifyDataSetChanged();
    }

    private class ContactFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ContactListItemModel> filteredContacts = filter(mOriginalContacts, constraint);
            FilterResults results = new FilterResults();
            results.values = filteredContacts;
            results.count = filteredContacts.size();
            return results;
        }

        private List<ContactListItemModel> filter(List<ContactListItemModel> models,
                                                  CharSequence query) {
            query = query.toString().toLowerCase();

            final List<ContactListItemModel> filteredModelList = new ArrayList<>();
            for (ContactListItemModel model : models) {
                final String text = model.getContact().getDisplayName().toLowerCase();
                if (text.contains(query)) {
                    //exclude premium contacts
                    if (!model.isPremium()) {
                        ContactListItemModel modelFound = new ContactListItemModel();
                        modelFound.setContact(model.getContact());
                        modelFound.setSeparator(Separator.NONE);
                        filteredModelList.add(model);
                    }
                }
            }
            return filteredModelList;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredContacts = (ArrayList<ContactListItemModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
