package edu.nyu.cs.cs2580;

/**
 * Created by sanchitmehta on 15/10/16.
 */
public class HTMLOutputFromater {

    public String getSearchResultRow(String title,String startingText,int numViews,Double score,int docId){
        String row = new String("<div class=\"row\">\n" +
                "                        <div class=\".col-xs-12 .col-md-6\">\n" +
                "                            <div class=\"card\">\n" +
                "                                <div class=\"top-buffer\"></div>\n" +
                "                                <span class=\"card-title\">"+title+"</span>\n" +
                "                                <div class=\"card-content\">\n" +
                "                                    <p> "+startingText+"</p>\n" +
                "                                </div>\n" +
                "                                \n" +
                "                                <div class=\"card-action\">\n" +
                "                                    <a href=\"#\" target=\"new_blank\"><i class=\"fa fa-eye\"></i> "+numViews+" </a>\n" +
                "                                    <a href=\"#\" target=\"new_blank\"><i class=\"fa fa-slack\"></i>&nbsp;"+score+"</a>\n" +
                "                                    <a href=\"#\" target=\"new_blank\"><i class=\"fa fa-folder\"></i>&nbsp;"+docId+"</a>\n" +
                "                                </div>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                </div>");
        return row;
    }

    public String getHeader(){
        String header = new String("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <meta name=\"description\" content=\"\">\n" +
                "    <meta name=\"author\" content=\"\">\n" +
                "\n" +
                "    <title>BitP Search</title>\n" +
                "\n" +
                "    <!-- Custom CSS -->\n" +
                "    <link href=\"static/css/card.css\" rel=\"stylesheet\">\n" +
                "    \n" +
                "    <!-- Bootstrap Core CSS -->\n" +
                "    <link href=\"static/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "\n" +
                "    <!-- Custom CSS for Page Scroll-->\n" +
                "    <link href=\"static/css/scrolling-nav.css\" rel=\"stylesheet\">\n" +
                "\n" +
                "    <!-- Font Awesome CSS -->\n" +
                "    <link href=\"static/css/font-awesome.css\" rel=\"stylesheet\">\n" +
                "\n" +
                "\n" +
                "    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->\n" +
                "    <!--[if lt IE 9]>\n" +
                "        <script src=\"https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js\"></script>\n" +
                "        <script src=\"https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js\"></script>\n" +
                "    <![endif]-->\n" +
                "\n" +
                "</head>\n" +
                "<form id=\"searchForm\" action=\"\" method=\"get\">\n" +
                "    <input type=\"hidden\" id=\"searchKey\" name=\"query\" />\n" +
                "    <input type=\"hidden\" id=\"ranker\" name=\"ranker\" />\n" +
                "    <input type=\"hidden\" id=\"num\" name=\"num\" />\n" +
                "    <input type=\"hidden\" id=\"format\" name=\"format\" />\n" +
                "</form>\n" +
                "<!-- The #page-top ID is part of the scrolling feature - the data-spy and data-target are part of the built-in Bootstrap scrollspy function -->\n" +
                "\n" +
                "<body id=\"page-top\" data-spy=\"scroll\" data-target=\".navbar-fixed-top\">\n" +
                "\n" +
                "    <!-- Navigation -->\n" +
                "    <nav class=\"navbar navbar-default navbar-fixed-top\" role=\"navigation\">\n" +
                "        <div class=\"container\">\n" +
                "            <div class=\"navbar-header page-scroll\">\n" +
                "                <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-ex1-collapse\">\n" +
                "                    <span class=\"sr-only\">Toggle navigation</span>\n" +
                "                    <span class=\"icon-bar\"></span>\n" +
                "                    <span class=\"icon-bar\"></span>\n" +
                "                    <span class=\"icon-bar\"></span>\n" +
                "                </button>\n" +
                "                <a class=\"page-scroll\" href=\"#page-top\"><style=\"margin:0;padding-bottom:10px!\"><img src=\"static/images/logo.png\" height=\"55\" width=\"154\"></style></a>\n" +
                "            </div>\n" +
                "            \n" +
                "\n" +
                "            <!-- Collect the nav links, forms, and other content for toggling -->\n" +
                "            <div class=\"collapse navbar-collapse navbar-ex1-collapse\">\n" +
                "                <ul class=\"nav navbar-nav\" style=\"background:678079;padding-top:4px\">\n" +
                "                    <!-- Hidden li included to remove active class from about link when scrolled up past about section -->\n" +
                " <li class=\"hidden\">\n" +
                "                        <a class=\"page-scroll\" href=\"#page-top\"></a>\n" +
                "                    </li>\n" +
                "                    <li>\n" +
                "                        <a class=\"page-scroll top-small-buffer\" href=# id=\"ql\">Ql</a>\n" +
                "                    </li>\n" +
                "                    <li>\n" +
                "                        <a class=\"page-scroll top-small-buffer\" href=# id=\"cosine\">Cosine</a>\n" +
                "                    </li>\n" +
                "                    <li>\n" +
                "                        <a class=\"page-scroll top-small-buffer\" href=# name=\"numviews\" id=\"numview\">NumViews</a>\n" +
                "                    </li>\n" +
                "                    <li>\n" +
                "                        <a class=\"page-scroll top-small-buffer\" href=# id=\"phrase\">Phrase</a>\n" +
                "                    </li>\n" +
                "                    <li>\n" +
                "                        <a class=\"page-scroll top-small-buffer\" href=# id=\"linear\">Linear</a>\n" +
                "                    </li>"+
                "                    \n" +
                "                </ul>\n" +
                "            </div>\n" +
                "            <!-- /.navbar-collapse -->\n" +
                "            <div class=\"box\">\n" +
                "                <div class=\"container-4 .col-xs-12 .col-md-6\">\n" +
                "                    <input type=\"search\" id=\"searchBar\" placeholder=\"Search...\" />\n" +
                "                    <button class=\"icon\" id=\"submitSearch\"><i class=\"fa fa-search\"></i></button>\n" +
                "                </div>\n" +
                "                <div style=\"padding-bottom:5px\">\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <!-- /.container -->\n" +
                "    </nav>\n" +
                "\n" +
                "    <!-- Intro Section -->\n" +
                "    <section id=\"intro\" class=\"intro-section\">\n" +
                "        <div class=\"container\">");
        return header;
    }

    public String getFooter(){
        String footer = new String("<!-- Contact Section -->\n" +
                "    <section id=\"contact\" class=\"contact-section\">\n" +
                "            <div class=\"row\">\n" +
                "                    <div class=\"footer-text\">Developed towards partial course requirement for CSGA2580 - Group 8</div>\n" +
                "            </div>\n" +
                "    </section>\n" +
                "\n" +
                "    <!-- jQuery -->\n" +
                "    <script src=\"static/js/jquery.js\"></script>\n" +
                "\n" +
                "    <!-- Bootstrap Core JavaScript -->\n" +
                "    <script src=\"static/js/bootstrap.min.js\"></script>\n" +
                "\n" +
                "    <!-- Scrolling Nav JavaScript -->\n" +
                "    <script src=\"static/js/jquery.easing.min.js\"></script>\n" +
                "    <script src=\"static/js/scrolling-nav.js\"></script>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>");
        return footer;
    }
}
