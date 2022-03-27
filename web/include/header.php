<div id="page">
    <div class="container navigation">
        <nav class="navbar navbar-inverse navbar-fixed-top navbar-expand-lg">
            <div class="container-fluid">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle navbar-toggler collapsed"
                            data-toggle="collapse"
                            data-target="#navbar"
                            aria-expanded="false"
                            aria-controls="navbar">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="/">리니지 서버 매니저</a>
                </div>
            </div>
            <div id="navbar" class="navbar-collapse collapse">
				<?php sideBar(); ?>
            </div>
        </nav>
    </div>
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-2 sidebar admin-menu">
                <div class="nav-menu">
					<?php sideBar(); ?>
                </div>
            </div>
            <div class="col-sm-10  col-md-10 col-sm-offset-3 col-md-offset-2" id="page-body">
