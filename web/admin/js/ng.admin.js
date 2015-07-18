
$(document).on('ready', function() {
    var $ticketsTable = $('#tickets');
    var $printResults = $('#print-results');
    $('#search-tickets').on('click', function(event) {
        event.preventDefault();
        var $trips = $('#trips');
        var tripId = $trips.find(':selected').attr('id');
        if (!tripId) return;

        new Request('ticketsForTrip', {tripId: tripId}).send(function(data) {
            $ticketsTable.empty();

            if (!data['tickets'] || data['tickets'].length === 0) {
                $ticketsTable.append($('<tr><td>Жодного квитка на дану поїздку не заброньовано</td></tr>'));
                $printResults.hide();
                return;
            }
            var tickets = data['tickets'];
            for (var i = 0, size = tickets.length; i < size; i++) {
                var ticket = tickets[i];
                var paymentInfo = ticket['price'] || '';
                if (paymentInfo) paymentInfo += '€';
                var transactionId = ticket['transactionId'];
                if (transactionId) {
                    paymentInfo += ' ('+transactionId+')';
                }
                var note = ticket['note']? ticket['note'] : '';
                var item = $('<tr id="'+ticket['id']+'"><td><a class="remove-ticket" href="#"><img class="delete" src="img/delete.jpeg" width="24" height="24" border="0" alt="Delete"></a></td><td>'+ticket['trip']+'</td>' + '<td>'+ticket['phones']+'</td>' +'<td>'+ticket['passenger']+'</td>' +'<td>'+ticket['seat']+'</td><td>'+paymentInfo+'</td><td>'+note+'</td></tr>');
                $ticketsTable.append(item);
            }
            $printResults.show();
        });
    });

    $ticketsTable.on('click', '.remove-ticket', function(event){
        event.preventDefault();
        var $tr = $(this).parents('tr');
        var ticketId = $tr.attr('id');
        new Request('removeTicket', {ticketId: ticketId}).send(function(event){
            $tr.remove();
        });
        return false;
    });

    $printResults.on('click', function(event){
        window.print();
    });

    $('#save-user').on('click', function(event) {
        var data = {};
        $('.forms.add-user input').each(function(index, element){
            data[element.name] = element.value;
        });

        data['role'] = $('.forms.add-user').find('#user-roles').find(':selected').attr('data-role');
        data['lang'] = $('.forms.add-user').find('#user-langs').find(':selected').attr('data-role');

        new Request('addUser', data).send(function(data){
            new Popup('Підтвердження', '<div>Користувач успішно створений</div>', '').show();
        });
    });

});