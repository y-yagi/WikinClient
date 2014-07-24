package com.example.yaginuma.wikinclient.providers;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.provider.SearchRecentSuggestions;

/**
 * Created by yaginuma on 14/07/23.
 */
public class WikinClientSuggestionProvider extends SearchRecentSuggestionsProvider {

    public WikinClientSuggestionProvider() {
        setupSuggestions("wikinclient", DATABASE_MODE_QUERIES);
    }
}
