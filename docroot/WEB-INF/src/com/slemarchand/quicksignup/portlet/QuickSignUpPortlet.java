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

package com.slemarchand.quicksignup.portlet;

import com.liferay.portal.ContactFirstNameException;
import com.liferay.portal.ContactFullNameException;
import com.liferay.portal.ContactLastNameException;
import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.EmailAddressException;
import com.liferay.portal.GroupFriendlyURLException;
import com.liferay.portal.RequiredFieldException;
import com.liferay.portal.RequiredUserException;
import com.liferay.portal.ReservedUserEmailAddressException;
import com.liferay.portal.ReservedUserScreenNameException;
import com.liferay.portal.UserEmailAddressException;
import com.liferay.portal.UserIdException;
import com.liferay.portal.UserPasswordException;
import com.liferay.portal.UserScreenNameException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.PasswordPolicy;
import com.liferay.portal.model.User;
import com.liferay.portal.service.PasswordPolicyLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import com.slemarchand.quicksignup.util.WebKeys;

import java.io.IOException;

import java.util.UUID;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import javax.servlet.http.HttpServletRequest;

/**
 * Portlet implementation class SignUpPortlet
 *
 * @author Sebastien Le Marchand
 */
public class QuickSignUpPortlet extends MVCPortlet {

	@Override
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

			ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			User user = null;

			try {
				user = addUser(actionRequest, actionResponse);
			}
			catch (Exception e) {
				if (e instanceof DuplicateUserEmailAddressException ||
					e instanceof DuplicateUserScreenNameException ||
					e instanceof ContactFirstNameException ||
					e instanceof ContactFullNameException ||
					e instanceof ContactLastNameException ||
					e instanceof EmailAddressException ||
					e instanceof GroupFriendlyURLException ||
					e instanceof RequiredFieldException ||
					e instanceof RequiredUserException ||
					e instanceof ReservedUserEmailAddressException ||
					e instanceof ReservedUserScreenNameException ||
					e instanceof UserEmailAddressException ||
					e instanceof UserIdException ||
					e instanceof UserPasswordException ||
					e instanceof UserScreenNameException) {

					SessionErrors.add(actionRequest, e.getClass(), e);
				}
				else {
					throw new PortletException(e);
				}
			}

			if (user != null) {
				try {
					String redirect = getSuccessRedirect(actionRequest, actionResponse);

					login(actionRequest, actionResponse, user, redirect);

				} catch (SystemException e) {
					throw new PortletException(e);
				} catch (PortalException e) {
					throw new PortletException(e);
				}
			}
	}

	protected User addUser(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		String authType = company.getAuthType();

		boolean autoPassword = false;
		String password1 = null;
		String password2 = null;
		boolean autoScreenName = !authType.equals(CompanyConstants.AUTH_TYPE_SN);
		String screenName = ParamUtil.getString(actionRequest, "screenName");
		String emailAddress = ParamUtil.getString(
			actionRequest, "emailAddress");
		long facebookId = 0;
		String openId = StringPool.BLANK;
		String firstName = ParamUtil.getString(actionRequest, "firstName");
		String middleName = StringPool.BLANK;
		String lastName = ParamUtil.getString(actionRequest, "lastName");
		int prefixId = 0;
		int suffixId = 0;
		boolean male = ParamUtil.getBoolean(actionRequest, "male", true);
		int birthdayMonth = 1;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = true;

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), actionRequest);

		password1 = ParamUtil.getString(actionRequest, "password");
		password2 = password1;

		User user = UserServiceUtil.addUser(
			company.getCompanyId(), autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, facebookId, openId,
			themeDisplay.getLocale(), firstName, middleName, lastName, prefixId,
			suffixId, male, birthdayMonth, birthdayDay, birthdayYear, jobTitle,
			groupIds, organizationIds, roleIds, userGroupIds, sendEmail,
			serviceContext);

		long userId = user.getUserId();

		user = UserLocalServiceUtil.updatePasswordReset(userId, false);

		PasswordPolicy passwordPolicy = PasswordPolicyLocalServiceUtil.getPasswordPolicyByUserId(userId);
		passwordPolicy.setChangeRequired(false);
		PasswordPolicyLocalServiceUtil.updatePasswordPolicy(passwordPolicy);

		if (_USERS_REMINDER_QUERIES_ENABLED) {
			user = UserLocalServiceUtil.updateReminderQuery(userId, StringPool.DASH,
					UUID.randomUUID().toString());
		}

		user = UserLocalServiceUtil.updateAgreedToTermsOfUse(userId, true);

		return user;
	}

	protected String getLogin(ActionRequest actionRequest, User user) throws SystemException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		String authType = company.getAuthType();

		String login;

		if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			login = user.getEmailAddress();
		} else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			login = user.getScreenName();
		} else {
			login = Long.toString(user.getUserId());
		}

		return login;
	}

	protected String getSuccessRedirect(ActionRequest actionRequest,
			ActionResponse actionResponse) throws PortalException, SystemException {

		String redirect = getRedirect(actionRequest, actionResponse);

		if (Validator.isNull(redirect)) {
			ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			redirect = PortalUtil.getLayoutURL(themeDisplay);
		}

		return redirect;
	}

	protected void login(ActionRequest actionRequest, ActionResponse actionResponse, User user, String redirect) throws SystemException {

		HttpServletRequest originalRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));

		originalRequest.setAttribute(WebKeys.QUICK_SIGN_UP_LOGIN, getLogin(actionRequest, user));
		originalRequest.setAttribute(WebKeys.QUICK_SIGN_UP_PASSWORD, actionRequest.getParameter("password"));
		originalRequest.setAttribute(WebKeys.QUICK_SIGN_UP_REDIRECT, redirect);
	}

	private static final boolean _USERS_REMINDER_QUERIES_ENABLED = GetterUtil.getBoolean(PropsUtil.get(PropsKeys.USERS_REMINDER_QUERIES_ENABLED));

	private static Log _log = LogFactoryUtil.getLog(QuickSignUpPortlet.class);

}