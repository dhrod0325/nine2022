<?php
error_reporting( E_ERROR );

require_once dirname( __FILE__ ) . '/../config.php';
require_once dirname( __FILE__ ) . '/Medoo.php';
require_once dirname( __FILE__ ) . '/Pagination.php';

use Medoo\Medoo;

$linDb = new Medoo( array(
	'type'     => 'mysql',
	'host'     => DB_URL,
	'database' => 'classic_310',
	'username' => DB_USER,
	'password' => DB_PASSWORD
) );


$GLOBALS['linDb'] = $linDb;

function getHeader() {
	require_once dirname( __FILE__ ) . '/../include/header.sub.php';
	require_once dirname( __FILE__ ) . '/../include/header.php';
}

function getFooter() {
	require_once dirname( __FILE__ ) . '/../include/footer.sub.php';
	require_once dirname( __FILE__ ) . '/../include/footer.php';
}

function sideBar() {
	?>
    <ul>
        <li><a href="/">시스템</a>
            <ul>
				<?php createSideBarItem( '/index.php', '서버정보' ); ?>
				<?php createSideBarItem( '/page/code.php', '코드 관리' ); ?>
            </ul>
        </li>
    </ul>
    <ul>
        <li>
            <a href="#">맵 관리 </a>
            <ul>
				<?php createSideBarItem( '/page/mapDb.php', '맵 디비 관리' ); ?>
				<?php createSideBarItem( '/page/mapBalance.php', '맵 밸런스 관리' ); ?>
				<?php createSideBarItem( '/page/mapDrop.php', '맵 드랍 관리' ); ?>
            </ul>
        </li>
    </ul>
    <ul>
        <li><a href="#">아이템 관리</a>
            <ul>
				<?php createSideBarItem( '/page/enchantPer.php', '인챈트 확률' ); ?>
				<?php createSideBarItem( '/page/enchantDmg.php', '인챈트 추타' ); ?>
				<?php createSideBarItem( '/page/enchantEff.php', '인챈트 효과' ); ?>
				<?php createSideBarItem( '/page/item.php', '아이템 디비 관리' ); ?>
            </ul>
        </li>
    </ul>

    <ul>
        <li><a href="#">기타</a>
            <ul>
				<?php createSideBarItem( '/page/bill.php', '후원 관리' ); ?>
				<?php createSideBarItem( '/page/board.php', '게시판' ); ?>
				<?php createSideBarItem( '/page/letter.php', '편지' ); ?>
				<?php createSideBarItem( '/page/chat.php', '채팅' ); ?>
            </ul>
        </li>
    </ul>

    <ul>
        <li>
            <a href="#">엔피씨 관리 </a>
            <ul>
				<?php createSideBarItem( '/page/npc.php', '엔피씨' ); ?>
            </ul>
        </li>
    </ul>
	<?php
}

function insertOrUpdate( $table, $p, $key ) {
	global $linDb;

	$count = $linDb->count( $table, '*', $key );

	if ( $count == 0 ) {
		$linDb->insert( $table, $p );
	} else {
		$linDb->update( $table, $p, $key );
	}
}

function getArrayValue( $array, $key, $defaultValue = '' ) {
	$result = @$array[ $key ];

	return @ ! is_null( $result ) ? $result : $defaultValue;
}

function input( $name, $value, $placeHolder = '', $cssClass = 'form-control' ) { ?>
    <input type="text" class="<?= $cssClass ?>" name="<?= $name ?>" id="<?= $name ?>" value="<?= $value ?>"
           placeholder="<?= $placeHolder ?>"/>
	<?php
}

function createSideBarItem( $link, $title, $icon = '' ) {
	$u = $_SERVER['PHP_SELF'];
	?>

    <li class="<?= $u == $link ? 'active' : '' ?>">
        <a href="<?= $link ?>">
            <span class="fa <?= $icon ? $icon : '' ?>"></span>
			<?= $title ?>
        </a>
    </li>
	<?php
}

function selectSettingList( array $params = array() ) {
	global $linDb;

	return $linDb->select( 'common_code', "*", $params );
}

