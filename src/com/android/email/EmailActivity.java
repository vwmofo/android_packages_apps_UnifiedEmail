/*
 * Copyright (C) 2012 Google Inc.
 * Licensed to The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.email;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.email.EmailActivity.AccountsSpinnerAdapter;
import com.android.email.EmailActivity.ConversationItemAdapter;
import com.android.email.browse.ConversationItemView;
import com.android.email.browse.ConversationItemViewModel;
import com.android.email.browse.ConversationViewActivity;
import com.android.email.providers.UIProvider;
import com.android.email.providers.protos.mock.MockUiProvider;

public class EmailActivity extends Activity implements OnItemSelectedListener, OnItemClickListener {

    private ListView mListView;
    private ConversationItemAdapter mListAdapter;
    private Spinner mAccountsSpinner;
    private AccountsSpinnerAdapter mAccountsAdapter;
    private ContentResolver mResolver;
    private String mSelectedAccount;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email);
        mListView = (ListView) findViewById(R.id.browse_list);
        mListView.setOnItemClickListener(this);
        mAccountsSpinner = (Spinner) findViewById(R.id.accounts_spinner);
        mResolver = getContentResolver();
        Cursor cursor = mResolver.query(MockUiProvider.getAccountsUri(),
                UIProvider.ACCOUNTS_PROJECTION, null, null, null);
        mAccountsAdapter = new AccountsSpinnerAdapter(this, cursor);
        mAccountsSpinner.setAdapter(mAccountsAdapter);
        mAccountsSpinner.setOnItemSelectedListener(this);
    }

    class AccountsSpinnerAdapter extends SimpleCursorAdapter implements SpinnerAdapter {

        private LayoutInflater mLayoutInflater;

        public AccountsSpinnerAdapter(Context context, Cursor cursor) {
            super(context, android.R.layout.simple_dropdown_item_1line, cursor,
                    UIProvider.ACCOUNTS_PROJECTION, null, 0);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return mLayoutInflater.inflate(android.R.layout.simple_dropdown_item_1line, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int accountNameCol = cursor.getColumnIndex(UIProvider.AccountColumns.NAME);
            ((TextView) view.findViewById(android.R.id.text1)).setText(cursor
                    .getString(accountNameCol));
        }
    }

    class ConversationItemAdapter extends SimpleCursorAdapter {

        public ConversationItemAdapter(Context context, int textViewResourceId, Cursor cursor) {
            super(context, textViewResourceId, cursor, UIProvider.CONVERSATION_PROJECTION, null, 0);
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            ConversationItemView view = new ConversationItemView(context, "test@testaccount.com");
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ((ConversationItemView) view).bind(cursor, null, "test@testaccount.com", null,
                    new ViewMode(EmailActivity.this));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Uri foldersUri = null;
        Cursor cursor = mAccountsAdapter.getCursor();
        if (cursor != null && cursor.moveToPosition(position)) {
            int uriCol = cursor.getColumnIndex(UIProvider.AccountColumns.FOLDER_LIST_URI);
            foldersUri = Uri.parse(cursor.getString(uriCol));
            mSelectedAccount = cursor.getString(UIProvider.ACCOUNT_NAME_COLUMN);
            cursor.close();
        }
        Uri conversationListUri = null;
        if (foldersUri != null) {
            cursor = mResolver.query(foldersUri, UIProvider.FOLDERS_PROJECTION, null, null, null);
            if (cursor != null) {
                int uriCol = cursor.getColumnIndex(UIProvider.FolderColumns.CONVERSATION_LIST_URI);
                cursor.moveToFirst();
                conversationListUri = Uri.parse(cursor.getString(uriCol));
                cursor.close();
            }
        }
        if (conversationListUri != null) {
            cursor = mResolver.query(conversationListUri, UIProvider.CONVERSATION_PROJECTION, null,
                    null, null);
        }
        mListAdapter = new ConversationItemAdapter(this, R.layout.conversation_item_view_normal,
                cursor);
        mListView.setAdapter(mListAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) mListAdapter.getItem(position);
        ConversationViewActivity.viewConversation(this,
                cursor.getString(UIProvider.CONVERSATION_URI_COLUMN), mSelectedAccount);
    }
}
