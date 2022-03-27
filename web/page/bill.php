<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$searchGift = getArrayValue( $_REQUEST, '$searchGift' );

$where = array();

if ( $searchGift == '지급' ) {
	$where[] = ' and gift=1';
} else if ( $searchGift == '미지급' ) {
	$where[] = ' and (gift=0 or gift is null)';
}

$w = '';

$pagination = new Pagination( array(
	'recordCountPerPage' => getArrayValue( $_REQUEST, 'recordCountPerPage', 10 )
) );

$col1 = 'count(*)';

$q = "select {$col1} cnt from bill a left join bill_gift b on a.transRefKey = b.transRefKey where 1=1 {$w}";

$totalCount = $linDb->query( $q )->fetch()['cnt'];
$pagination->setTotalRecordCount( $totalCount );

$recordCountPerPage = $pagination->getRecordCountPerPage();
$firstRecordIndex   = $pagination->getFirstRecordIndex();

$where[] = ' limit ' . $firstRecordIndex . ',' . $recordCountPerPage;

$w = join( ' ', $where );


$col1 = 'a.*,b.gift';

$q = "select {$col1} from bill a left join bill_gift b on a.transRefKey = b.transRefKey where 1=1 {$w}";

$list = $linDb->query( $q )->fetchAll();

$editItem = array();

$id = getArrayValue( $_REQUEST, 'pCode' );

if ( $id ) {
	for ( $i = 0; $i < count( $list ); $i ++ ) {
		if ( $id == $list[ $i ]['transRefKey'] ) {
			$editItem = $list[ $i ];
			break;
		}
	}
}

?>
    <h2 class="page-title">후원 관리</h2>
    <hr>
    <div class="row">
        <div class="col-lg-4">
            <form action="" id="editForm">
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <th>입금일자</th>
                        <td>
							<?php input( 'transDT', getArrayValue( $editItem, 'transDT', @$editItem['transDT'] ) ) ?>
                        </td>
                    </tr>
                    <tr>
                        <th>입금자명</th>
                        <td>
							<?php input( 'transRemark', getArrayValue( $editItem, 'transRemark', @$editItem['transRemark'] ) ) ?>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
        <div class="col-lg-8">
            <form action="" id="searchForm" method="get">
                <div class="col-lg-12">
                    <table class="table table-bordered">
                        <tr>
                            <th>지급여부</th>
                            <td>
                                <select name="searchGift" id="" class="form-control">
                                    <option value="" <?= empty( $searchGift ) ? "selected" : "" ?>>전체</option>
                                    <option value="지급" <?= $searchGift == '지급' ? "selected" : "" ?>>지급</option>
                                    <option value="미지급" <?= $searchGift == '미지급' ? "selected" : "" ?>>미지급</option>
                                </select>

                            </td>
                        </tr>
                    </table>

                    <div class="search-btn text-right">
                        <button type="submit" class="btn btn-primary" id="searchButton" onclick="search()">검색</button>
                    </div>
                </div>
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>입금일자</th>
                        <th>입금자명</th>
                        <th>입금금액</th>
                        <th>지급여부</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
					<?php foreach ( $list as $item ) : ?>
                        <tr>
                            <td><?= $item['transDT'] ?></td>
                            <td><?= $item['transRemark'] ?></td>
                            <td><?= $item['deposit'] ?></td>
                            <td><?= $item['gift'] == 1 ? "지급" : "미지급" ?></td>
                            <td>
                                <button class="btn btn-sm btn-primary" type="button"
                                        onclick="editItem('<?= $item['transRefKey'] ?>')">수정
                                </button>
                            </td>
                        </tr>
					<?php endforeach; ?>
                    </tbody>
                </table>

				<?php printPagination( $pagination, 'listPage' ); ?>

                <input type="hidden" name="pageNo" id="pageNo">
                <input type="hidden" name="pCode" id="pCode">
            </form>
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
    </script>
<?php
getFooter();