function printPagination( $pagination, $js = '' ) {
	?>
    <nav class="text-center">
        <ul class="pagination justify-content-center">
            <li class="page-item">
                <a href="javascript:void(0);"
                   class="paging_prev page-link"
                   onclick="<?= sprintf( '%s(%d)', $js, 1 ) ?>"
                   title="<?= $pagination->getFirstPageNo(); ?> page"><span class="fa fa-angle-double-left"></span></a>
            </li>

            <li class="page-item">
                <a class="paging_prev page-link"
                   title="<?= $pagination->getPrevPage(); ?> page"
                   onclick="<?= sprintf( '%s(%d)', $js, $pagination->getPrevPage() ) ?>"
                   href="javascript:void(0);"><span class="fa fa-angle-left"></span></a>
            </li>

			<?php for ( $i = $pagination->getFirstPageNoOnPageList(); $i <= $pagination->getLastPageNoOnPageList(); $i ++ ) { ?>
                <li class="page-item
                <?php if ( $pagination->getPageNo() == $i ) {
					echo 'active';
				} ?>">
                    <a href="javascript:void(0);"
                       onclick="<?= sprintf( '%s(%d)', $js, $i ) ?>"
                       class="page-link"
                       title="<?= $i ?> page"><?= $i ?>
                    </a>
                </li>
			<?php } ?>

            <li class="page-item">
                <a class="paging_next page-link" href="javascript:void(0);"
                   onclick="<?= sprintf( '%s(%d)', $js, $pagination->getNextPage() ) ?>"
                   title="<?= $pagination->getNextPage(); ?> page"><span class="fa fa-angle-right"></span></a></li>

            <li class="page-item">
                <a class="paging_prev page-link" href="javascript:void(0);"
                   title="<?= $pagination->getLastPageNo(); ?> page"
                   onclick="<?= sprintf( '%s(%d)', $js, $pagination->getLastPageNo() ) ?>">
                    <span class="fa fa-angle-double-right"></span></a>
            </li>
        </ul>
    </nav>
	<?php
}

function mapToArray( $map ) {
	$result = [];

	foreach ( $map as $item ) {
		$result[ $item['name'] ] = $item['value'];
	}

	return $result;
}

function printRowItem( $editItem, $field, $col = '3' ) { ?>
    <div class="col-lg-<?= $col ?>" style="margin-bottom:20px;">
        <label for="<?= $field ?>"><?= $field ?></label>
		<?php input( $field, $editItem[ $field ] ); ?>
    </div>
<?php }

function getTableColumnNames( $tableName ) {
	global $linDb;

	$rs = $linDb->query( 'DESCRIBE ' . $tableName );

	$list = $rs->fetchAll();

	$fieldNames = [];

	foreach ( $list as $row ) {
		$fieldNames[] = $row['Field'];
	}

	return $fieldNames;
}

function selectItems( Pagination $pagination ) {
	global $linDb;

	$table = 'all_item';

	$where = array();

	if ( $_REQUEST['name'] ) {
		$where['name[~]'] = $_REQUEST['name'];
	}

	if ( ! is_null( $_REQUEST['item_id'] ) && is_numeric( $_REQUEST['item_id'] ) ) {
		$where['item_id'] = $_REQUEST['item_id'];
	}

	$totalCount = $linDb->count( $table, '*', $where );

	$pagination->setTotalRecordCount( $totalCount );

	$recordCountPerPage = $pagination->getRecordCountPerPage();
	$firstRecordIndex   = $pagination->getFirstRecordIndex();

	$where['LIMIT'] = [ $firstRecordIndex, $recordCountPerPage ];

	$list = $linDb->select( $table, '*', $where );

	return array(
		'pagination' => $pagination,
		'list'       => $list
	);
}

function printPaginationInfo( $pagination ) {
	?>
    <p class="text-right">
        총 : <?= $pagination->getTotalRecordCount() ?>건 ( <?= $pagination->getPageNo() ?>
        / <?= $pagination->getTotalPageCount() ?>)
    </p>
	<?php
}