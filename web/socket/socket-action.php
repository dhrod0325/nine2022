<?php

require_once dirname(__FILE__).'/../lib/functions.php';

global $linDb;

$action = $_REQUEST['api_action'];

$data = array();

switch ( $action ) {
	case 'chat':
		try {
			$data['result'] = true;

			$context = new ZMQContext();
			$socket  = $context->getSocket( ZMQ::SOCKET_PUSH, 'myPusher' );
			$socket->connect( "tcp://127.0.0.1:10015" );

			$entryData = array(
				'category' => 'chat',
				'name'     => $_REQUEST['name'],
				'target'   => $_REQUEST['target'],
				'text'     => $_REQUEST['text'],
				'type'     => $_REQUEST['type'],
				'when'     => time()
			);

			$socket->send( json_encode( $entryData ) );
		} catch ( ZMQSocketException $e ) {
			$data['error'] = $e->getTraceAsString();
		}

		break;
}

header( 'Content-type: application/json' );
echo json_encode( $data );