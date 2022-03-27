<?php

require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$tableName = 'map_event_drop';

$where = array();

$where['GROUP'] = [ 'mapId', 'mapName' ];

if ( $_REQUEST['mapName'] ) {
	$where['mapName[~]'] = $_REQUEST['mapName'];
}

if ( $_REQUEST['mapId'] ) {
	$where['mapId'] = $_REQUEST['mapId'];
}

$pagination = new Pagination( array(
	'recordCountPerPage' => getArrayValue( $_REQUEST, 'recordCountPerPage', 8 )
) );

$totalCount = $linDb->count( $tableName, '*', $where );

$pagination->setTotalRecordCount( $totalCount );

$recordCountPerPage = $pagination->getRecordCountPerPage();
$firstRecordIndex   = $pagination->getFirstRecordIndex();

$where['LIMIT'] = [ $firstRecordIndex, $recordCountPerPage + 1 ];

$list = $linDb->select( $tableName, '*', $where );

$editItem     = array();
$editItemList = array();

$paramId = getArrayValue( $_REQUEST, 'pCode' );

if ( $paramId ) {
	$editItemList = $linDb->select( $tableName, '*', array( 'mapId' => $paramId ) );
}
?>
    <h2 class="page-title">맵 드랍 관리</h2>
    <hr>
    <div class="row">
        <form action="" method="get" id="searchForm">
            <div class="col-lg-12">
                <table class="table table-bordered">
                    <tr>
                        <th>맵아이디</th>
                        <td><?php input( 'mapId', $_REQUEST['mapId'] ); ?></td>
                        <th>맵이름</th>
                        <td><?php input( 'mapName', $_REQUEST['mapName'] ); ?></td>
                    </tr>
                </table>

                <div class="search-btn text-right">
                    <button type="submit" class="btn btn-primary" id="searchButton" onclick="search()">검색</button>
                </div>
            </div>
            <div class="col-lg-4">
				<?php printPaginationInfo( $pagination ); ?>

                <table class="table table-bordered table-center">
                    <thead>
                    <tr>
                        <th>맵아이디</th>
                        <th>맵이름</th>
                        <th style="width:130px;"></th>
                    </tr>
                    </thead>
                    <tbody>
					<?php foreach ( $list as $item ) : ?>
                        <tr>

                            <td><?= $item['mapId'] ?></td>
                            <td><?= $item['mapName'] ?></td>
                            <td>
                                <button class="btn btn-sm btn-primary" type="button"
                                        onclick="editItem('<?= $item['mapId'] ?>')">수정
                                </button>
                            </td>
                        </tr>
					<?php endforeach; ?>
                    </tbody>
                </table>

				<?php printPagination( $pagination, 'listPage' ); ?>

                <input type="hidden" name="pageNo" id="pageNo" value="<?= $pagination->getPageNo() ?>">
                <input type="hidden" name="pCode" id="pCode">
            </div>
        </form>
        <div class="col-lg-8">
            <div class="form-save">
                <form method="post" id="form">
                    <table class="table table-bordered table-center">
                        <thead>
                        <tr>
                            <th>itemId</th>
                            <th>itemName</th>
                            <th style="width:50px;">min</th>
                            <th style="width:50px;">max</th>
                            <th>chance</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>

						<?php foreach ( $editItemList as $editItem ) : ?>
                            <tr>
                                <td><?php input( 'itemId', $editItem['itemId'] ); ?></td>
                                <td><?php input( 'itemName', $editItem['itemName'] ); ?></td>
                                <td><?php input( 'min', $editItem['min'] ); ?></td>
                                <td><?php input( 'max', $editItem['max'] ); ?></td>
                                <td>
									<?php input( 'chance', $editItem['chance'] ); ?>
                                    <input type="hidden" name="mapId" value="<?= $editItem['mapId'] ?>">
                                </td>
                                <td>
                                    <button type="button" class="btn btn-danger btn-sm"
                                            onclick="deleteItem('<?= $editItem['itemId'] ?>')">삭제
                                    </button>
                                </td>
                            </tr>
						<?php endforeach; ?>

                        </tbody>
                    </table>
                    <button type="submit" class="btn btn-primary">저장</button>
                    <button type="button" onclick="listPage(1);" class="btn btn-default">취소</button>
                    <button type="button" onclick="addItem()" class="btn btn-danger">추가</button>
                    <button onclick="reloadMapBalance()" type="button" class="btn btn-primary">리로드 맵 드랍</button>
                </form>
            </div>
        </div>
    </div>

    <script>
        function search() {
            $('#pageNo').val(1);
            $('#searchForm').submit();
        }

        function listPage(pageNo) {
            $('#mapId').val('');
            $('#pageNo').val(pageNo);
            $('#searchForm').submit();
        }

        function editItem(code) {
            $('#pCode').val(code);
            $('#searchForm').submit();
        }

        function deleteItem(value) {
            if (!confirm('정말로 삭제하시겠습니까? 복구 할 수 없습니다')) {
                return;
            }

            const mapId = '<?= $editItem['mapId']?>';

            serverAction('deleteBy', {
                key: {
                    itemId: value,
                    mapId: mapId,
                },
                tableName: 'map_event_drop'
            }, function () {
                location.reload();
            })
        }

        function reloadMapBalance() {
            var mapId = '<?= $editItem['mapId']?>';

            if (!(mapId >= 0)) {
                alert('맵을 먼저 선택하셔야 합니다');
                return;
            }

            if (confirm('리로드 맵드랍 하시겠습니까?')) {
                callServerApi('리로드 맵이벤트드랍 ' + mapId, function (response) {
                    if (response.result) {
                        alert('맵드랍 리로드가 완료되었습니다');
                    }
                });
            }
        }

        function addItem() {
            window.open('/page/popup/searchItem.php', 'searchItem', 'width=600,height=800');
        }

        function searchItemCallBack(item) {
            const mapId = '<?= $editItem['mapId']?>';
            const mapName = '<?= $editItem['mapName']?>';

            if (!(mapId >= 0)) {
                alert('맵을 먼저 선택하셔야 합니다');
                return;
            }

            serverAction('insertMapDropItem', {
                mapId: mapId,
                mapName: mapName,
                itemId: item.itemId,
                itemName: item.itemName
            }, function () {
                location.reload();
            });
        }

        $(document).ready(function () {
            $('#form').submit(function (e) {
                e.preventDefault();

                const data = $(this).serializeTable();

                serverAction('updateMapEventDrop', data, function () {
                    location.reload();
                });
            });
        });
    </script>
<?php
getFooter();