<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="x_panel">
  <div class="x_title">
    <h2><i class="fa fa-bars"></i> Policy Number Update Processing</h2>
    <div class="clearfix"></div>
  </div>
  <div class="x_content">
    <div class="panel panel-default">
      <div class="panel-body">
        <div class="row">
          <sec:authorize access="hasAnyAuthority('ACCESS_UW')">
            <div class="col-md-6 col-sm-6 col-xs-12">
              <ul style="list-style-type: none;">
                <li><h5 style="font-weight: bolder;">Policy Number update</h5></li>
                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                  <li><a href="<c:url value="/protected/update/insurerpolno/updatecreation"/>">Upload
                    Policy Update File</a></li>
                </sec:authorize>
                <sec:authorize access="hasAnyAuthority('ACCESS_NEW_BUSINESS')">
                  <li><a href="<c:url value="/protected/update/insurerpolno/viewupdates"/>">Update
                    Policy Status Report</a></li>
                </sec:authorize>
              </ul>
            </div>
          </sec:authorize>
        </div>
      </div>
    </div>
  </div>
</div>