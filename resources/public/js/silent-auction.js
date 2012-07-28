$(function() {
  var $win = $(window),
    $nav = $('.navbar');

  $nav.click(function () {
    setTimeout(function () {
      $win.scrollTop($win.scrollTop() - 47);
    }, 0);
  });

  $('#create-item').click(function() {
    $('#modal').modal({});
  });

  $('.modal .save').click(function() {
    $(this).parents('.modal').find('form')[0].submit();
  });

  $('.delete-item').click(function() {
    if (confirm('Are you sure?')) {
      $.post($(this).attr('href'), {}, function() {
        window.location = "/";
      });
    }
    return false;
  });
});
