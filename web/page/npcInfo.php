<?php
require_once dirname( __FILE__ ) . '/../lib/functions.php';

global $editItem;

$columnNames = getTableColumnNames( 'npc' );

?>
<style>
</style>
<div id="npcInfo" class="">
	<?php foreach ( $columnNames as $name ) : ?>
        <div class="col-md-4">
            <div class="bx">
				<?= $name ?>
                <input type="text" class="form-control" value="<?= getArrayValue( $editItem, $name, '' ) ?>">
            </div>
        </div>
	<?php endforeach; ?>
</div>