/*
 *  Copyright (c) 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.ide.javaide.editor.view.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.duy.ide.R;
import com.duy.ide.code.api.SuggestItem;
import com.duy.ide.javaide.editor.autocomplete.model.ClassDescription;
import com.duy.ide.javaide.editor.autocomplete.util.JavaUtil;
import com.duy.ide.javaide.editor.autocomplete.util.SpanUtil;
import com.jecelyin.editor.v2.Preferences;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Duy on 26-Apr-17.
 */
public class CodeSuggestAdapter extends ArrayAdapter<SuggestItem> {
    @NonNull
    private Context context;
    @NonNull
    private LayoutInflater inflater;
    @NonNull
    private ArrayList<SuggestItem> clone;
    @NonNull
    private ArrayList<SuggestItem> suggestion;
    private int resourceID;
    @Nullable
    private OnSuggestItemClickListener listener;
    private float editorTextSize;
    private Filter codeFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object value) {
            if (value == null) {
                return "";
            }
            return ((SuggestItem) value).getInsertText();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            suggestion.clear();
            if (constraint != null) {
                for (SuggestItem item : clone) {
//                    if (item.compareTo(constraint.toString()) == 0) {
                    suggestion.add(item);
//                    }
                }
                filterResults.values = suggestion;
                filterResults.count = suggestion.size();
            }
            return filterResults;
        }

        @Override
        @SuppressWarnings("unchecked")

        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<SuggestItem> filteredList = (ArrayList<SuggestItem>) results.values;
            clear();
            if (filteredList != null && filteredList.size() > 0) {
                addAll(filteredList);
            }
            notifyDataSetChanged();
        }
    };

    @SuppressWarnings("unchecked")
    public CodeSuggestAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<SuggestItem> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.clone = (ArrayList<SuggestItem>) objects.clone();
        this.suggestion = new ArrayList<>();
        this.resourceID = resource;


        editorTextSize = Preferences.getInstance(context).getFontSize();
    }

    public ArrayList<SuggestItem> getAllItems() {
        return clone;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resourceID, null);
        }

        final SuggestItem item = getItem(position);
        TextView txtName = convertView.findViewById(R.id.txt_name);
        txtName.setTypeface(Typeface.MONOSPACE);
        txtName.setTextSize(editorTextSize);
        TextView txtType = convertView.findViewById(R.id.txt_type);
        txtType.setTypeface(Typeface.MONOSPACE);
        txtType.setTextSize(editorTextSize);
        TextView txtHeader = convertView.findViewById(R.id.txt_header);
        if (item != null) {
            if (item instanceof ClassDescription) {
                txtName.setText(SpanUtil.formatClass(context, (ClassDescription) item));
            } else {
                txtName.setText(item.toString());
                txtType.setText(item.getType() != null ? JavaUtil.getSimpleName(item.getType()) : "");
            }
//            txtHeader.setText(item.getTypeHeader());
            /**
             * if (item instanceof ClassDescription || item instanceof ConstructorDescription) {
             txtHeader.setText("c");
             } else if (item instanceof FieldDescription) {
             txtHeader.setText("f");
             } else if (item instanceof MethodDescription) {
             txtHeader.setText("m");
             } else if (item instanceof PackageDescription) {
             txtHeader.setText("p");
             }
             */
        }
        return convertView;
    }

    public void clearAllData() {
        super.clear();
        clone.clear();
    }

    public void addData(@NonNull Collection<? extends SuggestItem> collection) {
        addAll(collection);
        clone.addAll(collection);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return codeFilter;
    }

    public void setListener(OnSuggestItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnSuggestItemClickListener {
        void onClickSuggest(SuggestItem description);
    }
}
