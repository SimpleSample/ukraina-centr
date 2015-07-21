function printElement(elementHtml, title) {
    var mywindow = window.open('', title, 'height=400,width=600');
    mywindow.document.write('<!DOCTYPE html><html><head><title>'+title+'</title>');
    mywindow.document.write('<link rel="stylesheet" href="../admin/css/ng.admin.print.css" type="text/css" >');
    mywindow.document.write('</head><body >');
    mywindow.document.write(elementHtml);
    mywindow.document.write('</body></html>');

    mywindow.document.close(); // necessary for IE >= 10
    mywindow.focus(); // necessary for IE >= 10

    setTimeout(function(){
        mywindow.print();
        //mywindow.close();
    }, 1000);

    return true;
}

window.printTablesaw = function ($table, title) {
    var $tableElement = $('<div><table><thead><tr></tr></thead><tbody></tbody></table></div>');
    var $headerContainer = $tableElement.find('tr');

    // copy headers
    var headersCount = 0;
    var headers = $table.find('th');
    var excludedColumnsArray = [];
    for (var size = headers.length; headersCount < size; headersCount++) {
        var $header = $(headers[headersCount]);
        if ($header.data('print-exclude') === '') {
            excludedColumnsArray.push(headersCount);
            continue;
        }
        var headerName = '';
        var $sortableButton = $header.find('button');
        if ($sortableButton.length) {
            headerName = $sortableButton.text();
        } else {
            headerName = $header.text();
        }
        $headerContainer.append('<th>'+headerName+'</th>');
    }

    var $rowsContainer = $tableElement.find('tbody');
    var $rows = $table.find('tbody>tr');
    for (var i = 0, size = $rows.length; i < size; i++) {
        var $printRow = $('<tr></tr>');
        var $cells = $($rows[i]).find('td');
        for (var j = 0, cellSize = $cells.length; j < cellSize; j++) {
            if (excludedColumnsArray.indexOf(j) != -1) {
                continue;
            }
            var $cell = $($cells[j]);
            var cellText = '';
            var cellContent = $cell.find('.tablesaw-cell-content');
            if (cellContent.length) {
                cellText = cellContent.text();
            } else {
                cellText = $cell.text();
            }
            $printRow.append('<td>'+cellText+'</td>');
        }
        $rowsContainer.append($printRow);
    }
    printElement($tableElement.html(), title);
};