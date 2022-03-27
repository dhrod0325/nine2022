<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$pageNo   = getArrayValue( $_REQUEST, 'pageNo' );
$board_id = getArrayValue( $_REQUEST, 'board_id' );

$searchCode     = getArrayValue( $_REQUEST, 'code' );
$searchReceiver = getArrayValue( $_REQUEST, 'receiver' );
$searchSender   = getArrayValue( $_REQUEST, 'sender' );

$tableName = 'letter';

$where = array(
	'ORDER' => [ 'item_object_id' => 'DESC' ]
);

if ( $searchCode ) {
	$where['code'] = $searchCode;
}
if ( $searchReceiver ) {
	$where['receiver[~]'] = $searchReceiver;
}
if ( $searchSender ) {
	$where['sender[~]'] = $searchSender;
}

$pagination = new Pagination( array(
	'recordCountPerPage' => getArrayValue( $_REQUEST, 'recordCountPerPage', 8 )
) );

$totalCount = $linDb->count( $tableName, '*', $where );

$pagination->setTotalRecordCount( $totalCount );

$recordCountPerPage = $pagination->getRecordCountPerPage();
$firstRecordIndex   = $pagination->getFirstRecordIndex();

$where['LIMIT'] = [ $firstRecordIndex, $recordCountPerPage + 1 ];
$list           = $linDb->select( $tableName, '*', $where );

$editItem = array();

$pCode = getArrayValue( $_REQUEST, 'pCode' );

if ( $pCode ) {
	$editItem = $linDb->select( $tableName, '*', [ 'item_object_id' => $pCode ] )[0];
}

?>
    <h2 class="page-title">편지</h2>
    <hr>
    <div class="row">
        <div class="col-lg-4">
            <form action="" id="editForm">
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <th>편지 유형</th>
                        <td>
							<?php
							$options = $linDb->query( 'select code from letter group by code' )->fetchAll();
							?>
                            <select name="board_id" id="" class="form-control">
								<?php foreach ( $options as $option ) : ?>
                                    <option value="<?= $option['code'] ?>" <?= $board_id == $option['code'] ? 'selected' : '' ?>>
										<?= $option['code'] ?>
                                    </option>
								<?php endforeach; ?>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th>보낸사람</th>
                        <td>
							<?php input( 'sender', getArrayValue( $editItem, 'sender', @$editItem['sender'] ) ) ?>
                        </td>
                    </tr>
                    <tr>
                        <th>받는사람</th>
                        <td>
							<?php input( 'receiver', getArrayValue( $editItem, 'receiver', @$editItem['receiver'] ) ) ?>
                        </td>
                    </tr>
                    <tr>
                        <th>제목</th>
                        <td>
							<?php input( 'subject', getArrayValue( $editItem, 'subject', @$editItem['subject'] ) ) ?>
                        </td>
                    </tr>
                    <tr>
                        <th>내용</th>
                        <td>
                            <textarea name="content" style="height:300px;"
                                      class="form-control"><?= getArrayValue( $editItem, 'content' ) ?></textarea>

                        </td>
                    </tr>
                    </tbody>
                </table>

                <button type="submit" class="btn btn-primary">저장</button>
                <button type="button" onclick="cancelItem()" class="btn btn-primary">취소</button>
                <input type="hidden" name="item_object_id" value="<?= $pCode ?>">
            </form>
        </div>

        <div class="col-lg-8">
            <div class="form-save">
                <form action="" method="get" id="searchForm">
                    <table class="table table-bordered">
                        <tr>
                            <th>편지 유형</th>
                            <td>
                                <select name="code" id="" class="form-control">
                                    <option value="">전체</option>
									<?php foreach ( $options as $option ) : ?>
                                        <option value="<?= $option['code'] ?>" <?= $searchCode == $option['code'] ? 'selected' : '' ?>>
											<?= $option['code'] ?>
                                        </option>
									<?php endforeach; ?>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <th>보낸사람</th>
                            <td>
								<?php input( 'sender', $searchSender ); ?>
                            </td>
                        </tr>
                        <tr>
                            <th>받는사람</th>
                            <td>
								<?php input( 'receiver', $searchReceiver ); ?>
                            </td>
                        </tr>
                    </table>

                    <div class="search-btn text-right">
                        <button type="submit" class="btn btn-primary" id="searchButton" onclick="search()">검색</button>
                    </div>

                    <input type="hidden" name="pageNo" id="pageNo" value="<?= $pagination->getPageNo() ?>">
                    <input type="hidden" name="pCode" id="pCode">
                </form>

                <form method="post" id="form">
					<?php printPaginationInfo( $pagination ); ?>
                    <table class="table table-bordered table-center">
                        <thead>
                        <tr>
                            <th>일자</th>
                            <th>보낸사람</th>
                            <th>받는사람</th>
                            <th>제목</th>
                            <th style="width:120px;">관리</th>
                        </tr>
                        </thead>
                        <tbody>
						<?php foreach ( $list as $item ) : ?>
                            <tr>
                                <td><?= $item['date'] ?></td>
                                <td><?= $item['sender'] ?></td>
                                <td><?= $item['receiver'] ?></td>
                                <td><?= $item['subject'] ?></td>
                                <td>
                                    <button type="button" onclick="editItem('<?= $item['item_object_id'] ?>')"
                                            class="btn btn-primary">수정
                                    </button>
                                    <button type="button" class="btn btn-primary"
                                            onclick="deleteItem('<?= $item['item_object_id'] ?>')"> 삭제
                                    </button>
                                </td>
                            </tr>
						<?php endforeach; ?>
                        </tbody>
                    </table>

					<?php printPagination( $pagination, 'listPage' ); ?>
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