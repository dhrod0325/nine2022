<!doctype html>
<html lang="ko" ng-app="app">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>LINAGE ADMIN</title>

    <script>
        const web_http_url = 'http://127.0.0.1:10017';
        const web_socket_url = 'ws://127.0.0.1:10019';

        function callServerApi(action, callback) {
            $.get(web_http_url + '?action=' + action, function (response) {
                const res = JSON.parse(response);
                callback(res);
            });
        }

        function serverAction(action, data, callback) {
            $.post('/action/action.php', {
                action: action,
                param: JSON.stringify(data)
            }, function (response) {
                if (response.result) {
                    alert('저장되었습니다');

                    if (callback) {
                        callback(response);
                    }
                }
            });
        }
    </script>
    <link rel="stylesheet" href="/resources/lib/fontawesome/css/all.min.css"/>
    <link rel="stylesheet" href="/resources/lib/bootstrap/bootstrap.css"/>

    <script src="/resources/js/jquery-3.6.0.min.js"></script>
    <script src="/resources/lib/bootstrap/bootstrap.js"></script>
    <script src="/resources/lib/angular.min.js"></script>
    <script src="/resources/lib/autobahn/autobahn.js"></script>
    <script src="/resources/js/app.js?ver=<?= VER ?>"></script>

    <link rel="stylesheet" href="/resources/css/style.css?ver=<?= VER ?>"/>
    <script src="/resources/js/script.js?ver=<?= VER ?>"></script>
</head>
<body class="main">