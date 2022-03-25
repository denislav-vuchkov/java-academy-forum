$(document).ready(function () {
    $('#usersTable').after('<div id="nav"></div>');
    var rowShown = 10;
    var rowsTotal = $('#usersTable tbody tr').length;
    var numPages = rowsTotal / rowShown;
    for (let i = 0; i < numPages; i++) {
        var pageNum = i+1;
        $('#nav').append('<a href="#" rel="'+i+'" class="form-control btn btn primary btn user d-sm-inline"> '+pageNum+'</a> ');
    }
    $('#usersTable tbody tr').hide();
    $('#usersTable tbody tr').slice(0, rowShown).show();
    $('#nav a:first').addClass('active');
    $('#nav a').bind('click', function () {

        $('#nav a').removeClass('active');
        $(this).addClass('active');
        var currPage = $(this).attr('rel');
        var startItem = currPage * rowShown;
        var endItem = startItem + rowShown;
        $('#usersTable tbody tr').css('opacity', '0.0').hide().slice(startItem, endItem).
            css('display', 'table-row').animate({opacity:1}, 300);
    });
});

/*$(document).ready(function(){
    $('#usersTable').after('<div id="nav"></div>');
    var rowsShown = 4;
    var rowsTotal = $('#usersTable tbody tr').length;
    var numPages = rowsTotal/rowsShown;
    for(i = 0;i < numPages;i++) {
        var pageNum = i + 1;
        $('#nav').append('<a href="#" rel="'+i+'">'+pageNum+'</a> ');
    }
    $('#usersTable tbody tr').hide();
    $('#usersTable tbody tr').slice(0, rowsShown).show();
    $('#nav a:first').addClass('active');
    $('#nav a').bind('click', function(){

        $('#nav a').removeClass('active');
        $(this).addClass('active');
        var currPage = $(this).attr('rel');
        var startItem = currPage * rowsShown;
        var endItem = startItem + rowsShown;
        $('#usersTable tbody tr').css('opacity','0.0').hide().slice(startItem, endItem).
        css('display','table-row').animate({opacity:1}, 300);
    });
});*/

