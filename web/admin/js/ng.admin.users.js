$(document).on('ready', function() {
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