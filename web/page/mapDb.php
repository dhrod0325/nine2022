<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$mapId = 'mapIds';
$idKey = 'mapid';

$paramId = getArrayValue( $_REQUEST, 'pCode' );

$where = array();

if ( $_REQUEST['locationname'] ) {
	$where['locationname[~]'] = $_REQUEST['locationname'];
}

if ( ! is_null( $_REQUEST['mapid'] ) && is_numeric( $_REQUEST['mapid'] ) ) {
	$where['mapid'] = $_REQUEST['mapid'];
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

if ( $paramId ) {
	for ( $i = 0; $i < count( $list ); $i ++ ) {
		if ( $paramId == $list[ $i ][ $idKey ] ) {
			$editItem = $list[ $i ];
			break;
		}
	}
}

$fields = getTableColumnNames( 'mapids' );
?>
    <h2 class="page-title">맵 디비 관리</h2>
    <hr>
    <div class="row">
        <form action="" method="get" id="searchForm">
            <div class="col-lg-12">

                <table class="table table-bordered">
                    <tr>
                        <th>맵아이디</th>
                        <td><?php input( 'mapid', $_REQUEST['mapid'] ); ?></td>
                        <th>맵이름</th>
                        <td><?php input( 'locationname', $_REQUEST['mapName'] ); ?></td>
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
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
					<?php foreach ( $list as $item ) : ?>
                        <tr>

                            <td><?= $item['mapid'] ?></td>
                            <td><?= $item['locationname'] ?></td>
                            <td>
                                <button class="btn btn-sm btn-primary" type="button"
                                        onclick="editItem('<?= $item['mapid'] ?>')">수정
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
                <p>
                    <button onclick="reloadMap()" type="button" class="btn btn-primary">리로드 맵</button>
                    <button onclick="reloadMonster()" type="button" class="btn btn-primary">리로드 몬스터</button>
                </p>
                <form method="post" id="form">
                    <div class="row">
						<?php
						foreach ( $fields as $field ) {
							printRowItem( $editItem, $field, 12 );
						}
						?>
                    </div>
                    <button type="submit" class="btn btn-primary">저장</button>
                    <button type="button" onclick="listPage(1);" class="btn btn-default">취소</button>
                </form>
            </div>
        </div>

    </div>
    <script>
        function listPage(pageNo) {
            $('#pageNo').val(pageNo);
            $('#searchForm').submit();
        }

        function editItem(code) {
            $('#pCode').val(code);
            $('#searchForm').submit();
        }

        function reloadMap() {
            if (confirm('리로드 맵 하시겠습니까?')) {
                callServerApi('리로드 맵', function (response) {
                    if (response.result) {
                        alert('맵 리로드가 완료되었습니다');
                    }
                });
            }
        }

        function reloadMonster() {
            var mapId = '<?= $editItem['mapId']?>';

            if (!(mapId >= 0)) {
                alert('맵을 먼저 선택하셔야 합니다');
                return;
            }

            if (confirm('리로드 몬스터 하시겠습니까?')) {
                callServerApi('리로드 몬스터 ' + mapId, function (response) {
                    if (response.result) {
                        alert('몬스터 리로드가 완료되었습니다');
                    }
                });
            }
        }

        $(document).ready(function () {
            $('#form').submit(function (e) {
                e.preventDefault();

                var data = $(this).serializeObject();

                $.post('/action/action.php', {
                    action: 'updateMap',
                    param: JSON.stringify(data)
                }, function (response) {
                    if (response.result) {
                        alert('저장되었습니다');
                    }
                });
            });
        });
    </script>
<?php
getFooter();