package me.suisui.integration.hibernate;

import java.util.UUID;

import me.suisui.framework.util.VersionOneUuid;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.UUIDGenerationStrategy;

/**
 * 
 * 
 */
public final class CustomVersionOneStrategy implements UUIDGenerationStrategy {
	private static final long serialVersionUID = 81575248467114636L;
	public static final CustomVersionOneStrategy INSTANCE = new CustomVersionOneStrategy();

	/**
	 * A variant 1 + (random) strategy
	 */
	public int getGeneratedVersion() {
		// a modified version 1 uuid
		return 1;
	}

	public UUID generateUUID(SessionImplementor session) {
		return VersionOneUuid.randomUUID();
	}

}
