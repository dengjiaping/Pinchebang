package com.lepin.util;

import android.content.Intent;
import android.view.View;

public class Interfaces {

	public interface ActivityResult {
		void onActivityResult(int requestCode, int resultCode, Intent data);
	}

	public interface SetCurrentAddrress {
		void setAddress(String address);
	}
}
