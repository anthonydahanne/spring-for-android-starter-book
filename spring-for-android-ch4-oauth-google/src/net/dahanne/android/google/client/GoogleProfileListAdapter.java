/*
 * Copyright 2012 the original author or authors.
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
package net.dahanne.android.google.client;


import org.springframework.social.google.api.legacyprofile.LegacyGoogleProfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This class is based on the FacebookProfileListAdapter by Roy Clarkson
 */
public class GoogleProfileListAdapter extends BaseAdapter {
    private LegacyGoogleProfile googleProfile;
    private final LayoutInflater layoutInflater;

    public GoogleProfileListAdapter(Context context, LegacyGoogleProfile googleProfile) {
        if (googleProfile == null) {
            throw new IllegalArgumentException("googleProfile cannot be null");
        }

        this.googleProfile = googleProfile;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return 3;
    }

    public String[] getItem(int position) {
        String[] item = new String[2];

        switch (position) {
        case 0:
            item[0] = "Id";
            item[1] = String.valueOf(googleProfile.getId());
            break;
        case 1:
            item[0] = "Name";
            item[1] = googleProfile.getName();
            break;
        case 2:
            item[0] = "Email";
            item[1] = googleProfile.getEmail();
            break;
        default:
            item[0] = "";
            item[1] = "";
            break;
        }

        return item;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String[] item = getItem(position);
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(android.R.layout.two_line_list_item, parent, false);
        }

        TextView t = (TextView) view.findViewById(android.R.id.text1);
        t.setText(item[0]);

        t = (TextView) view.findViewById(android.R.id.text2);
        t.setText(item[1]);

        return view;
    }

}
