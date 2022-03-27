<?php
require_once dirname( __FILE__ ) . '/lib/functions.php';

getHeader();

$svSetting   = selectSettingList( array( 'codeGroup' => '서버' ) );
$clanSetting = selectSettingList( array( 'codeGroup' => '혈맹' ) );

$rateSetting      = selectSettingList( array( 'codeGroup' => '배율' ) );
$levSetting       = selectSettingList( array( 'codeGroup' => '레벨' ) );
$adenBoardSetting = selectSettingList( array( 'codeGroup' => '아데나게시판' ) );
$warSetting       = selectSettingList( array( 'codeGroup' => '공성전' ) );
$hpSetting        = selectSettingList( array( 'codeGroup' => '만피' ) );
$bsSetting        = selectSettingList( array( 'codeGroup' => '보스' ) );

$codeSetting = selectSettingList( array( 'codeGroup' => '코드' ) );

function printTable( $title, $list ) {
	?>
    <div class="col-lg-4">
        <h4><?= $title ?></h4>
        <div class="scrollBox">
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>이름</th>
                    <th>값</th>
                </tr>
                </thead>
				<?php foreach ( $list as $item ): ?>
                    <tr>
                        <td>
                            <label for="<?= $item['code'] ?>">
								<?= $item['title'] ?>
                            </label>
                        </td>
                        <td>
                            <input type="text" value="<?= $item['value'] ?>" name="<?= $item['code'] ?>"
                                   id="<?= $item['code'] ?>" class="form-control"/>
                        </td>
                    </tr>
				<?php endforeach; ?>
            </table>
        </div>
    </div>
	<?php
}

?>
    <style>
        .scrollBox {
            height: 300px;
            overflow-y: scroll;
            border: 1px solid #dedede;
            margin-bottom: 20px;
        }
    </style>
    <h2 class="page-title">서버정보</h2>
    <hr>
    <form action="/action/action.php" method="post" id="form">
        <div class="form-save">
            <button type="submit" class="btn btn-primary">저장</button>
        </div>

        <div class="row">
			<?php
			printTable( '서버', $svSetting );
			printTable( '배율', $rateSetting );
			printTable( '레벨', $levSetting );
			printTable( '혈맹', $clanSetting );
			printTable( '아데나 게시판', $adenBoardSetting );
			printTable( '공성전', $warSetting );
			printTable( '만피', $hpSetting );
			printTable( '보스', $bsSetting );
			printTable( '코드', $bsSetting );
			?>
        </div>
    </form>

    <script>
        $(document).ready(function () {
            $('#form').submit(function (e) {
                e.preventDefault();

                var data = $(this).serializeArray();

                $.post('/action/action.php', {
                    action: 'updateSetting',
                    param: JSON.stringify(data)
                }, function (response) {
                    if (response.result) {
                        if (confirm('저장되었습니다. 리로드 서버설정 하시겠습니까?')) {
                            callServerApi('리로드 서버설정', function (response) {
                                if (response.result) {
                                    alert('리로드 서버설정 완료');
                                }
                            });
                        }
                    }
                });
            });
        });
    </script>
<?php
getFooter();