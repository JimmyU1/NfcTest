package com.crsc.nfctest.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crsc.nfctest.R;
import com.crsc.nfctest.model.Record;

import java.util.List;

/**
 * Created by liuji on 2017/6/21.
 */

public class RecordAdapter extends ArrayAdapter<Record> {

    private int resourceId;

    public RecordAdapter(Context context, int resourceId, List<Record> objects){
        super(context, resourceId, objects);
        this.resourceId = resourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Record record = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        ImageView tagImage = (ImageView) view.findViewById(R.id.tag_image);
        TextView tagId = (TextView) view.findViewById(R.id.tag_id);
        tagImage.setImageResource(R.drawable.nfc_image);
        tagId.setText(record.getTagId());
        return view;
    }
}
