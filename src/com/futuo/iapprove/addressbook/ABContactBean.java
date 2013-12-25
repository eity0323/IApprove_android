package com.futuo.iapprove.addressbook;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.Employee;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.utils.DateStringUtils;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class ABContactBean implements Comparable<ABContactBean>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2767872627416788634L;

	private static final String LOG_TAG = ABContactBean.class
			.getCanonicalName();

	// row id, user id, enterprise id, avater, avatar url, employee name, sex,
	// nickname, birthday, department, approve number, phones, email, note and
	// frequency
	private Long rowId;
	private Long userId;
	private Long enterpriseId;
	private byte[] avatar;
	private String avatarUrl;
	private String employeeName;
	private ABContactSex sex;
	private String nickname;
	private Long birthday;
	private String department;
	private Long approveNumber;
	private List<ABContactPhoneBean> phones;
	private String email;
	private String note;
	private Long frequency;

	// local storage data dirty type
	private LocalStorageDataDirtyType lsDataDirtyType;

	// constructor
	public ABContactBean() {
		super();

		// set default birthday and frequency
		birthday = 0L;
		frequency = 0L;

		// initialize phones list
		phones = new ArrayList<ABContactPhoneBean>();

		// set default local storage data dirty type
		lsDataDirtyType = LocalStorageDataDirtyType.NORMAL;
	}

	// constructor with JSON object
	public ABContactBean(JSONObject contactJSONObject) {
		this();

		// check address book contact JSON object
		if (null != contactJSONObject) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// set address book contact attributes
			// user id
			try {
				userId = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										contactJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseABReqResp_employee_id)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get employee user id error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// avatar url
			avatarUrl = JSONUtils
					.getStringFromJSONObject(
							contactJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_avatarUrl));

			// employee name
			employeeName = JSONUtils
					.getStringFromJSONObject(
							contactJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_name));

			// sex
			try {
				sex = ABContactSex
						.getSex(Integer.parseInt(JSONUtils
								.getStringFromJSONObject(
										contactJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseABReqResp_employee_sex))));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG, "Get employee sex error, exception message = "
						+ e.getMessage());

				e.printStackTrace();
			}

			// nickname
			nickname = JSONUtils
					.getStringFromJSONObject(
							contactJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_nickname));

			// birthday
			// get and check birthday value
			Date _birthdayValue = DateStringUtils
					.shortDateString2Date(JSONUtils
							.getStringFromJSONObject(
									contactJSONObject,
									_appContext
											.getResources()
											.getString(
													R.string.rbgServer_getEnterpriseABReqResp_employee_birthday)));
			if (null != _birthdayValue) {
				birthday = _birthdayValue.getTime();
			}

			// department
			department = JSONUtils
					.getStringFromJSONObject(
							contactJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_department));

			// approve number
			try {
				approveNumber = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										contactJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseABReqResp_employee_approveNumber)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get employee approve number error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// phones
			// get and check phones value
			List<ABContactPhoneBean> _phonesValue = ABContactPhoneBean
					.getContactPhones(contactJSONObject);
			if (null != _phonesValue) {
				phones.addAll(_phonesValue);
			}

			// email
			email = JSONUtils
					.getStringFromJSONObject(
							contactJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_email));

			// note
			note = JSONUtils
					.getStringFromJSONObject(
							contactJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_note));
		} else {
			Log.e(LOG_TAG,
					"New address book contact with JSON object error, contact JSON object = "
							+ contactJSONObject);
		}
	}

	// constructor with cursor
	public ABContactBean(Cursor cursor) {
		this();

		// check the cursor
		if (null != cursor) {
			// set address book contact attributes
			// row id
			rowId = cursor.getLong(cursor.getColumnIndex(Employee._ID));

			// user id
			userId = cursor.getLong(cursor.getColumnIndex(Employee.USER_ID));

			// enterprise id
			enterpriseId = cursor.getLong(cursor
					.getColumnIndex(Employee.ENTERPRISE_ID));

			// avatar
			avatar = cursor.getBlob(cursor.getColumnIndex(Employee.AVATAR));

			// employee name
			employeeName = cursor.getString(cursor
					.getColumnIndex(Employee.NAME));

			// sex
			// get and check sex column index
			int _sexColumnIndex = cursor.getColumnIndex(Employee.SEX);
			if (!cursor.isNull(_sexColumnIndex)) {
				sex = ABContactSex.getSex(cursor.getInt(_sexColumnIndex));
			}

			// nickname
			nickname = cursor.getString(cursor
					.getColumnIndex(Employee.NICKNAME));

			// birthday
			// get and check birthday column index
			int _birthdayColumnIndex = cursor.getColumnIndex(Employee.BIRTHDAY);
			if (!cursor.isNull(_birthdayColumnIndex)) {
				birthday = cursor.getLong(_birthdayColumnIndex);
			}

			// department
			department = cursor.getString(cursor
					.getColumnIndex(Employee.DEPARTMENT));

			// approve number
			approveNumber = cursor.getLong(cursor
					.getColumnIndex(Employee.APPROVE_NUMBER));

			// phones
			// get and check phones value
			List<ABContactPhoneBean> _phonesValue = ABContactPhoneBean
					.getContactPhones(cursor);
			if (null != _phonesValue) {
				phones.addAll(_phonesValue);
			}

			// email
			email = cursor.getString(cursor.getColumnIndex(Employee.EMAIL));

			// note
			note = cursor.getString(cursor.getColumnIndex(Employee.NOTE));

			// frequency
			frequency = cursor.getLong(cursor
					.getColumnIndex(Employee.FREQUENCY));
		} else {
			Log.e(LOG_TAG,
					"New address book contact with cursor error, cursor = "
							+ cursor);
		}
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public ABContactSex getSex() {
		return sex;
	}

	public void setSex(ABContactSex sex) {
		this.sex = sex;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Long getApproveNumber() {
		return approveNumber;
	}

	public void setApproveNumber(Long approveNumber) {
		this.approveNumber = approveNumber;
	}

	public List<ABContactPhoneBean> getPhones() {
		return phones;
	}

	// get phone with type
	public ABContactPhoneBean getPhone(ABContactPhoneType phoneType) {
		ABContactPhoneBean _phone = null;

		for (ABContactPhoneBean phone : phones) {
			// check phone type
			if (phoneType == phone.getType()) {
				// get phone
				_phone = phone;

				// break immediately
				break;
			}
		}

		return _phone;
	}

	// get mobile phone number
	public Long getMobilePhoneNumber() {
		Long _mobilePhoneNumber = null;

		// get and check mobile phone
		ABContactPhoneBean _mobilePhone = getPhone(ABContactPhoneType.MOBILE);
		if (null != _mobilePhone) {
			// get mobile phone number
			_mobilePhoneNumber = _mobilePhone.getNumber();
		}

		return _mobilePhoneNumber;
	}

	// get office phone number
	public Long getOfficePhoneNumber() {
		Long _officePhoneNumber = null;

		// get and check office phone
		ABContactPhoneBean _officePhone = getPhone(ABContactPhoneType.OFFICE);
		if (null != _officePhone) {
			// get office phone number
			_officePhoneNumber = _officePhone.getNumber();
		}

		return _officePhoneNumber;
	}

	public void setPhones(List<ABContactPhoneBean> phones) {
		this.phones = phones;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getFrequency() {
		return frequency;
	}

	public void setFrequency(Long frequency) {
		this.frequency = frequency;
	}

	public LocalStorageDataDirtyType getLocalStorageDataDirtyType() {
		return lsDataDirtyType;
	}

	public void setLocalStorageDataDirtyType(
			LocalStorageDataDirtyType lsDataDirtyType) {
		this.lsDataDirtyType = lsDataDirtyType;
	}

	@Override
	public int compareTo(ABContactBean another) {
		int _result = -1;

		// check avatar url, employee name, sex, nickname, birthday, department,
		// mobile phone, office phone, email and note
		if ((null == employeeName && null == another.employeeName)
				|| (null != employeeName && null != another.employeeName && employeeName
						.equalsIgnoreCase(another.employeeName))) {
			if ((null == sex && null == another.sex)
					|| (null != sex && null != another.sex && sex == another.sex)) {
				if ((null == getMobilePhoneNumber() && null == another
						.getMobilePhoneNumber())
						|| (null != getMobilePhoneNumber()
								&& null != another.getMobilePhoneNumber() && getMobilePhoneNumber()
								.longValue() == another.getMobilePhoneNumber()
								.longValue())) {
					if ((null == getOfficePhoneNumber() && null == another
							.getOfficePhoneNumber())
							|| (null != getOfficePhoneNumber()
									&& null != another.getOfficePhoneNumber() && getOfficePhoneNumber()
									.longValue() == another
									.getOfficePhoneNumber().longValue())) {
						if ((null == email && null == another.email)
								|| (null != email && null != another.email && email
										.equalsIgnoreCase(another.email))) {
							_result = 0;
						} else {
							Log.d(LOG_TAG,
									"Address book contact email not equals, self email = "
											+ email + " and another email = "
											+ another.email);
						}
					} else {
						Log.d(LOG_TAG,
								"Address book contact office phone not equals, self office phone = "
										+ getOfficePhoneNumber()
										+ " and another office phone = "
										+ another.getOfficePhoneNumber());
					}
				} else {
					Log.d(LOG_TAG,
							"Address book contact mobile phone not equals, self mobile phone = "
									+ getMobilePhoneNumber()
									+ " and another mobile phone = "
									+ another.getMobilePhoneNumber());
				}
			} else {
				Log.d(LOG_TAG,
						"Address book contact sex not equals, self sex = "
								+ sex + " and another sex = " + another.sex);
			}
		} else {
			Log.d(LOG_TAG,
					"Address book contact employee name not equals, self employee name = "
							+ employeeName + " and another employee name = "
							+ another.employeeName);
		}

		return _result;
	}

	@Override
	public String toString() {
		// define description
		StringBuilder _description = new StringBuilder();

		// append enterprise address book contact id, avatar url, employee name,
		// sex, nickname, birthday, department, email, note and frequency
		_description
				.append("Enterprise address book contact row id = ")
				.append(rowId)
				.append(", ")
				.append("user id = ")
				.append(userId)
				.append(", ")
				.append("enterprise id = ")
				.append(enterpriseId)
				.append(", ")
				.append("avatar url = ")
				.append(avatarUrl)
				.append(", ")
				.append("employee name = ")
				.append(employeeName)
				.append(", ")
				.append("sex = ")
				.append(sex)
				.append(", ")
				.append("nickname = ")
				.append(nickname)
				.append(", ")
				.append("birthday = ")
				.append(new SimpleDateFormat("", Locale.getDefault())
						.format(new Date(birthday))).append(", ")
				.append("department = ").append(department).append(", ")
				.append("approve number = ").append(approveNumber).append(", ")
				.append("phones list = ").append(phones).append(", ")
				.append("email = ").append(email).append(", ")
				.append("note = ").append(note).append(" and ")
				.append("frequency = ").append(frequency);

		return _description.toString();
	}

}
