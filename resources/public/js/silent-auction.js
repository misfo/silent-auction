$(function() {
  var $win = $(window),
    $nav = $('.navbar');

  $nav.click(function () {
    setTimeout(function () {
      $win.scrollTop($win.scrollTop() - 47);
    }, 0);
  });

  function modalSave() {
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
          $controlGroups = $form.find('.control-group');
        $controlGroups.removeClass('error');
        $controlGroups.find('.help-inline').text('');

        for (var attr in data) {
          var help = data[attr].join(', '),
            $controlGroup = $form.find('input[name=' + attr + ']').parents('.control-group');
          $controlGroup.addClass('error');
          $controlGroup.find('.help-inline').text(help);
        }
      }
    });
  }

  $('#create-item, .edit-item').click(function() {
    var $this = $(this);
    $.get($this.attr('href'), {}, function(html) {
      $('#item-modal')
        .html(html)
        .modal({})
        .find('.save').click(modalSave);
    });
    return false;
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
