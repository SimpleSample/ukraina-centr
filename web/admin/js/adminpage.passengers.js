(function(){

    var printableTitle = '';
    $(document).ready(function() {
        TablesawUtils.bindCommonHandlers($('.history-table>table'), 'Ticket');

        $('#print-table').on('click', function() {
            printTablesaw($('.history-table>table'), printableTitle);
        });

        EventBus.addEventListener(TRIP_CHANGED_EVENT, function(ev, trip) {
            new Request('ticketsForTrip', {tripId: trip.id, excludeBlockedTickets: true}).send(function(data) {
                trip.tickets = data.tickets;

                TablesawUtils.renderTable($('.history-table>table>tbody'), trip.tickets, uc.adminTicketRecordTemplate);
            });

            printableTitle = trip.startDate + " " + trip.stringData;
        });
    });
})();

if (!uc) var uc = {};
window.uc.adminTicketRecordTemplate = function(ticket) {
    var note = ticket['note'] || '';
    return '<tr id="'+ticket['id']+'">' +
        '<td>'+ticket['agent']+'</td>' +
        '<td>'+ticket['passenger']+'</td>' +
        '<td>'+ticket['phones']+'</td>' +
        '<td class="ticket-trip">'+ticket['trip']+'</td>' +
        '<td class="ticket-seat">'+ticket['seat']+'</td>' +
        '<td>'+ticket['price']+'</td>' +
        '<td>'+note+'</td>' +
        '<td><span title="'+ticket['statusChangedDate']+'" class="ticket-status status-'+ticket.status+'">'+ticket.status+'</span></td>' +
        '<td><a class="action-glyph-link" href="#"><span class="glyphicon glyphicon-trash" title="Видалити" aria-hidden="true"></span></a></td>' +
        '</tr>';
};
