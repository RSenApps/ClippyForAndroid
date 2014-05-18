package wei.mark.example;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class ContactSearcher {
	public static String numberToID(String phoneNumber, Context context) {
		try {
			ContentResolver localContentResolver = context.getContentResolver();
			Cursor contactLookupCursor = localContentResolver.query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)), new String[] { PhoneLookup.DISPLAY_NAME, BaseColumns._ID }, null, null, null);
			try {
				while (contactLookupCursor.moveToNext()) {
					String contactName = contactLookupCursor.getString(contactLookupCursor.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
					return contactName;
				}
			} finally {
				contactLookupCursor.close();
			}
			String spacedNumber = "";
			for (char c : phoneNumber.toCharArray()) {
				spacedNumber += c + " ";
			}
			return spacedNumber;
		} catch (Exception e) {
			return "Unknown Number";
		}
	}
}