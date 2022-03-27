<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$where = array();

$pagination = new Pagination( array(
	'recordCountPerPage' => getArrayValue( $_REQUEST, 'recordCountPerPage', 10 )
) );

$totalCount = $linDb->count( 'common_code', '*', $where );

$pagination->setTotalRecordCount( $totalCount );

$recordCountPerPage = $pagination->getRecordCountPerPage();
$firstRecordIndex   = $pagination->getFirstRecordIndex();

$where['LIMIT'] = [ $firstRecordIndex, $recordCountPerPage ];

$list = $linDb->select( 'common_code', '*', $where );

$editItem = array();

$id = getArrayValue( $_REQUEST, 'pCode' );

if ( $id ) {
	for ( $i = 0; $i < count( $list ); $i ++ ) {
		if ( $id == $list[ $i ]['code'] ) {
			$editItem = $list[ $i ];
			break;
		}
	}
}
?>
    <h2 class="page-title">코드 관리</h2>
    <hr>
    <div class="row">
        <div class="col-lg-4">
            <form action="" id="editForm">
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <th>코드</th>
                        <td>
							<?php $code = getArrayValue( $editItem, 'code', $editItem['code'] ); ?>
							<?= $code ?>
                            <input type="hidden" name="code" value="<?= $code ?>">
                        </td>
                    </tr>
                    <tr>
                        <th>값</th>
                        <td><?php input( 'value', getArrayValue( $editItem, 'value', $editItem['value'] ) ) ?></td>
                    </tr>
                    <tr>
                        <th>설명</th>
                        <td><?php input( 'title', getArrayValue( $editItem, 'title', $editItem['title'] ) ) ?></td>
                    </tr>
                    <tr>
                        <th>메모</th>
                        <td><?php input( 'note', getArrayValue( $editItem, 'note', $editItem['note'] ) ) ?></td>
                    </tr>
                    <tr>
                        <th>그룹</th>
                        <td><?php input( 'codeGroup', getArrayValue( $editItem, 'codeGroup', $editItem['codeGroup'] ) ) ?></td>
                    </tr>
                    </tbody>
                </table>

                <button type="submit" class="btn btn-primary">저장</button>
            </form>
        </div>
        <div class="col-lg-8">
			<?php printPaginationInfo( $pagination ); ?>

            <form action="" id="searchForm" method="get">
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>코드</th>
                        <th>설명</th>
                        <th>그룹</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
					<?php foreach ( $list as $item ) : ?>
                        <tr>
                            <td><?= $item['code'] ?></td>
                            <td><?= $item['title'] ?></td>
                            <td><?= $item['codeGroup'] ?></td>
                            <td>
                                <button class="btn btn-sm btn-primary" type="button"
                                        onclick="editItem('<?= $item['code'] ?>')">수정
                                </button>
                            </td>
                        </tr>
					<?php endforeach; ?>
                    </tbody>
                </table>

				<?php printPagination( $pagination, 'listPage' ); ?>

                <input type="hidden" name="pageNo" id="pageNo" value="<?= $pagination->getPageNo() ?>">
                <input type="hidden" name="pCode" id="pCode">
            </form>
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

        $(document).ready(function () {
            $('#editForm').submit(function (e) {
                e.preventDefault();

                const data = $(this).serializeArray();

                $.post('/action/action.php', {
                    action: 'updateCode',
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