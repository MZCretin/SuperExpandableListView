package com.cretin.www.superexpandablelistview;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    //最外面一层 分组名
    private List<GroupModel> groupArray;
    //最外面一层 分组下面的详情
    private List<List<ChildModel>> childArray;
    //自定义的适配器
    private ExpandableAdapter expandableAdapter;

    //模拟加载数据的对话框
    private ProgressDialog progressDialog;

    //是否使用默认的指示器 默认true 使用者可以在这里通过改变这个值观察默认指示器和自定义指示器的区别
    private boolean use_default_indicator = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //取消对话框
            progressDialog.dismiss();
            if ( msg.what == 1 ) {
                //往对应分组的详情里面添加一条数据
                List<ChildModel> childModels = childArray.get(msg.arg1);
                childModels.add(new ChildModel("测试" + System.currentTimeMillis(), "[WIFI在线]"));
//            expandableAdapter.notifyDataSetChanged();
                //打开分组
                expandableListView.expandGroup(msg.arg1, true);
            } else {
                //第一次加载数据的时候 添加分组信息
                groupArray.add(new GroupModel("特别关心", "2/2"));
                groupArray.add(new GroupModel("就这样吧", "36/70"));
                groupArray.add(new GroupModel("曾经    启明星", "6/7"));
                groupArray.add(new GroupModel("青春    独家", "58/82"));

                //这里根据分组来创建对应的详情信息 创建好集合就行 具体数据等点击的时候再添加
                //特别关心
                List<ChildModel> tempArray0 = new ArrayList<>();
                childArray.add(tempArray0);
                //就这样吧
                List<ChildModel> tempArray1 = new ArrayList<>();
                childArray.add(tempArray1);
                //曾经    启明星
                List<ChildModel> tempArray2 = new ArrayList<>();
                childArray.add(tempArray2);
                //青春    独家
                List<ChildModel> tempArray3 = new ArrayList<>();
                childArray.add(tempArray3);

                expandableAdapter.notifyDataSetChanged();
            }
        }
    };

    //展示对话框
    private void showDialog(String msg) {
        if ( progressDialog == null ) {
            //创建ProgressDialog对象
            progressDialog = new ProgressDialog(
                    this);
            //设置进度条风格，风格为圆形，旋转的
            progressDialog.setProgressStyle(
                    ProgressDialog.STYLE_SPINNER);
            //设置ProgressDialog 标题图标
            progressDialog.setIcon(android.R.drawable.btn_star);
            //设置ProgressDialog 的进度条是否不明确
            progressDialog.setIndeterminate(false);
            //设置ProgressDialog 是否可以按退回按键取消
            progressDialog.setCancelable(false);
        }
        //设置ProgressDialog 提示信息
        progressDialog.setMessage("正在" + msg + "...");
        // 让ProgressDialog显示
        progressDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        expandableListView = ( ExpandableListView ) findViewById(R.id.expandable_listview);

        if ( use_default_indicator ) {
            //不做处理就是默认
        } else {
            expandableListView.setGroupIndicator(null);
        }

        //这里是通过改变默认的setGroupIndicator方式实现自定义指示器 但是效果不好 图标会被拉伸的很难看 不信你可以自己试试
//        expandableListView.setGroupIndicator(this.getResources().getDrawable(R.drawable.shape_expendable_listview));

        groupArray = new ArrayList<>();
        childArray = new ArrayList<>();

        //创建适配器
        expandableAdapter = new ExpandableAdapter(this, groupArray, R.layout.item_group, childArray, R.layout.item_child);
        expandableListView.setAdapter(expandableAdapter);

        //第一次加载数据
        showDialog("加载Group的数据");
        //模拟加载数据 1000后通知handler添加group的数据
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = 2;
                handler.sendMessage(message);
            }
        }, 10000);

        //分组的点击事件
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
                //如果分组被打开 直接关闭
                if ( expandableListView.isGroupExpanded(groupPosition) ) {
                    expandableListView.collapseGroup(groupPosition);
                }
                //否则模拟请求数据 1000 后自动添加一条数据
                else {
                    //显示对话框
                    showDialog("加载Child数据");
                    //模拟加载数据 1000后通知handler新增一条数据
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message message = handler.obtainMessage();
                            message.what = 1;
                            message.arg1 = groupPosition;
                            handler.sendMessage(message);
                        }
                    }, 1000);
                }
                //返回false表示系统自己处理展开和关闭事件 返回true表示调用者自己处理展开和关闭事件
                return true;
            }
        });

        //详情的点击事件
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                //在Kotlin正式加入使用之前 在使用任何数据之前 判断下是比较好的习惯
                List<ChildModel> childModels = childArray.get(groupPosition);
                if ( childModels != null ) {
                    ChildModel childModel = childModels.get(childPosition);
                    if ( childModel != null ) {
                        String name = childModel.getName();
                        if ( !TextUtils.isEmpty(name) ) {
                            Toast.makeText(MainActivity.this, name + "说：你点个屁哦", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return false;
            }
        });
    }

    class ExpandableAdapter extends BaseExpandableListAdapter {
        //视图加载器
        private LayoutInflater mInflater;
        private Context mContext;
        private int mExpandedGroupLayout;
        private int mChildLayout;
        private List<GroupModel> mGroupArray;
        private List<List<ChildModel>> mChildArray;

        /**
         * 构造函数
         *
         * @param context
         * @param groupData
         * @param expandedGroupLayout 分组视图布局
         * @param childData
         * @param childLayout         详情视图布局
         */
        public ExpandableAdapter(Context context, List<GroupModel> groupData, int expandedGroupLayout,
                                 List<List<ChildModel>> childData, int childLayout) {
            mContext = context;
            mExpandedGroupLayout = expandedGroupLayout;
            mChildLayout = childLayout;
            mGroupArray = groupData;
            mChildArray = childData;
            mInflater = ( LayoutInflater ) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public Object getChild(int groupPosition, int childPosition) {
            return childArray.get(groupPosition).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            // 取得显示给定分组给定子位置的数据用的视图。
            View v;
            if ( convertView == null ) {
                v = newChildView(parent);
            } else {
                v = convertView;
            }
            bindChildView(v, mChildArray.get(groupPosition).get(childPosition));
            return v;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // 取得指定分组的子元素数。
            return mChildArray.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            // 取得与给定分组关联的数据。
            return mGroupArray.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            // 取得分组数
            return mGroupArray.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            // 取得指定分组的ID。该组ID必须在组中是唯一的。组合的ID （参见getCombinedGroupId(long)）
            // 必须不同于其他所有ID（分组及子项目的ID）。
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            // 取得用于显示给定分组的视图。 这个方法仅返回分组的视图对象， 要想获取子元素的视图对象，
            // 就需要调用 getChildView(int, int, boolean, View, ViewGroup)。
            View v;
            if ( convertView == null ) {
                v = newGroupView(parent);
            } else {
                v = convertView;
            }
            bindGroupView(v, mGroupArray.get(groupPosition), isExpanded);
            return v;
        }

        /**
         * 绑定组数据
         *
         * @param view
         * @param data
         * @param isExpanded
         */
        private void bindGroupView(View view, GroupModel data, boolean isExpanded) {
            // 绑定组视图的数据 当然这些都是模拟的
            TextView tv_title = ( TextView ) view.findViewById(R.id.tv_title);
            TextView tv_online = ( TextView ) view.findViewById(R.id.tv_online);
            tv_title.setText(data.getTitle());
            tv_online.setText(data.getOnline());
            if ( !use_default_indicator ) {
                ImageView iv_tip = ( ImageView ) view.findViewById(R.id.iv_tip);
                if ( isExpanded ) {
                    iv_tip.setImageResource(R.mipmap.down);
                } else {
                    iv_tip.setImageResource(R.mipmap.right);
                }
            }
        }

        /**
         * 绑定子数据
         *
         * @param view
         * @param data
         */
        private void bindChildView(View view, ChildModel data) {
            // 绑定组视图的数据 当然这些都是模拟的
            TextView tv_name = ( TextView ) view.findViewById(R.id.tv_name);
            TextView tv_sig = ( TextView ) view.findViewById(R.id.tv_sig);
            tv_name.setText(data.getName());
            tv_sig.setText(data.getSig());
        }

        /**
         * 创建新的组视图
         *
         * @param parent
         * @return
         */
        public View newGroupView(ViewGroup parent) {
            return mInflater.inflate(mExpandedGroupLayout, parent, false);
        }

        /**
         * 创建新的子视图
         *
         * @param parent
         * @return
         */
        public View newChildView(ViewGroup parent) {
            return mInflater.inflate(mChildLayout, parent, false);
        }

        public boolean hasStableIds() {
            // 是否指定分组视图及其子视图的ID对应的后台数据改变也会保持该ID。
            return true;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // 指定位置的子视图是否可选择。
            return true;
        }
    }
}
