<!DOCTYPE html>
<%@page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
%><%
	String appUrl = "/console";
	response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	response.setHeader("Location", appUrl);
	response.sendRedirect(appUrl);
%><html><meta http-equiv="refresh" content="0;URL=<%= appUrl %>"/></head></html>