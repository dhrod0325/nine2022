<?php

class Pagination {
	private $pageNo;
	private $recordCountPerPage;
	private $pageSize;
	private $totalRecordCount;

	public function __construct( $args = array() ) {
		$pageNo = @$_REQUEST['pageNo'];

		if ( ! $pageNo ) {
			$pageNo = 1;
		}

		$default = array( 'recordCountPerPage' => 10, 'pageSize' => 5, 'pageNo' => $pageNo );

		$args = array_merge( $default, $args );

		$pageNo = $args['pageNo'];

		if ( ! $pageNo || $pageNo < 0 ) {
			$pageNo = 1;
		}

		$this->pageNo             = $pageNo;
		$this->recordCountPerPage = $args['recordCountPerPage']; //노출되는 줄의 수
		$this->pageSize           = $args['pageSize'];  //한 블럭당 표시될수 있는 페이징 번호의수
		$this->totalRecordCount   = 0;
	}

	/**
	 * @return int|mixed
	 */
	public function getPageNo() {
		return $this->pageNo;
	}

	/**
	 * @param int|mixed $pageNo
	 */
	public function setPageNo( $pageNo ) {
		$this->pageNo = $pageNo;
	}

	/**
	 * @return mixed
	 */
	public function getRecordCountPerPage() {
		return $this->recordCountPerPage;
	}

	/**
	 * @param mixed $recordCountPerPage
	 */
	public function setRecordCountPerPage( $recordCountPerPage ) {
		$this->recordCountPerPage = $recordCountPerPage;
	}

	/**
	 * @return mixed
	 */
	public function getPageSize() {
		return $this->pageSize;
	}

	/**
	 * @param mixed $pageSize
	 */
	public function setPageSize( $pageSize ) {
		$this->pageSize = $pageSize;
	}

	/**
	 * @return int
	 */
	public function getTotalRecordCount() {
		return $this->totalRecordCount;
	}

	/**
	 * @param int $totalRecordCount
	 */
	public function setTotalRecordCount( $totalRecordCount ) {
		$this->totalRecordCount = $totalRecordCount;
	}

	public function getTotalPageCount() {
		return (int) ( ( $this->getTotalRecordCount() - 1 ) / $this->getRecordCountPerPage() ) + 1;
	}

	public function getFirstPageNo() {
		return 1;
	}

	public function getLastPageNo() {
		return $this->getTotalPageCount();
	}

	public function getFirstPageNoOnPageList() {
		$a = ( ( $this->getPageNo() - 1 ) / $this->getPageSize() );

		return (int) $a * $this->getPageSize() + 1;
	}

	public function getLastPageNoOnPageList() {
		$lastPageNoOnPageList = $this->getFirstPageNoOnPageList() + $this->getPageSize() - 1;
		if ( $lastPageNoOnPageList > $this->getTotalPageCount() ) {
			$lastPageNoOnPageList = $this->getTotalPageCount();
		}

		return $lastPageNoOnPageList;
	}

	public function getFirstRecordIndex() {
		return ( $this->getPageNo() - 1 ) * $this->getRecordCountPerPage();
	}

	public function getLastRecordIndex() {
		return $this->getPageNo() * $this->getRecordCountPerPage();
	}

	public function getPrevPage() {
		if ( $this->getFirstPageNoOnPageList() > $this->getPageSize() ) {
			return $this->getFirstPageNoOnPageList() - $this->getPageSize();
		} else {
			return $this->getFirstPageNo();
		}
	}

	public function getNextPage() {

		$result = $this->getFirstPageNoOnPageList() + $this->getPageSize();

		if ( $result > $this->getLastPageNo() ) {
			$result = $this->getLastPageNo();
		}

		return $result;
	}
}