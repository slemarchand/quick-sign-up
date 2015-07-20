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

package com.slemarchand.quicksignup.hook.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalUtil;

import com.slemarchand.quicksignup.util.LoginUtil;
import com.slemarchand.quicksignup.util.WebKeys;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sebastien Le Marchand
 *
 */
public class ServicePostAction extends Action {

	public ServicePostAction() {
		super();
	}

	public void run(HttpServletRequest request, HttpServletResponse response) throws ActionException {

		String login = (String)request.getAttribute(WebKeys.QUICK_SIGN_UP_LOGIN);
		String password = (String)request.getAttribute(WebKeys.QUICK_SIGN_UP_PASSWORD);

		if (login != null && password != null) {
			try {
				String authType = PortalUtil.getCompany(request).getAuthType();

				LoginUtil.login(request, response, login, password, false, authType);

				String redirect = (String)request.getAttribute(WebKeys.QUICK_SIGN_UP_REDIRECT);

				if (Validator.isNull(redirect)) {
					redirect = PortalUtil.getHomeURL(request);
				}

				response.sendRedirect(redirect);

			} catch (Exception e) {
				throw new ActionException(e);
			}
		}
	}

	private static Log _log = LogFactoryUtil.getLog(ServicePostAction.class);
}