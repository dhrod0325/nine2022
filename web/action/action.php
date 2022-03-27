<?php
include_once( dirname( __FILE__ ) . '/../lib/functions.php' );

global $linDb;

$action = $_REQUEST['action'];

$data = array();

$param = $_REQUEST['param'];
$param = str_replace( "\\\"", "\"", $param );
$param = preg_replace( '/[\x00-\x1F\x7F]/u', '', $param );

$parameter = json_decode( $param, true );

switch ( $action ) {
	case 'updateBoard':
		$p = mapToArray( $parameter );

		if ( $p['id'] ) {
			$linDb->update( 'board', $p, [ 'id' => $p['id'] ] );
		} else {
			$linDb->insert( 'board', $p );
		}


		$data['result'] = true;
		break;
	case 'updateItem':
		$data['result'] = true;

		break;
	case 'updateCode':
		$p = mapToArray( $parameter );

		$linDb->update( 'common_code', $p, [ 'code' => $p['code'] ] );
		$data['result'] = true;

		break;
	case 'deleteBy':
		$linDb->delete( $parameter['tableName'], $parameter['key'] );

		$data['result'] = true;

		break;
	case 'delete':

		$key = array();

		$key[ $parameter['key'] ] = $parameter['value'];

		$linDb->delete( $parameter['tableName'], $key );

		$data['result'] = true;

		break;
	case 'insertMapDropItem':
		$table = 'map_event_drop';

		$cnt = $linDb->count( $table, '*', $parameter );

		if ( $cnt > 0 ) {

		} else {
			$linDb->insert( $table, $parameter );
		}

		$data['result'] = true;

		break;
	case 'updateSetting':
		foreach ( $parameter as $item ) {
			$linDb->update( 'common_code',
				array( 'value' => $item['value'] ),
				array( 'code' => $item['name'] ) );
		}

		$data['result'] = true;

		break;
	case 'updateMapEventDrop':
		$table = 'map_event_drop';

		foreach ( $parameter as $o ) {
			$key = array( 'mapId' => $o['mapId'], 'itemId' => $o['itemId'] );

			insertOrUpdate( $table, $o, $key );
		}

		$data['result'] = true;

		break;
	case 'updateMapBalance':
		$table = 'map_balance';

		$p   = mapToArray( $parameter );
		$key = array( 'mapId' => $p['mapId'] );

		insertOrUpdate( $table, $p, $key );

		$data['result'] = true;

		break;
	case 'updateMap':
		$table = 'mapids';

		$key = [
			'mapid' => $parameter['mapid']
		];

		insertOrUpdate( $table, $parameter, $key );

		$data['result'] = true;

		break;
	case 'updateEnchantPer':
		$table = 'enchant_setting';

		foreach ( $parameter as $o ) {
			$key = [
				'safeEnchant'  => $o['safeEnchant'],
				'enchantLevel' => $o['enchantLevel'],
				'scrollType'   => $o['scrollType']
			];

			insertOrUpdate( $table, [ 'per' => $o['per'] ], $key );
		}

		$data['result'] = true;

		break;
}

header( 'Content-type: application/json' );
echo json_encode( $data );