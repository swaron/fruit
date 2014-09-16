package me.suisui.framework.util;

import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;

import org.joda.time.DateTime;

/**
 * Generate UUID for used as distributed database identity, it is a modification
 * version of uuid version 1. the time part is from 1970-01-01, same as unix
 * timestamp. the node part is random bits. refer to <code>UUID</code> for
 * example
 * 
 * @author swaron
 * 
 */
public class VersionOneUuid {
	static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;

	/*
	 * The random number generator used by this class to create random based
	 * UUIDs.
	 */
	private static volatile SecureRandom numberGenerator = null;

	/**
	 * * The most significant long consists of the following unsigned fields:
	 * 
	 * <pre>
	 * 0xFFFFFFFF00000000 time_low
	 * 0x00000000FFFF0000 time_mid
	 * 0x000000000000F000 version
	 * 0x0000000000000FFF time_hi
	 * </pre>
	 * 
	 * The least significant long consists of the following unsigned fields:
	 * 
	 * <pre>
	 * 0xC000000000000000 variant
	 * 0x3FFF000000000000 clock_seq
	 * 0x0000FFFFFFFFFFFF node. random bits in this generator
	 * </pre>
	 * 
	 * @return version 1 uuid
	 */
	public static UUID randomUUID() {
		SecureRandom ng = numberGenerator;
		if (ng == null) {
			numberGenerator = ng = new SecureRandom();
		}
		long now = (System.nanoTime() / 100) + NUM_100NS_INTERVALS_SINCE_UUID_EPOCH;

		long most = now << 32; // time_low
		most |= (now >> 16) & 0x00000000FFFF0000L; // time_mid
		most |= 0x0000000000001000L; /* set to version 1 */
		most |= (now >> 48) & 0x0000000000000FFFL; // time_hi

		long least = ng.nextLong();
		least &= 0x3FFFFFFFFFFFFFFFL;/* clear variant */
		least |= 0x8000000000000000L;/* set to IETF variant */
		return new UUID(most, least);
	}

	public static Date getDate(UUID uuid) {
		long timestamp = uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH;
		return new Date(timestamp / 10000);
	}

	public static DateTime getDateTime(UUID uuid) {
		long timestamp = uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH;
		return new DateTime(timestamp / 10000);
	}
}
