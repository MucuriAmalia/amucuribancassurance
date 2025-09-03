<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/medicalsetups/dependents.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-depend-types">New</button>
    <div class="x_title">
        <h4>Member Types</h4>
    </div>
    <div class="cutom-container">
    <table id="dep-type-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Member Type ID</th>
            <th>Member Type Desc</th>
            <th>Main Member?</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>
<div class="modal fade" id="dependTypesModal" tabindex="-1" role="dialog"
     aria-labelledby="dependTypesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="dependTypesModalLabel">
                    Edit/Add Dependent Type
                </h4>
            </div>
            <div class="modal-body" id="branch_model">
                <form id="dep-type-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="depend-type-pk" name="depId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Dependent Type ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="depend-type-id"
                                   name="depShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Dependent Type Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="depend-type-desc"
                                   name="depDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Main Member?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="mainMember" id="dep-main-member">
                            </label>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveDepType"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>
