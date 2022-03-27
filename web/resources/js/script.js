(function ($) {
    $.fn.serializeObject = function () {
        "use strict"
        var result = {}
        var extend = function (i, element) {
            var node = result[element.name]
            if ("undefined" !== typeof node && node !== null) {
                if ($.isArray(node)) {
                    node.push(element.value)
                } else {
                    result[element.name] = [node, element.value]
                }
            } else {
                result[element.name] = element.value
            }
        }

        $.each(this.serializeArray(), extend)
        return result
    }

    $.fn.serializeTable = function () {
        "use strict"

        var result = [];

        var extend = function (key, element) {
            $.each(element, function (i, v) {
                var object = result[i];

                if ("undefined" !== typeof object && object !== null) {
                    object[key] = v;
                } else {
                    result[i] = {};
                    result[i][key] = v;
                }
            });
        }

        $.each(this.serializeObject(), extend);

        return result;
    }
})(jQuery);

function closePopup() {
    window.close();
}

var messageStorageKey = 'ChattingControllerMessages';

function getMessages() {
    if (typeof (localStorage) !== "undefined") {
        const messages = localStorage.getItem(messageStorageKey);
        return messages ? JSON.parse(messages) : [];
    } else {
        return [];
    }
}

const chatConn = new ab.Session(web_socket_url,
    function () {
        chatConn.subscribe('chat', function (topic, data) {
            if (topic === 'chat') {
                const msg = getMessages();
                msg.push(data);

                if (typeof (localStorage) !== "undefined") {
                    localStorage.setItem(messageStorageKey, JSON.stringify(msg));
                }
            }
        });
    }
);