<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/setups/deployments.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Workflow Deployments</h2>
        <div class="clearfix"></div>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>

    <div class="x_content">
        <form id="doc-upload-form" class="form-horizontal">

        <div class="item form-group">
            <div class="col-md-6 col-xs-12 form-required">
            <label for="doc-name" class="col-md-3 label-align">Document Type</label>

            <div class="col-md-9">
                <select class="form-control" id="doc-name" name="processName">
                    <option value="">Select Document Type</option>
                </select>
            </div>
                </div>
            <div class="col-md-6 col-xs-12">
                <button data-loading-text="Saving..." id="updateWFDoc"
                        type="button" class="btn btn-primary">
                    Update
                    </button>
            </div>
        </div>

            <div id="diagram" class="center-block">

                    <img id="proc-main-diagram" class="img-responsive img-rounded proc-diagram" src=""
                         alt="Workflow Process Diagram">

            </div>
        </form>

        </div>
    </div>



