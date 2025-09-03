<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/medicalsetups/familysize.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-fam-size">New</button>
    <div class="x_title">
        <h4>Family Sizes</h4>
    </div>
    <table id="family-size-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>ID</th>
            <th>Description</th>
            <th>Family Size</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
</div>
<div class="modal fade" id="famSizeModal" tabindex="-1" role="dialog"
     aria-labelledby="famSizeModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="famSizeModalLabel">
                    Edit/Add Family Size
                </h4>
            </div>
            <div class="modal-body">
                <form id="family-size-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="fam-size-pk" name="famId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Sht Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="fam-size-id"
                                   name="famShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Description</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="fam-size-desc"
                                   name="famDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Family Size</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="fam-size"
                                   name="famSize"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveFamilySize"
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

