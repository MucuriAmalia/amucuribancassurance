<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/pendingtrans.js"/>"></script>

<div class="x_panel">
    <div class="card-box table-responsive">
        <div class="x_title">
            <h2><i class="fa fa-bars"></i> Sap Export File</h2>
            <div class="clearfix"></div>
        </div>
        <form id="export-form" class="form-horizontal">
            <div class="item form-group">
                <button type="button" id="export-all-btn" class="btn btn-success float-right" style="margin-right: 10px;">Export All Batches</button>
            </div>
        </form>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        $('#export-all-btn').click(function() {
            Swal.fire({
                title: 'Exporting All Batches...',
                text: 'Please wait while we prepare your file',
                allowOutsideClick: false,
                showConfirmButton: false,
                willOpen: () => {
                    Swal.showLoading();
                }
            });

            $.ajax({
                url: 'export-all',
                type: 'GET',
                success: function(response) {
                    if (response) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Success',
                            text: response
                        });

                        // Trigger file download
                        window.open('download-zip', '_blank');
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: 'Export failed'
                        });
                    }
                },
                error: function(xhr) {
                    console.error('Export error:', xhr);
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: xhr.responseText || 'Export failed. Please try again.'
                    });
                }
            });
        });
    });
</script>