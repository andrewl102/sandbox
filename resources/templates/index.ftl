<#-- @ftlvariable name="data" type="java.util.List<ktorexample.MappingForDisplay>" -->
<#-- @ftlvariable name="query" type="java.lang.String" -->
<#-- @ftlvariable name="shortQuery" type="java.lang.String" -->
<!DOCTYPE html>
<html>
<head>
    <style type="text/css">
        /* search box */

        .button_box2 {
            margin: 20px auto;
        }

        /*-------------------------------------*/
        .cf:before, .cf:after {
            content: "";
            display: table;
        }

        .cf:after {
            clear: both;
        }

        .cf {
            zoom: 1;
        }

        /*-------------------------------------*/

        .form-wrapper-2 {
            width: 1330px;
            padding: 15px;
            background: #555;
            -moz-border-radius: 10px;
            -webkit-border-radius: 10px;
            border-radius: 10px;
            -moz-box-shadow: 0 1px 1px rgba(0, 0, 0, .4) inset, 0 1px 0 rgba(255, 255, 255, .2);
            -webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, .4) inset, 0 1px 0 rgba(255, 255, 255, .2);
            box-shadow: 0 1px 1px rgba(0, 0, 0, .4) inset, 0 1px 0 rgba(255, 255, 255, .2);
        }

        .form-wrapper-2 input {
            width: 1210px;
            height: 20px;
            padding: 10px 5px;
            float: left;
            font: bold 15px 'Raleway', sans-serif;
            border: 0;
            background: #eee;
            -moz-border-radius: 3px 0 0 3px;
            -webkit-border-radius: 3px 0 0 3px;
            border-radius: 3px 0 0 3px;
        }

        .form-wrapper-2 input:focus {
            outline: 0;
            background: #fff;
            -moz-box-shadow: 0 0 2px rgba(0, 0, 0, .8) inset;
            -webkit-box-shadow: 0 0 2px rgba(0, 0, 0, .8) inset;
            box-shadow: 0 0 2px rgba(0, 0, 0, .8) inset;
        }

        .form-wrapper-2 input::-webkit-input-placeholder {
            color: #999;
            font-weight: normal;
            font-style: italic;
        }

        .form-wrapper-2 input:-moz-placeholder {
            color: #999;
            font-weight: normal;
            font-style: italic;
        }

        .form-wrapper-2 input:-ms-input-placeholder {
            color: #999;
            font-weight: normal;
            font-style: italic;
        }

        .form-wrapper-2 button {
            overflow: visible;
            position: relative;
            float: right;
            border: 0;
            padding: 0;
            cursor: pointer;
            height: 40px;
            width: 110px;
            font: bold 15px/40px 'Raleway', sans-serif;
            color: #fff;
            text-transform: uppercase;
            background: #D88F3C;
            -moz-border-radius: 0 3px 3px 0;
            -webkit-border-radius: 0 3px 3px 0;
            border-radius: 0 3px 3px 0;
            text-shadow: 0 -1px 0 rgba(0, 0, 0, .3);
        }

        .form-wrapper-2 button:hover {
            background: #FA8807;
        }

        .form-wrapper-2 button:active,
        .form-wrapper-2 button:focus {
            background: #c42f2f;
        }

        .form-wrapper-2 button:before {
            content: '';
            position: absolute;
            border-width: 8px 8px 8px 0;
            border-style: solid solid solid none;
            border-color: transparent #D88F3C transparent;
            top: 12px;
            left: -6px;
        }

        .form-wrapper-2 button:hover:before {
            border-right-color: #FA8807;
        }

        .form-wrapper-2 button:focus:before {
            border-right-color: #c42f2f;
        }

        .form-wrapper-2 button::-moz-focus-inner {
            border: 0;
            padding: 0;
        }

        /* Custom */
        th {
            text-align: left;
        }

        ul {
            padding-left: 10px;
            margin: 0px;
        }

        #example_length {
            position: absolute;
            bottom: 10px;
            left: 5px;
        }
        #example_wrapper {
            background: wheat;
        }
        #example_info {
            position: absolute;
            left: 200px;
        }
    </style>
    <link rel="stylesheet" href="//cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
    <link rel="stylesheet" href="//cdn.datatables.net/plug-ins/1.10.19/features/searchHighlight/dataTables.searchHighlight.css">
    <script type="text/javascript" src="//code.jquery.com/jquery-3.3.1.js"></script>
    <script type="text/javascript" src="//bartaz.github.io/sandbox.js/jquery.highlight.js"></script>
    <script type="text/javascript" src="//cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="//cdn.datatables.net/plug-ins/1.10.19/features/searchHighlight/dataTables.searchHighlight.min.js"></script>
    <title>Translation Global Search</title>
</head>
<body>
<h4>By default, the search will match all strings beginning with the given text or an exact key match. Place a * (e.g. *Some value) before your search
to search the entire string for a match, but note that these searches might take longer to execute</h4>
<!-- search form 6 -->
<div class="button_box2">
    <form class="form-wrapper-2 cf" method="post" action="query">
        <input name="/5Z5zQ5Wx/query" type="text" value="${query}" placeholder="English source / translated value / source key" required>
        <button type="submit">Search</button>
        <table style="background-color: white;width: 100%;margin-top: 5px;font-size: 10px" id="example" border="1" class="stripe">
            <thead>
            <tr>
                <th style="width: 30px">Lang</th>
                <th style="width: 15%">Key</th>
                <th style="width: 25%">Value</th>
                <th style="width: 25%">English</th>
                <th>Translations</th>
                <th style="width: 12%">Project</th>
                <th style="width: 40px">Link</th>
            </tr>
            </thead>
            <tbody>
            <#list data as item>
            <tr>
                <td>${item.language}</td>
                <td>${item.key}</td>
                <td><pre>${item.value}</pre></td>
                <td><pre>${item.english}</pre></td>
                <td>
                <#if item.map?size != 0>
                    <span class="expand"><a href="#">View</a></span> <ul style="display: none">
                    <#list item.map as propName, propValue>
                        <li style="list-style: none">${propName} <pre>${propValue}</pre>
                    </#list>
                </#if>
                </td>
                <td>${item.project}</td>
                <td><a href="${item.url}" target="_blank">View</a></td>
            </tr>
            </#list>
            </tbody>
        </table>

    </form>

    <script type="text/javascript">
        $(document).ready(function() {
            var table = $('#example').DataTable(
                    {
                        // "paging":   false,
                        "ordering": false,
                        "info" : true,
                        "searching" : false
                    }
            );

            <#if shortQuery != "">
            // table.on( 'draw', function () {
                <#--body.highlight('${query}');-->
            // } );

                setTimeout(function() {
                    var body = $(table.table().body());
                    // body.unhighlight();

                    body.highlight('${shortQuery}')
                },250);
                setInterval(function() {
                    var body = $(table.table().body());
                    // body.unhighlight();

                    body.highlight('${shortQuery}')
                },1500);
            </#if>

            $(".expand").click(function(e){
                $(this).parent().find("span").hide();
                $(this).parent().find("ul").show();
            });
        } );
    </script>
</div>
</body>
</html>

