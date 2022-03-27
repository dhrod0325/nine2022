<?php require_once dirname( __FILE__ ) . '/include/header.php'; ?>

<?php
$pagination = new Pagination( array(
	'recordCountPerPage' => getArrayValue( $_REQUEST, 'recordCountPerPage', 20 )
) );

$selectItems = selectItems( $pagination );

$list = $selectItems['list'];

?>
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
                <button type="button" class="btn btn-default" onclick="closePopup()">닫기</button>
            </div>
        </div>

        <div class="col-lg-3">
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
                                    onclick="selectItem(
                                            '<?= $item['item_id'] ?>',
                                            '<?= $item['name'] ?>')">
                                선택
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
    <script>
        function listPage(pageNo) {
            $('#pageNo').val(pageNo);
            $('#searchForm').submit();
        }

        function selectItem(itemId, itemName) {
            opener.searchItemCallBack({
                itemId: itemId,
                itemName: itemName
            });

            self.close();
        }
    </script>


<?php require_once dirname( __FILE__ ) . '/include/footer.php'; ?>