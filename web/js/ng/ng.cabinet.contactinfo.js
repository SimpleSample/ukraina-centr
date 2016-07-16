
(function() {

    $(document).on('ready', function () {
        var contactInfoPromise = new Promise(function(resolve, reject) {
            new Request('getUser').send(resolve, reject);
        });
        var contactInfoBlock = $('.contact-info-block');

        var editContactData = $('#edit-contact-data');
        var saveContactData = $('#save-contact-data');
        var cancelContactData = $('#cancel-contact-data');

        var readonlyFields = contactInfoBlock.find('.area-value');
        var editableFields = contactInfoBlock.find('.area-value-edit');

        contactInfoPromise.then(function(user) {
            var $viewNameField = $('#username-field');
            var $editNameField = $('#username-edit-field');
            var $viewSurnameField = $('#surname-field');
            var $editSurnameField = $('#surname-edit-field');
            var $viewLanguageField = $('#language-field');
            var $editLanguageField = $('#language-edit-field');
            var $viewEmailField = $('#email-field');
            // var $editEmailField = $('#email-edit-field');
            var $viewPhoneField = $('#phone-field');
            var $editPhoneField = $('#phone-edit-field');

            $viewNameField.text(user.name);
            $editNameField.val(user.name);
            $viewSurnameField.text(user.surname);
            $editSurnameField.val(user.surname);
            $editLanguageField.val(user.language);
            $viewLanguageField.text($editLanguageField.find(':selected').text());
            $viewEmailField.text(user.email);
            // $editEmailField.val(user.email);
            $viewPhoneField.text(user.phone);
            $editPhoneField.val(user.phone);

            editContactData.click(function() {
                if (readonlyFields.hasClass('hidden')) {
                    return false;
                }

                readonlyFields.addClass('hidden');
                editableFields.removeClass('hidden');

                saveContactData.removeClass('hidden');
                cancelContactData.removeClass('hidden');
                editContactData.addClass('hidden');
            });
            var contactDataForm = $('#contact-data-form');
            contactDataForm.submit(function(event) {
                event.stopPropagation();
                if (editableFields.hasClass('hidden')) {
                    return;
                }

                var form = this;
                if (!form.checkValidity()) {
                    return;
                }

                user.name = $editNameField.val();
                user.surname = $editSurnameField.val();
                // user.email = $editEmailField.val();
                user.phone = $editPhoneField.val();
                user.language = $editLanguageField.val();

                new Request('updateUser', user).send(function() {
                    $viewNameField.text(user.name);
                    $viewSurnameField.text(user.surname);
                    $viewLanguageField.text($editLanguageField.find(':selected').text());
                    $viewEmailField.text(user.email);
                    $viewPhoneField.text(user.phone);

                    saveContactData.addClass('hidden');
                    cancelContactData.addClass('hidden');
                    editContactData.removeClass('hidden');

                    editableFields.addClass('hidden');
                    readonlyFields.removeClass('hidden');

                    updateCookie(user.name, user.surname, user.phone);
                });

                event.preventDefault();
                return false;
            });

            cancelContactData.click(function() {
                if (editableFields.hasClass('hidden')) {
                    return false;
                }
                $editNameField.val(user.name);
                $editSurnameField.val(user.surname);
                $editLanguageField.val(user.language);
                // $editEmailField.val(user.email);
                $editPhoneField.val(user.phone);

                saveContactData.addClass('hidden');
                cancelContactData.addClass('hidden');
                editContactData.removeClass('hidden');

                editableFields.addClass('hidden');
                readonlyFields.removeClass('hidden')
            });
        });
    });
})();