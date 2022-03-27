<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$pageNo      = getArrayValue( $_REQUEST, 'pageNo' );
$searchNpcId = getArrayValue( $_REQUEST, 'npcid' );
$searchName  = getArrayValue( $_REQUEST, 'name' );
$pageType    = getArrayValue( $_REQUEST, 'pageType', 'info' );

$tableName = 'npc';

$where = array();

if ( $searchNpcId ) {
	$where['npcid'] = $searchNpcId;
}
if ( $searchName ) {
	$where['name[~]'] = $searchName;
}

$pagination = new Pagination( array(
	'recordCountPerPage' => getArrayValue( $_REQUEST, 'recordCountPerPage', 20 )
) );

$totalCount = $linDb->count( $tableName, '*', $where );

$pagination->setTotalRecordCount( $totalCount );

$recordCountPerPage = $pagination->getRecordCountPerPage();
$firstRecordIndex   = $pagination->getFirstRecordIndex();

$where['LIMIT'] = [ $firstRecordIndex, $recordCountPerPage ];

$list = $linDb->select( $tableName, '*', $where );

$editItem = array();

$pCode = getArrayValue( $_REQUEST, 'pCode' );

if ( $pCode ) {
	$editItem = $linDb->select( $tableName, '*', [ 'npcid' => $pCode ] )[0];
}

?>
    <h2 class="page-title">엔피씨</h2>
    <hr>
    <div class="row">
        <div class="col-md-4">
            <form action="" method="get" id="searchForm">
                <table class="table table-bordered">
                    <tr>
                        <th>npcid</th>
                        <td><?php input( 'npcid', $searchNpcId ); ?></td>
                    </tr>
                    <tr>
                        <th>name</th>
                        <td><?php input( 'name', $searchName ); ?></td>
                    </tr>
                </table>

                <div class="search-btn text-right">
                    <button type="submit" class="btn btn-primary" id="searchButton" onclick="search()">검색</button>
                </div>

				<?php printPaginationInfo( $pagination ); ?>

                <table class="table table-bordered table-hover table-hover-hr">
                    <thead>
                    <tr>
                        <th>npcId</th>
                        <th>name</th>
                    </tr>
                    </thead>
                    <tbody>
					<?php foreach ( $list as $item ): ?>
                        <tr onclick="editItem('<?= $item['npcid'] ?>')">
                            <td><?= $item['npcid'] ?></td>
                            <td><?= $item['name'] ?></td>
                        </tr>
					<?php endforeach; ?>
                    </tbody>
                </table>

				<?php printPagination( $pagination, 'listPage' ); ?>

                <input type="hidden" name="pageType" value="<?= $pageType ?>" id="pageType">
                <input type="hidden" name="pageNo" id="pageNo" value="<?= $pagination->getPageNo() ?>">
                <input type="hidden" name="pCode" id="pCode">
            </form>
        </div>
        <div class="col-md-8">
            <ul class="nav nav-tabs">
                <li class="<?= $pageType == 'info' ? 'active' : '' ?>"><a href="#" onclick="pageType('info')">정보관리</a>
                </li>
                <li class="<?= $pageType == 'shop' ? 'active' : '' ?>"><a href="#" onclick="pageType('shop')">상점관리</a>
                </li>
                <li class="<?= $pageType == 'material' ? 'active' : '' ?>"><a href="#" onclick="pageType('material')">제작관리</a>
                </li>
            </ul>

            <div id="tabs-content">
				<?php
				if ( $pageType == 'info' ) {
					require_once dirname( __FILE__ ) . '/npcInfo.php';
				} else if ( $pageType == 'shop' ) {
					require_once dirname( __FILE__ ) . '/npcShop.php';
				}else if($pageType == 'material'){
					require_once dirname( __FILE__ ) . '/npcMaterial.php';
                }
				?>
            </div>
        </div>
    </div>
    <script>
        function pageType(pageType) {
            $('#pageType').val(pageType);
            $('#searchForm').submit();
        }

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

        function cancelItem() {
            $('#pCode').val('');
            $('#searchForm').submit();
        }

        function deleteItem(value) {
            if (!confirm('정말로 삭제하시겠습니까? 복구 할 수 없습니다')) {
                return;
            }

            serverAction('deleteBy', {
                key: {
                    id: value
                },
                tableName: 'board'
            }, function () {
                location.reload();
            })
        }

        $(document).ready(function () {
            $('#editForm').submit(function (e) {
                e.preventDefault();

                const data = $(this).serializeArray();

                serverAction('updateBoard', data, function (resposne) {
                    location.reload();
                });
            });
        });
    </script>

<?php
getFooter();