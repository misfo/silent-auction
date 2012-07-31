$(function() {
  var $win = $(window),
    $nav = $('.navbar');

  $nav.click(function () {
    setTimeout(function () {
      $win.scrollTop($win.scrollTop() - 47);
    }, 0);
  });

  $('#create-item').click(function() {
    $('#item-modal').modal({});
  });

  $('.modal .save').click(function() {
    var $form = $(this).parents('.modal').find('form');
    $.ajax({
      type: 'POST',
      url: $form.attr('action'),
      data: $form.serialize(),
      success: function() {
        window.location = "/";
      },
      error: function(jqXHR) {
        var data = $.parseJSON(jqXHR.responseText);
        console.log(data);
      }
    });
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
