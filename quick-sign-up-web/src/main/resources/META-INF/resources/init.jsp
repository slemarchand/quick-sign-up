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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.kernel.exception.CompanyMaxUsersException" %><%@
page import="com.liferay.portal.kernel.exception.ContactNameException" %><%@
page import="com.liferay.portal.kernel.exception.EmailAddressException" %><%@
page import="com.liferay.portal.kernel.exception.GroupFriendlyURLException" %><%@
page import="com.liferay.portal.kernel.exception.RequiredFieldException" %><%@
page import="com.liferay.portal.kernel.exception.UserEmailAddressException" %><%@
page import="com.liferay.portal.kernel.exception.UserIdException" %><%@
page import="com.liferay.portal.kernel.exception.UserPasswordException" %><%@
page import="com.liferay.portal.kernel.exception.UserScreenNameException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PrefsPropsUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsKeys" %><%@
page import="com.liferay.portal.kernel.util.PropsUtil" %><%@
page import="com.liferay.portal.kernel.util.StringPool" %><%@
page import="com.liferay.portal.kernel.model.CompanyConstants" %><%@
page import="com.liferay.portal.kernel.model.Contact" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLUtil" %><%@
page import="com.liferay.portal.kernel.security.auth.FullNameDefinition" %><%@
page import="com.liferay.portal.kernel.security.auth.FullNameDefinitionFactory" %>

<%@ page import="javax.portlet.PortletMode" %><%@
page import="javax.portlet.PortletURL" %><%@
page import="javax.portlet.WindowState" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
PortletMode portletMode = liferayPortletRequest.getPortletMode();
WindowState windowState = liferayPortletRequest.getWindowState();

PortletURL currentURLObj = PortletURLUtil.getCurrent(liferayPortletRequest, liferayPortletResponse);

String currentURL = currentURLObj.toString();
%>