(function(){
    window.uc = {};
    var clientBundle = window.clientBundle;

    location.getParam = function (name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results == null ? null : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

    $(document).on('ready', function() {

        $('.navbar-header .navbar-toggle').on('click tap', function(e){
            $('#navbar').animate({height: 'toggle'}, 300);
        });

        EventBus.addEventListener('user_authorized', function(event) {
            var user = cookies.get('name');
            $('#cabinet').text(user);
            showUpperRightMenu(true);
        });

        EventBus.addEventListener('user_loggedout', function(event) {
            cookies.removeAll();
            showUpperRightMenu(false);
        });
        if (window.isAuthorized()) {
            EventBus.dispatch('user_authorized');
        } else {
            showUpperRightMenu(false);
        }
        if (window.cookies.get("pass")) {
            var data = {email: window.cookies.get("email"),
                password: window.cookies.get("pass"),
                auto: true};
            new Request("login", data).send(function(data){
                showCurrencyWidget(data['EURUAH']);
            }, function() {}); // quietly fails if server error arises
        } else {
            var req = new Request('welcome');
            req.setShowLoading(false);
            req.send(function(data) {
                showCurrencyWidget(data['EURUAH']);
            }, function() {}); // quietly fails if server error arises
        }
        // setting up content height
        var $content = $('.page-content');
        if ($content[0]) {
            var minHeight = $( window ).height() - $('footer').outerHeight() - $('header').outerHeight()-($content.outerHeight() - $content.height());
            if ($('body').hasClass('content-fixed')) {
                $content.css('height', minHeight + 'px');
            } else {
                $content.css('min-height', minHeight + 'px');
            }
        }
    });

    function showCurrencyWidget(rate) {
        window.EURUAH = rate? rate : 26.15;
        $('#currency-container').append(uc.currencyWidget(EURUAH));
    }

    function showUpperRightMenu(isLoggedIn) {
        function showAndHide(show1, bind1, show2, bind2, hide1, unbind1, hide2, unbind2){
            show1.show();
            show2.show();
            if (hide1.css('display') !== 'none') {
                hide1.hide();
                hide1.off('click');
                hide2.hide();
                hide2.off('click');
            }
            show1.on('click', function(event){event.preventDefault(); bind1(); return false});
            show2.on('click', function(event){event.preventDefault(); bind2(); return false});
        }
        var $cabinet = $('#cabinet');
        var $logout = $('#logout');
        var $login = $('#login');
        var $register = $('#register');
        if (isLoggedIn) {
            showAndHide($cabinet, cabinet, $logout, logout, $login, login, $register, register);
        } else {
            showAndHide($login, login, $register, register, $cabinet, cabinet, $logout, logout);
        }
    }

    function cabinet() {
        window.location.assign(location.origin + '/cabinet');
    }

    window.isAuthorized = function() {
        return window.cookies.get("email");
    };

    window.register = function(callback) {
        var content = window.uc.registerTemplate;
        var popup = new Popup(clientBundle.registration, content, '', function(popupId) { //TODO localize
            var captchaWidgetId = grecaptcha.render(
                'captcha-form',
                {sitekey:"6LfUKfQSAAAAAN_RtEDkPQnIn9sT5YS0Kp9yK__1", theme: 'light'}
            );

            $(popupId + ' #btn-register').on('click', function(event){
                event.preventDefault();
                var data = {};
                $(popupId + ' .forms input').each(function(index, element){
                    data[element.name] = element.value;
                });
                var errors = '';
                var $forms = $(popupId + ' .forms');
                var name = $forms.find('#reg-username').val();
                var surname = $forms.find('#reg-surname').val();
                var email = $forms.find('#reg-email').val();
                var phone = $forms.find('#reg-phone').val();
                var pass = $forms.find('#reg-pass').val();

                if (name === '') errors += clientBundle.name_field_should_not_be_empty+'<br>';
                if (surname === '') errors += clientBundle.surname_field_should_not_be_empty+'<br>';
                var isValidEmail = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(email);
                if (!isValidEmail) errors += clientBundle.wrong_email_format+'<br>';
                var isPhoneValid = /^[\s()+-]*([0-9][\s()+-]*){6,20}$/.test(phone);
                if (!isPhoneValid) errors += clientBundle.wrong_phone_format+'<br>';
                var isPassValid = /^[0-9a-zA-Z]{8,}$/.test(pass);
                if (!isPassValid) errors += clientBundle.password_should_be_at_least_8_symbols_long +'<br>';

                if (errors !== '') {
                    new Message(errors, 10000);
                    return false;
                }

                data['captcha'] = grecaptcha.getResponse(captchaWidgetId);

                var req = new Request('reg', data);
                req.send(function(data){
                    popup.destroy();
                    onAuthorized(data);
                    if (callback) callback();
                });
                return false;
            });
        });
        popup.show();
    };

    window.login = function(callback) {
        var content = window.uc.loginTemplate;
        var popup = new Popup(clientBundle.login, content, '', function(popupId) { //TODO localize
            $(popupId + ' #btn-login').on('click', function(event){
                event.preventDefault();
                var data = {};
                $(popupId + ' .forms input').each(function(index, element){
                    data[element.name] = element.value;
                });

                var req = new Request('login', data);
                req.send(function(data){
                    popup.destroy();
                    onAuthorized(data);
                    if (callback) callback();
                });
                return false;
            });

            $(popupId + ' #or-register-link').click(function(e){
                e.preventDefault();
                popup.destroy();
                setTimeout(register, 100);
                return false;
            });

            $(popupId + ' #forgot-pass-link').click(function(e) {
                e.preventDefault();
                popup.destroy();
                renewPassword();
                return false;
            });
        });
        popup.show();
    };

    window.renewPassword = function() {
        var content = window.uc.renewPassTemplate;
        var popup = new Popup(clientBundle.password_renewal, content, '', function(popupId) {
            $(popupId + ' #btn-renew-pass').click(function(event){
                event.preventDefault();
                var email = $(popupId).find('#form-email').val();
                var isValidEmail = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(email);
                if (!isValidEmail) {
                    new Message(clientBundle.wrong_email_format, 5000);
                    return false;
                }
                var data = {email: email};
                var req = new Request('renewPass', data);
                req.send(function(data){
                    popup.destroy();
                    new Popup(clientBundle.password_was_generated,
                        '<div>'+clientBundle.generated_password_was_sent_to_your_email_Please_use_it_in_order_to_have_access_to_the_service+'</div>')
                        .show();
                });
                return false;
            });
        });
        popup.show();
    };

    function onAuthorized(data) {
        document.cookie = "user=" + data['name'];
        document.cookie = "name=" + data['name'] + ' ' + data['surname'];
        document.cookie = "pass=" + data['pass'];
        document.cookie = "email=" + data['email'];
        document.cookie = "phone=" + data['phone'];
        EventBus.dispatch('user_authorized');
    }

    window.logout = function(callback) {
        var req = new Request('logout');
        req.send(function(data){
            EventBus.dispatch('user_loggedout');
            if (callback) callback();
        });
    };
})();


/*HTML Templates*/
(function(){
    window.uc.registerTemplate =
        '<div class="forms">' +
        '<label><input type="text" id="reg-username" name="username" placeholder="'+clientBundle.name+'"></label>' +
        '<label><input type="text" id="reg-surname" name="surname" placeholder="'+clientBundle.surname+'"></label>' +
        '<label><input type="email" id="reg-email" name="email" placeholder="Email"></label>' +
        '<label><input type="tel" id="reg-phone" name="phone" placeholder="'+clientBundle.phone+'"></label>' +
        '<label><input type="password" id="reg-pass" name="password" placeholder="'+clientBundle.password+'"></label>' +
        '<div id="captcha-form"></div>' +
        '<div class="clear-both"><button id="btn-register" class="btn btn-right btn-green">'+clientBundle.register+'</button></div>' + //TODO localize
        '</div>';

    window.uc.loginTemplate =
        '<div class="forms">' +
        '<label><input type="email" name="email" placeholder="Email"></label>' +
        '<label><input type="password" name="password" placeholder="'+clientBundle.password+'"></label>' +
        '<div class="forgot-pass"><a id="forgot-pass-link" href="#">'+clientBundle.forgot_your_password+'</a></div>'+
        '<div class="clear-both">' +
        '<button id="btn-login" class="btn btn-left btn-green">'+clientBundle.login1+'</button>' +
        '<div class="or-register"><span class="or-key">'+clientBundle.or+'</span><a id="or-register-link" href="#">'+clientBundle.register+'</a></div>' +
        '</div>' +
        '</div>';

    window.uc.renewPassTemplate =
        '<div class="forms">' +
        '<label><input type="email" id="form-email" name="email" placeholder="Email"></label>' +
        '<div class="clear-both">' +
        '<button id="btn-renew-pass" class="btn btn-left btn-green">'+clientBundle.renew+'</button>' +
        '</div>' +
        '</div>';

    window.uc.currencyWidget = function(eurUah){
        return '<div id="currency"><table><tbody><tr><td>'+clientBundle.exchange_rate+':</td><td>1€ -</td><td id="EUR-UAH">'+eurUah+'₴</td></tr><tr></tr></tbody></table></div>';
    }
})();