package com.android.guide.expandablelistview;

import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @author Sanath Nandasiri this is expandable list adapter
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

	LayoutInflater inflater;

	/* list of group */
	private ArrayList<MusicAlbum> groups;

	public ExpandableListAdapter(Context context, ArrayList<MusicAlbum> groups) {
		super();
		this.groups = groups;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * @param child
	 * @param group
	 *            use for adding item to list view
	 */
	public void addItem(MusicFile child, MusicAlbum group) {
		if (!groups.contains(group)) {
			groups.add(group);
		}
		int index = groups.indexOf(group);
		ArrayList<MusicFile> ch = groups.get(index).getChildrens();
		ch.add(child);
		groups.get(index).setChildrens(ch);
	}

	public MusicFile getChild(int groupPosition, int childPosition) {
		ArrayList<MusicFile> ch = groups.get(groupPosition).getChildrens();
		return ch.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		ArrayList<MusicFile> ch = groups.get(groupPosition).getChildrens();
		return ch.size();
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		MusicFile child = (MusicFile) getChild(groupPosition, childPosition);
		final MusicAlbum group = getGroup(groupPosition);
		TextView childName = null;
		CheckBox checkBox = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.child_view, null);
		}
		childName = (TextView) convertView.findViewById(R.id.textViewChildName);

		checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
		if (child.isCheck())
			checkBox.setChecked(true);
		else
			checkBox.setChecked(false);
		checkBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("haipn", "on child checkbox click");
//				for (MusicFile item : group.childrens) {
//					if (item.isCheck()) {
//						group.setCheck(true);
////						notifyDataSetChanged();
//						return;
//					}
//				}
				group.setCheck(false);
				notifyDataSetChanged();
			}
		});
		childName.setText(child.getTitle());

		return convertView;
	}

	public MusicAlbum getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	public int getGroupCount() {
		return groups.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView groupName = null;
		final MusicAlbum group = (MusicAlbum) getGroup(groupPosition);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.group_view, null);
		}
		groupName = (TextView) convertView.findViewById(R.id.textViewGroupName);
		final CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
		checkbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("haipn", "on checkbox group click");
				boolean isChecked = checkbox.isChecked();
				for (MusicFile child : group.childrens) {
					child.setCheck(isChecked);
				}
//				checkbox.setChecked(isChecked);
				group.setCheck(isChecked);
				notifyDataSetChanged();
			}
		});
//		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				Log.d("haipn", "on checked");
//				for (MusicFile child : group.childrens) {
//					child.setCheck(isChecked);
//				}
//				notifyDataSetChanged();
//			}
//		});

		if (group.isCheck()) {
			checkbox.setChecked(true);
		} else {
			checkbox.setChecked(false);
		}
		groupName.setText(group.getAlbumName());
		return convertView;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}
}
