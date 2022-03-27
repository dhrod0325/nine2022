<?php

session_start();

function parseProperties( $propFile ) {
	$value         = null;
	$txtProperties = file_get_contents( $propFile );

	$result = array();
	$lines  = explode( "\n", $txtProperties );

	$key                = "";
	$isWaitingOtherLine = false;

	foreach ( $lines as $i => $line ) {
		if ( empty( $line ) || ( ! $isWaitingOtherLine && strpos( $line, "#" ) === 0 ) ) {
			continue;
		}

		if ( ! $isWaitingOtherLine ) {
			$key   = substr( $line, 0, strpos( $line, '=' ) );
			$value = substr( $line, strpos( $line, '=' ) + 1, strlen( $line ) );
		} else {
			$value .= $line;
		}

		if ( strrpos( $value, "\\" ) === strlen( $value ) - strlen( "\\" ) ) {
			$value              = substr( $value, 0, strlen( $value ) - 1 ) . "\n";
			$isWaitingOtherLine = true;
		} else {
			$isWaitingOtherLine = false;
		}

		$result[ $key ] = trim( $value );

		unset( $lines[ $i ] );
	}

	return $result;
}

if ( @$_SESSION['DB_URL'] ) {
	define( 'DB_URL', $_SESSION['DB_URL'] );
	define( 'DB_USER', $_SESSION['DB_USER'] );
	define( 'DB_PASSWORD', $_SESSION['DB_PASSWORD'] );
} else {
	$properties = parseProperties( dirname( __FILE__ ) . '/../data/config/application-local.properties' );

	$webServerPort = $properties['l1j.web-server.port'];
	$tempDbUrl     = $properties['l1j.datasource.url'];
	$userName      = $properties['l1j.datasource.username'];
	$password      = $properties['l1j.datasource.password'];

	$dbUrl  = explode( ':', $tempDbUrl );
	$driver = $dbUrl[1];
	$host   = str_replace( '//', '', $dbUrl[2] );

	$a        = explode( '/', $dbUrl[3] );
	$port     = $a[0];
	$database = explode( '?', $a[1] )[0];

	define( 'DB_URL', $host . ':' . $port );
	define( 'DB_USER', $userName );
	define( 'DB_PASSWORD', $password );

	$_SESSION['DB_URL']      = DB_URL;
	$_SESSION['DB_USER']     = DB_USER;
	$_SESSION['DB_PASSWORD'] = DB_PASSWORD;
}

define('VER',date('YmdHis'));