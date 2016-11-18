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

package com.slemarchand.quick.sign.up.web.portlet;


import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.EmailAddressException;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredFieldException;
import com.liferay.portal.kernel.exception.RequiredUserException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserIdException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.kernel.service.PasswordPolicyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.slemarchand.quick.sign.up.web.constants.QuickSignUpPortletKeys;

import java.io.IOException;
import java.util.UUID;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sebastien Le Marchand
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-quick-sign-up",
		"com.liferay.portlet.display-category=category.tools",
		"com.liferay.portlet.icon=/icon.png",
		"com.liferay.portlet.preferences-owned-by-group=true", //
		"com.liferay.portlet.private-request-attributes=false", //
		"com.liferay.portlet.private-session-attributes=false", //
		"com.liferay.portlet.render-weight=50", //
		"com.liferay.portlet.use-default-template=true", //
		"javax.portlet.display-name=Quick Sign Up",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.add-process-action-success-action=false", //
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + QuickSignUpPortletKeys.QUICK_SIGN_UP,
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
    },
    service = Portlet.class
)
public class QuickSignUpPortlet extends MVCPortlet {

	@Override
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

			ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			String password = ParamUtil.getString(actionRequest, "password");
			
			User user = null;

		try {
			user = addUser(actionRequest, actionResponse, password);
		}
		catch (UserEmailAddressException 
						| UserScreenNameException
						| ContactNameException 
						| EmailAddressException
						| GroupFriendlyURLException 
						| RequiredFieldException
						| RequiredUserException 
						| UserIdException
						| UserPasswordException e) {
			SessionErrors.add(actionRequest, e.getClass(), e);
		}
		catch (SystemException | PortalException e) {
			throw new PortletException(e);
		} 
				
			if (user != null && !themeDisplay.isSignedIn()) {
				try {
					String redirect = getSuccessRedirect(actionRequest, actionResponse);

					login(themeDisplay, actionRequest, actionResponse, user, password, redirect);

				} catch (Exception e) {
					throw new PortletException(e);
				}
			}
	}

	protected User addUser(
			ActionRequest actionRequest,
			ActionResponse actionResponse,
			String password)
			throws SystemException, PortalException {

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

		password1 = password;
		password2 = password;

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

	protected String getLogin(
		ThemeDisplay themeDisplay, ActionRequest actionRequest, User user)
		throws SystemException {

		Company company = themeDisplay
			.getCompany();

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

	protected void login(
		ThemeDisplay themeDisplay, ActionRequest actionRequest,
		ActionResponse actionResponse, User user, String password,
		String redirect)
		throws Exception {

		HttpServletRequest request = PortalUtil.getOriginalServletRequest(
				PortalUtil.getHttpServletRequest(actionRequest));
		HttpServletResponse response = PortalUtil.getHttpServletResponse(
				actionResponse);		

		String login = getLogin(themeDisplay, actionRequest, user);
		
		boolean rememberMe = false;
		
		String authType = themeDisplay.getCompany().getAuthType();
		
		AuthenticatedSessionManagerUtil.login(
				request, response, login, password, rememberMe, authType);	
		
		actionResponse.sendRedirect(redirect);
	}

	private static final boolean _USERS_REMINDER_QUERIES_ENABLED = GetterUtil.getBoolean(PropsUtil.get(PropsKeys.USERS_REMINDER_QUERIES_ENABLED));

	private static Log _log = LogFactoryUtil.getLog(QuickSignUpPortlet.class);

}