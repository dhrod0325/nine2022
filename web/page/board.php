<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$pageNo   = getArrayValue( $_REQUEST, 'pageNo' );
$board_id = getArrayValue( $_REQUEST, 'board_id' );

$tableName = 'board';

$where = array(
	'ORDER' => [ 'id' => 'DESC' ]
);

if ( $board_id ) {
	$where['board.board_id'] = $board_id;
}

$pagination = new Pagination( array(
	'recordCountPerPage' => getArrayValue( $_REQUEST, 'recordCountPerPage', 8 )
) );

$joinTable = [
	'[>]npc' => [ 'board_id' => 'npcid' ]
];

$totalCount = $linDb->count( $tableName, $joinTable, '*', $where );

$pagination->setTotalRecordCount( $totalCount );

$recordCountPerPage = $pagination->getRecordCountPerPage();
$firstRecordIndex   = $pagination->getFirstRecordIndex();

$where['LIMIT'] = [ $firstRecordIndex, $recordCountPerPage + 1 ];
$list           = $linDb->select( $tableName, $joinTable, [
	'board.id',
	'board.name',
	'board.date',
	'board.title',
	'board.content',
	'npc.name(board_name)'
], $where );

$editItem = array();

$pCode = getArrayValue( $_REQUEST, 'pCode' );

if ( $pCode ) {
	$editItem = $linDb->select( 'board', '*', [ 'id' => $pCode ] )[0];
}

?>
    <h2 class="page-title">게시판</h2>
    <hr>
    <div class="row">
        <div class="col-lg-6">
            <form action="" id="editForm">
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <th>게시판 유형</th>
                        <td>
							<?php
							$options = $linDb->query( 'select b.name,board_id from board a,npc b where a.board_id=b.npcid group by board_id' )->fetchAll();
							?>
                            <select name="board_id" id="" class="form-control">
								<?php foreach ( $options as $option ) : ?>
                                    <option value="<?= $option['board_id'] ?>" <?= $board_id == $option['board_id'] ? 'selected' : '' ?>>
										<?= $option['name'] ?>
                                    </option>
								<?php endforeach; ?>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th>작성자</th>
                        <td>
							<?php input( 'title', getArrayValue( $editItem, 'name', @$editItem['name'] ) ) ?>
                        </td>
                    </tr>
                    <tr>
                        <th>제목</th>
                        <td>
							<?php input( 'title', getArrayValue( $editItem, 'title', @$editItem['title'] ) ) ?>
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
                <input type="hidden" name="id" value="<?= $pCode ?>">
            </form>
        </div>

        <div class="col-lg-6">
            <form action="" method="get" id="searchForm">
                <table class="table table-bordered">
                    <tr>
                        <th>게시판 유형</th>
                        <td>
							<?php
							$options = $linDb->query( 'select b.name,board_id from board a,npc b where a.board_id=b.npcid group by board_id' )->fetchAll();
							?>
                            <select name="board_id" id="" class="form-control">
                                <option value="">전체</option>
								<?php foreach ( $options as $option ) : ?>
                                    <option value="<?= $option['board_id'] ?>" <?= $board_id == $option['board_id'] ? 'selected' : '' ?>>
										<?= $option['name'] ?>
                                    </option>
								<?php endforeach; ?>
                            </select>
                        </td>
                    </tr>
                </table>

                <div class="search-btn text-right">
                    <button type="submit" class="btn btn-primary" id="searchButton" onclick="search()">검색</button>
                </div>


                <input type="hidden" name="pageNo" id="pageNo" value="<?= $pagination->getPageNo() ?>">
                <input type="hidden" name="pCode" id="pCode">
            </form>

            <div class="form-save">
                <form method="post" id="form">
					<?php printPaginationInfo( $pagination ); ?>
                    <table class="table table-bordered table-center">
                        <thead>
                        <tr>
                            <th>게시판</th>
                            <th>작성자</th>
                            <th>제목</th>
                            <th style="width:120px;">관리</th>
                        </tr>
                        </thead>
                        <tbody>
						<?php foreach ( $list as $item ) : ?>
                            <tr>
                                <td><?= $item['board_name'] ?></td>
                                <td><?= $item['name'] ?></td>
                                <td><?= $item['title'] ?></td>
                                <td>
                                    <button type="button" onclick="editItem('<?= $item['id'] ?>')"
                                            class="btn btn-primary">수정
                                    </button>
                                    <button type="button" class="btn btn-primary"
                                            onclick="deleteItem('<?= $item['id'] ?>')"> 삭제
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