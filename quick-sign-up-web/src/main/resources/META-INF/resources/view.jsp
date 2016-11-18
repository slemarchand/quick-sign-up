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
	
		<liferay-ui:error exception="<%= CompanyMaxUsersException.class %>" message="unable-to-create-user-account-because-the-maximum-number-of-users-has-been-reached" />
		<liferay-ui:error exception="<%= ContactNameException.MustHaveFirstName.class %>" message="please-enter-a-valid-first-name" />
		<liferay-ui:error exception="<%= ContactNameException.MustHaveLastName.class %>" message="please-enter-a-valid-last-name" />
		<liferay-ui:error exception="<%= ContactNameException.MustHaveValidFullName.class %>" message="please-enter-a-valid-first-middle-and-last-name" />
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
		<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeDuplicate.class %>" message="the-email-address-you-requested-is-already-taken" />
		<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeNull.class %>" message="please-enter-an-email-address" />
		<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBePOP3User.class %>" message="the-email-address-you-requested-is-reserved" />
		<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeReserved.class %>" message="the-email-address-you-requested-is-reserved" />
		<liferay-ui:error exception="<%= UserEmailAddressException.MustNotUseCompanyMx.class %>" message="the-email-address-you-requested-is-not-valid-because-its-domain-is-reserved" />
		<liferay-ui:error exception="<%= UserEmailAddressException.MustValidate.class %>" message="please-enter-a-valid-email-address" />

		<liferay-ui:error exception="<%= UserPasswordException.MustBeLonger.class %>">

			<%
			UserPasswordException.MustBeLonger upe = (UserPasswordException.MustBeLonger)errorException;
			%>
	
			<liferay-ui:message arguments="<%= String.valueOf(upe.minLength) %>" key="that-password-is-too-short" translateArguments="<%= false %>" />
		</liferay-ui:error>
	
		<liferay-ui:error exception="<%= UserPasswordException.MustComplyWithModelListeners.class %>" message="that-password-is-invalid-please-enter-a-different-password" />
	
		<liferay-ui:error exception="<%= UserPasswordException.MustComplyWithRegex.class %>">
	
			<%
			UserPasswordException.MustComplyWithRegex upe = (UserPasswordException.MustComplyWithRegex)errorException;
			%>
	
			<liferay-ui:message arguments="<%= upe.regex %>" key="that-password-does-not-comply-with-the-regular-expression" translateArguments="<%= false %>" />
		</liferay-ui:error>
	
		<liferay-ui:error exception="<%= UserPasswordException.MustMatch.class %>" message="the-passwords-you-entered-do-not-match" />
		<liferay-ui:error exception="<%= UserPasswordException.MustNotBeNull.class %>" message="the-password-cannot-be-blank" />
		<liferay-ui:error exception="<%= UserPasswordException.MustNotBeTrivial.class %>" message="that-password-uses-common-words-please-enter-a-password-that-is-harder-to-guess-i-e-contains-a-mix-of-numbers-and-letters" />
		<liferay-ui:error exception="<%= UserPasswordException.MustNotContainDictionaryWords.class %>" message="that-password-uses-common-dictionary-words" />
		<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeDuplicate.class %>" focusField="screenName" message="the-screen-name-you-requested-is-already-taken" />
		<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNull.class %>" focusField="screenName" message="the-screen-name-cannot-be-blank" />
		<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNumeric.class %>" focusField="screenName" message="the-screen-name-cannot-contain-only-numeric-values" />
		<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeReserved.class %>" message="the-screen-name-you-requested-is-reserved" />
		<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeReservedForAnonymous.class %>" focusField="screenName" message="the-screen-name-you-requested-is-reserved-for-the-anonymous-user" />
		<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeUsedByGroup.class %>" focusField="screenName" message="the-screen-name-you-requested-is-already-taken-by-a-site" />
		<liferay-ui:error exception="<%= UserScreenNameException.MustProduceValidFriendlyURL.class %>" focusField="screenName" message="the-screen-name-you-requested-must-produce-a-valid-friendly-url" />
	
		<liferay-ui:error exception="<%= UserScreenNameException.MustValidate.class %>" focusField="screenName">
	
			<%
			UserScreenNameException.MustValidate usne = (UserScreenNameException.MustValidate)errorException;
			%>
	
			<liferay-ui:message key="<%= usne.screenNameValidator.getDescription(locale) %>" />
		</liferay-ui:error>
	
		<aui:model-context model="<%= Contact.class %>" />
	
		<aui:fieldset column="<%= true %>">
			<aui:col width="<%= 50 %>">
	
				<aui:input autoFocus="<%= true %>" model="<%= User.class %>" name="emailAddress">
					<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_EMAIL_ADDRESS_REQUIRED) %>">
						<aui:validator name="required" />
					</c:if>
				</aui:input>
	
				<%
				FullNameDefinition fullNameDefinition = FullNameDefinitionFactory.getInstance(locale);
				%>
	
				<aui:input autoFocus="<%= windowState.equals(WindowState.MAXIMIZED) %>" model="<%= User.class %>" name="firstName">
					<c:if test="<%= fullNameDefinition.isFieldRequired("first-name") %>">
						<aui:validator name="required" />
					</c:if>
				</aui:input>
					
				<aui:input model="<%= User.class %>" name="lastName">
					<c:if test="<%= fullNameDefinition.isFieldRequired("last-name") %>">
						<aui:validator name="required" />
					</c:if>
				</aui:input>

				<liferay-ui:error exception="<%= ContactNameException.MustHaveFirstName.class %>" message="please-enter-a-valid-first-name" />
				<liferay-ui:error exception="<%= ContactNameException.MustHaveValidFullName.class %>" message="please-enter-a-valid-first-middle-and-last-name" />

			</aui:col>
	
			<aui:col width="<%= 50 %>">
	
				<c:if test="<%= authType.equals(CompanyConstants.AUTH_TYPE_SN) %>">
					<aui:input model="<%= User.class %>" name="screenName" />
				</c:if>
	
				<aui:input label="password" name="password" size="30" type="password" value="">
					<aui:validator name="required" />
				</aui:input>
	
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