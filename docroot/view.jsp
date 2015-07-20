<%--
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
--%>

<%@ include file="/init.jsp" %>

<c:if test="<%= !themeDisplay.isSignedIn() %>" >

	<%
	String authType = portletPreferences.getValue("authType", StringPool.BLANK);
	String redirect = ParamUtil.getString(request, "redirect");
	boolean male = ParamUtil.getBoolean(request, "male", true);
	%>

	<portlet:actionURL var="actionURL">
	</portlet:actionURL>
	
	<aui:form action="<%= actionURL %>" method="post" name="fm">
		<aui:input name="saveLastPath" type="hidden" value="<%= false %>" />
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	
		<liferay-ui:error exception="<%= ContactFirstNameException.class %>" message="please-enter-a-valid-first-name" />
		<liferay-ui:error exception="<%= ContactFullNameException.class %>" message="please-enter-a-valid-first-middle-and-last-name" />
		<liferay-ui:error exception="<%= ContactLastNameException.class %>" message="please-enter-a-valid-last-name" />
		<liferay-ui:error exception="<%= DuplicateUserEmailAddressException.class %>" message="the-email-address-you-requested-is-already-taken" />
		<liferay-ui:error exception="<%= DuplicateUserIdException.class %>" message="the-user-id-you-requested-is-already-taken" />
		<liferay-ui:error exception="<%= DuplicateUserScreenNameException.class %>" message="the-screen-name-you-requested-is-already-taken" />
		<liferay-ui:error exception="<%= EmailAddressException.class %>" message="please-enter-a-valid-email-address" />
	
		<liferay-ui:error exception="<%= GroupFriendlyURLException.class %>">
	
			<%
			GroupFriendlyURLException gfurle = (GroupFriendlyURLException)errorException;
			%>
	
			<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.DUPLICATE %>">
				<liferay-ui:message key="the-screen-name-you-requested-is-associated-with-an-existing-friendly-url" />
			</c:if>
		</liferay-ui:error>
	
		<liferay-ui:error exception="<%= RequiredFieldException.class %>" message="please-fill-out-all-required-fields" />
		<liferay-ui:error exception="<%= ReservedUserEmailAddressException.class %>" message="the-email-address-you-requested-is-reserved" />
		<liferay-ui:error exception="<%= ReservedUserIdException.class %>" message="the-user-id-you-requested-is-reserved" />
		<liferay-ui:error exception="<%= ReservedUserScreenNameException.class %>" message="the-screen-name-you-requested-is-reserved" />
		<liferay-ui:error exception="<%= UserEmailAddressException.class %>" message="please-enter-a-valid-email-address" />
		<liferay-ui:error exception="<%= UserIdException.class %>" message="please-enter-a-valid-user-id" />
	
		<liferay-ui:error exception="<%= UserPasswordException.class %>">
	
			<%
			UserPasswordException upe = (UserPasswordException)errorException;
			%>
	
			<c:if test="<%= upe.getType() == UserPasswordException.PASSWORD_CONTAINS_TRIVIAL_WORDS %>">
				<liferay-ui:message key="that-password-uses-common-words-please-enter-in-a-password-that-is-harder-to-guess-i-e-contains-a-mix-of-numbers-and-letters" />
			</c:if>
	
			<c:if test="<%= upe.getType() == UserPasswordException.PASSWORD_INVALID %>">
				<liferay-ui:message key="that-password-is-invalid-please-enter-in-a-different-password" />
			</c:if>
	
			<c:if test="<%= upe.getType() == UserPasswordException.PASSWORD_LENGTH %>">
	
				<%
				PasswordPolicy passwordPolicy = PasswordPolicyLocalServiceUtil.getDefaultPasswordPolicy(company.getCompanyId());
				%>
	
				<%= LanguageUtil.format(pageContext, "that-password-is-too-short-or-too-long-please-make-sure-your-password-is-between-x-and-512-characters", String.valueOf(passwordPolicy.getMinLength()), false) %>
			</c:if>
	
			<c:if test="<%= upe.getType() == UserPasswordException.PASSWORD_TOO_TRIVIAL %>">
				<liferay-ui:message key="that-password-is-too-trivial" />
			</c:if>
	
			<c:if test="<%= upe.getType() == UserPasswordException.PASSWORDS_DO_NOT_MATCH %>">
				<liferay-ui:message key="the-passwords-you-entered-do-not-match-each-other-please-re-enter-your-password" />
			</c:if>
		</liferay-ui:error>
	
		<liferay-ui:error exception="<%= UserScreenNameException.class %>" message="please-enter-a-valid-screen-name" />
	
		<aui:model-context model="<%= Contact.class %>" />
	
		<aui:fieldset column="<%= true %>">
			<aui:col width="<%= 50 %>">
	
				<aui:input autoFocus="<%= true %>" model="<%= User.class %>" name="emailAddress">
					<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_EMAIL_ADDRESS_REQUIRED) %>">
						<aui:validator name="required" />
					</c:if>
				</aui:input>
	
				<aui:input autoFocus="<%= windowState.equals(WindowState.MAXIMIZED) %>" model="<%= User.class %>" name="firstName" />
	
				<aui:input model="<%= User.class %>" name="lastName">
					<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_LAST_NAME_REQUIRED, GetterUtil.getBoolean(PropsUtil.get(PropsKeys.USERS_LAST_NAME_REQUIRED), false)) %>">
						<aui:validator name="required" />
					</c:if>
				</aui:input>
	
			</aui:col>
	
			<aui:col width="<%= 50 %>">
	
				<c:if test="<%= authType.equals(CompanyConstants.AUTH_TYPE_SN) %>">
					<aui:input model="<%= User.class %>" name="screenName" />
				</c:if>
	
				<aui:input label="password" name="password" size="30" type="password" value="" />
	
				<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.FIELD_ENABLE_COM_LIFERAY_PORTAL_MODEL_CONTACT_MALE) %>">
					<aui:select label="gender" name="male">
						<aui:option label="male" value="1" />
						<aui:option label="female" selected="<%= !male %>" value="0" />
					</aui:select>
				</c:if>
	
			</aui:col>
		</aui:fieldset>
	
		<aui:button-row>
			<aui:button type="submit" />
		</aui:button-row>
	</aui:form>

</c:if>