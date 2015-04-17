
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
                var transactionId = ticket['transactionId'];
                if (transactionId != '') {
                    transactionId = '20€ ('+transactionId+')';
                }
                var item = $('<tr id="'+ticket['id']+'"><td><a class="remove-ticket" href="#"><img class="delete"></a></td><td>'+ticket['trip']+'</td>' + '<td>'+ticket['phones']+'</td>' +'<td>'+ticket['passenger']+'</td>' +'<td>'+ticket['seat']+'</td><td>'+ticket['transactionId']+'</td><td></td></tr>');
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

    var $tripsSearch = $('.forms.show-trips');
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
            new Popup('Підтвердження', '<div>Користувач успішно створений</div>', 'white').show();
        });
    });

});