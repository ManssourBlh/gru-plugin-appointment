<jsp:useBean id="annualCalendare" scope="session" class="fr.paris.lutece.plugins.appointment.web.AppointmentAnnualCalendarJspBean" />
<% String strContent = annualCalendare.processController( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
