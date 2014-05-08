package com.android.guide.expandablelistview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListView;

public class MainActivity extends Activity {
	/*our expandable adapter */
	ExpandableListAdapter expandableListAdapter;
	/*expandable list*/
	ExpandableListView expandableListView;
	/*list items*/
	ArrayList<Group> groups=new ArrayList<Group>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	/*genarate data for list view*/
    	genarateData();
    	/*instantiate adapter with our item list*/
    	expandableListAdapter=new ExpandableListAdapter(this, groups);
    	/*we get list view*/
        expandableListView=(ExpandableListView) findViewById(R.id.expandableListView);
        /*set adapter to list view*/
        expandableListView.setAdapter(expandableListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    //Generate dummy data for list view
    public void genarateData() {
    	Group group;
    	for (int i = 0; i < 10; i++) {
    		
    		ArrayList<Child> childrens=new ArrayList<Child>();
    		childrens.clear();
    		Child child;
			for (int j = 0; j < 5; j++) {
				child=new Child(""+j, "I am Child "+j);
				childrens.add(child);
			}
			group=new Group(""+i, "I am Group "+i, childrens);
			groups.add(group);
		}
    }
}
