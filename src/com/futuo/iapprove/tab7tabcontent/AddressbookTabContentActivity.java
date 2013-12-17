package com.futuo.iapprove.tab7tabcontent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.addressbook.AddressbookContactBean;
import com.futuo.iapprove.customwidget.IApproveTabContentActivity;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.Employee;
import com.futuo.iapprove.service.CoreService;
import com.richitec.commontoolkit.customadapter.CTListCursorAdapter;
import com.richitec.commontoolkit.user.UserManager;

public class AddressbookTabContentActivity extends IApproveTabContentActivity {

	// address book contact list cursor adapter
	private ABContactListCursorAdapter _mABContactListCursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.addressbook_tab_content_activity_layout);

		// set subViews
		// set title
		setTitle(R.string.addressbook_tab7nav_title);

		//

		// get address book contact listView
		ListView _addressbookContactListView = (ListView) findViewById(R.id.ab_contact_listView);

		// set address book contact list cursor adapter
		_addressbookContactListView
				.setAdapter(_mABContactListCursorAdapter = new ABContactListCursorAdapter(
						this,
						R.layout.addressbook_contact_layout,
						getContentResolver()
								.query(ContentUris
										.withAppendedId(
												Employee.ENTERPRISE_CONTENT_URI,
												IAUserExtension
														.getUserLoginEnterpriseId(UserManager
																.getInstance()
																.getUser())),
										null, null, null, null), new String[] {
								ABContactListCursorAdapter.ABCONTACT_AVATAR,
								ABContactListCursorAdapter.ABCONTACT_NAME },
						new int[] { R.id.abc_avatar_imageView,
								R.id.abc_name_textView }));

		// set address book contact list on item click listener
		_addressbookContactListView
				.setOnItemClickListener(new ABContactListOnItemClickListener());
	}

	@Override
	protected void onResume() {
		super.onResume();

		// change enterprise address book query cursor
		_mABContactListCursorAdapter.changeCursor(getContentResolver().query(
				ContentUris.withAppendedId(Employee.ENTERPRISE_CONTENT_URI,
						IAUserExtension.getUserLoginEnterpriseId(UserManager
								.getInstance().getUser())), null, null, null,
				null));
	}

	@Override
	protected boolean bindCoreServiceWhenOnResume() {
		// binder core service when on resume
		return true;
	}

	@Override
	protected void onCoreServiceConnected(CoreService coreService) {
		super.onCoreServiceConnected(coreService);

		// start get user login enterprise address book
		coreService.startGetEnterpriseAddressbook(IAUserExtension
				.getUserLoginEnterpriseId(UserManager.getInstance().getUser()));
	}

	@Override
	protected void onCoreServiceDisconnected(CoreService coreService) {
		super.onCoreServiceDisconnected(coreService);

		// stop get user login enterprise address book
		coreService.stopGetEnterpriseAddressbook();
	}

	// inner class
	// address book contact list cursor adapter
	class ABContactListCursorAdapter extends CTListCursorAdapter {

		private final String LOG_TAG = ABContactListCursorAdapter.class
				.getCanonicalName();

		// address book contact list cursor adapter data keys
		private static final String ABCONTACT_AVATAR = "addressbook_contact_avatar";
		private static final String ABCONTACT_NAME = "addressbook_contact_name";

		public ABContactListCursorAdapter(Context context,
				int itemsLayoutResId, Cursor c, String[] dataKeys,
				int[] itemsComponentResIds) {
			super(context, itemsLayoutResId, c, dataKeys, itemsComponentResIds,
					true);
		}

		@Override
		protected void onContentChanged() {
			// auto requery
			super.onContentChanged();

			Log.d(LOG_TAG,
					"Address book contact list cursor adapter on content changed, auto requery");
		}

		@Override
		protected void appendCursorData(List<Object> data, Cursor cursor) {
			// check the cursor
			if (null != cursor) {
				// get address book contact bean and append to data list
				data.add(new AddressbookContactBean(cursor));
			} else {
				Log.e(LOG_TAG,
						"Query user login enterprise address book all contacts error, cursor = "
								+ cursor);
			}
		}

		@Override
		protected Map<String, ?> recombinationData(String dataKey,
				Object dataObject) {
			// define return data map and the data value for key in data object
			Map<String, Object> _dataMap = new HashMap<String, Object>();
			Object _dataValue = null;

			// check data object and convert to address book contact object
			try {
				// convert data object to address book contact
				AddressbookContactBean _abcontactObject = (AddressbookContactBean) dataObject;

				// check data key and get data value for it
				if (ABCONTACT_AVATAR.equalsIgnoreCase(dataKey)) {
					// avatar
					// get avatar data stream
					InputStream _avatarDataStream = new ByteArrayInputStream(
							_abcontactObject.getAvatar());

					// check avatar data stream
					if (null != _avatarDataStream) {
						_dataValue = BitmapFactory
								.decodeStream(_avatarDataStream);

						// close photo data stream
						_avatarDataStream.close();
					}
				} else if (ABCONTACT_NAME.equalsIgnoreCase(dataKey)) {
					// name
					_dataValue = _abcontactObject.getEmployeeName();
				} else {
					Log.e(LOG_TAG, "Recombination data error, data key = "
							+ dataKey + " and data object = " + dataObject);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Convert data object to address book contact bean object error, data = "
								+ dataObject);

				e.printStackTrace();
			}

			// put data value to map and return
			_dataMap.put(dataKey, _dataValue);
			return _dataMap;
		}

		@Override
		protected void bindView(View view, Map<String, ?> dataMap,
				String dataKey) {
			// get item data object
			Object _itemData = dataMap.get(dataKey);

			// check view type
			// textView
			if (view instanceof TextView) {
				// generate view text
				SpannableString _viewNewText = new SpannableString(
						null == _itemData ? "" : _itemData.toString());

				// check data class name
				if (_itemData instanceof SpannableString) {
					_viewNewText.setSpan(new ForegroundColorSpan(Color.RED), 0,
							_viewNewText.length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				// set view text
				((TextView) view).setText(_viewNewText);
			} else if (view instanceof ImageView) {
				try {
					// define item data bitmap and convert item data to bitmap
					Bitmap _itemDataBitmap = (Bitmap) _itemData;

					// check and set imageView image
					if (null != _itemDataBitmap) {
						((ImageView) view).setImageBitmap(_itemDataBitmap);
					}
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item data to bitmap error, item data = "
									+ _itemData);
				}
			} else {
				Log.e(LOG_TAG, "Bind view error, view = " + view
						+ " not recognized, data key = " + dataKey
						+ " and data map = " + dataMap);
			}
		}

	}

	// address book contact list on item click listener
	class ABContactListOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> abcontactListView,
				View abcItemContentView, int position, long id) {
			Log.d("@@", "Click contact info = "
					+ _mABContactListCursorAdapter.getDataList().get(position));

			//
		}

	}

}
