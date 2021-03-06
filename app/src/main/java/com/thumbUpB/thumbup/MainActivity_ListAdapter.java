package com.thumbUpB.thumbup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.thumbUpB.thumbup.DataBase.DBCallBack;
import com.thumbUpB.thumbup.DataBase.DBManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivity_ListAdapter extends BaseAdapter {

    private View view;
    DBManager dbManager = DBManager.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    String meetingId;

    // 생성자로부터 전달된 resource id 값을 저장.
    int resourceId;

    // 생성자. 마지막에 ListBtnClickListener 추가
    MainActivity_ListAdapter(Context context, int resource, ArrayList<MainActivity_ItemData> list) {
        super();

        // resource id 값 복사
        this.resourceId = resource;
    }

    private ArrayList<MainActivity_ItemData> listViewItemList = new ArrayList<MainActivity_ItemData>();

    public MainActivity_ListAdapter() {

    }

    public MainActivity_ListAdapter(View view) {
        this.view = view;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();//헉 됐어요!! 그 삭제는 원래 댓는데..
        MainActivity_ListAdapter adapter = this;

        // listview 생성 및 adapter 지정.
        final ListView listview = (ListView) view.findViewById(R.id.main_listView);

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_main_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.main_meetingIcon);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.main_meetingTitle);
        TextView infoTextView = (TextView) convertView.findViewById(R.id.main_meetingInfo);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        MainActivity_ItemData listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getData_meetingIcon());
        titleTextView.setText(listViewItem.getData_meetingTitle());
        infoTextView.setText(listViewItem.getData_meetingInfo());

        ImageButton btnPopup = (ImageButton) convertView.findViewById(R.id.btnPopup);
        btnPopup.findViewById(R.id.btnPopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_delete) {
                            //모임 나가기
                            AlertDialog.Builder dlg1 = new AlertDialog.Builder(context);
                            dlg1.setMessage("모임에서 나가시겠습니까?");
                            dlg1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    meetingId = listViewItem.getData_meetingId();
                                    // 아이템 삭제
                                    for (int i = 0; i < listViewItemList.size(); i++) {
                                        if (listViewItemList.get(i).getData_meetingId().equals(meetingId) == true) {
                                            listViewItemList.remove(i);
                                            break;
                                        }
                                    }
                                    dbManager.Lock(context);
                                    dbManager.WithdrawMeeting(meetingId, new DBCallBack() {
                                        @Override
                                        public void success(Object data) {
                                            // listview 갱신.
                                            dbManager.UnLock();
                                            adapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void fail(String errorMessage) {

                                        }
                                    });
                                }
                            });
                            dlg1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                            dlg1.create().show();
                        } else if (menuItem.getItemId() == R.id.action_modify) {
                            // 모임 수정
                            meetingId = listViewItem.getData_meetingId();
                            Intent intent = new Intent(view.getContext(), ModifyMeetingActivity.class);
                            intent.putExtra("meetingId", meetingId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            view.getContext().startActivity(intent);
                        } else {
                            //모임 코드 복사
                            final LinearLayout linear = (LinearLayout) View.inflate(getApplicationContext(),
                                    R.layout.dialog_main_copy_key, null);

                            meetingId = listViewItem.getData_meetingId();

                            AlertDialog.Builder dlg3 = new AlertDialog.Builder(context);
                            dlg3.setView(linear);
                            TextView k_meetingKey;
                            k_meetingKey = (TextView)linear.findViewById(R.id.k_meetingKey);
                            k_meetingKey.setText(meetingId);
                            dlg3.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }).show();
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        return convertView;
    }

    public void addItem(Drawable icon, String title, String info, String mid) {
        MainActivity_ItemData item = new MainActivity_ItemData();

        item.setData_meetingIcon(icon);
        item.setData_meetingTitle(title);
        item.setData_meetingInfo(info);
        item.setData_meetingId(mid);

        listViewItemList.add(item);
    }

}