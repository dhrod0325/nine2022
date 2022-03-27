<?php

require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$mapId = 'map_balance';
$idKey = 'mapId';

$where = array();

if ( $_REQUEST['mapName'] ) {
	$where['mapName[~]'] = $_REQUEST['mapName'];
}

$pagination = new Pagination( array(
	'recordCountPerPage' => getArrayValue( $_REQUEST, 'recordCountPerPage', 8 )
) );

$totalCount = $linDb->count( $mapId, '*', $where );

$pagination->setTotalRecordCount( $totalCount );

$recordCountPerPage = $pagination->getRecordCountPerPage();
$firstRecordIndex   = $pagination->getFirstRecordIndex();

$where['LIMIT'] = [ $firstRecordIndex, $recordCountPerPage ];

$list = $linDb->select( $mapId, '*', $where );

$editItem = array();

$paramId = getArrayValue( $_REQUEST, 'pCode' );

if ( $paramId ) {
	for ( $i = 0; $i < count( $list ); $i ++ ) {
		if ( $paramId == $list[ $i ][ $idKey ] ) {
			$editItem = $list[ $i ];
			break;
		}
	}
}
?>
    <h2 class="page-title">맵 밸런스 관리</h2>
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
            <div class="col-lg-6">
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
                                <button class="btn btn-sm btn-danger" type="button"
                                        onclick="deleteItem('<?= $item['mapId'] ?>')">삭제
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
        <div class="col-lg-6">
            <div class="form-save">
                <form method="post" id="form">
                    <table class="table table-bordered">
                        <tbody>
                        <tr>
                            <th>맵아이디</th>
                            <td><?php input( 'mapId', $editItem['mapId'] ); ?></td>
                        </tr>
                        <tr>
                            <th>맵이름</th>
                            <td><?php input( 'mapName', $editItem['mapName'] ); ?></td>
                        </tr>
                        <tr>
                            <th>HP</th>
                            <td>
								<?php input( 'hpLeverage', $editItem['hpLeverage'] ); ?>
                            </td>
                        </tr>
                        <tr>
                            <th>MP</th>
                            <td><?php input( 'mpLeverage', $editItem['mpLeverage'] ); ?></td>
                        </tr>
                        <tr>
                            <th>근거리 명중</th>
                            <td><?php input( 'hitLeverage', $editItem['hitLeverage'] ); ?></td>
                        </tr>
                        <tr>
                            <th>근거리 대미지</th>
                            <td><?php input( 'dmgLeverage', $editItem['dmgLeverage'] ); ?></td>
                        </tr>
                        <tr>
                            <th>마법 적중</th>
                            <td><?php input( 'magicHitLeverage', $editItem['magicHitLeverage'] ); ?></td>
                        </tr>
                        <tr>
                            <th>마법 대미지</th>
                            <td><?php input( 'magicDmgLeverage', $editItem['magicDmgLeverage'] ); ?></td>
                        </tr>
                        </tbody>
                    </table>
                    <button type="submit" class="btn btn-primary">저장</button>
                    <button type="button" onclick="listPage(1);" class="btn btn-default">취소</button>
                    <button onclick="reloadMapBalance()" type="button" class="btn btn-primary">리로드 맵 밸런스</button>
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
            $('#pageNo').val(pageNo);
            $('#searchForm').submit();
        }

        function editItem(code) {
            $('#pCode').val(code);
            $('#searchForm').submit();
        }

        function deleteItem(code) {
            if (!confirm('정말로 삭제하시겠습니까? 복구 할 수 없습니다')) {
                return;
            }

            serverAction('delete', {
                key: 'mapId',
                value: code,
                tableName: 'map_balance'
            }, function () {
                location.reload();
            })
        }

        function reloadMapBalance() {
            var mapId = '<?= $editItem['mapId']?>';

            if (!(mapId >= 0) || mapId.length === 0) {
                alert('맵을 먼저 선택하셔야 합니다');
                return;
            }

            if (confirm('리로드 맵 밸런스 하시겠습니까?')) {
                callServerApi('리로드 맵밸런스 ' + mapId, function (response) {
                    if (response.result) {
                        alert('맵 밸런스 리로드가 완료되었습니다');
                    }
                });
            }
        }

        $(document).ready(function () {
            $('#form').submit(function (e) {
                e.preventDefault();

                const data = $(this).serializeArray();

                serverAction('updateMapBalance', data, function () {
                    location.reload();
                });
            });
        });
    </script>
<?php
getFooter();