<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();
?>
    <h2 class="page-title">채팅</h2>
    <hr>
    <div ng-controller="ChattingController">
        <p class="text-right" style="display: none">
            <button class="btn btn-primary" ng-click="toggleAutoScroll()">자동 스크롤 {{ useAutoScroll ? '끄기' : '켜기'}}
            </button>
        </p>

        <div class="chattingBox" id="chattingBox">
            <ul>
                <li ng-repeat="message in messages track by $index">
                    <p>{{message.name }} : {{message.text}}</p>
                </li>
            </ul>
        </div>
        <form style="margin-top:10px;" ng-submit="sendMessage($event)">
            <div class="row">
                <div class="col-lg-12">
                    <div class="input-group input-group-lg">
                        <input type="text" class="form-control input-lg" id="search-church"
                               placeholder="메세지를 입력하세요" ng-model="msg">
                        <span class="input-group-btn">
                        <button class="btn btn-default " type="submit">전송</button>
                    </span>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <script>
        controllers.controller('ChattingController', function ($scope, $interval, $timeout) {
            $scope.useAutoScroll = true;

            function scrollBottom() {
                $timeout(function () {
                    var $chattingBox = $('#chattingBox');
                    var diff = $chattingBox[0].scrollHeight - $chattingBox.scrollTop();

                    if (diff < 500) {
                        $chattingBox.scrollTop($chattingBox[0].scrollHeight);
                    }
                }, 50);
            }

            $scope.sendMessage = function (e) {
                e.currentTarget.reset();

                callServerApi('전체멘트 ' + $scope.msg);
            };

            $scope.messages = getMessages();

            const conn = new ab.Session(web_socket_url,
                function () {
                    console.log('WebSocket connection');

                    conn.subscribe('chat', function (topic, data) {
                        $scope.messages = getMessages();
                        
                        if (typeof $scope.messages.push == 'function') {
                            $scope.messages.push(data);
                            $scope.$apply();

                            scrollBottom();
                        }
                    });
                },
                function () {
                    console.warn('WebSocket connection closed');
                }
            );

            scrollBottom();
        });
    </script>
<?php
getFooter();