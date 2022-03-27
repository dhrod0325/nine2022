<?php

require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$tableName = 'enchant_setting';

$where = array();

$scrollType   = getArrayValue( $_REQUEST, 'scrollType' );
$safeEnchant  = getArrayValue( $_REQUEST, 'safeEnchant' );
$enchantLevel = getArrayValue( $_REQUEST, 'enchantLevel' );

if ( $scrollType ) {
	$where['scrollType'] = $scrollType;
}

if ( $safeEnchant ) {
	$where['safeEnchant'] = $safeEnchant;
}

if ( $enchantLevel ) {
	$where['enchantLevel'] = $enchantLevel;
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

$scrollTypeGroup = array( 'scrollType', 'note' );
$scrollTypes     = $linDb->select( $tableName, $scrollTypeGroup, array(
	'GROUP' => $scrollTypeGroup,
	'ORDER' => 'note'
) );

$safeEnchantGroup  = array( 'safeEnchant' );
$safeEnchantGroups = $linDb->select( $tableName, $safeEnchantGroup, array( 'GROUP' => $safeEnchantGroup ) );

$enchantGroup  = array( 'enchantLevel' );
$enchantGroups = $linDb->select( $tableName, $enchantGroup, array( 'GROUP' => $enchantGroup ) );

?>
    <h2 class="page-title">인챈률 관리</h2>
    <hr>
    <div class="row">
        <form action="" method="get" id="searchForm">
            <div class="col-lg-12">
                <table class="table table-bordered">
                    <tr>
                        <th>인챈트 유형</th>
                        <td>
                            <select name="scrollType" id="scrollType" class="form-control">
                                <option value="">선택</option>
								<?php foreach ( $scrollTypes as $scroll_type ) : ?>
                                    <option value="<?= $scroll_type['scrollType'] ?>" <?= $scroll_type['scrollType'] == $scrollType ? 'selected' : '' ?>>
										<?= $scroll_type['note'] ?>
                                    </option>
								<?php endforeach; ?>
                            </select>
                        </td>
                        <th>안전 인챈</th>
                        <td>
                            <select name="safeEnchant" id="safeEnchant" class="form-control">
                                <option value="">선택</option>
								<?php foreach ( $safeEnchantGroups as $safe_enchant_group ) : ?>
                                    <option value="<?= $safe_enchant_group['safeEnchant'] ?>" <?= $safe_enchant_group['safeEnchant'] == $safeEnchant ? 'selected' : '' ?>>
										<?= $safe_enchant_group['safeEnchant'] ?>
                                    </option>
								<?php endforeach; ?>
                            </select>
                        </td>
                        <th>인챈트</th>
                        <td>
                            <select name="enchantLevel" id="enchantLevel" class="form-control">
                                <option value="">선택</option>
								<?php foreach ( $enchantGroups as $enchant_group ) : ?>
                                    <option value="<?= $enchant_group['enchantLevel'] ?>" <?= $enchant_group['enchantLevel'] == $enchantLevel ? 'selected' : '' ?>>
										<?= $enchant_group['enchantLevel'] ?></option>
								<?php endforeach; ?>
                            </select>
                        </td>
                    </tr>
                </table>

                <div class="search-btn text-right">
                    <button type="submit" class="btn btn-primary" id="searchButton" onclick="search()">검색</button>
                </div>
            </div>

            <input type="hidden" name="pageNo" id="pageNo" value="<?= $pagination->getPageNo() ?>">
            <input type="hidden" name="pCode" id="pCode">
        </form>
        <div class="col-lg-12">
            <div class="form-save">
                <form method="post" id="form">
                    <p>
                        <button onclick="reloadEnchant()" type="button" class="btn btn-primary">리로드 인챈트</button>
                        <button type="submit" class="btn btn-primary">저장</button>
                    </p>

	                <?php printPaginationInfo( $pagination ); ?>
                    <table class="table table-bordered table-center">
                        <thead>
                        <tr>
                            <th>주문서</th>
                            <th>인챈트</th>
                            <th>안전 인챈트</th>
                            <th>확률</th>
                        </tr>
                        </thead>
                        <tbody>
						<?php foreach ( $list as $item ) : ?>
                            <tr>
                                <td><?= $item['note'] ?></td>
                                <td><?= $item['enchantLevel'] ?></td>
                                <td><?= $item['safeEnchant'] ?></td>
                                <td>
									<?php input( 'per', $item['per'] ); ?>
                                    <input type="hidden" name="enchantLevel" value="<?= $item['enchantLevel'] ?>">
                                    <input type="hidden" name="safeEnchant" value="<?= $item['safeEnchant'] ?>">
                                    <input type="hidden" name="scrollType" value="<?= $item['scrollType'] ?>">
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

        function reloadEnchant() {
            if (confirm('리로드 인챈트 하시겠습니까?')) {
                callServerApi('리로드 인챈트', function (response) {
                    if (response.result) {
                        alert('인챈트 리로드가 완료되었습니다');
                    }
                });
            }
        }

        $(document).ready(function () {
            $('#form').submit(function (e) {
                e.preventDefault();

                const data = $(this).serializeTable();

                serverAction('updateEnchantPer', data, function () {
                    location.reload();
                });
            });
        });
    </script>
<?php getFooter();