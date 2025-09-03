<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Create parameter</h2>
        <div class="clearfix"></div>
    </div>
    <div class="" role="tabpanel" data-example-id="togglable-tabs">
        <form id="parameter-form" class="form-horizontal"
              action="<c:url value='/protected/stretchyparameters/createStretchyParam'/>" method="post">
            <div class="x_panel">
                <div class="form-group">
                    <c:if test="${empty param.type}">
                        <a href="<c:url value='/protected/stretchyparameters/params'/> "
                           class="btn btn-primary pull-right">Back</a>
                    </c:if>
                    <button data-loading-text="Saving..." id="saveStretchyParameterBtn"
                            type="submit" class="btn btn-success pull-right">
                        Save
                    </button>
                </div>
            </div>
            <!-- Start of adding fields -->
            <div class="x_panel">

                <div class="form-group form-required">
                    <div class="col-md-4 col-sm-12">
                        <label>Parameter Name*</label>
                        <input type="text" name="paramName" id="parameter-name" class="form-control"
                               placeholder="Parameter Name" required>
                    </div>
                    <div class="col-md-4 col-sm-12" id="report-sub-type-container">
                        <label>Parameter Label*</label>
                        <input type="text" name="paramActualName" id="parameter-label" class="form-control"
                               placeholder="Parameter Label" required>
                    </div>
                    <div class="col-md-4 col-sm-12">
                        <label for="param-type">Format Type*</label>
                        <select class="form-control" id="param-type" name="paramType" required>
                            <option value="">Select Parameter Type</option>
                            <option value="D">Date</option>
                            <option value="N">Number</option>
                            <option value="T">Text</option>
                            <option value="L">LOV</option>
                            <option value="O">Options</option>
                        </select>
                    </div>
                </div>

                <div class="form-group form-required">
                    <div class="col-md-4 col-sm-12">
                        <label>Options(separated by commas)</label>
                        <input class="form-control" rows="5" id="option-name" name="options" disabled required
                               placeholder="pdf,csv,xlsx">
                    </div>
                    <div class="col-md-4 col-sm-12">
                        <label for="lov-name">Lov Name</label>

                        <select class="form-control" id="lov-name" name="lovName" disabled required>
                            <option value="">Select LOV Name</option>
                            <option value="C">Client</option>
                            <option value="A">Insurer</option>
                            <option value="SA">Sub Agent</option>
                            <option value="U">User</option>
                            <option value="P">Policy</option>
                            <option value="B">Branch</option>
                            <option value="PR">Product</option>
                            <option value="BI">Binder</option>
                            <option value="CR">Currency</option>
                            <option value="R">Remmittance</option>
                            <option value="CT">Certificate Type</option>
                            <option value="PM">Payment Mode</option>
                            <option value="PRO">Prospect</option>
                            <option value="MRK">Marketer</option>
                            <option value="INT">Introducer</option>
                            <option value="REG">Region</option>
                            <option value="TASK">Task Type</option>
                            <option value="CHECKER">Checker</option>
                            <option value="ACTIVITY">Activity</option>


                        </select>

                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<script type="text/javascript" src="<c:url value="/js/modules/developermodule/stretchyparameters.js"/>"></script>

<script>

    $(function () {
        $(document).ready(function () {
            STRETCHY_PARAMETERS_UTILITIES.createStretchyParameters();
        });
    });
</script>

<script>
    var redirectUrl = "<c:url value='/protected/stretchyparameters/params'/>";
</script>
