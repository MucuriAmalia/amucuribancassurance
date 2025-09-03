<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/medicalsetups/specialist.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-specialist">New</button>
    <div class="x_title">
        <h4>Specialist Fees</h4>
    </div>
    <table id="specialist-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>ID</th>
            <th>Description</th>
            <th>Upper Limit</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
</div>
<div class="modal fade" id="specialistModal" tabindex="-1" role="dialog"
     aria-labelledby="specialistModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="specialistModalLabel">
                    Edit/Add Specialist Fees
                </h4>
            </div>
            <div class="modal-body">
                <form id="specialist-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="specialist-pk" name="specId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="specialist-id"
                                   name="specShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="specialist-desc"
                                   name="specDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Upper Limit</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="specialist-upper-limit"
                                   name="upperLimit"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveSpecialists"
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

