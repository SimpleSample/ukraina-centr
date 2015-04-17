package com.nagornyi.uc;

/**
 * @author Nagorny
 * Date: 13.05.14
 */
public enum Role {

    USER(4),
    PARTNER(1),
    ADMIN(0);

	public final int level;

	private Role(int level) {
		this.level = level;
	}

	public static Role valueOf(int level) {
		for (Role role: Role.values()) {
			if (role.level == level) return role;
		}
		return null;
	}

}
