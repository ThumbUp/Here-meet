package com.example.thumbup;

import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MeetingActivity extends AppCompatActivity {
    ListView meetingListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting);

        meetingListView = (ListView) findViewById(R.id.meeting_list);
        ArrayList<MeetingListViewItem> meetingListViewItem = new ArrayList<>();

        String[] meetingListViewItem_date = {"4/1", "4/8", "4/15", "4/22", "4/29"};
        String meetingListViewItem_name = "정기모임";
        String meetingListViewItem_time = "미정";
        String meetingListViewItem_place = "성신여대";

        for (int i = 0; i < 5; i++) {
            MeetingListViewItem item = new MeetingListViewItem();
            item.MeetingListViewItem_date = meetingListViewItem_date[i];
            item.MeetingListViewItem_name = meetingListViewItem_name;
            item.MeetingListViewItem_time = meetingListViewItem_time;
            item.MeetingListViewItem_place = meetingListViewItem_place;
            meetingListViewItem.add(item);
        }

        MeetingAdapter meetingAdapter = new MeetingAdapter(meetingListViewItem);
        meetingListView.setAdapter(meetingAdapter);
    }
}
