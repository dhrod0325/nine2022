var app = angular.module('app', [
    'controllers'
]);

app.config(function ($httpProvider) {
    $httpProvider.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded;charset=utf-8";
    $httpProvider.defaults.transformRequest.unshift(function (data, headersGetter) {
        var key, result = [];
        for (key in data) {
            if (data.hasOwnProperty(key)) {
                result.push(encodeURIComponent(key) + "=" + encodeURIComponent(data[key]));
            }
        }
        return result.join("&");
    });

    $httpProvider.interceptors.push(function ($q) {
        return {
            'request': function (config) {
                return $q.when(config);
            }, 'response': function (response) {
                return $q.when(response);
            }, 'responseError': function (rejection) {
                return $q.reject(rejection);
            }
        };
    });
});

var controllers = angular.module('controllers', []);