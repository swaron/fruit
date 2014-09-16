package me.suisui.framework.web.support;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public abstract class JsonUtils {
	public static JsonFactory factroy = new JsonFactory();

	public static String readString(String attrs, String field) {
		try {
			JsonParser parser = factroy.createJsonParser(attrs);
			while (parser.nextToken() != JsonToken.END_OBJECT) {
				String fieldname = parser.getCurrentName();
				if (field.equals(fieldname)) {
					return parser.nextTextValue();
				}
			}
			parser.close();

		} catch (JsonParseException e) {
		} catch (IOException e) {
		}
		return null;

	}
}
