<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

getHeader();

global $linDb;

$tableName = 'enchant_bonus';

$where = array();

$enchantLevel = getArrayValue( $_REQUEST, 'enchantLevel' );
$itemName     = getArrayValue( $_REQUEST, 'itemName' );

if ( $enchantLevel ) {
	$where['enchantLevel'] = $enchantLevel;
}
if ( $itemName ) {
	$where['itemName[~]'] = $itemName;
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

$editItem = array();

$enchantGroup  = array( 'enchantLevel' );
$enchantGroups = $linDb->select( $tableName, $enchantGroup, array( 'GROUP' => $enchantGroup ) );

$paramEnchantLevel = getArrayValue( $_REQUEST, 'pCode1' );
$paramItemId       = getArrayValue( $_REQUEST, 'pCode2' );

if ( $paramEnchantLevel && $paramItemId ) {
	$editItem = $linDb->select( $tableName, '*', array(
		'enchantLevel' => $paramEnchantLevel,
		'itemId'       => $paramItemId
	) )[0];

}
?>
    <h2 class="page-title">인챈트 효과</h2>
    <hr>
    <div class="row">
        <form action="" method="get" id="searchForm">
            <div class="col-lg-12">
                <table class="table table-bordered">
                    <tr>
                        <th>아이템명</th>
                        <td>
							<?php input( 'itemName', $itemName ); ?>
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
            <input type="hidden" name="pCode1" id="pCode1">
            <input type="hidden" name="pCode2" id="pCode2">
        </form>
        <div class="col-lg-4">
            <div class="form-save">
                <form method="post" id="form">
                    <p>
                        <button onclick="reloadEnchantBonus()" type="button" class="btn btn-primary">리로드 인챈트보너스</button>
                        <button type="submit" class="btn btn-primary">저장</button>
                    </p>
	                <?php printPaginationInfo( $pagination ); ?>
                    <table class="table table-bordered table-center">
                        <thead>
                        <tr>
                            <th>아이템명</th>
                            <th>인챈트</th>
                            <th>선택</th>
                        </tr>
                        </thead>
                        <tbody>
						<?php foreach ( $list as $item ) : ?>
                            <tr>
                                <td>
									<?= $item['itemName'] ?>
                                </td>
                                <td>
									<?= $item['enchantLevel'] ?>
                                    <input type="hidden" name="enchantLevel" value="<?= $item['enchantLevel'] ?>">
                                </td>
                                <td>
                                    <button type="button" class="btn btn-primary btn-sm"
                                            onclick="editItem('<?= $item['enchantLevel'] ?>','<?= $item['itemId'] ?>')">
                                        수정
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
        <div class="col-lg-8">
			<?php

			$fields = array(
				'itemName',
				'enchantLevel',
				'ac',
				'addDmg',
				'addBowDmg',
				'addHitUp',
				'addBowHitUp',
				'addSp',
				'addMr',
				'dmg',
				'reduction',
				'hitUp',
				'potionPer',
				'addHp',
				'addMp',
				'addHpr',
				'addMpr',
				'addExp',
				'magicHit',
				'registStun',
				'pvpDmg',
				'pvpReduction',
				'str',
				'dex',
				'intel',
				'cha',
				'con',
				'perDmg',
				'perDmgPer',
				'perDmgEffect',
				'weight',
				'stunHitUp',
				'ignoreReduction',
				'registElf',
				'addEr',
				'weightReduction',
				'criticalPer',
				'bowCriticalPer',
				'addDouble',
			);

			?>

			<?php
			foreach ( $fields as $field ) {
				printRowItem( $editItem, $field );
			}
			?>
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

        function editItem(enchantLevel, itemId) {
            $('#pCode1').val(enchantLevel);
            $('#pCode2').val(itemId);

            $('#searchForm').submit();
        }

        function reloadEnchantBonus() {
            if (confirm('리로드 인챈트보너스 하시겠습니까?')) {
                callServerApi('리로드 인챈트보너스', function (response) {
                    if (response.result) {
                        alert('인챈트보너스 리로드가 완료되었습니다');
                    }
                });
            }
        }

        $(document).ready(function () {
            $('#form').submit(function (e) {
                e.preventDefault();

                const data = $(this).serializeTable();

                serverAction('updateEnchantBonus', data, function () {
                    location.reload();
                });
            });
        });
    </script>
<?php
getFooter();