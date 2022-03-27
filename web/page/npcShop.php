<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

global $editItem, $linDb;

$sql  = 'select a.*,b.`name` from shop a,all_item b  where npc_id = :npc_id and a.item_id = b.item_id order by order_id';
$list = $linDb->query( $sql, [ ':npc_id' => $editItem['npcid'] ] )->fetchAll();

$columnNames = getTableColumnNames( 'shop' );
?>

<div id="npcInfo" class="">
    <table class="table table-bordered">
        <tr>
            <th>아이템명</th>
			<?php foreach ( $columnNames as $name ) : ?>
                <th><?= $name ?></th>
			<?php endforeach; ?>
        </tr>

		<?php foreach ( $list as $item ) : ?>
            <tr>
                <td><?= $item['name'] ?></td>
				<?php foreach ( $columnNames as $name ) : ?>
                    <td>
                        <input type="text" class="form-control" value="<?= $item[ $name ] ?>">
                    </td>
				<?php endforeach; ?>
            </tr>
		<?php endforeach; ?>
    </table>

</div>
