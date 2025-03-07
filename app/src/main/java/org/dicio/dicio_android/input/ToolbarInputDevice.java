package org.dicio.dicio_android.input;

import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import org.dicio.dicio_android.util.StringUtils;

public class ToolbarInputDevice extends InputDevice {
    @Nullable private MenuItem textInputItem = null;

    public void setTextInputItem(@Nullable final MenuItem textInputItem) {
        if (this.textInputItem != null) {
            ((SearchView) this.textInputItem.getActionView()).setOnQueryTextListener(null);
        }

        this.textInputItem = textInputItem;
        if (textInputItem != null) {
            textInputItem.setOnMenuItemClickListener(item -> {
                ToolbarInputDevice.super.tryToGetInput(true); // notify we are starting to get input
                return false; // returning false causes the item to expand
            });
            textInputItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(final MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(final MenuItem item) {
                    notifyNoInputReceived();
                    return true;
                }
            });

            final SearchView textInputView = (SearchView) textInputItem.getActionView();
            textInputView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String query) {
                    if (StringUtils.isNullOrEmpty(query)) {
                        notifyNoInputReceived();
                    } else {
                        notifyInputReceived(query);
                    }
                    textInputItem.collapseActionView();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(final String newText) {
                    // here we could call notifyPartialInputReceived(), but it would be useless
                    // since the user can already see what he is typing elsewhere
                    return true;
                }
            });
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        setTextInputItem(null);
    }

    @Override
    public void load() {
    }

    @Override
    public void tryToGetInput(boolean manual) {
        if (textInputItem != null) {
            super.tryToGetInput(manual);
            textInputItem.expandActionView();
        }
    }

    @Override
    public void cancelGettingInput() {
        if (textInputItem != null && textInputItem.isActionViewExpanded()) {
            textInputItem.collapseActionView();
            notifyNoInputReceived();
        }
    }
}
