package com.futuo.iapprove.tab7tabcontent;

import android.os.Bundle;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveTabContentActivity;

public class SettingsTabContentActivity extends IApproveTabContentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.settings_tab_content_activity_layout);

		// set title
		setTitle(R.string.settings_tab7nav_title);

		//
	}

}