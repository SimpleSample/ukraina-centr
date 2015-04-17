package com.nagornyi.uc.action;

import com.nagornyi.uc.Role;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Nagorny
 * Date: 29.04.14
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorized {

    Role role() default Role.USER;
}
