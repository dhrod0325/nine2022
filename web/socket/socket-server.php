<?php

require_once dirname( __FILE__ ) . '/../vendor/autoload.php';

use Ratchet\ConnectionInterface;
use Ratchet\Http\HttpServer;
use Ratchet\Server\IoServer;
use Ratchet\Wamp\WampServer;
use Ratchet\Wamp\WampServerInterface;
use Ratchet\WebSocket\WsServer;
use React\EventLoop\Loop;
use React\Socket\Server;
use React\ZMQ\Context;

class Pusher implements WampServerInterface {
	protected $subscribedTopics = array();

	public function onSubscribe( ConnectionInterface $conn, $topic ) {
		$this->subscribedTopics[ $topic->getId() ] = $topic;

		printf("onSubscribe\n");
	}

	public function onBlogEntry( $entry ) {
		$entryData = json_decode( $entry, true );

		if ( ! array_key_exists( $entryData['category'], $this->subscribedTopics ) ) {
			return;
		}

		$topic = $this->subscribedTopics[ $entryData['category'] ];

		$topic->broadcast( $entryData );
	}

	function onOpen( ConnectionInterface $conn ) {
		printf("onOpen\n");
	}

	function onClose( ConnectionInterface $conn ) {
		printf("onClose\n");
	}

	function onError( ConnectionInterface $conn, \Exception $e ) {
		printf("onError\n");
	}

	function onCall( ConnectionInterface $conn, $id, $topic, array $params ) {
		printf("onCall\n");
	}

	function onUnSubscribe( ConnectionInterface $conn, $topic ) {
		printf("onUnSubscribe\n");
	}

	function onPublish( ConnectionInterface $conn, $topic, $event, array $exclude, array $eligible ) {
		printf("onPublish\n");
	}
}

$loop   = Loop::get();
$pusher = new Pusher();

$context = new Context( $loop );
$pull    = $context->getSocket( ZMQ::SOCKET_PULL );
$pull->bind( 'tcp://127.0.0.1:10015' );
$pull->on( 'message', array( $pusher, 'onBlogEntry' ) );

$webSock   = new Server( '0.0.0.0:10019', $loop );
$webServer = new IoServer( new HttpServer( new WsServer( new WampServer( $pusher ) ) ), $webSock );

$loop->run();