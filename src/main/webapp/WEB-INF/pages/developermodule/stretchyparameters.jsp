<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<div class="x_panel" id="streparam_model">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Report parameter</h2>
        <div class="clearfix"></div>
    </div>
    <a href="<c:url value='/protected/stretchyparameters/stretchyParametersForm'/> " class="btn btn-info pull-right">New</a>
    <div class="table-responsive">
        <table id="streparamtbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Parameter ID</th>
                <th>Parameter Name</th>
                <th>Parameter Label</th>
                <th>Format Type</th>
                <th width="5%"></th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<script type="text/javascript" src="<c:url value="/js/modules/developermodule/stretchyparameters.js"/>"></script>


<script type="text/javascript">

    $(document).ready(function () {
        STRETCHY_PARAMETERS_UTILITIES.createStretchyParametersListing();
    });

</script>