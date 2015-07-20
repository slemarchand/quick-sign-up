/**
 * Copyright (c) 2015-present Sebastien Le Marchand All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.slemarchand.quicksignup.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ClassResolverUtil;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.PortalClassInvoker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Sebastien Le Marchand
 *
 */
public class LoginUtil {

	public static final String _CLASS_NAME =
			"com.liferay.portlet.login.util.LoginUtil";

	public static MethodKey _loginMethodKey = new MethodKey(ClassResolverUtil.resolveByPortalClassLoader(_CLASS_NAME), "login", HttpServletRequest.class, HttpServletResponse.class, String.class, String.class, Boolean.TYPE, String.class);

	public static void login(HttpServletRequest request, HttpServletResponse response, String login, String password, boolean rememberMe, String authType) throws Exception {

		PortalClassInvoker.invoke(false, _loginMethodKey, request, response, login, password, Boolean.valueOf(rememberMe), authType);
	}

	private static Log _log = LogFactoryUtil.getLog(LoginUtil.class);

}