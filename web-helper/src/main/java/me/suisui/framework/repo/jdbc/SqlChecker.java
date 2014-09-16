package me.suisui.framework.repo.jdbc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.base.CharMatcher;

public class SqlChecker {
	static Pattern tablePattern = Pattern.compile("\\w+");
	static Pattern columnPattern = Pattern.compile("\\w+||(\\w+->>'\\w+')");
	static Pattern valuePattern = Pattern.compile("[\\w <>=']+");

	public static void checkTableName(String tableName) {
		Matcher matcher = tablePattern.matcher(tableName);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(
					"The table name is not conform with rule, only word character are allowed. tableName: " + tableName);
		}

	}

	public static void checkColName(String colName) {
		if(StringUtils.containsWhitespace(colName) || StringUtils.containsAny(colName, '(',')')){
			throw new IllegalArgumentException(
					"The column name is not conform with rule, only word character are allowed. columnName: " + colName);
		}
	}

	@Deprecated
    private static void checkColValue(String val) {
        Matcher matcher = valuePattern.matcher(val);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "The column value is not conform with rule, only word character are allowed. value: " + val);
        }
    }
	public static void main(String[] args) {
		SqlChecker.checkColName("产品标题");
		System.out.println();
	}
}
