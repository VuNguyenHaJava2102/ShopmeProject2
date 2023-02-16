$(document).ready(function() {
    $(".link-logout").on("click", function(e) {
        e.preventDefault();

        $("#formLogout").submit();
    });
});