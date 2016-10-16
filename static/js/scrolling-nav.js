//jQuery to collapse the navbar on scroll
$(window).scroll(function() {
    if ($(".navbar").offset().top > 50) {
        $(".navbar-fixed-top").addClass("top-nav-collapse");
    } else {
        $(".navbar-fixed-top").removeClass("top-nav-collapse");
    }
});

//jQuery for page scrolling feature - requires jQuery Easing plugin
$(function() {
  /*
    $('a.page-scroll').bind('click', function(event) {
        var $anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: $($anchor.attr('href')).offset().top
        }, 1500, 'easeInOutExpo');
        event.preventDefault();
    });
   */
});


$( document ).ready(function() {
        var url      = window.location.href;     // Returns full URL
        var getUrlParameter = function getUrlParameter(sParam) {
                    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
                    sURLVariables = sPageURL.split('&'),
                    sParameterName,
                    i;
                    
                    for (i = 0; i < sURLVariables.length; i++) {
                        sParameterName = sURLVariables[i].split('=');
                    
                        if (sParameterName[0] === sParam) {
                            return sParameterName[1] === undefined ? true : sParameterName[1];
                        }
                    }
                    
        };
                  
                    initRankerParams();
                    
                    function initRankerParams() {
                        var ranker =  getUrlParameter("ranker");
                        if(ranker=="cosine"){
                            $("#cosine").addClass("add-bold");
                        }else if(ranker=="ql"){
                            $("#ql").addClass("add-bold");
                        }else if(ranker=="linear"){
                            $("#linear").addClass("add-bold");
                        }else if(ranker=="numviews"){
                            $("#numview").addClass("add-bold");
                        }else if(ranker=="phrase"){
                            $("#phrase").addClass("add-bold");
                        }
                    }
        $("#searchBar").val(decodeURI(getUrlParameter("query")).replace(/\+/g, ' '));
        $("#ranker").val(getUrlParameter("ranker"));
        $("#num").val(getUrlParameter("num"));
        $("#format").val(getUrlParameter("format"));
                    
        $( "#submitSearch" ).click(function() {
            $("#searchKey").val($("#searchBar").val());
            $( "#searchForm" ).submit();
        });
        $( "#cosine" ).click(function() {
            $("#searchKey").val($("#searchBar").val());
            $("#ranker").val("cosine");
            $( "#searchForm" ).submit();
        });
        $( "#ql" ).click(function() {
            $("#searchKey").val($("#searchBar").val());
            $("#ranker").val("ql");
            $( "#searchForm" ).submit();
        });
        $( "#phrase" ).click(function() {
            $("#searchKey").val($("#searchBar").val());
            $("#ranker").val("phrase");
            $( "#searchForm" ).submit();
        });
        $( "#linear" ).click(function() {
            $("#searchKey").val($("#searchBar").val());
            $("#ranker").val("linear");
            $( "#searchForm" ).submit();
        });
        $( "#numview" ).click(function() {
            $("#searchKey").val($("#searchBar").val());
            $("#ranker").val("numviews");
            $( "#searchForm" ).submit();
        });
                    
        $("#searchBar").keypress(function (e) {
            if (e.which == 13) {
                    $("#searchKey").val($("#searchBar").val());
                    $('#searchForm').submit();
                    return false;
            }
        });
        
});