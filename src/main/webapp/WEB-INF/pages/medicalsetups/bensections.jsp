<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/medicalsetups/benefits.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-benefit-sections">New</button>
    <div class="x_title">
        <h4>Claim Networks</h4>
    </div>
    <div class="cutom-container">
    <table id="benefits-sections-tbl" class="table" style="width:100%">
        <thead>
        <tr>

            <th>Networks Code</th>
            <th>Network Desc</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>
<div class="modal fade" id="sectionModal" tabindex="-1" role="dialog"
     aria-labelledby="sectionModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="sectionModalLabel">
                    Edit/Add Medical Networks
                </h4>
            </div>
            <div class="modal-body">
                <form id="benefits-section-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="benefit-pk" name="benId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Network ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="benefit-id"
                                   name="benShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Network Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="benefit-desc"
                                   name="benDesc"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBenefitsSection"
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

