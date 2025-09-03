$(document).ready(function () {
    // Set up a click event handler for all tabs
    $('.nav-tabs a').on('click', function () {
        var sheetName = $(this).attr('id').replace('-tab', ''); // Extract sheet name from tab id
        loadSheetData(sheetName);
    });
});

// Function to load data based on sheetName (tab clicked)
function loadSheetData(sheetName) {
    $.ajax({
        url: 'getSfiSheetData', // Your backend endpoint
        type: 'GET',
        dataType: 'json',
        data: { sheetName: sheetName }, // Pass the sheetName to backend
        success: function (data) {
            populateSheetTable(sheetName, data); // Populate the respective table
        },
        error: function (error) {
            console.error('Error fetching data for ' + sheetName + ':', error);
        }
    });
}

// Function to dynamically populate table based on sheetName
function populateSheetTable(sheetName, data) {
    // Clear existing table content (headers and body)
    $('#' + sheetName + 'TableHead').empty();
    $('#' + sheetName + 'TableBody').empty();

    // Check if data exists and has length > 0
    if (data && data.length > 0) {
        var headers = Object.keys(data[0]); // Get column headers from first row

        // Populate table headers dynamically
        headers.forEach(function (header) {
            $('#' + sheetName + 'TableHead').append('<th>' + header + '</th>');
        });

        // Populate table rows with data
        data.forEach(function (row) {
            var rowHtml = '<tr>';
            headers.forEach(function (header) {
                rowHtml += '<td>' + row[header] + '</td>';
            });
            rowHtml += '</tr>';
            $('#' + sheetName + 'TableBody').append(rowHtml);
        });
    } else {
        // If no data available, show a placeholder row
        $('#' + sheetName + 'TableBody').append('<tr><td colspan="5">No data available</td></tr>');
    }
}
