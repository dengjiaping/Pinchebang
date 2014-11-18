package com.lepin.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.lepin.entity.PushMsg;
import com.lepin.entity.PushMsg.PUSH_MSG_TYPE;

/**
 * 处理消息中心类型
 * 
 * @author Administrator
 * 
 */
public class PushMsgTypeDeserualizer implements JsonDeserializer<PushMsg.PUSH_MSG_TYPE> {
	@Override
	public PUSH_MSG_TYPE deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		String str = json.getAsString();
		try {
			return PUSH_MSG_TYPE.valueOf(str);
		} catch (Exception e) {
			return PUSH_MSG_TYPE.OTHER;
		}
	}
}