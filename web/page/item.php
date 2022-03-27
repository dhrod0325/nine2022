<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$table = 'all_item';

$paramId = getArrayValue( $_REQUEST, 'pCode' );

$where = array();

if ( $_REQUEST['name'] ) {
	$where['name[~]'] = $_REQUEST['name'];
}

if ( ! is_null( $_REQUEST['item_id'] ) && is_numeric( $_REQUEST['item_id'] ) ) {
	$where['item_id'] = $_REQUEST['item_id'];
}

$pagination = new Pagination( array(
	'recordCountPerPage' => getArrayValue( $_REQUEST, 'recordCountPerPage', 8 )
) );

$selectItems = selectItems( $pagination );

$list = $selectItems['list'];

$editItem = array();

if ( $paramId ) {
	$k = array( 'item_id' => $paramId );

	$search = $linDb->select( 'weapon', '*', $k );

	if ( empty( $search ) ) {
		$search = $linDb->select( 'armor', '*', $k );

		if ( empty( $search ) ) {
			$search = $linDb->select( 'etcitem', '*', $k );
		}
	}

	if ( $search ) {
		$editItem = $search[0];
	}
}

$fields = getTableColumnNames( 'weapon' );
?>
    <h2 class="page-title">아이템 관리</h2>
    <hr>
    <div class="row">
        <form action="" method="get" id="searchForm">
            <div class="col-lg-12">
                <table class="table table-bordered">
                    <tr>
                        <th>itemId</th>
                        <td><?php input( 'item_id', $_REQUEST['item_id'] ); ?></td>
                        <th>name</th>
                        <td><?php input( 'name', $_REQUEST['name'] ); ?></td>
                    </tr>
                </table>

                <div class="search-btn text-right">
                    <button type="submit" class="btn btn-primary" id="searchButton" onclick="search()">검색</button>
                </div>
            </div>

            <div class="col-lg-3">
	            <?php printPaginationInfo( $pagination ); ?>

                <table class="table table-bordered table-center">
                    <thead>
                    <tr>
                        <th>itemId</th>
                        <th>name</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
					<?php foreach ( $list as $item ) : ?>
                        <tr>
                            <td><?= $item['item_id'] ?></td>
                            <td><?= $item['name'] ?></td>
                            <td>
                                <button class="btn btn-sm btn-primary" type="button"
                                        onclick="editItem('<?= $item['item_id'] ?>')">수정
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
        <div class="col-lg-9">
            <div class="form-save">
                <form method="post" id="form">
                    <p>
                        <button type="submit" class="btn btn-primary">저장</button>
                        <button type="button" onclick="listPage(1);" class="btn btn-default">취소</button>
                    </p>

					<?php
					foreach ( $fields as $field ) {
						printRowItem( $editItem, $field, 3 );
					}
					?>
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

        function search() {
            $('#pageNo').val(1);
        }

        function reloadMap() {
            if (confirm('리로드 아이템 하시겠습니까?')) {
                callServerApi('리로드 아이템', function (response) {
                    if (response.result) {
                        alert('아이템 리로드가 완료되었습니다');
                    }
                });
            }
        }

        $(document).ready(function () {
            $('#form').submit(function (e) {
                e.preventDefault();

                var data = $(this).serializeArray();

                $.post('/action/action.php', {
                    action: 'updateItem',
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